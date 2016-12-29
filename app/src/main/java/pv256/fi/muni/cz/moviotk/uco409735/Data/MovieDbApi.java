package pv256.fi.muni.cz.moviotk.uco409735.Data;

import pv256.fi.muni.cz.moviotk.uco409735.Movie;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 11/8/2016.
 */

public interface MovieDbApi {
    String IMAGES_URL = "https://image.tmdb.org/t/p/original";
    String URL = "https://api.themoviedb.org/";
    String API_KEY = "58c85d23ae0adc29b9921f8a08b1c335";
    String QUERY_PARAM_API_KEY = "api_key";
    String QUERY_PARAM_SORT_BY = "popularity.desc";
    String QUERY_PARAM_LANGUAGE = "en-US";
    String QUERY_PARAM_WITH_GENRES = "with_genres";


    @GET("3/discover/movie")
    Call<Movie[]> loadMovies(
        @Query(QUERY_PARAM_API_KEY) String apiKey,
        @Query(QUERY_PARAM_LANGUAGE) String language,
        @Query(QUERY_PARAM_SORT_BY) String sortBy,
        @Query(value = "primary_release_date.gte") String dateFrom,
        @Query(value = "primary_release_date.lte") String dateTo,
        @Query(QUERY_PARAM_WITH_GENRES) String withGenres
    );
}
