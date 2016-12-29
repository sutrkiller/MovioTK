package pv256.fi.muni.cz.moviotk.uco409735.Db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

/**
 * Created by Tobias on 12/29/2016.
 */

public class MovioContract {
    public static final String CONTENT_AUTHORITY = "cz.muni.fi.pv256.moviotk.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_COVER_PATH = "coverPath";
        public static final String COLUMN_POPULARITY = "overview";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_FROM_DB = "fromDb";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
