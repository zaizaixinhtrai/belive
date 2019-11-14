package com.appster.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.appster.R;
import com.appster.customview.EditTextBackPress;
import com.appster.features.stream.viewer.MediaPlayerFragment;
import com.appster.features.user_profile.DialogUserProfileFragment;
import com.appster.models.NotificationPushModel;
import com.appster.models.event_bus_models.EventBusPushNotification;
import com.apster.common.Constants;
import com.apster.common.view.NotificationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.appster.features.stream.viewer.MediaPlayerFragment.PLAYER_TAG;


/**
 * @hide
 */
public class MediaPlayerActivity extends BaseActivity {
    private static final String TAG = "MediaPlayerActivity";
//    @Inject
//    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    MediaPlayerFragment mediaPlayerFragment;
    @Bind(R.id.notificationView)
    NotificationView notificationView;
    private String streamSlug;
    private String mediaUrl;
    private String userImage;
    boolean isRecorded;
    private boolean mIsRestoredToTop;

    //    BroadcastReceiver streamEndedReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(mediaPlayerFragment!=null) mediaPlayerFragment.onReceivedEndStreamNotification(intent.getStringExtra("slug"));
//        }
//    };
    public static Intent createIntent(Context context, String streamSlug, String argUrl, boolean isRecord, String userUrl) {
        Intent intent = new Intent(context, MediaPlayerActivity.class);
        if (argUrl != null) intent.putExtra(MediaPlayerFragment.ARG_URL, argUrl);
        if (streamSlug != null) intent.putExtra(MediaPlayerFragment.STREAM_SLUG, streamSlug);
        if (isRecord) intent.putExtra(MediaPlayerFragment.IS_RECORD, true);
        if (userUrl != null) intent.putExtra(MediaPlayerFragment.USER_URL, userUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setUseImmersiveMode(false);
//        setTheme(R.style.AppTheme_NoActionBar_Fullscreen_Xmas);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_playback);
        ButterKnife.bind(this);
//        AndroidInjection.inject(this);
        Timber.e("MediaPlayerActivity onCreate");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        streamSlug = getIntent().getStringExtra(MediaPlayerFragment.STREAM_SLUG);
        mediaUrl = getIntent().getStringExtra(MediaPlayerFragment.ARG_URL);
        userImage = getIntent().getStringExtra(MediaPlayerFragment.USER_URL);
        isRecorded = getIntent().getBooleanExtra(MediaPlayerFragment.IS_RECORD, false);

//        checkNotNull(mediaUrl, "MediaPlayerActivity started without a mediaUrl");
//        if(getSupportFragmentManager().findFragmentByTag(PLAYER_TAG)!=null){
//            Timber.e("Found instance of player");
//            final MediaPlayerFragment backgroundFragment = (MediaPlayerFragment) getSupportFragmentManager().findFragmentByTag(PLAYER_TAG);
//            backgroundFragment.stopCurrentPlayer();
//            backgroundFragment.onDestroy();
//        }else{
//            Timber.e("not found instance of player");
//        }

        if (savedInstanceState == null) {

            if (mediaPlayerFragment == null) {
                mediaPlayerFragment = MediaPlayerFragment.newInstance(mediaUrl, streamSlug, userImage, isRecorded);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mediaPlayerFragment, PLAYER_TAG)
                    .commit();
            //after transaction you must call the executePendingTransaction
            getSupportFragmentManager().executePendingTransactions();
        }

        EditTextBackPress.sethActivity(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mediaPlayerFragment != null)
            mediaPlayerFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mediaPlayerFragment != null)
            mediaPlayerFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.e("received new intent");
        if ((intent.getFlags() | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) > 0) {
            mIsRestoredToTop = true;
        }
        if (intent.getStringExtra(MediaPlayerFragment.STREAM_SLUG).equalsIgnoreCase(streamSlug)) {
            //do nothing if stream is same as current stream
            return;
        }
        streamSlug = intent.getStringExtra(MediaPlayerFragment.STREAM_SLUG);
        mediaUrl = intent.getStringExtra(MediaPlayerFragment.ARG_URL);
        userImage = intent.getStringExtra(MediaPlayerFragment.USER_URL);
        isRecorded = intent.getBooleanExtra(MediaPlayerFragment.IS_RECORD, false);
        final DialogUserProfileFragment profileFragment = (DialogUserProfileFragment) getSupportFragmentManager().findFragmentByTag("UserProfileView");
        if (profileFragment != null) {
            profileFragment.dismiss();
        }
        if (mediaPlayerFragment != null) {
            mediaPlayerFragment.hideAllPopup();
            mediaPlayerFragment.inflateEndStreamLayout("open other stream");
            mediaPlayerFragment = null;
        }

        mediaPlayerFragment = MediaPlayerFragment.newInstance(mediaUrl, streamSlug, userImage, isRecorded);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mediaPlayerFragment, PLAYER_TAG)
                .commit();
        //after transaction you must call the executePendingTransaction
        getSupportFragmentManager().executePendingTransactions();
    }

    public void endStreamClick(View v) {

    }

    @Override
    public void finish() {
        super.finish();
        if (android.os.Build.VERSION.SDK_INT >= 19 && !isTaskRoot() && mIsRestoredToTop) {
            // https://issuetracker.google.com/issues/36986021#c2
            // 4.4.2 platform issues for FLAG_ACTIVITY_REORDER_TO_FRONT,
            // reordered activity back press will go to home unexpectly, 
            // Workaround: move reordered activity current task to front when it's finished.
            ActivityManager tasksManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (tasksManager != null) {
                tasksManager.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_NO_USER_ACTION);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        LocalBroadcastManager.getInstance(this).registerReceiver(streamEndedReceiver,
//                new IntentFilter(streamSlug));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if (mediaPlayerFragment != null) {
            mediaPlayerFragment.onBackPress();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onClickViewDoNoThing(View view) {
        // TODO: 7/5/2016
        // this handle when click on back ground end stream
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        Timber.e("MediaPlayer Activity onDestroy");
//        if (streamEndedReceiver != null) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(streamEndedReceiver);
//        }
        EditTextBackPress.sethActivity(null);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) showFullScreen();

    }

    private void showFullScreen() {
        if (getWindow() != null && getWindow().getDecorView() != null)
            getWindow().getDecorView().setSystemUiVisibility(getFullScreenVisibilityFlags());
    }

    private int getFullScreenVisibilityFlags() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventBusPushNotification event) {
        if (event.getNotificationType() == Constants.NOTIFYCATION_TYPE_NEWPOST
                || event.getNotificationType() == Constants.NOTIFYCATION_TYPE_NEW_RECORD) {
            return;
        }
        if (isInFront) {
            handlePushNotificationData(event);
        }
    }

    public void handlePushNotificationData(EventBusPushNotification event) {
//        AppsterApplication.mAppPreferences.setNumberUnreadNotification(event.getUnreadNumber());
        if (event.getNotificationType() == Constants.NOTIFYCATION_TYPE_LIVESTREAM) {
            if (notificationView != null) {
                notificationView.updateDataNotification(event.getPushNotificationModel());
                notificationView.showAnimationThenAutoDismissFullscreen();
            }
        }
    }

    @OnClick(R.id.notificationView)
    void onNotificationViewClick() {
        NotificationPushModel notificationPushModel = notificationView.getPushModel();
        if (notificationPushModel != null
                && notificationPushModel.getNotificationType() == Constants.NOTIFYCATION_TYPE_LIVESTREAM && mediaPlayerFragment != null) {
            mediaPlayerFragment.closeStreamWhenNewStreamNotification();
            getSupportFragmentManager().beginTransaction().remove(mediaPlayerFragment).commit();
            mediaPlayerFragment = MediaPlayerFragment.newInstance(notificationPushModel.getPlayUrl(), notificationPushModel.getSlug(),
                    notificationPushModel.getUserImage(), false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mediaPlayerFragment, PLAYER_TAG)
                    .commit();
            //after transaction you must call the executePendingTransaction
            getSupportFragmentManager().executePendingTransactions();
        }
    }

//    @Override
//    public AndroidInjector<Fragment> supportFragmentInjector() {
//        return fragmentDispatchingAndroidInjector;
//    }
}
