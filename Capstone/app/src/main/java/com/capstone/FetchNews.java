package com.capstone;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.capstone.data.NewsContract.News;
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
import java.sql.SQLException;

public class FetchNews extends AsyncTask<Void, Void, Void>{
    private final String LOG_TAG = FetchNews.class.getSimpleName();
    private Context mContext;
    private String sourceString;

    public FetchNews(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL url = null;
        String NewsJsonStr = null;
        final String NEWS_BASE_URL;

        try {
            SharedPreferences source = PreferenceManager.getDefaultSharedPreferences(mContext);
            sourceString = source.getString(mContext.getString(R.string.source_string), mContext.getString(R.string.default_source_string));
            if(sourceString==null) {
                sourceString = mContext.getString(R.string.default_source_string);
            }
            NEWS_BASE_URL = mContext.getString(R.string.base_url) + sourceString + "&apiKey=";
            url = new URL(NEWS_BASE_URL.concat(BuildConfig.API_KEY));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            NewsJsonStr = buffer.toString();
            insertNewsIntoDatabase(NewsJsonStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            //Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    //Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            return null;
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

            //Insert data into the database using contentvalues
            ContentValues values = new ContentValues();
            values.put(News.COLUMN_URL, url);
            values.put(News.COLUMN_SOURCE, sourceString);
            values.put(News.COLUMN_AUTHOR, author);
            values.put(News.COLUMN_TITLE, title);
            values.put(News.COLUMN_DESCRIPTION, description);
            values.put(News.COLUMN_IMAGEURL, urlToImage);
            values.put(News.COLUMN_PUBLISHED, publishedAt);

            //inserted contains the uri of the element added in the database
            Uri inserted = mContext.getContentResolver().insert(News.CONTENT_URI, values);
        }
    }
}
