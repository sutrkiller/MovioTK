package pv256.fi.muni.cz.moviotk.uco409735.main;

import android.content.Context;
import android.database.Cursor;

/**
 * MVP interface for MainActivity
 */

public interface MainContract {
    interface View{
        void reloadMovies(boolean fromDb);
        void showMessage(String text);
        void restartLoader();
    }

    interface UserInteractions{
        void onDbSourceCheckedChange(boolean isChecked);
        void updateDb(Context context, boolean fromDb);
        void onLoaderFinished(Cursor data, boolean fromDb);
    }
}
