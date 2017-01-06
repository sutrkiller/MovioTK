package pv256.fi.muni.cz.moviotk.uco409735.moviesList;

import java.util.ArrayList;
import java.util.Map;

import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

/**
 * Interfaces for implementing MVP pattern for MainFragment
 */

public interface ListContract {
    interface View {
        void fillRecyclerView(android.view.View rootView, Map<String, ArrayList<Movie>> movies);
    }

    interface UserInteractions {
        void loadMovies(android.view.View view, String genres, boolean source);
    }
}
