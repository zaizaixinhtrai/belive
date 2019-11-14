package com.appster.features.maintenance;

import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.appster.AppsterApplication;
import com.appster.BuildConfig;
import com.appster.features.stream.StreamingActivityGLPlus;
import com.appster.utility.RxUtils;
import com.appster.webservice.AppsterWebServices;
import com.apster.common.Constants;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobConfig;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by Ngoc on 5/23/2017.
 */

public class CheckMaintenanceSyncJob extends Job {
    public static final String TAG = "CheckMaintenanceSyncJob";
    private Subscription subscriptionMaintenance;

    @Override
    @NonNull
    protected Result onRunJob(final Params params) {
        RxUtils.unsubscribeIfNotNull(subscriptionMaintenance);
        long time = SystemClock.currentThreadTimeMillis();
        subscriptionMaintenance = AppsterWebServices.get().checkMaintenance(BuildConfig.AWS_S3_SERVER_LINK + "maintenance/maintenance.json?t=" + time)
                .onErrorResumeNext(Observable::error)
                .subscribe(maintenance -> {
                    Timber.e(TAG);
                    if (maintenance == null) return;

                    AppsterApplication.mAppPreferences.saveMaintenanceModel(maintenance);
                    EventBus.getDefault().post(maintenance);
                    if (maintenance.maintenanceMode == Constants.MAINTENANCE_MODE_START && !AppsterApplication.isApplicationSentToBackground(getContext())) {

                        if (AppsterApplication.getCurrentActivityRunning(getContext()).equalsIgnoreCase(MaintenanceActivity.class.getName()) ||
                                AppsterApplication.getCurrentActivityRunning(getContext()).equalsIgnoreCase(StreamingActivityGLPlus.class.getName())) {
                            return;
                        }
                        MaintenanceActivity.startMaintenanceActivity(getContext(), maintenance);
                    }

                }, Timber::e);

        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                JobConfig.setAllowSmallerIntervalsForMarshmallow(true);
                new JobRequest.Builder(CheckMaintenanceSyncJob.TAG)
                        .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                        .setRequirementsEnforced(true)
                        .setUpdateCurrent(true)
                        .build()
                        .schedule();
            } else {
            /*workaround to fix https://fabric.io/appster1110/android/apps/com.appster/issues/59369b7dbe077a4dccc010e2?time=last-seven-days
            * https://github.com/yigit/android-priority-jobqueue/issues/202
            * */

                if (JobManager.instance().getAllJobRequestsForTag(CheckMaintenanceSyncJob.TAG).size() > 80) {
                    Timber.e("pending job > 80");
                    JobManager.instance().cancelAllForTag(CheckMaintenanceSyncJob.TAG);
                }
                new JobRequest.Builder(CheckMaintenanceSyncJob.TAG)
                        .setPeriodic(JobRequest.MIN_INTERVAL)
                        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                        .setRequirementsEnforced(true)
                        .setUpdateCurrent(true)
                        .build()
                        .schedule();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
