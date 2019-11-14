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

import static com.appster.features.jobs.DailyBonusJobCreator.WEEKLY;
import static com.appster.utility.DailyBonusUtils.pushNotification;

/**
 * Created by thanhbc on 11/21/17.
 */

public class WeeklyTreatJob extends Job {
    public static final String JOB_TAG = "weekly_treat_job";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        final Context context = getContext();
        AppPreferences preferences = new AppPreferences(context);
        int weeklyPush = params.getExtras().getInt(WEEKLY, -1);
        UserModel userModel = preferences.getUserModel();
        if (userModel != null && userModel.getUserId().equalsIgnoreCase(String.valueOf(weeklyPush))) {
            NotificationModel.NotificationEntity entity = new NotificationModel.NotificationEntity();
            entity.setNotification_type(100);
            entity.setMessage(context.getString(R.string.treats_reseted));
            pushNotification(context, entity);
        }
        return Result.SUCCESS;
    }

    public static void scheduleWithExtras(int userId, int secondToCountDown) {
        try {
            PersistableBundleCompat extras = new PersistableBundleCompat();
            extras.putInt(WEEKLY, userId);
            if (JobManager.instance().getAllJobRequestsForTag(WeeklyTreatJob.JOB_TAG).size() > 0) {
                JobManager.instance().cancelAllForTag(WeeklyTreatJob.JOB_TAG);
            }
            new JobRequest.Builder(WeeklyTreatJob.JOB_TAG)
                    .setExact(TimeUnit.SECONDS.toMillis(secondToCountDown))
                    .setExtras(extras)
                    .build()
                    .schedule();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
