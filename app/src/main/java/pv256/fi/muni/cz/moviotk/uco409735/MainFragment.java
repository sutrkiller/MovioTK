package pv256.fi.muni.cz.moviotk.uco409735;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.media.midi.MidiOutputPort;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pv256.fi.muni.cz.moviotk.uco409735.Adapters.MovieRecyclerViewAdapter;
import pv256.fi.muni.cz.moviotk.uco409735.Adapters.SimpleSectionedRecyclerViewAdapter;
import pv256.fi.muni.cz.moviotk.uco409735.Data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.service.MovieDownloadService;

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
    private BroadcastReceiver mBroadcastReceiver;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        Log.i(MainFragment.class.getName(),"onCreateView");

        final View view = inflater.inflate(R.layout.fragment_main, container,false);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

            if(mPosition != RecyclerView.NO_POSITION) {
                SimpleSectionedRecyclerViewAdapter adapter = (SimpleSectionedRecyclerViewAdapter) mRecyclerView.getAdapter();
                mRecyclerView.smoothScrollToPosition(adapter.positionToSectionedPosition(mPosition));
            }
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fillRecyclerView(view, MoviesStorage.getInstance().getMovieMap());
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,new IntentFilter(MovieDownloadService.BROADCAST_INTENT));

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

        boolean any = false;
        if(movies != null && !movies.isEmpty()) {
            for (ArrayList<Movie> movie : movies.values()) {
                    any |= !movie.isEmpty();
            }
        }

        if(any) {
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

        if (movieRV.getAdapter() != null) {
            movieRV.swapAdapter(mSectionedAdapter,true);
        } else {
            movieRV.setAdapter(mSectionedAdapter);
        }
    }

    private boolean mLastSource;
    public void loadMovies(View view, String genres, boolean source) {
        MoviesStorage storage = MoviesStorage.getInstance();
//        if (source != mLastSource) {
//            storage.clearMap();
//            storage.setSelectedGenres("-1");
//            mLastSource = source;
//        }
        if (!source) {
            if (!storage.isMapEmpty() && genres.equals(storage.getSelectedGenres())) {
                fillRecyclerView(view, storage.getMovieMap());
            } else {
                MovieDownloadService.startDownload(getActivity(),MovieDownloadService.RESULT_KEY,genres);
            }
        } else {

                fillRecyclerView(view, storage.getMovieMap());

        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public interface OnMovieSelectedListener {
        void onMovieSelect(Movie movie);
    }
}
