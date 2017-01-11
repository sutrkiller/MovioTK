package pv256.fi.muni.cz.moviotk.uco409735.main;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import pv256.fi.muni.cz.moviotk.uco409735.data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.sync.UpdaterSyncAdapter;

/**
 * Implements MainContract.UserInteractions to handle user operations from MainActivity
 */

public class MainPresenter implements MainContract.UserInteractions {
    private MainContract.View mView;

    public MainPresenter(MainContract.View view){
        mView = view;
    }

    @Override
    public void onDbSourceCheckedChange(boolean isChecked) {
        MoviesStorage.getInstance().clearMap();
        mView.showMessage("Source changed to " + (isChecked ? "Database" : "Internet"));
        if (!isChecked) {
            mView.reloadMovies(false);
        } else {
            mView.restartLoader();
        }
    }

    @Override
    public void updateDb(Context context, boolean fromDb) {
        mView.showMessage("Checking updates...");
        UpdaterSyncAdapter.syncImmediately(context);
        if (fromDb) {
            mView.restartLoader();
        }
        mView.showMessage("Update finished");
    }

    public void onLoaderFinished(Cursor data, boolean fromDb) {
        if (data != null && fromDb) {
            MoviesStorage storage = MoviesStorage.getInstance();
            storage.setSelectedGenres("-1");
            ArrayList<Movie> list = new ArrayList<>();
            while (data.moveToNext()) {
                list.add(MovieManager.getMovieFromCursor(data));
            }
            data.close();
            storage.addMovieCategory("Favorites",list);
            mView.reloadMovies(true);
        }
    }


}
