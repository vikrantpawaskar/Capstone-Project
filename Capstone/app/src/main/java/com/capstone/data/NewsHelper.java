package com.capstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.capstone.data.NewsContract.News;

public class NewsHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "news.db";

    public NewsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + News.TABLE_NAME + " ( " +
                //News.COLUMN_ID + " INTEGER AUTOINCREMENT, " +
                News.COLUMN_URL + " TEXT PRIMARY KEY, " +
                News.COLUMN_SOURCE + " TEXT, " +
                News.COLUMN_AUTHOR + " TEXT, " +
                News.COLUMN_TITLE + " TEXT, " +
                News.COLUMN_DESCRIPTION + " TEXT, " +
                News.COLUMN_IMAGEURL + " TEXT, " +
                News.COLUMN_PUBLISHED + " TEXT ); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + News.TABLE_NAME);
        onCreate(db);
    }
}
