package pv256.fi.muni.cz.moviotk.uco409735;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

/**
 * Launching class of application
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {
   private Activity mCurrent;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        if (BuildConfig.DEBUG) {
            initStrictMode();
        }
    }



    private void initStrictMode() {
        StrictMode.ThreadPolicy.Builder tpb = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tpb.penaltyFlashScreen();
        }
        StrictMode.setThreadPolicy(tpb.build());

        StrictMode.VmPolicy.Builder vmpb = new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            vmpb.detectLeakedClosableObjects();
        }
        StrictMode.setVmPolicy(vmpb.build());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        mCurrent = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mCurrent = activity;

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCurrent = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public Activity getCurrentActivity() {
        return mCurrent;
    }
}
