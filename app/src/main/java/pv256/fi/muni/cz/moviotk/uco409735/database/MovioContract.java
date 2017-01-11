package pv256.fi.muni.cz.moviotk.uco409735.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database contract
 */

public class MovioContract {
    static final String CONTENT_AUTHORITY = "pv256.fi.muni.cz.moviotk.uco409735.app";
    static final String PATH_MOVIE = "movie";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        static final String TABLE_NAME = "movies";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_COVER_PATH = "coverPath";
        static final String COLUMN_POPULARITY = "popularity";
        static final String COLUMN_BACKDROP_PATH = "backdropPath";
        static final String COLUMN_OVERVIEW = "overview";
        static final String COLUMN_FROM_DB = "fromDb";

        static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
