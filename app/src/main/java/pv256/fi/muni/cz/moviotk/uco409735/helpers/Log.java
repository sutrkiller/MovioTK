package pv256.fi.muni.cz.moviotk.uco409735.helpers;


import pv256.fi.muni.cz.moviotk.uco409735.BuildConfig;

/**
 * Logging class that checks whether logging is enabled and calls same methods in android.utils.Log
 */

@SuppressWarnings("unused")
public class Log  {

    /**
     * Verbose log message.
     */
    public static int v(String tag, String message) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.v(tag, message);
        }
        return 0;
    }

    /**
     * Verbose log message with exception.
     */
    public static int v(String tag, String message, Throwable exception) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.v(tag, message, exception);
        }
        return 0;
    }

    /**
     * Debug log message.
     */
    public static int d(String tag, String message) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.d(tag, message);
        }
        return 0;
    }

    /**
     * Debug log message with exception.
     */
    public static int d(String tag, String message, Throwable exception) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.d(tag, message, exception);
        }
        return 0;
    }

    /**
     * Informational log message.
     */
    public static int i(String tag, String message) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.i(tag, message);
        }
        return 0;
    }

    /**
     * Informational log message with exception.
     */
    public static int i(String tag, String message, Throwable exception) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.i(tag, message, exception);
        }
        return 0;
    }

    /**
     * Warning log message.
     */
    public static int w(String tag, String message) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.w(tag, message);
        }
        return 0;
    }

    /**
     * Warning log message with exception.
     */
    public static int w(String tag, String message, Throwable exception) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.w(tag, message, exception);
        }
        return 0;
    }

    /**
     * Error log message.
     */
    public static int e(String tag, String message) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.e(tag, message);
        }
        return 0;
    }

    /**
     * Error log message with exception.
     */
    public static int e(String tag, String message, Throwable exception) {
        if (BuildConfig.LOGGING_ENABLED) {
            return android.util.Log.e(tag, message, exception);
        }
        return 0;
    }
}
