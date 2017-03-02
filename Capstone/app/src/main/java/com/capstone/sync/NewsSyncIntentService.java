package com.capstone.sync;

import android.app.IntentService;
import android.content.Intent;

public class NewsSyncIntentService extends IntentService {

    public NewsSyncIntentService() {
        super("NewsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NewsSyncTask newsSyncTask = new NewsSyncTask();
        newsSyncTask.syncNews(this);
    }
}