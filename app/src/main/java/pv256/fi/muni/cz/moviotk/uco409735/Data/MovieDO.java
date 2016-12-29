package pv256.fi.muni.cz.moviotk.uco409735.Data;

import com.google.gson.annotations.SerializedName;

import pv256.fi.muni.cz.moviotk.uco409735.Movie;


/**
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 11/8/2016.
 */

public class MovieDO {
    @SerializedName("results")
    private Movie[] mResults;

    public MovieDO() {
    }

    public Movie[] getResults() {
        return mResults;
    }

    public void setResults(Movie[] results) {
        mResults = results;
    }
}
