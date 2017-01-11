package pv256.fi.muni.cz.moviotk.uco409735;

import android.test.AndroidTestCase;

import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovioContract;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

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
