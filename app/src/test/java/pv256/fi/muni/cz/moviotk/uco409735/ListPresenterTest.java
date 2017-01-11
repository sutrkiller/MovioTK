package pv256.fi.muni.cz.moviotk.uco409735;

/**
 * Testing MainFragment
 */

import android.content.Context;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pv256.fi.muni.cz.moviotk.uco409735.data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.moviesList.ListContract;
import pv256.fi.muni.cz.moviotk.uco409735.moviesList.ListPresenter;
import pv256.fi.muni.cz.moviotk.uco409735.service.MovieDownloadService;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MoviesStorage.class,MovieDownloadService.class})
public class ListPresenterTest {

    @Mock
    private ListContract.View listView;
    @Mock
    private MoviesStorage storage;
    @Mock
    private View view;
    @Mock
    private Context context;

    private ListPresenter listPresenter;
    private Map<String,ArrayList<Movie>> movies;

    @Before
    public void setUp() throws Exception {
        listPresenter = new ListPresenter(listView);

        movies = new HashMap<>();
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie(1, "Title", 1, "coverPath", "backdropPath", "2017-1-1", "Overview 1", true));
        movieList.add(new Movie(2, "Title 2", 2, "coverPath 2", "backdropPath 2", "2017-1-1", "Overview 2", true));
        movieList.add(new Movie(3, "Title 3", 3, "coverPath 3", "backdropPath 3", "2017-1-1", "Overview 3", true));
        movies.put("Category",movieList);

        PowerMockito.mockStatic(MoviesStorage.class);
    }

    @Test
    public void testLoadMovies_fromDb() throws Exception {
        when(MoviesStorage.getInstance()).thenReturn(storage);
        when(storage.getMovieMap()).thenReturn(movies);

        listPresenter.loadMovies(view,"",true);

        verify(storage).getMovieMap();
        verify(listView).fillRecyclerView(view,movies);
    }

    @Test
    public void testLoadMovies_fromNet_sameGenres() throws Exception {
        when(MoviesStorage.getInstance()).thenReturn(storage);

        when(storage.isMapEmpty()).thenReturn(false);
        when(storage.getSelectedGenres()).thenReturn("1,2");
        when(storage.getMovieMap()).thenReturn(movies);

        listPresenter.loadMovies(view,"1,2",false);

        verify(storage).getMovieMap();
        verify(listView).fillRecyclerView(view,movies);
    }


    @Test
    public void testLoadMovies_fromNet_diffGenres() throws Exception {
        PowerMockito.mockStatic(MovieDownloadService.class);
        PowerMockito.mockStatic(MoviesStorage.class);

        when(MoviesStorage.getInstance()).thenReturn(storage);
        when(storage.isMapEmpty()).thenReturn(false);
        when(storage.getSelectedGenres()).thenReturn("");
        when(view.getContext()).thenReturn(context);
        PowerMockito.doNothing().when(MovieDownloadService.class, "startDownload", Mockito.any(Context.class), Mockito.any(String.class));

        listPresenter.loadMovies(view,"1,2",false);

        verify(view).getContext();

        PowerMockito.verifyStatic();
        MovieDownloadService.startDownload(eq(context), eq("1,2"));
    }
}

