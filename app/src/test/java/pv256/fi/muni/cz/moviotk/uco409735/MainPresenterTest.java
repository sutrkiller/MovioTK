package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import pv256.fi.muni.cz.moviotk.uco409735.data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.main.MainContract;
import pv256.fi.muni.cz.moviotk.uco409735.main.MainPresenter;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.sync.UpdaterSyncAdapter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MoviesStorage.class, UpdaterSyncAdapter.class, MovieManager.class})
public class MainPresenterTest {

    @Mock
    private MainContract.View mainView;
    @Mock
    private MoviesStorage storage;
    @Mock
    private Context context;
    @Mock
    private Cursor cursor;

    private MainPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new MainPresenter(mainView);

        PowerMockito.mockStatic(MoviesStorage.class);
        PowerMockito.mockStatic(UpdaterSyncAdapter.class);
        PowerMockito.mockStatic(MovieManager.class);
    }

    @Test
    public void testOnDbSourceCheckedChange_true() throws Exception {
        when(MoviesStorage.getInstance()).thenReturn(storage);

        presenter.onDbSourceCheckedChange(true);

        verify(mainView).showMessage(Mockito.eq("Source changed to Database"));
        verify(mainView).restartLoader();
    }

    @Test
    public void testOnDbSourceCheckedChange_false() throws Exception {
        when(MoviesStorage.getInstance()).thenReturn(storage);

        presenter.onDbSourceCheckedChange(false);

        verify(mainView).showMessage(Mockito.eq("Source changed to Internet"));
        verify(mainView).reloadMovies(false);
    }

    @Test
    public void testUpdateDb_true() throws Exception {
        when(MoviesStorage.getInstance()).thenReturn(storage);
        PowerMockito.doNothing().when(UpdaterSyncAdapter.class,"syncImmediately", Mockito.any(Context.class));

        presenter.updateDb(context,true);

        verify(mainView).showMessage(Mockito.eq("Checking updates..."));
        PowerMockito.verifyStatic();
        UpdaterSyncAdapter.syncImmediately(eq(context));
        verify(mainView).restartLoader();
        verify(mainView).showMessage(Mockito.eq("Update finished"));
    }

    @Test
    public void testUpdateDb_false() throws Exception {
        when(MoviesStorage.getInstance()).thenReturn(storage);
        PowerMockito.doNothing().when(UpdaterSyncAdapter.class,"syncImmediately", Mockito.any(Context.class));

        presenter.updateDb(context,false);

        verify(mainView).showMessage(Mockito.eq("Checking updates..."));
        PowerMockito.verifyStatic();
        UpdaterSyncAdapter.syncImmediately(eq(context));
        verify(mainView).showMessage(Mockito.eq("Update finished"));
    }

    @Test
    public void testOnLoaderFinished_true() throws Exception {
        Movie movie = new Movie(1,"Title",1,"coverPath","backdropPath","2017-1-1",true);

        when(MoviesStorage.getInstance()).thenReturn(storage);
        doNothing().when(storage).setSelectedGenres(any(String.class));
        when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(MovieManager.getMovieFromCursor(eq(cursor))).thenReturn(movie);

        presenter.onLoaderFinished(cursor,true);

        PowerMockito.verifyStatic();
        MovieManager.getMovieFromCursor(eq(cursor));
        verify(storage).addMovieCategory(eq("Favorites"), Matchers.<ArrayList<Movie>>any());
        verify(mainView).reloadMovies(true);
    }

    @Test
    public void testOnLoaderFinished_false() throws Exception {
        presenter.onLoaderFinished(cursor,false);

        verifyZeroInteractions(storage, cursor,mainView);
    }

}
