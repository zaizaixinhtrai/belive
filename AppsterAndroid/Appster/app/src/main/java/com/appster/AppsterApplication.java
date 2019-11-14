package com.appster;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.appster.data.AppPreferences;
import com.appster.features.jobs.DailyBonusJobCreator;
import com.appster.features.login.LoginActivity;
import com.appster.features.maintenance.CheckMaintenanceJobCreator;
import com.appster.features.maintenance.CheckMaintenanceSyncJob;
import com.appster.manager.AppsterChatManger;
import com.appster.manager.WallFeedManager;
import com.appster.tracking.EventTracker;
import com.appster.utility.CrashlyticsUtil;
import com.appster.utility.OneSignalUtil;
import com.appster.utility.SocialManager;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.bumptech.glide.request.target.ViewTarget;
import com.contacts.Contacts;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;
import com.onesignal.OneSignal;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.agora.AgoraAPIOnlySignal;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by sonnguyen on 9/22/15.
 */
public class AppsterApplication extends androidx.multidex.MultiDexApplication
        implements HasActivityInjector {

    private static final String TAG = AppsterApplication.class.getSimpleName();

    private static AppsterApplication mInstance;
    private AgoraAPIOnlySignal m_agoraAPI;
    public static AppPreferences mAppPreferences;
    public Context mContext;
    private Scheduler subscribeScheduler;
    //    private static String versionTag = "";
    private static String S3Link;
    private AppComponent mComponent;

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        // OneSignal - notification
        OneSignalUtil.init(this);
        OneSignal.setLocationShared(false);
        Contacts.initialize(this);
        ViewTarget.setTagId(R.id.glide_tag);

//        Fabric.with(this, new Crashlytics());
        // Twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);

        // Fabric
        Fabric.Builder builder = new Fabric.Builder(this).kits(new Crashlytics(), new Twitter(authConfig), new Answers());
        Fabric.with(builder.build());

//        EventTracker.initialize(getApplicationContext(),this);


        registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());

        // GoPlaySDK
//        Playground.initialize(this, BuildConfig.PLAYTOKEN_APP_GUID, !BuildConfig.PLAYTOKEN_APP_DEBUG)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        response -> {
//                            // success
//                            Log.d(TAG, "Success to initialize GOPlay Playground SDK");
//                        }, error -> {
//                            // failed
//                            Log.d(TAG, "Failed to initialize GOPlay Playground SDK");
//                        }
//                );

        mContext = getApplicationContext();
        mAppPreferences = AppPreferences.getInstance(this);
//        AppsterApplication.mAppPreferences.setCachedTime(System.currentTimeMillis());
        initializeDependencies();
        setupAgoraEngine();
        // update crashlytics user data
        CrashlyticsUtil.setUser(mAppPreferences.getUserModel());
        // update amplitude user data
        EventTracker.initialize(this, this, mAppPreferences.getUserModel());
        EventTracker.setUser(mAppPreferences.getUserModel());
        // OneSignal
        OneSignalUtil.setUser(mAppPreferences.getUserModel());

        if (BuildConfig.DEBUG) {
            initStetho();
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        }
        Branch.getAutoInstance(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        refWatcher = LeakCanary.install(this);
        JobManager manager = JobManager.create(this);
        manager.addJobCreator(new DailyBonusJobCreator());
        manager.addJobCreator(new CheckMaintenanceJobCreator());
        CheckMaintenanceSyncJob.scheduleJob();
//        createJobId();
    }

    private void initializeDependencies() {
        mComponent = DaggerAppComponent.builder()
                .application(this)
                .serviceUrl(BuildConfig.API_ENDPOINT)
                .build();
        mComponent.inject(this);
    }

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        AppsterApplication application = (AppsterApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private void initStetho() {
// Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);

// Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        ).enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                //.enableWebKitInspector(RealmInspectorModulesProvider.builder(this)
                        .build();

// Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

// Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }


    public static String getCurrentVersionName(Context context) throws PackageManager.NameNotFoundException {
        String pkg = get(context).getPackageName();
        return get(context).getPackageManager().getPackageInfo(pkg, 0).versionName;
    }

    public static void logout(Context context) {
        mAppPreferences.clearAllParamLogin();
        SocialManager.getInstance().logOut();
        AppsterChatManger.getInstance(context).destroy();
        AppsterChatManger.getInstance(context).disConnectXMPP();
        Intent intent = new Intent(context.getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        context.getApplicationContext().startActivity(mainIntent);
        clearAllNotification(context);
        AppsterWebServices.resetAppsterWebserviceAPI();
        WallFeedManager.getInstance().clear();
        saveCurrentVersionCode();
    }

    private static void saveCurrentVersionCode() {
        AppsterApplication.mAppPreferences.saveCurrentVersionCode(BuildConfig.VERSION_CODE);
    }

    private static void clearAllNotification(Context context) {
        // Clear all notification
        NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    public static AppsterApplication get(Context context) {
        return (AppsterApplication) context.getApplicationContext();
    }

    final Observable.Transformer schedulersTransformer = new Observable.Transformer() {
        @Override
        public Observable call(Object o) {
            return ((Observable) o).subscribeOn(defaultSubscribeScheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(defaultSubscribeScheduler());
        }
    };

    //Reusing Transformers - Singleton
    @SuppressWarnings("unchecked")
    public <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (subscribeScheduler == null) {
            subscribeScheduler = Schedulers.io();
        }
        return subscribeScheduler;
    }

    public static String getCurrentActivityRunning(Context context) {
        String activityName = "";
        ActivityManager am = (ActivityManager) get(context).getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        ComponentName componentInfo = taskInfo.get(0).topActivity;
//        Log.d("NCS", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName() + "   Package Name :  " + componentInfo.getPackageName());
        if (!taskInfo.isEmpty()) activityName = taskInfo.get(0).topActivity.getClassName();
        return activityName;
    }

    /**
     * Checks if the application is being sent in the background (i.e behind
     * another application's Activity).
     *
     * @return <code>true</code> if another application will be above this one.
     */
    public static boolean isApplicationSentToBackground(Context context) {

        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    /**
     * API level 14 and higher
     * https://github.com/adjust/android_sdk#sdk-add
     */
    private static final class ActivityLifecycleHandler implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
    public AgoraAPIOnlySignal getmAgoraAPI() {
        return m_agoraAPI;
    }
    private void setupAgoraEngine() {
        String appID = getString(R.string.agora_app_id);

        try {
            m_agoraAPI = AgoraAPIOnlySignal.getInstance(this, appID);


        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }
    public static AppsterApplication getApplication() {
        return mInstance;
    }

    public AppComponent daggerAppComponent() {
        return mComponent;
    }
}
