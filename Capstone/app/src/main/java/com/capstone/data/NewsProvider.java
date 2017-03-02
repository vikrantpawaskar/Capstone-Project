package com.capstone.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.capstone.data.NewsContract.News;

public class NewsProvider extends ContentProvider{

    //A log tag to print log messages
    public static final String LOG_TAG = NewsProvider.class.getSimpleName();

    //Create an PetDbHelper object to access the database
    private NewsHelper mDbHelper;

    //Generate IDs for different patterns in uri
    private static final int NEWS = 100;
    private static final int NEWS_ID = 101;

    //Declare a uri matcher to match the uris
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //link the ids to the uri patterns using adddUri()
    static {
        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS, NEWS);
        sUriMatcher.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS + "/#", NEWS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NewsHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case NEWS:
                cursor = db.query(News.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            /*case NEWS_ID:
                selection = News.COLUMN_ID + "=?;";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(News.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;*/
            default:
                throw new IllegalArgumentException("Cannot query unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NEWS:
                return News.CONTENT_TYPE;
            case NEWS_ID:
                return News.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long row = -1;
        Uri tempUri = null;

        try {
            row = db.insert(News.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e){
            return null;
        }
        if (row >= 0) {
            tempUri = ContentUris.withAppendedId(uri, row);
        }
        else {
            throw new IllegalArgumentException("Cannot query unknown uri: " + uri);
        }
        //Notify all the listeners that data has changed for the URI
        getContext().getContentResolver().notifyChange(uri, null);
        return tempUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int row = 0;
        switch (sUriMatcher.match(uri)) {
            case NEWS:
                row = db.delete(News.TABLE_NAME, selection, selectionArgs);
                break;
            /*case NEWS_ID:
                //set the selection and arguments
                selection = News.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                row = db.delete(News.TABLE_NAME, selection, selectionArgs);
                break;*/
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //Notify all the listeners that data has changed for the URI
        getContext().getContentResolver().notifyChange(uri, null);
        return row;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int row = 0;
        switch (sUriMatcher.match(uri)) {
            case NEWS:
                row = db.update(News.TABLE_NAME, values, selection, selectionArgs);
                break;
            /*case NEWS_ID:
                //set the selection and arguments
                selection = News.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                row = db.update(News.TABLE_NAME, values, selection, selectionArgs);
                break;*/
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //Notify all the listeners that data has changed for the URI
        getContext().getContentResolver().notifyChange(uri, null);
        return row;
    }
}
