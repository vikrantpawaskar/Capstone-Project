package com.capstone.data;

import android.content.ContentResolver;
import android.net.Uri;

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.capstone";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_NEWS = "news";

    public static final class News {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        //Table Name
        public static final String TABLE_NAME = "news";

        //Columns of the MOVIES TABLE
        //public static final String COLUMN_ID = "Id";
        public static final String COLUMN_URL = "Url";
        public static final String COLUMN_SOURCE = "Source";
        public static final String COLUMN_AUTHOR = "Author";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_DESCRIPTION = "Description";
        public static final String COLUMN_IMAGEURL = "Image_Url";
        public static final String COLUMN_PUBLISHED = "Published";

    }
}
