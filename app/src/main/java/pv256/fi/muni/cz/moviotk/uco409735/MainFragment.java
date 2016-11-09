package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.Response;
import pv256.fi.muni.cz.moviotk.uco409735.Adapters.MovieRecyclerViewAdapter;
import pv256.fi.muni.cz.moviotk.uco409735.Adapters.SimpleSectionedRecyclerViewAdapter;
import pv256.fi.muni.cz.moviotk.uco409735.Data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.Data.ApiClient;
import pv256.fi.muni.cz.moviotk.uco409735.Data.MovieDO;

/**
 * Main fragment shows in main activity.
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";

    private int mPosition = RecyclerView.NO_POSITION;
    private OnMovieSelectedListener mListener;
    private Context mContext;
    private RecyclerView mRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(MainFragment.class.getName(),"onAttach");
        try {
            mListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG,"Activity must implement OnMovieSelectedListener",e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(MainFragment.class.getName(),"onDetach");
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainFragment.class.getName(),"onCreate");
        mContext = getActivity().getApplicationContext();
    }

    //private boolean test = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        Log.i(MainFragment.class.getName(),"onCreateView");

        View view = inflater.inflate(R.layout.fragment_main, container,false);

       loadMovies(view);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

            if(mPosition != RecyclerView.NO_POSITION) {
                SimpleSectionedRecyclerViewAdapter adapter = (SimpleSectionedRecyclerViewAdapter) mRecyclerView.getAdapter();
                mRecyclerView.smoothScrollToPosition(adapter.positionToSectionedPosition(mPosition));
            }
        }

//        FloatingActionButton but = (FloatingActionButton) view.findViewById(R.id.testButton);
//        but.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fillRecyclerView(view,test ? MovieData.getInstance().getMovieList() : null);
//                test = !test;
//
//            }
//        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(MainFragment.class.getName(),"onSaveInstanceState");
        if(mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void fillRecyclerView(View rootView, Map<String,ArrayList<Movie>> movies) {
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_movies);
        ViewStubCompat viewStubEmpty = (ViewStubCompat) rootView.findViewById(R.id.recyclerView_movies_empty);
        View inflated = rootView.findViewById(R.id.recyclerView_movies_empty_infl);

        if(movies != null && !movies.isEmpty()) {
            if (inflated != null) inflated.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            setAdapter(mRecyclerView,movies);
        } else {
            if (inflated == null)inflated = viewStubEmpty.inflate();
            TextView textView = (TextView) inflated.findViewById(R.id.recyclerView_movies_empty_text);
            String text = isNetworkConnected() ? "No movies available." : "Internet connection not available";


            textView.setText(text);
            inflated.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void setAdapter(RecyclerView movieRV, final Map<String,ArrayList<Movie>> moviesMap) {
        final ArrayList<Movie> movies = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>(moviesMap.keySet());
        Collections.sort(keys);
        List<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<>();
        for (String key : keys) {
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(movies.size(),key));
            movies.addAll(moviesMap.get(key));
        }

        MovieRecyclerViewAdapter.OnItemClickListener listener = new MovieRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                mPosition = movies.indexOf(movie);
                mListener.onMovieSelect(movie);
            }

            @Override
            public void onItemLongClick(Movie movie) {
                Toast.makeText(mContext,movie.getTitle(),Toast.LENGTH_SHORT).show();
            }
        };

        MovieRecyclerViewAdapter adapter = new MovieRecyclerViewAdapter(mContext,movies,listener);

        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(R.layout.list_section_movie,R.id.list_section_title,adapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        if (movieRV.getAdapter() != null) {//
            movieRV.swapAdapter(mSectionedAdapter,true);//
        } else {//
            movieRV.setAdapter(mSectionedAdapter);
        }//
    }

    private GetMoviesTask mAsyncTask;
    private void loadMovies(View view) {
        if (!MoviesStorage.getInstance().isMapEmpty()) {

                fillRecyclerView(view, MoviesStorage.getInstance().getMovieMap());
        } else {
            if (mAsyncTask != null) {
                mAsyncTask.cancel(true);
            }
            mAsyncTask = new GetMoviesTask(this);
            mAsyncTask.execute();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
    }

//    public void stopAsyncTask() {
//
//    }


    private class GetMoviesTask extends AsyncTask<Void, String, Boolean> {
        private static final String URL = "https://api.themoviedb.org/3/discover/movie?";
        private static final String API_KEY = "58c85d23ae0adc29b9921f8a08b1c335";
        private Gson mGson = new Gson();
        private final WeakReference<MainFragment> mFragmentWeakReference;
        private final ApiClient mClient;

        public GetMoviesTask(MainFragment mainFragment) {
            mFragmentWeakReference = new WeakReference<MainFragment>(mainFragment);
            mClient = ApiClient.getInstance();
        }

        private boolean downloadUpcomingMovies() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateNow = format.format(Calendar.getInstance().getTime());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            String dateTo = format.format(cal.getTime());
            Response response = mClient.doGet(URL + "api_key=" + API_KEY + "&language=en-US&sort_by=popularity.desc&page=1&primary_release_date.gte=" + dateNow + "&primary_release_date.lte=" + dateTo);

            if (response != null) {
                if (response.isSuccessful()) {
                    try {
                        final MovieDO movieDO = mGson.fromJson(response.body().charStream(), MovieDO.class);
                        response.body().close();
                        MoviesStorage.getInstance().addMovieCategory("Upcoming movies", new ArrayList<>(Arrays.asList(movieDO.getResults())));
                        return true;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return false;
        }

        private boolean downloadNowPlayingMovies() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateNow = format.format(Calendar.getInstance().getTime());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            String dateFrom = format.format(cal.getTime());
            Response response = mClient.doGet(URL + "api_key=" + API_KEY + "&language=en-US&sort_by=popularity.desc&page=1&primary_release_date.gte=" + dateFrom + "&primary_release_date.lte=" + dateNow);

            if (response != null) {
                if (response.isSuccessful()) {
                    try {
                        final MovieDO movieDO = mGson.fromJson(response.body().charStream(), MovieDO.class);
                        response.body().close();
                        MoviesStorage.getInstance().addMovieCategory("Now playing", new ArrayList<>(Arrays.asList(movieDO.getResults())));
                        return true;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean r1 = downloadUpcomingMovies();
            boolean r2 = downloadNowPlayingMovies();
            if (r1 || r2) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            MainFragment fragment = mFragmentWeakReference.get();
            if (fragment == null) return;
            if (result == null || !result) {
                MoviesStorage.getInstance().clearMap();
                Toast.makeText(mContext,"Unable to parse response.",Toast.LENGTH_LONG).show();
            }
            View view = fragment.getView();
            if (view != null) {
                fragment.fillRecyclerView(view, MoviesStorage.getInstance().getMovieMap());
            }
        }
    }


    public interface OnMovieSelectedListener {
        void onMovieSelect(Movie movie);
    }
}
