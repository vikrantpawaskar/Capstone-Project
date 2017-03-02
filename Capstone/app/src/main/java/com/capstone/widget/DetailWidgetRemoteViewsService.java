package com.capstone.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.capstone.R;
import com.capstone.data.NewsContract.News;


public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    private String[] columns = {News.COLUMN_TITLE};

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                Log.v("WIDGET","DATA CHANGED");
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(News.CONTENT_URI, columns, null, null, News.COLUMN_PUBLISHED + " DESC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                final RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.widget_detail);

                String title = data.getString(data.getColumnIndex(News.COLUMN_TITLE));
                remoteView.setTextViewText(R.id.widget_detail_text, title);
                return remoteView;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
