package com.appster.features.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by thanhbc on 11/21/17.
 */

public class DailyBonusJobCreator implements JobCreator {
    public static final String DAILY = "daily_bonus_reset";
    public static final String WEEKLY = "weekly_bonus_reset";
    @Override
    public Job create(String tag) {
        switch (tag) {
            case WeeklyTreatJob.JOB_TAG:
                return new WeeklyTreatJob();
            case DailyTreatJob.JOB_TAG:
                return new DailyTreatJob();
            default:
                return null;
        }
    }
}
