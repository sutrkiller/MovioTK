package pv256.fi.muni.cz.moviotk.uco409735.Data;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pv256.fi.muni.cz.moviotk.uco409735.Movie;

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

    public ArrayList<Movie> getMovieCategory(String category) {
        Log.d(MoviesStorage.class.getName(), "getMovieCategory() called");
        ArrayList<Movie> list = mMovieMap.get(category);
        return list == null ? new ArrayList<Movie>() : list;
    }

    public void setMovieMap(Map<String, ArrayList<Movie>> map) {
        Log.d(MoviesStorage.class.getName(), "setMovieMap() called");
        if (map != null) {
            mMovieMap.clear();
            mMovieMap.putAll(map);
        }
    }

    public void addMovieCategory(String category, ArrayList<Movie> movies) {
        Log.d(MoviesStorage.class.getName(), "addMovieCategory() called");
        mMovieMap.put(category, movies);
    }

    public void addMovieMap(Map<String, ArrayList<Movie>> map) {
        Log.d(MoviesStorage.class.getName(), "addMovieMap() called");
        mMovieMap.putAll(map);
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

