package com.appster.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.appster.AppsterApplication;
import com.appster.R;
import com.appster.activity.MediaPlayerActivity;
import com.appster.features.friend_suggestion.FriendSuggestionActivity;
import com.appster.features.messages.MessageListActivity;
import com.appster.features.messages.chat.ChatActivity;
import com.appster.features.stream.StreamingActivityGLPlus;
import com.appster.main.MainActivity;
import com.appster.models.FollowItemModel;
import com.appster.models.NotificationModel;
import com.appster.models.NotificationPushModel;
import com.appster.models.UserModel;
import com.appster.models.event_bus_models.EventBusPushNotification;
import com.appster.tracking.EventTracker;
import com.appster.utility.ConstantBundleKey;
import com.appster.utility.glide.GlideApp;
import com.appster.utility.glide.GlideRequest;
import com.appster.webservice.AppsterWebServices;
import com.appster.webservice.request_models.CreditsRequestModel;
import com.apster.common.Constants;
import com.apster.common.Utils;
import com.bumptech.glide.request.RequestOptions;
import com.data.room.FollowingLocalDbRxHelper;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.pack.utility.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by gaku on 12/28/17.
 */

public class BeLivePushManager {

    public static final int NOTIFICATION_BIG_PICTURE_WIDTH = Utils.dpToPx(450);
    public final static int NOTIFICATION_BIG_PICTURE_HEIGHT = Utils.dpToPx(192);
    private static final String NOTIFICATION_CHANNEL_ID = "com.appsters.CHANNEL_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "belive noti";
    protected static BeLivePushManager mInstance = new BeLivePushManager();

    public static BeLivePushManager getInstance() {
        return mInstance;
    }

    public NotificationPushModel getPushModelFromJson(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(message, NotificationPushModel.class);
        } catch (Exception e) {
            Timber.e("json format is wrong. message=" + message);
        }
        return null;
    }

    @Inject
    FollowingLocalDbRxHelper mFollowingLocalDbRxHelper;

    private BeLivePushManager() {
        AppsterApplication.getApplication().daggerAppComponent().inject(this);
    }

