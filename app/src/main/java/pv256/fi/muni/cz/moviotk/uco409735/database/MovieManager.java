package pv256.fi.muni.cz.moviotk.uco409735.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import pv256.fi.muni.cz.moviotk.uco409735.helpers.Log;

import java.util.ArrayList;

import pv256.fi.muni.cz.moviotk.uco409735.database.MovioContract.MovieEntry;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

/**
 * Created by Tobias on 12/29/2016.
 */

public class MovieManager {
    private static final String TAG = MovieManager.class.getName();

    private static final String WHERE_ID = MovieEntry._ID + " = ?";

    public static final String[] MOVIE_COLS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_COVER_PATH,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_FROM_DB
    };

    private Context mContext;

    public MovieManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean contains(long id) {
        Cursor cursor = mContext.getContentResolver()
                .query(MovieEntry.CONTENT_URI, new String[]{MovieEntry._ID}, WHERE_ID,
                        new String[]{String.valueOf(id)}, null);

        if (cursor == null) {
            return false;
        }

        boolean result = cursor.getCount() == 1;
        cursor.close();
        return result;
    }

    public Movie find(long id) {

        Cursor cursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,
                MOVIE_COLS, WHERE_ID, new String[]{String.valueOf(id)}, null);

        if (cursor == null) {
            return null;
        }

        if (cursor.getCount() == 0){
            cursor.close();
            return  null;
        }

        cursor.moveToFirst();
        Movie movie = getMovieFromCursor(cursor);
        cursor.close();
        return movie;
    }

    public ArrayList<Movie> findAll() {

        ArrayList<Movie> list = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, MOVIE_COLS, null, null, null);

        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            list.add(getMovieFromCursor(cursor));
        }
        cursor.close();
        return list;
    }

    public long add(Movie movie) {
        Log.d(TAG, "add() called with: " + "movie = [" + movie + "]");
        return ContentUris.parseId(mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, getContentValuesFromMovie(movie)));
    }

    public void update(Movie movie) {
        mContext.getContentResolver()
                .update(MovieEntry.CONTENT_URI, getContentValuesFromMovie(movie), WHERE_ID,
                        new String[]{String.valueOf(movie.getId())});
    }

    public void remove(Movie movie) {
        remove(movie.getId());
    }

    public void remove(long id) {
        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, WHERE_ID,
                new String[]{String.valueOf(id)});
    }

    public static Movie getMovieFromCursor(Cursor openedCursor) {
        Movie movie = new Movie(
                openedCursor.getLong(0),
                openedCursor.getString(1),
                openedCursor.getFloat(2),
                openedCursor.getString(3),
                openedCursor.getString(4),
                openedCursor.getString(5),
                openedCursor.getInt(6) > 0
        );
        return movie;
    }

    public static ContentValues getContentValuesFromMovie(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry._ID, movie.getId());
        contentValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        contentValues.put(MovieEntry.COLUMN_COVER_PATH, movie.getCoverPath());
        contentValues.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieEntry.COLUMN_FROM_DB, movie.isFromDb() ? 1 :0);
        return contentValues;
    }
}
