package com.apster.common;

import android.util.Log;

import com.appster.BuildConfig;

public class LogUtils {
	private final static boolean locker = false;

	public static void logV(String TAG, String Message) {
		if (!locker && BuildConfig.DEBUG && Message!=null) {
			Log.v(TAG, Message);

		}

	}

	public static void logD(String TAG, String Message) {
		if (!locker && BuildConfig.DEBUG && Message!=null) {
			Log.d(TAG, Message);

		}

	}

	public static void logD(String TAG,  String format, Object... args) {
		if (!locker && BuildConfig.DEBUG) {
			Log.d(TAG, String.format(format, args));

		}

	}

	public static void logE(String TAG, String Message) {
		logE(TAG, Message, null);
	}

	public static void logE(String TAG, String Message, Throwable throwable) {
		if (!locker && BuildConfig.DEBUG && Message!=null) {
			if (throwable == null) {
				Log.e(TAG, Message);
			} else {
				Log.e(TAG, Message, throwable);
			}
		}

	}

	public static void logI(String TAG, String Message) {
		if (!locker && BuildConfig.DEBUG && Message!=null) {
			Log.i(TAG, Message);

		}

	}

	public static void logW(String TAG, String Message) {
		if (!locker && BuildConfig.DEBUG && Message!=null) {
			Log.w(TAG, Message);

		}

	}

	public static void logW(String TAG, String Message, Exception e) {
		if (!locker && BuildConfig.DEBUG && Message!=null) {
			Log.i(TAG, Message, e);
		}
	}

}
