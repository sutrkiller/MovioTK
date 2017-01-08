package pv256.fi.muni.cz.moviotk.uco409735.detail;

import android.os.Bundle;
import pv256.fi.muni.cz.moviotk.uco409735.helpers.Log;

import pv256.fi.muni.cz.moviotk.uco409735.DetailFragment;
import pv256.fi.muni.cz.moviotk.uco409735.R;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.detail.DetailContract.View;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

/**
 * Presenter for DetailFragment provides database access.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 */

public class DetailPresenter implements DetailContract.UserInteractions {

    private final MovieManager mManager;
    private View mView;
    private Movie mMovie;

    public DetailPresenter(View view, Movie movie, MovieManager manager) {
        this.mView = view;
        mMovie = movie;
        mManager = manager;
    }

    @Override
    public boolean valid() {
        return mMovie != null;
    }

    public void onSaveToFavoritesClicked() {
        mMovie.setFromDb(!mMovie.isFromDb());
        if (!mMovie.isFromDb()) {
            Log.d(DetailFragment.class.getName(), "Remove from db clicked");
            mManager.remove(mMovie.getId());
        } else {
            Log.d(DetailFragment.class.getName(), "Add to db clicked");
            mManager.add(mMovie);
        }
        mView.setFavoritesButtonDrawable(mMovie.isFromDb() ? R.drawable.ic_grade_black_24dp : R.drawable.ic_add_black_24dp);
    }

    public Movie getMovie() {
        return mMovie;
    }


}