//    private void messageReceived(String message, Context context) {
//        if (message != null && !message.isEmpty()) {
//            Gson gson = new Gson();
//            // this is the cheating way . after server handle the structure  for notification model. we will change just using 1 model for all
//            NotificationPushModel pushModel = gson.fromJson(message, NotificationPushModel.class);
//
//            int notifyType = pushModel.getNotificationType();
//
//            int unreadMessage = AppsterApplication.mAppPreferences.getNumberUnreadNotification();
//            unreadMessage++;
//            if (shouldShowNotification(notifyType, service)) {
//                switch (notifyType) {
//                    case Constants.NOTIFYCATION_TYPE_RECEIVE_GIFT:
//                    case Constants.NOTIFYCATION_TYPE_FROMADMINCREDIT:
//                        EventTracker.trackPushNotification(pushModel.getMessage(),"ADMIN_CREDIT");
//                    case Constants.NOTIFYCATION_TYPE_COMISSION:
//                        updateCreditsAfterReceiveGift();
//                        break;
//
//                    case Constants.NOTIFICATION_DAILY_BONUS_NEW_DAY:
//                        EventTracker.trackPushNotification(pushModel.getMessage(), Constants.AMPLITUDE_EVENT_PUSH_TYPE_DAILY_BONUS_NEW_DAY);
//                        break;
//
//                    case Constants.NOTIFICATION_DAILY_BONUS_RESET_WEEK:
//                        EventTracker.trackPushNotification(pushModel.getMessage(), Constants.AMPLITUDE_EVENT_PUSH_TYPE_DAILY_BONUS_RESET_WEEK);
//                        break;
//
//                    case Constants.NOTIFYCATION_TYPE_MESSAGE:
//                        if (AppsterApplication.isApplicationSentToBackground(service)) {
//                            int unreadPrivateMessages = AppsterApplication.mAppPreferences.getNumberUnreadMessage();
//                            AppsterApplication.mAppPreferences.setNumberUnreadMessage(++unreadPrivateMessages);
//                        }
//                        unreadMessage--;
//                        break;
//
//                    case Constants.NOTIFYCATION_TYPE_LIVESTREAM:
//                        unreadMessage--;
//                        EventTracker.trackPushNotification(pushModel.getMessage(),"USER_LIVE", pushModel.getSlug());
////                        AppsterApplication.mAppPreferences.setNumberUnreadNotification(unreadMessage);
//                        break;
//                    case Constants.NOTIFYCATION_TYPE_FOLLOW:
//                        onFollowNotifyReceived(pushModel);
//                        break;
//                    case Constants.NOTIFYCATION_TYPE_FROMADMINMSG:
//                        EventTracker.trackPushNotification(pushModel.getMessage(),"ADMIN_MESSAGE");
//                        break;
//                    case Constants.NOTIFYCATION_TYPE_ADMINBONUSGIFT:
//                        EventTracker.trackPushNotification(pushModel.getMessage(),"ADMIN_BONUS_GIFT");
//                        break;
//                }
//
//                NotificationModel.NotificationEntity notificationEntity = new NotificationModel.NotificationEntity(pushModel);
//                pushNotification(notificationEntity, service);
//            }
//
//            if (isNewPost(notifyType) || isNewRecord(notifyType)) {
//                AppsterApplication.mAppPreferences.setIsIsNewPostFromFollowingUsers(true);
//            } else {
//                AppsterApplication.mAppPreferences.setNumberUnreadNotification(unreadMessage);
//            }
//            EventBus.getDefault().post(new EventBusPushNotification(unreadMessage, pushModel));
//        }
//    }

    public boolean handlePushData(NotificationPushModel pushModel, Context context) {
        int notifyType = pushModel.getNotificationType();
        int unreadMessage = AppsterApplication.mAppPreferences.getNumberUnreadNotification();
        unreadMessage++;
        boolean result = shouldShowNotification(notifyType, context);
        Timber.e("shouldShowNotification %s", result);
        if (result) {
            switch (notifyType) {
                case Constants.NOTIFYCATION_TYPE_RECEIVE_GIFT:
                case Constants.NOTIFYCATION_TYPE_FROMADMINCREDIT:
                    EventTracker.trackPushNotification(pushModel.getMessage(), "ADMIN_CREDIT");
                case Constants.NOTIFYCATION_TYPE_COMISSION:
                    updateCreditsAfterReceiveGift();
                    break;

                case Constants.NOTIFICATION_DAILY_BONUS_NEW_DAY:
                    EventTracker.trackPushNotification(pushModel.getMessage(), Constants.AMPLITUDE_EVENT_PUSH_TYPE_DAILY_BONUS_NEW_DAY);
                    break;

                case Constants.NOTIFICATION_DAILY_BONUS_RESET_WEEK:
                    EventTracker.trackPushNotification(pushModel.getMessage(), Constants.AMPLITUDE_EVENT_PUSH_TYPE_DAILY_BONUS_RESET_WEEK);
                    break;

                case Constants.NOTIFYCATION_TYPE_MESSAGE:
                    if (AppsterApplication.isApplicationSentToBackground(context)) {
                        int unreadPrivateMessages = AppsterApplication.mAppPreferences.getNumberUnreadMessage();
                        AppsterApplication.mAppPreferences.setNumberUnreadMessage(++unreadPrivateMessages);
                    }
                    unreadMessage--;
                    break;

                case Constants.NOTIFYCATION_TYPE_LIVESTREAM:
                    if (isCurrentUserPush(pushModel.getUserId())) {
                        // ignore same user push
                        result = false;
                    }

                    unreadMessage--;
                    EventTracker.trackPushNotification(pushModel.getMessage(), "USER_LIVE", pushModel.getSlug());
//                        AppsterApplication.mAppPreferences.setNumberUnreadNotification(unreadMessage);
                    break;
                case Constants.NOTIFYCATION_TYPE_FOLLOW:
                    onFollowNotifyReceived(pushModel);
                    break;
                case Constants.NOTIFYCATION_TYPE_FROMADMINMSG:
                    EventTracker.trackPushNotification(pushModel.getMessage(), "ADMIN_MESSAGE");
                    break;
                case Constants.NOTIFYCATION_TYPE_ADMINBONUSGIFT:
                    EventTracker.trackPushNotification(pushModel.getMessage(), "ADMIN_BONUS_GIFT");
                    break;
                default:
                    Timber.d("message just for showing");
                    break;
            }
        }

        if (isNewPost(notifyType) || isNewRecord(notifyType)) {
            AppsterApplication.mAppPreferences.setIsIsNewPostFromFollowingUsers(true);
        } else {
            AppsterApplication.mAppPreferences.setNumberUnreadNotification(unreadMessage);
        }
        EventBus.getDefault().post(new EventBusPushNotification(unreadMessage, pushModel));

        return result;
    }

    private boolean isCurrentUserPush(int userId) {
        String loginUserId = AppsterApplication.mAppPreferences.getUserId();
        if (!TextUtils.isEmpty(loginUserId) && loginUserId.equals("" + userId)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldShowNotification(int notifyType, Context context) {
        String currentActivity = AppsterApplication.getCurrentActivityRunning(context);
        return !isChatting(currentActivity, notifyType) && !isStreaming(currentActivity, notifyType) && !isNewPost(notifyType) && !isNewRecord(notifyType);
    }

    private boolean isChatting(String currentActivity, int notifyType) {
        return (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(ChatActivity.class.getName()))
                || (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(MessageListActivity.class.getName()));
    }

    private boolean isStreaming(String currentActivity, int notifyType) {
        return (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(StreamingActivityGLPlus.class.getName()))
                || (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(MediaPlayerActivity.class.getName()));
    }

    private boolean isNewPost(int notifyType) {
        return notifyType == Constants.NOTIFYCATION_TYPE_NEWPOST;
    }

    private boolean isNewRecord(int notifyType) {
        return notifyType == Constants.NOTIFYCATION_TYPE_NEW_RECORD;
    }

    public int getNotifyType(NotificationModel.NotificationEntity notificationEntity) {
        String notifyTypeString = String.valueOf(notificationEntity.getNotification_type()) + String.valueOf(notificationEntity.getUserId());
        int notifyType;
        try {
            notifyType = Integer.parseInt(notifyTypeString);
        } catch (Exception e) {
            Timber.e(e);
            notifyType = 0;
        }
        return notifyType;
    }

    public NotificationCompat.Builder createNotificationBuilder(NotificationModel.NotificationEntity notificationEntity, NotificationCompat.Builder outBuilder, Context context) {
        createChannel(context);
        int notifyType = getNotifyType(notificationEntity);

        int size = Utils.dpToPx(48);
        Bitmap bigIcon = fetchImageSync(notificationEntity.getProfilePic(), size, size, true, context);

        //setup notification
        String title = TextUtils.isEmpty(notificationEntity.getTitle()) ? context.getString(R.string.app_name) : notificationEntity.getTitle();
        String message = '\u200e' + notificationEntity.getMessage() + '\u200e';
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ConstantBundleKey.BUNDLE_NOTIFICATION_KEY, notificationEntity);
        if (!isDailyBonusType(notifyType)) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = null;
        // Set no action on Suggestion screen
        if (!AppsterApplication.getCurrentActivityRunning(context).equals(FriendSuggestionActivity.class.getName())) {
            pendingIntent = PendingIntent.getActivity(context, notifyType /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Style style;
        if (TextUtils.isEmpty(notificationEntity.getPushImageUrl())) {
            style = new NotificationCompat.BigTextStyle().bigText(message);

        } else {// has a picture
            Bitmap bigPicture = fetchImageSync(notificationEntity.getPushImageUrl(), 0, 0, false, context);
            bigPicture = centerInsidePicture(bigPicture);
            style = new NotificationCompat.BigPictureStyle()
                    .bigPicture(bigPicture)
                    .setSummaryText(message);
        }

        NotificationCompat.Builder notificationBuilder;
        if (outBuilder != null) {
            notificationBuilder = outBuilder;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
            } else {
                notificationBuilder = new NotificationCompat.Builder(context);
            }
        }

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            notificationBuilder.setColor(ContextCompat.getColor(context, R.color.accent));
//        }
        notificationBuilder.setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(StringUtil.decodeString(message))
                .setStyle(style)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (bigIcon != null) {
            notificationBuilder.setLargeIcon(bigIcon);
        }

        notificationBuilder.setVibrate(new long[]{500, 500});
        UserModel userModel = AppsterApplication.mAppPreferences.getUserModel();
        if (userModel != null && userModel.getNotificationSound() == 1) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(defaultSoundUri);
            OneSignal.enableSound(true);

        } else {
            OneSignal.enableSound(false);
        }

        if (notificationEntity.getNotification_type() == Constants.NOTIFYCATION_TYPE_LIVESTREAM) {
            AppsterApplication.mAppPreferences.saveNotificationModel(notificationEntity);
        }
        return notificationBuilder;
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_logo_white : R.mipmap.ic_launcher;
    }

    public void pushNotification(NotificationModel.NotificationEntity notificationEntity, Context context) {

//        String notifyTypeString = String.valueOf(notificationEntity.getNotification_type()) + String.valueOf(notificationEntity.getUserId());
//        int notifyType;
//        try {
//            notifyType = Integer.parseInt(notifyTypeString);
//        } catch (Exception e) {
//            Timber.e(e);
//            notifyType = 0;
//        }
//        int size = Utils.dpToPx(48);
//        Bitmap bigIcon = fetchImageSync(notificationEntity.getProfilePic(), size, size, true, context);
//
//        //setup notification
//        String title = TextUtils.isEmpty(notificationEntity.getTitle()) ? context.getString(R.string.app_name) : notificationEntity.getTitle();
//        String message = '\u200e' + notificationEntity.getMessage() + '\u200e';
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.putExtra(ConstantBundleKey.BUNDLE_NOTIFICATION_KEY, notificationEntity);
//        if(!isDailyBonusType(notifyType)) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyType /* Request code */, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Style style;
//        if (TextUtils.isEmpty(notificationEntity.getPushImageUrl())) {
//            style = new NotificationCompat.BigTextStyle().bigText(message);
//
//        } else {// has a picture
//            Bitmap bigPicture = fetchImageSync(notificationEntity.getPushImageUrl(), 0, 0, false, context);
//            bigPicture = centerInsidePicture(bigPicture);
//            style = new NotificationCompat.BigPictureStyle()
//                    .bigPicture(bigPicture)
//                    .setSummaryText(message);
//        }
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setContentText(StringUtil.decodeString(message))
//                .setStyle(style)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//
//        if (bigIcon != null) {
//            notificationBuilder.setLargeIcon(bigIcon);
//        }
//
//        notificationBuilder.setVibrate(new long[]{500, 500});
//        UserModel userModel = AppsterApplication.mAppPreferences.getUserModel();
//        if (userModel != null && userModel.getNotificationSound() == 1) {
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            notificationBuilder.setSound(defaultSoundUri);
//        }
//
//        if (notificationEntity.getNotification_type() == Constants.NOTIFYCATION_TYPE_LIVESTREAM) {
//            AppsterApplication.mAppPreferences.saveNotificationModel(notificationEntity);
//        }

        int notifyType = getNotifyType(notificationEntity);
        NotificationCompat.Builder notificationBuilder = createNotificationBuilder(notificationEntity, null, context);

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyType /* ID of notification */, notification);
    }

    public boolean isDailyBonusType(int notifyType) {
        return notifyType == 29 || notifyType == 30;
    }

    @SuppressLint({"RxSubscribeOnError", "RxLeakedSubscription"})
    private void updateCreditsAfterReceiveGift() {
        CreditsRequestModel request = new CreditsRequestModel();
        //noinspection AndroidLintCustomError
        AppsterWebServices.get().getUserCredits("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
                .subscribe(creditsResponseModel -> {
                    if (creditsResponseModel == null) return;
                    if (creditsResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
                        UserModel userModel = AppsterApplication.mAppPreferences.getUserModel();
                        if (userModel != null) {
                            userModel.setTotalGold(creditsResponseModel.getData().getTotal_gold());
                            userModel.setTotalBean(creditsResponseModel.getData().getTotal_bean());
                            userModel.setTotalGoldFans(creditsResponseModel.getData().getTotalGoldFans());
                            AppsterApplication.mAppPreferences.saveUserInforModel(userModel);
                            Timber.d("get credit %d on thread %s", userModel.getTotalBean(), Thread.currentThread().getName());
                        }
                    }
                }, Timber::e);
    }

    /**
     * Use Picasso to load image from remote server synchronously
     *
     * @return the user avatar if success otherwise the launcher icon will be returned
     */
    @SuppressWarnings("CheckResult")
    private Bitmap fetchImageSync(String url, int width, int height, boolean isCircleTransform, Context context) {
        Bitmap bitmap = null;

        if (url != null && !url.isEmpty()) {

            try {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.error(R.mipmap.ic_launcher);

                if (width > 0 && height > 0) {
                    requestOptions.override(width, height);
                }

                GlideRequest<Bitmap> glideReq = GlideApp.with(context)
                        .asBitmap()
                        .apply(requestOptions)
                        .load(url);

                if (isCircleTransform) {
                    //requestOptions.transform(new CircleTransformation(0));
                    glideReq.circleCrop();
                }

                bitmap = glideReq.submit().get();

            } catch (InterruptedException | ExecutionException e) {
                Timber.e(e, "url=" + url);
            }
        }

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        }

        return bitmap;
    }

    private Bitmap centerInsidePicture(Bitmap srcBitmap) {
        if (srcBitmap == null) {
            return null;
        }
        Bitmap canvasBmp;

        // Calculate the number of pixels of width and height of the target with the pixel density taken into account
        int targetWidthPx = NOTIFICATION_BIG_PICTURE_WIDTH;
        int targetHeightPx = NOTIFICATION_BIG_PICTURE_HEIGHT;

        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();

        // padding to get Matrix is ​​0 and small images do not enlarge
        Matrix mat = getMatrix(srcWidth, srcHeight, targetWidthPx, targetHeightPx, 0, false);

        // Create a Paint
        Paint bmpPaint = new Paint();
        bmpPaint.setFilterBitmap(true);

        // Create a canvas whose background is transparent with target width and height
        canvasBmp = Bitmap.createBitmap(targetWidthPx, targetHeightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBmp);

        // Composite images on a transparent canvas
        canvas.drawBitmap(srcBitmap, mat, bmpPaint);

        Timber.i("srcWidth ="
                + srcWidth + "srcHeight =" + srcHeight + "targetWidthPx ="
                + targetWidthPx + "targetHeightPx =" + targetHeightPx);
        return canvasBmp;
    }

    /**
     * @return Scale value to fit
     * @ Param sw Source width
     * @ Param sh Source height
     * @ Param pw Fit this width
     * @ Param ph Fit this height
     * @ Param padding Padding to consider when fitting
     * @ Param enableMagnify Whether or not to enlarge when the image is small
     */
    private static Matrix getMatrix(int sw, int sh, int pw, int ph, int padding, boolean enableMagnify) {
        Float scale = getMaxScaleToParent(sw, sh, pw, ph, padding);
        if (!enableMagnify && scale > 1.0f) {
            scale = 1f;
        }
        Timber.i("# getMatrix ()", "scale =" + scale);
        Matrix mat = new Matrix();
        mat.postScale(scale, scale);
        mat.postTranslate((pw - (int) (sw * scale)) / 2, (ph - (int) (sh * scale)) / 2);
        return mat;
    }

    private static float getMaxScaleToParent(int sw, int sh, int pw, int ph, int padding) {
        Float hScale = (float) (pw - padding) / (float) sw;
        Float vScale = (float) (ph - padding) / (float) sh;
        return Math.min(hScale, vScale);
    }

    /**
     * check whether the notification is belong to current user or not,
     * if not then should ignore this notification
     *
     * @param userId of user should received the notification sent from server
     * @return true if the current user id and user id `sent by server are not equal.
     */
    private boolean shouldIgnoreNotification(int userId) {
        return !AppsterApplication.mAppPreferences.isUserLogin() || !AppsterApplication.mAppPreferences.getUserId().equals(String.valueOf(userId));
    }

    private void onFollowNotifyReceived(NotificationPushModel pushModel) {
        Timber.d("onFollowNotifyReceived thread %s", Thread.currentThread().getName());
        FollowItemModel follower = new FollowItemModel();
        follower.setUserId(String.valueOf(pushModel.getUserId()));
        follower.setDisplayName(pushModel.getUserDisplayName());
        follower.setUserName(pushModel.getUserName());
        follower.setIsFollow(Constants.IS_FOLLOWING_USER);
        follower.setProfilePic(pushModel.getUserName());

        mFollowingLocalDbRxHelper.insertOne(follower).subscribe();
    }

    @SuppressLint("NewApi")
    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
}
