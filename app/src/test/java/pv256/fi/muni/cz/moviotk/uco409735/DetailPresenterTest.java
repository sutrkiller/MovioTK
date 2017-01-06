package pv256.fi.muni.cz.moviotk.uco409735;

import android.os.Bundle;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.detail.DetailContract;
import pv256.fi.muni.cz.moviotk.uco409735.detail.DetailPresenter;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(android.util.Log.class)
public class DetailPresenterTest {

    @Mock
    private DetailContract.View detailView;
    @Mock
    private MovieManager movieManager;
    private DetailPresenter detailPresenterNotInDb;
    private DetailPresenter detailPresenterInDb;
    private DetailPresenter detailPresenterInvalid;

    private Movie movieNotInDb = new Movie(1,"Title",1,"coverPath","backdropPath","2017-1-1",false);
    private Movie movieInDb = new Movie(2,"Title",1,"coverPath","backdropPath","2017-1-1",true);
    private Movie movieInvalid = null;

    @Before
    public void setUp() throws Exception {
        detailPresenterNotInDb = new DetailPresenter(detailView,movieNotInDb,movieManager);
        detailPresenterInDb = new DetailPresenter(detailView,movieInDb,movieManager);
        detailPresenterInvalid = new DetailPresenter(detailView,movieInvalid,movieManager);
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void testValid_correct() throws Exception {
        boolean result = detailPresenterInDb.valid();
        boolean result2 = detailPresenterNotInDb.valid();
        assertTrue(result);
        assertTrue(result2);
    }

    @Test
    public void testValid_incorrect() throws Exception {
        boolean result = detailPresenterInvalid.valid();
        assertFalse(result);
    }

    @Test
    public void onSaveToFavoritesClicked_notInDb() throws Exception {
        detailPresenterNotInDb.onSaveToFavoritesClicked();

        verify(movieManager).add(movieNotInDb);
        verify(detailView).setFavoritesButtonDrawable(R.drawable.ic_grade_black_24dp);
    }

    @Test
    public void onSaveToFavoritesClicked_inDb() throws Exception {
        detailPresenterInDb.onSaveToFavoritesClicked();

        verify(movieManager).remove(2);
        verify(detailView).setFavoritesButtonDrawable(R.drawable.ic_add_black_24dp);
    }

    @Test
    public void testGetMovie() {
        Movie movie = detailPresenterInDb.getMovie();
        Movie movie1 = detailPresenterNotInDb.getMovie();
        Movie movie2 = detailPresenterInvalid.getMovie();

        assertEquals(movieInDb,movie);
        assertEquals(movieNotInDb,movie1);
        assertNull(movie2);
    }
}