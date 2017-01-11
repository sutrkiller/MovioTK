package pv256.fi.muni.cz.moviotk.uco409735.data;

import pv256.fi.muni.cz.moviotk.uco409735.helpers.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

/**
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 11/8/2016.
 */

public class MoviesStorage {
    private static MoviesStorage mInstance = null;

    private Map<String, ArrayList<Movie>> mMovieMap = new HashMap<>();
    private String selectedGenres = "";

    private MoviesStorage() {
    }

    public static MoviesStorage getInstance() {
        if (mInstance == null) {
            mInstance = new MoviesStorage();
        }
        return mInstance;
    }

    public Map<String, ArrayList<Movie>> getMovieMap() {
        Log.d(MoviesStorage.class.getName(), "getMovieMap() called");
        return mMovieMap;
    }

    public void addMovieCategory(String category, ArrayList<Movie> movies) {
        Log.d(MoviesStorage.class.getName(), "addMovieCategory() called");
        mMovieMap.put(category, movies);
    }

    public void clearMap() {
        mMovieMap.clear();
    }



    public boolean isMapEmpty() {
        return mMovieMap == null || mMovieMap.isEmpty();
    }


    public String getSelectedGenres() {
        return selectedGenres;
    }

    public void setSelectedGenres(String selectedGenres) {
        this.selectedGenres = selectedGenres;
    }

}

