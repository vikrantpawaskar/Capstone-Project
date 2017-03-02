package com.capstone;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.capstone.data.NewsContract.News;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsAdapterViewHolder>{
    private Context mContext;
    private Cursor mCursor;
    private NewsAdapterOnClickHandler mClickHandler;
    public static final String[] MAIN_NEWS_PROJECTION = {
            News.COLUMN_TITLE, News.COLUMN_DESCRIPTION, News.COLUMN_AUTHOR, News.COLUMN_URL, News.COLUMN_IMAGEURL, News.COLUMN_PUBLISHED
    };
    private String sourceString;

    public interface NewsAdapterOnClickHandler {
        void onClick(String url);
    }

    public NewsAdapter(@NonNull Context context, NewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        SharedPreferences source = PreferenceManager.getDefaultSharedPreferences(mContext);
        sourceString = source.getString(mContext.getString(R.string.source_string), mContext.getString(R.string.default_source_string));
        if(sourceString == null){
            sourceString = mContext.getString(R.string.default_source_string);
        }
        String selection = News.COLUMN_SOURCE + "=?";
        String[] arguments = {sourceString};
        mCursor = mContext.getContentResolver().query(News.CONTENT_URI, MAIN_NEWS_PROJECTION, selection, arguments, null);
    }

    @Override
    public NewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewgroup_layout, parent, false);
        view.setFocusable(true);
        return new NewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        //Get the data from the database
        String urlToImage = mCursor.getString(mCursor.getColumnIndex(News.COLUMN_IMAGEURL));
        String url = mCursor.getString(mCursor.getColumnIndex(News.COLUMN_URL));
        String title = mCursor.getString(mCursor.getColumnIndex(News.COLUMN_TITLE));
        String description = mCursor.getString(mCursor.getColumnIndex(News.COLUMN_DESCRIPTION));
        String author = mCursor.getString(mCursor.getColumnIndex(News.COLUMN_AUTHOR));
        if(author==null){
            author = "anonymous";
        }
        String published = mCursor.getString(mCursor.getColumnIndex(News.COLUMN_PUBLISHED));

        //Get display size to resize image
        int width= mContext.getResources().getDisplayMetrics().widthPixels;
        int height= mContext.getResources().getDisplayMetrics().heightPixels;

        //Load the data into the viewgroup
        holder.titleView.setText(title);
        holder.descriptionView.setText(description);
        //holder.authorView.setText(author);
        holder.dateView.setText(published);
        if(MainActivity.mTwoPane){
            //Picasso.with(mContext).load(urlToImage).resize(2*width/5, 2*width/5).into(holder.imageView);
        } else{
            Picasso.with(mContext).load(urlToImage).resize(width, width-50).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // This class is used to call the layouts once.
    class NewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView titleView;
        final TextView descriptionView;
        //final TextView authorView;
        final TextView dateView;

        NewsAdapterViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageView);
            titleView = (TextView) view.findViewById(R.id.titleView);
            descriptionView = (TextView) view.findViewById(R.id.descriptionView);
            //authorView = (TextView) view.findViewById(R.id.authorView);
            dateView = (TextView) view.findViewById(R.id.dateView);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String url = mCursor.getString(mCursor.getColumnIndex(News.COLUMN_URL));
            mClickHandler.onClick(url);
        }
    }
}
