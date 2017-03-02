package com.capstone.sync;

import android.content.Context;
import android.os.AsyncTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class NewsFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchNewsTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        mFetchNewsTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                NewsSyncTask newsSyncTask = new NewsSyncTask();
                newsSyncTask.syncNews(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchNewsTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchNewsTask != null) {
            mFetchNewsTask.cancel(true);
        }
        return true;
    }
}