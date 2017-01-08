package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import pv256.fi.muni.cz.moviotk.uco409735.Db.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.Db.MovioContract;

import static org.junit.Assert.*;

public class MovioDatabaseTest extends AndroidTestCase {
    private MovieManager mManager;

    @Override
    protected void setUp() throws Exception {
        mManager = new MovieManager(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        mContext.getContentResolver().delete(
                MovioContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
    }

    public void testFindMovieById() {
        Movie expected = new Movie(1,"Rogue One",5,"http://path","http://backdropPath","2016-12-10",true);
        mManager.add(expected);

        Movie returned = mManager.find(1);
        assertEquals(expected, returned);
    }

    private void assertEquals(Movie expected, Movie actual) {
        if (expected != null && actual != null) {
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getReleaseDate(), actual.getReleaseDate());
            assertEquals(expected.getPopularity(), actual.getPopularity());
            assertEquals(expected.getCoverPath(), actual.getCoverPath());
            assertEquals(expected.getBackdropPath(), actual.getBackdropPath());
            assertEquals(expected.getTitle(), actual.getTitle());
        }
    }

}
