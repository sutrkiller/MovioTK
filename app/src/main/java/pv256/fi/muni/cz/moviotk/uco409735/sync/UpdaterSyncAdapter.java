package pv256.fi.muni.cz.moviotk.uco409735.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import pv256.fi.muni.cz.moviotk.uco409735.data.MovieDbApi;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tobias on 1/4/2017.
 */

public class UpdaterSyncAdapter extends AbstractThreadedSyncAdapter {
    // Interval at which to sync with the server, in seconds.
    public static final int SYNC_INTERVAL =  60 * 60 * 24; //day
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public UpdaterSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(Bundle.EMPTY) //enter non null Bundle, otherwise on some phones it crashes sync
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one if the
     * fake account doesn't exist yet.  If we make a new account, we call the onAccountCreated
     * method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        UpdaterSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);



        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(UpdaterSyncAdapter.class.getName(), "onPerformSync()");

        int updated = 0;
        int count = 0;
        try {
            MovieManager mm = new MovieManager(getContext());
            ArrayList<Movie> movies = mm.findAll();
            count = movies.size();

            for (Movie movie : movies) {
                MovieDbApi api = initRetrofit();
                Movie newMovie = api.getMovie(movie.getId(), MovieDbApi.API_KEY, MovieDbApi.QUERY_PARAM_LANGUAGE).execute().body();
                if (newMovie != null && newMovie.getId() == movie.getId()) {
                    if (!movie.equals(newMovie)) {
                        mm.update(newMovie);
                        updated++;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(UpdaterSyncAdapter.class.getName(), e.getMessage());
        } finally {
            if (updated > 0) {
                notifyUpdated(updated, count);
            }
        }
    }

    private void notifyUpdated(int updated, int count) {
        Notification notification = getNotificationBuilderWithIcon()
                .setContentTitle("Favorite movies updated")
                .setContentText(updated + " from " + count + " were updated.")
                .build();
        showNotification(notification);
    }

    private void showNotification(Notification n) {
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, n);
    }

    private NotificationCompat.Builder getNotificationBuilderWithIcon() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.drawable.ic_stat_notification_icon);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(Color.argb(255, 148, 24, 224));
        }
        return builder;
    }

    private MovieDbApi initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MovieDbApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MovieDbApi.class);

    }
}
