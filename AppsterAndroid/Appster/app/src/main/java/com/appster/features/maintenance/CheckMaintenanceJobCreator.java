package com.appster.features.maintenance;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by Ngoc on 5/23/2017.
 */

public class CheckMaintenanceJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case CheckMaintenanceSyncJob.TAG:
                return new CheckMaintenanceSyncJob();
            default:
                return null;
        }
    }

}
