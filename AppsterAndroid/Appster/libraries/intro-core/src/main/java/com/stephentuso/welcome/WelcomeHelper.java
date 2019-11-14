package com.stephentuso.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by stephentuso on 11/15/15.
 * Class that handles showing a welcome screen
 */
public class WelcomeHelper {

    public static final int DEFAULT_WELCOME_SCREEN_REQUEST = 1;

    private static final String KEY_WELCOME_SCREEN_STARTED = "com.stephentuso.welcome.welcome_screen_started";

    private Activity mActivity;
    private Class<? extends WelcomeActivity> mActivityClass;
    private boolean welcomeScreenStarted = false;
    private Intent mNavigationIntent;

    /**
     * @param activity      An activity
     * @param activityClass Class of your welcome screen. An Activity that extends {@link WelcomeActivity WelcomeActivity}
     */
    public WelcomeHelper(Activity activity, Class<? extends WelcomeActivity> activityClass, Intent navigationIntent) {
        mActivity = activity;
        mActivityClass = activityClass;
        this.mNavigationIntent = navigationIntent;
    }

    private boolean getWelcomeScreenStarted(Bundle savedInstanceState) {
        if (!welcomeScreenStarted) {
            welcomeScreenStarted = savedInstanceState != null && savedInstanceState.getBoolean(KEY_WELCOME_SCREEN_STARTED, false);
        }
        return welcomeScreenStarted;
    }

    private boolean shouldShow(Bundle savedInstanceState) {
        return !getWelcomeScreenStarted(savedInstanceState) &&
                !WelcomeSharedPreferencesHelper.welcomeScreenCompleted(mActivity, WelcomeUtils.getKey(mActivityClass));
    }

    /**
     * Shows the welcome screen if it hasn't already been started or completed yet
     *
     * @param savedInstanceState Saved instance state Bundle
     * @return true if the welcome screen was shown, false if it wasn't
     */
    public boolean show(Bundle savedInstanceState) {
        return show(savedInstanceState, DEFAULT_WELCOME_SCREEN_REQUEST);
    }

    /**
     * Shows the welcome screen if it hasn't already been started or completed yet
     *
     * @param savedInstanceState Saved instance state Bundle
     * @param requestCode        The request code that will be returned with the result of the welcome screen
     *                           in your Activity's onActivityResult
     * @return true if the welcome screen was shown, false if it wasn't
     */
    public boolean show(Bundle savedInstanceState, int requestCode) {
        boolean shouldShow = shouldShow(savedInstanceState);
        if (shouldShow) {
            welcomeScreenStarted = true;
            startActivity(requestCode, false);
        }
        return shouldShow;
    }

    /**
     * Always shows the welcome screen
     */
    public void forceShow() {
        forceShow(DEFAULT_WELCOME_SCREEN_REQUEST, false);
    }

    public void forceShowAndCloseActivity() {
        forceShow(DEFAULT_WELCOME_SCREEN_REQUEST, true);
    }

    /**
     * Always show the welcome screen
     *
     * @param requestCode The request code that will be returned with the result of the welcome screen
     *                    in your Activity's onActivityResult
     */
    public void forceShow(int requestCode, boolean useFadeInAnimation) {
        startActivity(requestCode, useFadeInAnimation);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_WELCOME_SCREEN_STARTED, welcomeScreenStarted);
    }

    private void startActivity(int requestCode, boolean useFadeInAnimation) {
//        Intent intent = new Intent(mActivity, mActivityClass);
        if (mNavigationIntent == null) {
            mNavigationIntent = new Intent(mActivity, mActivityClass);
        }
        mActivity.startActivityForResult(mNavigationIntent, requestCode);
        if (useFadeInAnimation)
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private static class CompleteActivityRunnable implements Runnable {
        final WeakReference<Activity> mActivityWeakReference;

        CompleteActivityRunnable(Activity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            Activity activity = mActivityWeakReference.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }

}
