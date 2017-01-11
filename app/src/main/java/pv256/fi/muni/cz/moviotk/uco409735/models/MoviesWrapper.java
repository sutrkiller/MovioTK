package pv256.fi.muni.cz.moviotk.uco409735.models;

import com.google.gson.annotations.SerializedName;


/**
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 11/8/2016.
 */

public class MoviesWrapper {
    @SerializedName("results")
    private Movie[] mResults;

    public MoviesWrapper() {
    }

    public Movie[] getResults() {
        return mResults;
    }

    @SuppressWarnings("unused")
    public void setResults(Movie[] results) {
        mResults = results;
    }
}
