package com.appster.features.maintenance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.app.ActivityOptionsCompat;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.R;
import com.appster.activity.BaseActivity;
import com.appster.customview.CustomFontTextView;
import com.appster.customview.ScalableVideo.ScalableType;
import com.appster.customview.VideoViewMoreScreenLogin;
import com.appster.features.login.LoginActivity;
import com.appster.main.MainActivity;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.response.MaintenanceModel;
import com.apster.common.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by Ngoc on 3/1/2017.
 */

public class MaintenanceActivity extends BaseActivity {

    @Bind(R.id.video_view)
    VideoViewMoreScreenLogin videoView;
    @Bind(R.id.tvMaintenanceMessage)
    CustomFontTextView tvMaintenanceMessage;

    private MaintenanceModel mMaintenanceModle;

    public static void startMaintenanceActivity(Context context, MaintenanceModel model) {
        Intent intent = new Intent(context, MaintenanceActivity.class);
        if (model != null) {
            intent.putExtra(ConstantBundleKey.BUNDLE_MAINTENANCE_MODLE, model);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.push_in_to_right, R.anim.push_in_to_left);

        context.getApplicationContext().startActivity(intent, options.toBundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            mMaintenanceModle = getIntent().getExtras().getParcelable(ConstantBundleKey.BUNDLE_MAINTENANCE_MODLE);
        }

        calcKeyboardHeight();
        playVideo();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mIsPausing.set(false);
        pingMaintenance();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mIsPausing.set(true);
    }

    private Subscription subscription;
//    private AtomicBoolean mIsPausing = new AtomicBoolean(false);

    private void pingMaintenance() {
        RxUtils.unsubscribeIfNotNull(subscription);
//        subscription = Observable.interval(60, TimeUnit.SECONDS)
//                .flatMap(n -> AppsterWebServices.get().checkMaintenance(BuildConfig.AWS_S3_SERVER_LINK + "maintenance/maintenance.json"))
//                .filter(aLong -> !mIsPausing.get())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(maintenance -> {
//                    if (maintenance == null) {
//                        return;
//                    }
//                    Timber.e("pingMaintenance");
//                    if (maintenance.maintenanceMode == Constants.MAINTENANCE_MODE_STOP) {
//                        if (isInFront) {
//                            startMain();
//                        }
//                    }
//                }, throwable -> {
//                    Timber.e(throwable.getMessage());
//                });

        long time = SystemClock.currentThreadTimeMillis();
        subscription = AppsterWebServices.get().checkMaintenance(BuildConfig.AWS_S3_SERVER_LINK + "maintenance/maintenance.json?t=" + time)
                .subscribe(maintenance -> {
                    if (maintenance == null) {
                        return;
                    }
                    Timber.e("pingMaintenance");
                    if (maintenance.maintenanceMode == Constants.MAINTENANCE_MODE_STOP) {
                        startMain();
                    }
                }, error -> {
                    Timber.e(error);
                });
    }

    void startMain() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(MaintenanceActivity.this,
                R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent;
        intent = new Intent(MaintenanceActivity.this, AppsterApplication.mAppPreferences.isUserLogin() ? MainActivity.class : LoginActivity.class);
        MaintenanceActivity.this.startActivity(intent, options.toBundle());
        finish();
    }


    void init() {
        if (mMaintenanceModle != null) {
            tvMaintenanceMessage.setText(mMaintenanceModle.message);
        }
    }

    private void calcKeyboardHeight() {
        final View root = findViewById(R.id.root_view);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                root.getWindowVisibleDisplayFrame(r);
                int screenHeight = root.getRootView().getHeight();
                int keyboardHeight = screenHeight - (r.bottom);

                // IF height diff is more then 150, consider keyboard as visible.
                if (keyboardHeight > 150) {
                    AppsterApplication.mAppPreferences.setIntPreferenceData(Constants.KEYBOARD_HEIGHT, keyboardHeight);
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                videoView.setScalableType(ScalableType.CENTER_CROP);
                videoView.invalidate();
            }
        });
    }

    private void playVideo() {

        releaseMedia();

        try {
            videoView.setRawData(R.raw.video_login);
            videoView.setVolume(0, 0);
            videoView.setLooping(true);
            videoView.prepare(mp -> videoView.start());
        } catch (Exception ioe) {
            Timber.e(ioe);
        }

    }

    @Override
    protected void onDestroy() {
        releaseMedia();
        videoView = null;
        RxUtils.unsubscribeIfNotNull(subscription);
        subscription = null;

        super.onDestroy();
    }

    private void releaseMedia() {

        if (videoView != null) {
            try {
                videoView.stop();
                videoView.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
