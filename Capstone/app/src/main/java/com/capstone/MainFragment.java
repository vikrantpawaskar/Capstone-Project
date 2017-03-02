package com.capstone;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.data.NewsContract.News;
import com.capstone.sync.NewsSyncUtils;
import com.capstone.widget.NewsWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, NewsAdapter.NewsAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private NewsAdapter mNewsAdapter;
    private String sourceString;
    private LinearLayoutManager layoutManager;
    public static int SettingsListener = 0;

    //Set the loader id to load the loader
    private static final int ID_NEWS_LOADER = 44;

    // The columns of data that we are interested in displaying within our MainActivity's list of news data.
    public static final String[] MAIN_NEWS_PROJECTION = {
            News.COLUMN_TITLE, News.COLUMN_DESCRIPTION, News.COLUMN_AUTHOR, News.COLUMN_URL, News.COLUMN_IMAGEURL, News.COLUMN_PUBLISHED
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences source = PreferenceManager.getDefaultSharedPreferences(getContext());
        sourceString = source.getString(getString(R.string.source_string),getString(R.string.default_source_string));
        if(sourceString == null){
            sourceString = getString(R.string.default_source_string);
            new FetchNews(getActivity()).execute();
        } else {
            NewsSyncUtils.startImmediateSync(getContext());
        }

        //Notify the widget
        if(SettingsListener!=0){
            //notify the widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), NewsWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            SettingsListener=0;
        }

        //Display ads
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

        //Get the recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_news_list);

        //Set the recycler view to display as a list using linear layout vertical orientation
        if(MainActivity.mTwoPane) {
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        } else{
            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        //Create the adapter
        mNewsAdapter = new NewsAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mNewsAdapter);

        //Set a line as divider between two items in recyclerview
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        //Call the loader
        getActivity().getSupportLoaderManager().initLoader(ID_NEWS_LOADER, null, (LoaderManager.LoaderCallbacks<Cursor>)this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_NEWS_LOADER:
                String selection = News.COLUMN_SOURCE + "=?";
                String[] arguments = {sourceString};

                return new CursorLoader(getContext(), News.CONTENT_URI, MAIN_NEWS_PROJECTION, selection, arguments, News.COLUMN_PUBLISHED + " DESC");
            default:
                throw new RuntimeException(getString(R.string.loader_not_implemented) + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewsAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Log.d("Reset Loader", "Called");
        mNewsAdapter.swapCursor(null);
    }

    @Override
    public void onClick(String url) {
        View detailContainer = getActivity().findViewById(R.id.news_detail_container);
        if(detailContainer!=null){
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.pass_url), url);
            DetailFragment mFragment = new DetailFragment();
            mFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.news_detail_container, mFragment).commit();
        }
        else {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra(getString(R.string.pass_url), url);
            startActivity(intent);
        }
    }
}