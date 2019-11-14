package com.appster.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.bundle.BaseBundle;
import com.appster.comments.CommentActivity;
import com.appster.core.BeLiveDefaultTheme;
import com.appster.core.BeLiveThemeHelper;
import com.appster.features.edit_video.EditActivity;
import com.appster.features.edit_video.RecordActivity;
import com.appster.features.edit_video.ShortVideoConfig;
import com.appster.features.messages.chat.ChatActivity;
import com.appster.manager.AppsterChatManger;
import com.appster.manager.ShowErrorManager;
import com.appster.models.ListenerEventModel;
import com.appster.models.NotificationModel;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.RxUtils;
import com.appster.webservice.response.MaintenanceModel;
import com.apster.common.Constants;
import com.apster.common.CustomDialogUtils;
import com.apster.common.FileUtility;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pack.utility.DialogInfoUtility;
import com.pack.utility.StringUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.apster.common.FileUtility.getOutputMediaFile;

/**
 * Created by User on 9/12/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {


    private static final int ACTION_REQUEST_GALLERY = 3;


    public Uri fileUri;
    private Locale locale = null;

    Intent intent;
    // create static Dialog cause of crashing when transition between activity, activity should have  dialog
    public boolean isShowing = false;
    public com.pack.progresshud.ProgressHUD dialog;

    BaseBundle baseBundle;

    private long mLastClickTime;

    public DialogInfoUtility utility;
    protected boolean isInFront;

    protected CompositeSubscription mCompositeSubscription;
    protected RxPermissions mRxPermissions;
    protected BeLiveThemeHelper mBeLiveThemeHelper;

    public BaseBundle getBaseBundle(String key) {
        baseBundle = getIntent().getExtras().getParcelable(key);
        return baseBundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new DialogInfoUtility();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mRxPermissions = new RxPermissions(this);
        mBeLiveThemeHelper = new BeLiveDefaultTheme();
        if (mBeLiveThemeHelper.isTransparentStatusBarRequired()) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (fileUri != null && outState != null) {
            outState.putString("uriMedia", fileUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        final String keyUri = savedInstanceState.getString("uriMedia", "");
        if (!keyUri.isEmpty()) fileUri = Uri.parse(keyUri);


    }

    @Override
    protected void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInFront = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    public void startActivityProfile(String userID, String userName, String displayName) {
        if (preventMultiClicks()) {
            return;
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(Constants.USER_PROFILE_ID, userID);
        intent.putExtra(Constants.ARG_USER_NAME, userName);
        intent.putExtra(Constants.USER_PROFILE_DISPLAYNAME, displayName);
        startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_USER_PROFILE, options.toBundle());
    }

    public void startActivityProfile(String userID, String displayName) {
        startActivityProfile(userID, "", displayName);
    }

    public void handleError(String errorMessage, int errorCode) {
        if (this.isFinishing()) {
            return;
        }
        if (errorCode == Constants.RETROFIT_ERROR) {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                utility.showMessage(getString(R.string.app_name), getString(R.string.check_your_connection), BaseActivity.this);
            } else {
                utility.showMessage(getString(R.string.app_name), getString(R.string.activity_sign_unknown_error), BaseActivity.this);
            }
        } else if (errorCode == ShowErrorManager.account_deactivate_or_invalid_email ||
                errorCode == ShowErrorManager.account_deactivated_or_suspended ||
                errorCode == ShowErrorManager.authentication_error ||
                errorCode == Constants.RESPONSE_DUPLICATE_LOGIN) {
            View.OnClickListener mclick = v -> AppsterApplication.logout(v.getContext());

            utility.showMessage(getString(R.string.app_name), errorMessage, BaseActivity.this, mclick);

        } else {
//            ShowErrorManager.show(errorCode, BaseActivity.this);
            utility.showMessage(getString(R.string.app_name), errorMessage, BaseActivity.this);
        }

    }

    public Uri getOutputMediaFileUri(int type) throws NullPointerException {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public void loadVideoAfterPickFromGallery(Uri uri) {
        if (uri != null) {
            final Uri content = uri;
            String videoFilePath;
            if (FileUtility.isGoogleVideosUri(content)) {
//                    Toast.makeText(getApplicationContext(), getString(R.string.fail_to_load_video), Toast.LENGTH_SHORT).show();
                videoFilePath = FileUtility.getGoogleFilePath(this, content);
            } else {
                videoFilePath = FileUtility.getPath(this, content);
            }

            if (videoFilePath != null) {
                String fileExtention = videoFilePath.substring(videoFilePath.lastIndexOf("."));
                if (fileExtention.equalsIgnoreCase(getString(R.string.mp4_extention))) {
                    openTrimVideoScreen(BaseActivity.this, content);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.support_mp4), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.fail_to_load_video), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.fail_to_load_video), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadVideoAfterRecorded(Uri uri) {
        openTrimVideoScreen(BaseActivity.this, uri);

    }

    public void openTrimVideoScreen(Activity activity, Uri uri) {
        Intent i = new Intent(activity, EditActivity.class);
        i.putExtra(Constants.VIDEO_PATH, uri.toString());
        startActivityForResult(i, Constants.VIDEO_TRIMMED_REQUEST);
    }

    public void showVideosPopUp() {
        mCompositeSubscription.add(mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        CustomDialogUtils.openRecordVideoDialog(this, getString(R.string.select_a_videos_from), v -> startCameraVideosActivity(), v -> startPickVideos());
                    }
                }));
    }


    public void showPicPopUp() {
        mCompositeSubscription.add(mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        CustomDialogUtils.openRecordVideoDialog(this, getString(R.string.select_a_picture_from), v -> takePictureFromCamera(), v -> takePictureFromGallery());
                    }
                }));
    }

    public void startPickVideos() {
        try {
            final Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(intent, Constants.PICK_VIDEO_REQUEST);
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }

    }

    public void startCameraVideosActivity() {
//        Intent intent = new Intent(this, RecordActivity.class);
//        startActivityForResult(intent, Constants.RECORD_VIDEO_REQUEST);

        ShortVideoConfig config = RecordActivity.createConfig();
        RecordActivity.startActivity(this, Constants.RECORD_VIDEO_REQUEST,
                config);


    }

    public void takePictureFromCamera() {
//        Intent intent = new Intent(this, CameraActivity.class);
//        startActivityForResult(intent, Constants.REQUEST_PIC_FROM_CAMERA);

        ShortVideoConfig config = RecordActivity.createConfig();
        RecordActivity.startActivity(this, Constants.REQUEST_PIC_FROM_CAMERA,
                config);

    }

    public void takePictureFromGallery() {//com.google.android.apps.photos
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        photoPickerIntent.setType("image/*");
        if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, Constants.REQUEST_PIC_FROM_LIBRARY);
        }
    }

    private void takePictureFromGalleryWithoutGooglePhotos() {
        List<Intent> intentShareList = new ArrayList<>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_PICK);
        shareIntent.setType("image/*");
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(shareIntent, 0);

        for (ResolveInfo resInfo : resolveInfoList) {
            String packageName = resInfo.activityInfo.packageName;
            String name = resInfo.activityInfo.name;

            if (!packageName.contains("com.google.android.apps.photos")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, name));
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intentShareList.add(intent);
            }
        }

        if (intentShareList.isEmpty()) {
            Toast.makeText(this, "No apps available!", Toast.LENGTH_SHORT).show();
        } else {
            Intent chooserIntent = Intent.createChooser(intentShareList.remove(0), "");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentShareList.toArray(new Parcelable[]{}));
            startActivityForResult(chooserIntent, Constants.REQUEST_PIC_FROM_LIBRARY);
        }
    }

    /**
     * this function does the crop operation.
     */

    public void performCrop() {

    }

    public void performCrop(Uri inPut, Uri outPut) {
        // take care of exceptions
        try {
            int maxWidth = 800;// width and height;
            int maxHeight = 800;// width and height;
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);
            options.setHideBottomControls(true);
            options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.color_header_title));

            UCrop uCrop = UCrop.of(inPut, outPut);
            uCrop = uCrop.withAspectRatio(1, 1);//set squared output
            uCrop = uCrop.withMaxResultSize(maxWidth, maxHeight);
            uCrop.withOptions(options);
            uCrop.start(this, Constants.REQUEST_PIC_FROM_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
            anfe.printStackTrace();
        }
    }


    public String getRealPathFromURI(Uri contentURI, Activity activity) {

        if (contentURI == null) {
            return "";
        }
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void showDialog(Context mcContext, String message) {
        if (dialog == null) {
            dialog = new com.pack.progresshud.ProgressHUD(mcContext,
                    R.style.ProgressHUD);
            dialog.setTitle("");
            dialog.setContentView(R.layout.progress_hudd);
            if (message == null || message.length() == 0) {
                dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
            } else {
                TextView txt = (TextView) dialog.findViewById(R.id.message);
                txt.setText(message);
            }
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.2f;
            dialog.getWindow().setAttributes(lp);

            dialog.show();

            isShowing = true;
        }

    }


    public void dismisDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException error) {
                    error.printStackTrace();
                    Timber.e(error);
                }
            }
            destroyDialog();

        }
    }

    public void destroyDialog() {
        dialog = null;
        isShowing = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyDialog();
    }

    public void toastTextOnTheEndListListener(String message) {

        if (StringUtil.isNullOrEmptyString(message)) {
//            Toast.makeText(this, "Is the end of list", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void scrollBottomOfListView(final PullToRefreshListView listView) {
        listView.post(new Runnable() {

            @Override
            public void run() {
                listView.onRefreshComplete();
            }
        });

    }

    public void toastTextWhenNoInternetConnection(String message) {
        if (StringUtil.isNullOrEmptyString(message)) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean preventMultiClicks() {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return true;
        }

        mLastClickTime = SystemClock.elapsedRealtime();

        return false;
    }

    public void setLocale() {
        Locale myLocale = new Locale(AppsterApplication.mAppPreferences.getAppLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    public void eventChange(ListenerEventModel listenerEventModel) {

    }

    public void openViewLiveStream(String livePlayUrl, String slug, String userImage, boolean isRecord) {
        if (preventMultiClicks()) {
            return;
        }

        if (!isRecord && isMaintenance()) {
            return;
        }

//        if (AppsterUtility.loadPrefList(this.getApplicationContext(), Constants.STREAM_BLOCKED_LIST, AppsterApplication.mAppPreferences.getUserId()).contains(slug)) {
//            Toast.makeText(this.getApplicationContext(), "You have been blocked for this stream!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Intent playbackIntent = MediaPlayerActivity.createIntent(this, slug, livePlayUrl, isRecord, userImage);
        startActivityForResult(playbackIntent, Constants.REQUEST_MEDIA_PLAYER_STREAM);

    }

    public void openViewLiveStream(String livePlayUrl, String slug, boolean isRecord) {
        if (preventMultiClicks()) {
            return;
        }

        if (!isRecord && isMaintenance()) {
            return;
        }

//        if (AppsterUtility.loadPrefList(this.getApplicationContext(), Constants.STREAM_BLOCKED_LIST, AppsterApplication.mAppPreferences.getUserId()).contains(slug)) {
//            Toast.makeText(this.getApplicationContext(), "You have been blocked for this stream!", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Intent playbackIntent = MediaPlayerActivity.createIntent(this, slug, livePlayUrl, isRecord, null);
        startActivityForResult(playbackIntent, Constants.REQUEST_MEDIA_PLAYER_STREAM);

    }

    public void openViewLiveStream(String livePlayUrl, String slug) {
        if (preventMultiClicks()) {
            return;
        }
//        if (AppsterUtility.loadPrefList(this.getApplicationContext(), Constants.STREAM_BLOCKED_LIST, AppsterApplication.mAppPreferences.getUserId()).contains(slug)) {
//            Toast.makeText(this.getApplicationContext(), "You have been blocked for this stream!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Intent playbackIntent = MediaPlayerActivity.createIntent(this, slug, livePlayUrl, false, null);
        startActivityForResult(playbackIntent, Constants.REQUEST_MEDIA_PLAYER_STREAM);
    }

    public void redirectNotificationShowing(NotificationModel.NotificationEntity notificationEntity) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        if (notificationEntity != null) {
            Timber.e("getNotification_type() =" + notificationEntity.getNotification_type());
            switch (notificationEntity.getNotification_type()) {
                case Constants.NOTIFYCATION_TYPE_MESSAGE:

                    Intent intent = ChatActivity.Companion.createIntent(this, String.valueOf(notificationEntity.getUserId()), notificationEntity.getUsername(), notificationEntity.getDisplayName(), notificationEntity.getProfilePic(), -1);
                    startActivityForResult(intent, Constants.CONVERSATION_REQUEST, options.toBundle());

                    break;
                case Constants.NOTIFYCATION_TYPE_RECEIVE_GIFT:

                    Intent intentGift = new Intent(this, TopFanActivity.class);
                    intentGift.putExtra(ConstantBundleKey.BUNDLE_PROFILE, AppsterApplication.mAppPreferences.getUserModel());

                    this.startActivityForResult(intentGift, Constants.REQUEST_TOPFAN_ACTIVITY, options.toBundle());

                    break;
                case Constants.NOTIFYCATION_TYPE_LIKE:

//                    Intent intentDetailLike = new Intent(this, PostDetailActivity.class);
//                    intentDetailLike.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, String.valueOf(notificationEntity.getPostId()));
//                    intentDetailLike.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_USER_ID, String.valueOf(notificationEntity.getUserId()));
//                    this.startActivityForResult(intentDetailLike, Constants.REQUEST_VIEW_NOTIFY, options.toBundle());
//                    break;

                case Constants.NOTIFYCATION_USER_TAGGED_IN_POST:
                case Constants.NOTIFYCATION_TYPE_NEWPOST:
//                    Intent intentDetaiPost = new Intent(this, PostDetailActivity.class);
//                    intentDetaiPost.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, String.valueOf(notificationEntity.getPostId()));
//                    intentDetaiPost.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_USER_ID, String.valueOf(notificationEntity.getUserId()));
//                    this.startActivityForResult(intentDetaiPost, Constants.REQUEST_VIEW_NOTIFY, options.toBundle());
//                    break;

                case Constants.NOTIFYCATION_USER_TAGGED_IN_POST_COMMENT:
                case Constants.NOTIFYCATION_TYPE_COMMENT:
//                    Intent intentDetail = new Intent(this, PostDetailActivity.class);
//                    intentDetail.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, String.valueOf(notificationEntity.getPostId()));
//                    intentDetail.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_USER_ID, String.valueOf(notificationEntity.getUserId()));
//                    this.startActivityForResult(intentDetail, Constants.REQUEST_VIEW_NOTIFY, options.toBundle());
                    openPostDetail(String.valueOf(notificationEntity.getPostId()), String.valueOf(notificationEntity.getUserId()), Constants.REQUEST_VIEW_NOTIFY);
                    break;

                case Constants.NOTIFYCATION_USER_TAGGED_IN_STREAM_COMMENT:
                    Intent commentIntent = CommentActivity.createIntent(this, notificationEntity.getPostId(),
                            notificationEntity.getSlug(), 0, Constants.COMMENT_TYPE_STREAM, notificationEntity.getActionUserId());
                    startActivity(commentIntent);
                    break;

                case Constants.NOTIFYCATION_TYPE_COMISSION:
//
                    Intent intentPocket = new Intent(this, UserProfileActivity.class);
                    intentPocket.putExtra(Constants.USER_PROFILE_DISPLAYNAME, notificationEntity.getDisplayName());
                    intentPocket.putExtra(Constants.USER_PROFILE_ID, String.valueOf(notificationEntity.getUserId()));
                    this.startActivity(intentPocket, options.toBundle());

                    Timber.e("notificationEntity.getUserId()" + notificationEntity.getUserId());

                    break;

                case Constants.NOTIFYCATION_TYPE_FOLLOW:
                    startActivityProfile(String.valueOf(notificationEntity.getUserId()), notificationEntity.getDisplayName());
                    break;

                case 5:

                    break;

                case Constants.NOTIFYCATION_TYPE_LIVESTREAM:

                    mCompositeSubscription.add(Observable.just(true)
                            .subscribeOn(Schedulers.newThread())
                            .map((Func1<Boolean, Object>) integer -> {
                                AppsterChatManger.getInstance(BaseActivity.this).reconnectIfViaPushNotification();
                                return String.valueOf(integer);
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Object>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onNext(Object user) {
//                                    AppsterChatManger.getInstance(BaseActivity.this).createGroupChat(notificationEntity.getSlug(), false, notificationEntity.getUsername());
                                    openViewLiveStream(notificationEntity.getPlayUrl(),
                                            notificationEntity.getSlug(),
                                            notificationEntity.getProfilePic(), false);
                                }
                            }));
                    break;

                case Constants.NOTIFYCATION_TYPE_COMMENT_STREAM:
                    intent = new Intent(this, PostDetailActivity.class);
                    intent.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_SLUG_STREAM, notificationEntity.getSlug());
                    startActivity(intent, options.toBundle());
                    break;
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NotificationModel.NotificationEntity entity) {
        if (isInFront) {
            redirectNotificationShowing(entity);
        }
    }

    public boolean isMaintenance() {
        MaintenanceModel model = AppsterApplication.mAppPreferences.getMaintenanceModel();
        if (model == null) return false;

        if (model.maintenanceMode == Constants.MAINTENANCE_MODE_STANDBY) {
            utility.showMessage(getString(R.string.app_name), model.message, this);
            return true;
        }

        return false;
    }

    public void openPostDetail(String postId, String userId, int requestCode) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.push_in_to_right, R.anim.push_in_to_left);
        Intent intentDetailLike = new Intent(this, PostDetailActivity.class);
        intentDetailLike.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_POST_ID, postId);
        intentDetailLike.putExtra(ConstantBundleKey.BUNDLE_POST_DETAIL_USER_ID, userId);
        this.startActivityForResult(intentDetailLike, requestCode, options.toBundle());
    }

    protected boolean isActivityRunning() {
        return !isFinishing() && !isDestroyed();
    }
}
