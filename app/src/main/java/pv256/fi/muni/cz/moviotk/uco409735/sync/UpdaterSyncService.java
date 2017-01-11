package pv256.fi.muni.cz.moviotk.uco409735.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service to create updater.
 */

public class UpdaterSyncService extends Service {
    private static final Object LOCK = new Object();
    private static UpdaterSyncAdapter sUpdaterSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (LOCK) {
            if (sUpdaterSyncAdapter == null) {
                sUpdaterSyncAdapter = new UpdaterSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sUpdaterSyncAdapter.getSyncAdapterBinder();
    }
}
