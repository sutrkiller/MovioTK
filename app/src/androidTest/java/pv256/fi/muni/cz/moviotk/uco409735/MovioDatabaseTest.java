package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovioContract;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

import static org.junit.Assert.assertTrue;

public class MovioDatabaseTest {
    private MovieManager mManager;
    private Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        mManager = new MovieManager(mContext);
    }

    @After
    public void tearDown() throws Exception {
        mContext.getContentResolver().delete(
                MovioContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
    }

    @Test
    public void testFindMovieById() {
        Movie expected = new Movie(1, "Rogue One", 5, "http://path", "http://backdropPath", "2016-12-10", "Overview 1", true);
        mManager.add(expected);

        Movie returned = mManager.find(1);
        assertTrue(expected.equals(returned));
    }

}
