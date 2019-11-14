package com.appster.features.jobs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.appster.R;
import com.appster.data.AppPreferences;
import com.appster.models.NotificationModel;
import com.appster.models.UserModel;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.appster.features.jobs.DailyBonusJobCreator.DAILY;
import static com.appster.utility.DailyBonusUtils.pushNotification;

/**
 * Created by thanhbc on 11/21/17.
 */

public class DailyTreatJob extends Job {
    public static final String JOB_TAG = "daily_treat_job";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        final Context context = getContext();
        AppPreferences preferences = new AppPreferences(context);
        int dailyPush = params.getExtras().getInt(DAILY, -1);
        UserModel userModel = preferences.getUserModel();
        if (userModel != null && userModel.getUserId().equalsIgnoreCase(String.valueOf(dailyPush))) {
            NotificationModel.NotificationEntity entity = new NotificationModel.NotificationEntity();
            entity.setNotification_type(99);
            entity.setMessage(context.getString(R.string.get_daily_next_treat));
            pushNotification(context, entity);
        }
        return Result.SUCCESS;
    }

    public static void scheduleWithExtras(int userId,int secondToCountDown) {
        try {
            PersistableBundleCompat extras = new PersistableBundleCompat();
            extras.putInt(DAILY, userId);
            if (JobManager.instance().getAllJobRequestsForTag(DailyTreatJob.JOB_TAG).size() > 0) {
                JobManager.instance().cancelAllForTag(DailyTreatJob.JOB_TAG);
            }
            new JobRequest.Builder(DailyTreatJob.JOB_TAG)
                    .setExact(TimeUnit.SECONDS.toMillis(secondToCountDown))
                    .setExtras(extras)
                    .build()
                    .schedule();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
