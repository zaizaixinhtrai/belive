package com.appster.services;

import android.os.Bundle;

import com.appster.models.NotificationModel;
import com.appster.models.NotificationPushModel;
import com.apster.common.Utils;
import com.google.android.gms.gcm.GcmListenerService;

import timber.log.Timber;

/**
 * Created by sonnguyen on 9/10/15.
 */
public class ApptersGCMListenerService extends GcmListenerService {
    private static final int NOTIFICATION_BIG_PICTURE_WIDTH = Utils.dpToPx(450);
    private final static int NOTIFICATION_BIG_PICTURE_HEIGHT = Utils.dpToPx(192);

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        String message = data.getString("data");
        Timber.e("notification message %s", message);

        BeLivePushManager pushManager = BeLivePushManager.getInstance();

        NotificationPushModel pushModel = pushManager.getPushModelFromJson(message);
        if (pushModel == null) {
            return;
        }

        if (pushManager.handlePushData(pushModel, this)) {
            NotificationModel.NotificationEntity notificationEntity = new NotificationModel.NotificationEntity(pushModel);
            pushManager.pushNotification(notificationEntity, this);
        }
    }


//    @Override
//    public void onMessageReceived(String from, Bundle data) {
//        super.onMessageReceived(from, data);
//        String message = data.getString("data");
//        Timber.e("notification message %s", message);
//
//        if (message != null && !message.isEmpty()) {
//            Gson gson = new Gson();
//            // this is the cheating way . after server handle the structure  for notification model. we will change just using 1 model for all
//            NotificationPushModel pushModel = gson.fromJson(message, NotificationPushModel.class);
//            int notifyType = pushModel.getNotificationType();
//
//            int unreadMessage = AppsterApplication.mAppPreferences.getNumberUnreadNotification();
//            unreadMessage++;
//            if (shouldShowNotification(notifyType)) {
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
//                        if (AppsterApplication.isApplicationSentToBackground(this)) {
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
//                pushNotification(notificationEntity);
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
//
//    private boolean shouldShowNotification(int notifyType) {
//        String currentActivity = AppsterApplication.getCurrentActivityRunning(this);
//        return !isChatting(currentActivity, notifyType) && !isStreaming(currentActivity, notifyType) && !isNewPost(notifyType) && !isNewRecord(notifyType);
//    }
//
//    private boolean isChatting(String currentActivity, int notifyType) {
//        return (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(ActivityChat.class.getName()))
//                || (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(MessageListActivity.class.getName()));
//    }
//
//    private boolean isStreaming(String currentActivity, int notifyType) {
//        return (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(StreamingActivityGLPlus.class.getName()))
//                || (notifyType == Constants.NOTIFYCATION_TYPE_MESSAGE && currentActivity.equalsIgnoreCase(MediaPlayerActivity.class.getName()));
//    }
//
//    private boolean isNewPost(int notifyType) {
//        return notifyType == Constants.NOTIFYCATION_TYPE_NEWPOST;
//    }
//
//    private boolean isNewRecord(int notifyType) {
//        return notifyType == Constants.NOTIFYCATION_TYPE_NEW_RECORD;
//    }
//
//    private void pushNotification(NotificationModel.NotificationEntity notificationEntity) {
//
//        String notifyTypeString = String.valueOf(notificationEntity.getNotification_type()) + String.valueOf(notificationEntity.getUserId());
//        int notifyType;
//        try {
//            notifyType = Integer.parseInt(notifyTypeString);
//        } catch (Exception e) {
//            Timber.e(e);
//            notifyType = 0;
//        }
//        int size = Utils.dpToPx(48);
//        Bitmap bigIcon = fetchImageSync(notificationEntity.getProfilePic(), size, size, true);
//
//        //setup notification
//        String title = TextUtils.isEmpty(notificationEntity.getTitle()) ? getString(R.string.app_name) : notificationEntity.getTitle();
//        String message = '\u200e' + notificationEntity.getMessage() + '\u200e';
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra(ConstantBundleKey.BUNDLE_NOTIFICATION_KEY, notificationEntity);
//        if(!isDailyBonusType(notifyType)) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifyType /* Request code */, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Style style;
//        if (TextUtils.isEmpty(notificationEntity.getPushImageUrl())) {
//            style = new NotificationCompat.BigTextStyle().bigText(message);
//
//        } else {// has a picture
//            Bitmap bigPicture = fetchImageSync(notificationEntity.getPushImageUrl(), 0, 0, false);
//            bigPicture = centerInsidePicture(bigPicture);
//            style = new NotificationCompat.BigPictureStyle()
//                    .bigPicture(bigPicture)
//                    .setSummaryText(message);
//        }
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
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
//
//        Notification notification = notificationBuilder.build();
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(notifyType /* ID of notification */, notification);
//    }
//
//    private boolean isDailyBonusType(int notifyType) {
//        return notifyType==29 || notifyType==30;
//    }
//
//    @SuppressLint("RxSubscribeOnError")
//    private void updateCreditsAfterReceiveGift() {
//        CreditsRequestModel request = new CreditsRequestModel();
//        //noinspection AndroidLintCustomError
//        AppsterWebServices.get().getUserCredits("Bearer " + AppsterApplication.mAppPreferences.getUserToken(), request)
//                .subscribe(creditsResponseModel -> {
//                    if (creditsResponseModel == null) return;
//                    if (creditsResponseModel.getCode() == Constants.RESPONSE_FROM_WEB_SERVICE_OK) {
//                        UserModel userModel = AppsterApplication.mAppPreferences.getUserModel();
//                        if (userModel != null) {
//                            userModel.setTotalGold(creditsResponseModel.getData().getTotal_gold());
//                            userModel.setTotalBean(creditsResponseModel.getData().getTotal_bean());
//                            userModel.setTotalGoldFans(creditsResponseModel.getData().getTotalGoldFans());
//                            AppsterApplication.mAppPreferences.saveUserInforModel(userModel);
//                            Timber.d("get credit %d on thread %s", userModel.getTotalBean(), Thread.currentThread().getName());
//                        }
//                    }
//                }, Timber::e);
//    }
//
//    /**
//     * Use Picasso to load image from remote server synchronously
//     *
//     * @return the user avatar if success otherwise the launcher icon will be returned
//     */
//    @SuppressWarnings("CheckResult")
//    private Bitmap fetchImageSync(String url, int width, int height, boolean isCircleTransform) {
//        Bitmap bitmap = null;
//        try {
//                RequestOptions requestOptions = new RequestOptions();
//                requestOptions.error(R.mipmap.ic_launcher);
//
//                if (width > 0 && height > 0) {
//                    requestOptions.override(width, height);
//                }
//                if (isCircleTransform) {
//                    requestOptions.transform(new CircleTransformation(0));
//                }
//
//            bitmap = GlideApp.with(getApplicationContext())
//                    .asBitmap()
//                    .load(url)
//                    .apply(requestOptions)
//                    .submit()
//                    .get();
//        } catch (InterruptedException | ExecutionException e) {
//            Timber.e(e, "url=" + url);
//        }
//
//        if (bitmap == null){
//            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        }
//
//        return bitmap;
//    }
//
//    private Bitmap centerInsidePicture(Bitmap srcBitmap) {
//        if (srcBitmap == null) {
//            return null;
//        }
//        Bitmap canvasBmp;
//
//        // Calculate the number of pixels of width and height of the target with the pixel density taken into account
//        int targetWidthPx = NOTIFICATION_BIG_PICTURE_WIDTH;
//        int targetHeightPx = NOTIFICATION_BIG_PICTURE_HEIGHT;
//
//        int srcWidth = srcBitmap.getWidth();
//        int srcHeight = srcBitmap.getHeight();
//
//        // padding to get Matrix is ​​0 and small images do not enlarge
//        Matrix mat = getMatrix(srcWidth, srcHeight, targetWidthPx, targetHeightPx, 0, false);
//
//        // Create a Paint
//        Paint bmpPaint = new Paint();
//        bmpPaint.setFilterBitmap(true);
//
//        // Create a canvas whose background is transparent with target width and height
//        canvasBmp = Bitmap.createBitmap(targetWidthPx, targetHeightPx, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(canvasBmp);
//
//        // Composite images on a transparent canvas
//        canvas.drawBitmap(srcBitmap, mat, bmpPaint);
//
//        Timber.i("srcWidth ="
//                + srcWidth + "srcHeight =" + srcHeight + "targetWidthPx ="
//                + targetWidthPx + "targetHeightPx =" + targetHeightPx);
//        return canvasBmp;
//    }
//
//    /**
//     * @return Scale value to fit
//     * @ Param sw Source width
//     * @ Param sh Source height
//     * @ Param pw Fit this width
//     * @ Param ph Fit this height
//     * @ Param padding Padding to consider when fitting
//     * @ Param enableMagnify Whether or not to enlarge when the image is small
//     */
//    private static Matrix getMatrix(int sw, int sh, int pw, int ph, int padding, boolean enableMagnify) {
//        Float scale = getMaxScaleToParent(sw, sh, pw, ph, padding);
//        if (!enableMagnify && scale > 1.0f) {
//            scale = 1f;
//        }
//        Timber.i("# getMatrix ()", "scale =" + scale);
//        Matrix mat = new Matrix();
//        mat.postScale(scale, scale);
//        mat.postTranslate((pw - (int) (sw * scale)) / 2, (ph - (int) (sh * scale)) / 2);
//        return mat;
//    }
//
//    private static float getMaxScaleToParent(int sw, int sh, int pw, int ph, int padding) {
//        Float hScale = (float) (pw - padding) / (float) sw;
//        Float vScale = (float) (ph - padding) / (float) sh;
//        return Math.min(hScale, vScale);
//    }
//
//    /**
//     * check whether the notification is belong to current user or not,
//     * if not then should ignore this notification
//     *
//     * @param userId of user should received the notification sent from server
//     * @return true if the current user id and user id `sent by server are not equal.
//     */
//    private boolean shouldIgnoreNotification(int userId) {
//        return !AppsterApplication.mAppPreferences.isUserLogin() || !AppsterApplication.mAppPreferences.getUserId().equals(String.valueOf(userId));
//    }
//
//    private void onFollowNotifyReceived(NotificationPushModel pushModel) {
//        Timber.d("onFollowNotifyReceived thread %s", Thread.currentThread().getName());
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        FollowItemModel follower = new FollowItemModel();
//        follower.setUserId(String.valueOf(pushModel.getUserId()));
//        follower.setDisplayName(pushModel.getUserDisplayName());
//        follower.setUserName(pushModel.getUserName());
//        follower.setIs_follow(Constants.IS_FOLLOWING_USER);
//        follower.setProfilePic(pushModel.getUserName());
//        realm.copyToRealmOrUpdate(follower);
//        realm.commitTransaction();
//        realm.close();
//    }

}
