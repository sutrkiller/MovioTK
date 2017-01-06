package pv256.fi.muni.cz.moviotk.uco409735.detail;

import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

/**
 * Interface for DetailsFragment
 */

public interface DetailContract {
    String ARGS_MOVIE = "args_movie";

    interface View {
        void setFavoritesButtonDrawable(int id);
    }

    interface UserInteractions {
        boolean valid();
        void onSaveToFavoritesClicked();
        Movie getMovie();
    }
}
