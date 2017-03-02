package com.capstone.sync;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.capstone.BuildConfig;
import com.capstone.MainFragment;
import com.capstone.data.NewsContract;
import com.capstone.widget.NewsWidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NewsSyncTask {
    private Context mContext;
    private String sourceString;
    private String LOG_TAG = NewsSyncTask.class.getSimpleName();

    synchronized public void syncNews(Context context) {
        mContext = context;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL url = null;
        String NewsJsonStr = null;
        final String NEWS_BASE_URL;

        try {
            SharedPreferences source = PreferenceManager.getDefaultSharedPreferences(context);
            sourceString = source.getString("source","techcrunch");
            if(sourceString==null) {
                sourceString = "techcrunch";
            }
            NEWS_BASE_URL = "https://newsapi.org/v1/articles?source=" + sourceString + "&apiKey=";
            url = new URL(NEWS_BASE_URL.concat(BuildConfig.API_KEY));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //Log.v("URL", url.toString());

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
            }
            NewsJsonStr = buffer.toString();
            //Log.v("OUTPUT", NewsJsonStr);
            insertNewsIntoDatabase(NewsJsonStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void insertNewsIntoDatabase(String NewsJSONStr) throws JSONException{
        JSONObject newsObject = new JSONObject(NewsJSONStr);
        JSONArray articles = newsObject.getJSONArray("articles");
        for(int i=0; i<articles.length(); i++){
            JSONObject news = articles.getJSONObject(i);
            String url = news.getString("url");
            String author = news.getString("author");
            String title = news.getString("title");
            String description = news.getString("description");
            String urlToImage = news.getString("urlToImage");
            String publishedAt = news.getString("publishedAt");
            //Log.d("DATA", url + " " + author + " " + title);

            //Insert data into the database using contentvalues
            ContentValues values = new ContentValues();
            values.put(NewsContract.News.COLUMN_URL, url);
            values.put(NewsContract.News.COLUMN_SOURCE, sourceString);
            values.put(NewsContract.News.COLUMN_AUTHOR, author);
            values.put(NewsContract.News.COLUMN_TITLE, title);
            values.put(NewsContract.News.COLUMN_DESCRIPTION, description);
            values.put(NewsContract.News.COLUMN_IMAGEURL, urlToImage);
            values.put(NewsContract.News.COLUMN_PUBLISHED, publishedAt);

            //inserted contains the uri of the element added in the database
            Uri inserted=null;
            try {
                inserted = mContext.getContentResolver().insert(NewsContract.News.CONTENT_URI, values);
            } catch (Exception e){

            }
            if(inserted!=null){
                MainFragment.SettingsListener = 1;
            }
        }
    }
}