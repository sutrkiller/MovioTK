package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import pv256.fi.muni.cz.moviotk.uco409735.Adapters.MovieRecyclerViewAdapter;

/**
 * Main fragment shows in main activity.
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";

    private int mPosition = RecyclerView.NO_POSITION;
    private OnMovieSelectedListener mListener;
    private Context mContext;
    private RecyclerView mRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG,"Activity must implement OnMovieSelectedListener",e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container,false);

        fillRecyclerView(view);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

            if(mPosition != RecyclerView.NO_POSITION) {
                mRecyclerView.smoothScrollToPosition(mPosition);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void fillRecyclerView(View rootView) {
        ArrayList<Movie> movies = MovieData.getInstance().getMovieList();

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_movies);

        if(movies != null && !movies.isEmpty()) {
            setAdapter(mRecyclerView,movies);
        }
    }

    private void setAdapter(RecyclerView movieRV, final ArrayList<Movie> movies) {
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
        movieRV.setAdapter(adapter);
    }


    public interface OnMovieSelectedListener {
        void onMovieSelect(Movie movie);
    }
}
