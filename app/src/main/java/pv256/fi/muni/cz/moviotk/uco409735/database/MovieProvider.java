package pv256.fi.muni.cz.moviotk.uco409735.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import pv256.fi.muni.cz.moviotk.uco409735.helpers.Log;

import java.util.Arrays;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovioContract.MovieEntry;

/**
 * Created by Tobias on 12/29/2016.
 */

public class MovieProvider extends ContentProvider {
    private static final String TAG = MovieProvider.class.getName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIES = 100;
    public static final int MOVIE_ID = 101;

    private Context mContext;
    private MovioDbHelper mDBHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovioContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovioContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovioContract.PATH_MOVIE + "/#", MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDBHelper = new MovioDbHelper(mContext);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Log.d(TAG, Arrays.toString(selectionArgs));
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_ID: {
                retCursor = mDBHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIES: {
                retCursor = mDBHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(mContext.getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, values.toString());
        Uri returnUri;
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
        if (_id > 0) {
            returnUri = MovieEntry.buildMovieUri(_id);
        } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowsUpdated =
                db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
