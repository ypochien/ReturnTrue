package com.returntrue.util;

import android.util.Log;


/**
 * 設定Log紀錄等級 當logLevel設定為VERBOSE，(VERBOSE,DEBUG,INFO,WARN,ERROR)層級的log會被記錄
 * 當logLevel設定為DEBUG，(DEBUG,INFO,WARN,ERROR)層級的log會被記錄
 * 當logLevel設定為INFO，(INFO,WARN,ERROR)層級的log會被記錄
 * 當logLevel設定為WARN，(WARN,ERROR)層級的log會被記錄 當logLevel設定為ERROR，(ERROR)層級的log會被記錄
 * 當logLevel設定為ASSERT，所有層級的log皆不記錄
 *
 * @author JosephWang
 */
public class JLog {
    public static boolean showLog = Const.SHOW_LOGCAT;
    public static final String TAG = "hyxen";
    public static final String JosephWang = "JosephWang";

    /**
     * set level of log
     */
    private static int logLevel = 2;           // /主要設定範圍

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Set up for ignoring all Log. Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    private JLog() {
    }

    static {
        if (Const.IS_DEBUG_MODE) {
            showLog = true;
        } else {
            showLog = false;
        }
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (logLevel <= VERBOSE && showLog) {
            Log.v(tag, msg);
        }
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (logLevel <= VERBOSE && showLog) {
            Log.v(tag, msg, tr);
        }
    }

    public static void v(String msg, Throwable tr) {
        v(TAG, msg, tr);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (logLevel <= DEBUG && showLog) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (logLevel <= DEBUG && showLog) {
            Log.d(tag, msg, tr);
        }
    }

    public static void d(String msg, Throwable tr) {
        d(TAG, msg, tr);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (logLevel <= INFO && showLog) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (logLevel <= INFO && showLog) {
            Log.i(tag, msg, tr);
        }
    }

    public static void i(String msg, Throwable tr) {
        i(TAG, msg, tr);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (logLevel <= WARN && showLog) {
            Log.w(tag, msg);
        }
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (logLevel <= WARN && showLog) {
            Log.w(tag, msg, tr);
        }
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param tr  An exception to log
     */
    public static void w(String tag, Throwable tr) {
        if (logLevel <= WARN && showLog) {
            Log.w(tag, tr);
        }
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (logLevel <= ERROR && showLog) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (logLevel <= ERROR && showLog) {
            Log.e(tag, msg, tr);
        }
    }

    public static void e(String msg, Throwable tr) {
        e(TAG, msg, tr);
    }
}