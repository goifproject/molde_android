package com.limefriends.molde.comm.fcm;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MoldeJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}
