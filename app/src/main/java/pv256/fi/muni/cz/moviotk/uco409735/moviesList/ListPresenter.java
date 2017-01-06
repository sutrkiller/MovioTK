package pv256.fi.muni.cz.moviotk.uco409735.moviesList;

import android.view.View;

import pv256.fi.muni.cz.moviotk.uco409735.data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.service.MovieDownloadService;

/**
 * Presenter for MainFragments. Handles downloading data from db or net.
 */

public class ListPresenter implements ListContract.UserInteractions {

    private final ListContract.View mView;

    public ListPresenter(ListContract.View view) {
        mView = view;
    }

    public void loadMovies(View view, String genres, boolean source) {
        MoviesStorage storage = MoviesStorage.getInstance();
        if (!source) {
            if (!storage.isMapEmpty() && genres.equals(storage.getSelectedGenres())) {
                mView.fillRecyclerView(view, storage.getMovieMap());
            } else {
                MovieDownloadService.startDownload(view.getContext(),MovieDownloadService.RESULT_KEY,genres);
            }
        } else {

            mView.fillRecyclerView(view, storage.getMovieMap());

        }

    }
}
