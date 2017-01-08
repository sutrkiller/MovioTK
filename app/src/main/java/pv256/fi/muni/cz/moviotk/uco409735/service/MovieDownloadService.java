package pv256.fi.muni.cz.moviotk.uco409735.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import pv256.fi.muni.cz.moviotk.uco409735.Data.MovieDO;
import pv256.fi.muni.cz.moviotk.uco409735.Data.MovieDbApi;
import pv256.fi.muni.cz.moviotk.uco409735.Data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.R;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDownloadService extends IntentService {

    public static final String BROADCAST_INTENT = "pv256.fi.muni.cz.moviotk.uco409735.Services.action.broadcast_intent";
    public static final String ACTION_DOWNLOAD = "pv256.fi.muni.cz.moviotk.uco409735.Services.action.download_movies";
    public static final String ACTION_SINGLE = "pv256.fi.muni.cz.moviotk.uco409735.Services.action.download_single";
    public static final String RESULT_KEY = "pv256.fi.muni.cz.moviotk.uco409735.Services.action.data_result";

    private static final String EXTRA_GENRES = "pv256.fi.muni.cz.moviotk.uco409735.Services.extra.GENRES";
    private static final String EXTRA_ID = "pv256.fi.muni.cz.moviotk.uco409735.Services.extra.ID";
    private static final String EXTRA_DOWNLOAD_KEY = "pv256.fi.muni.cz.moviotk.uco409735.Services.extra.DOWNLOAD";

    private Gson mGson;
    private MovieManager mManager;
    private Retrofit mRetrofit;
    private MovieDbApi movieDbApi;

    public MovieDownloadService() {
        this("MovieDownloadService");
    }

    public MovieDownloadService(String name) {
        super(name);
        mGson = new Gson();
        initRetrofit();
    }

    /**
     * Starts this service to perform action  with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startDownload(Context context, String download_key, String genres) {
        Intent intent = new Intent(context, MovieDownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_DOWNLOAD_KEY, download_key);
        intent.putExtra(EXTRA_GENRES, genres);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String download_key = intent.getStringExtra(EXTRA_DOWNLOAD_KEY);
                final String genres = intent.getStringExtra(EXTRA_GENRES);
                handleActionDownload(download_key, genres);
            }
        }
    }

    /**
     * Handle action DOWNLOAD in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownload(String download_key, String genres) {
        if (isNetworkConnected()) {
            notifyDownloading();

            MoviesStorage.getInstance().clearMap();
            MoviesStorage.getInstance().setSelectedGenres("-1");
            //initRetrofit();
            Calendar cal = Calendar.getInstance();
            String from = encodeDate(cal);
            cal.add(Calendar.DATE, 7);
            String to = encodeDate(cal);
            Call<Movie[]> moviesCallUpcoming = movieDbApi.loadMovies(MovieDbApi.API_KEY, MovieDbApi.QUERY_PARAM_LANGUAGE, MovieDbApi.QUERY_PARAM_SORT_BY, from, to, genres.equals("") ? null : genres);
            to = from;
            cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            from = encodeDate(cal);
            Call<Movie[]> moviesCallPlaying = movieDbApi.loadMovies(MovieDbApi.API_KEY, MovieDbApi.QUERY_PARAM_LANGUAGE, MovieDbApi.QUERY_PARAM_SORT_BY, from, to, genres.equals("") ? null : genres);

            boolean success = doCall(moviesCallPlaying, "Now Playing");
            success &= doCall(moviesCallUpcoming, "Upcoming");
            if (success) {
                MoviesStorage.getInstance().setSelectedGenres(genres);
                notifyDownloadFinished();
            }
        }
        Intent broadcastIntent = new Intent(BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private String encodeDate(Calendar cal) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(cal.getTime());
    }

    private boolean doCall(Call<Movie[]> moviesCall, String category) {
        try {
            Response response = moviesCall.execute();

            if (response != null) {
                if (response.isSuccessful()) {
                    Movie[] movies = (Movie[]) response.body();

                    mManager = new MovieManager(this);
                    for (Movie mov: movies) {
                        if (mManager.contains(mov.getId())) {
                            mov.setFromDb(true);
                        }
                    }

                    MoviesStorage.getInstance().addMovieCategory(category, new ArrayList<>(Arrays.asList(movies)));
                    return true;
                } else {
                    notifyError();
                }
            } else {
                notifyError();
            }
        } catch (IOException ex) {
            notifyError();
        }
        return false;
    }

    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void notifyError() {
        MoviesStorage.getInstance().clearMap();
        MoviesStorage.getInstance().setSelectedGenres("-1");
        Notification notify = getNotificationBuilderWithIcon()
                .setContentTitle(getString(R.string.notification_error_title))
                .setContentText(getString(R.string.notification_error_text))
                .build();
        showNotification(notify);
    }

    private void notifyDownloading() {
        Notification notification = getNotificationBuilderWithIcon()
                .setContentTitle(getString(R.string.notification_downloading_title))
                .setOngoing(true)
                .setProgress(100, 0, true)
                .build();
        showNotification(notification);
    }

    private void notifyDownloadFinished() {
        Notification notification = getNotificationBuilderWithIcon()
                .setContentTitle(getString(R.string.notification_downloaded_title))
                .setContentText(getString(R.string.notification_downloaded_text))
                .build();
        showNotification(notification);
    }

    private void showNotification(Notification n) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, n);
    }

    private NotificationCompat.Builder getNotificationBuilderWithIcon() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification_icon);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(Color.argb(255, 148, 24, 224));
        }
        return builder;
    }

    private MovieDbApi initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(MovieDbApi.URL)
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, java.lang.annotation.Annotation[] annotations, Retrofit retrofit) {

                            return new Converter<ResponseBody, Movie[]>() {
                                @Override
                                public Movie[] convert(ResponseBody value) throws IOException {
                                    MovieDO resultWrapper = mGson.fromJson(value.charStream(), MovieDO.class);
                                    value.close();
                                    return resultWrapper.getResults();
                                }
                            };
                    }
                })
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieDbApi = mRetrofit.create(MovieDbApi.class);
        return movieDbApi;
    }


}
