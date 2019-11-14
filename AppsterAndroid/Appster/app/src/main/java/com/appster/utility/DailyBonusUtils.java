package com.appster.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.appster.R;
import com.appster.main.MainActivity;
import com.appster.models.NotificationModel;
import com.pack.utility.StringUtil;

/**
 * Created by thanhbc on 11/20/17.
 */

public class DailyBonusUtils {
    public static void setupDailyBonusNotification(int userId, int timeCountDownInSecond, String type) {
//        if (timeCountDownInSecond == -1) return;
//        switch (type){
//            case DailyBonusJobCreator.DAILY:
//                DailyTreatJob.scheduleWithExtras(userId,timeCountDownInSecond);
//                break;
//            case DailyBonusJobCreator.WEEKLY:
//                WeeklyTreatJob.scheduleWithExtras(userId,timeCountDownInSecond);
//                break;
//            default:
//                break;
//        }
    }


//    private static void updateDailyBonusAlarm(Context context, int reqCode, long timeCountDownInSecond, Intent intentAlarm) {
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//        Long timeCountDown = new GregorianCalendar().getTimeInMillis() + timeCountDownInSecond * 1000;
//        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, timeCountDown, pendingIntent);
//    }
    public static void pushNotification(Context context, NotificationModel.NotificationEntity notificationEntity) {
        int notifyType = notificationEntity.getNotification_type();

        Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        //setup notification
        String title = TextUtils.isEmpty(notificationEntity.getTitle()) ? context.getString(R.string.app_name) : notificationEntity.getTitle();
        String message = '\u200e' + notificationEntity.getMessage() + '\u200e';
        Intent intent = new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyType /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Style style;
        style = new NotificationCompat.BigTextStyle().bigText(message);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bigIcon)
                .setContentTitle(title)
                .setContentText(StringUtil.decodeString(message))
                .setStyle(style)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationBuilder.setVibrate(new long[]{500, 500});

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyType /* ID of notification */, notification);
    }
}
