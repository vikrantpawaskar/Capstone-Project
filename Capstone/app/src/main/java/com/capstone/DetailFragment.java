package com.capstone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.capstone.data.NewsContract;

public class DetailFragment extends Fragment {
    private Context mContext;
    private View rootView;
    public static String sourceString;
    public static String url;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        WebView webView = (WebView) rootView.findViewById(R.id.webView);

        String[] projection = {NewsContract.News.COLUMN_URL};
        SharedPreferences source = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sourceString = source.getString(getString(R.string.source_string), getString(R.string.default_source_string));
        if (sourceString == null) {
            sourceString = getString(R.string.default_source_string);
        }
        String selection = NewsContract.News.COLUMN_SOURCE + "=?";
        String[] arguments = {sourceString};
        String sortBy = NewsContract.News.COLUMN_PUBLISHED + " DESC";

        if (getArguments() == null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Cursor cursor = getActivity().getContentResolver().query(NewsContract.News.CONTENT_URI, projection, selection, arguments, sortBy);
            cursor.moveToFirst();
            url = cursor.getString(cursor.getColumnIndex(NewsContract.News.COLUMN_URL));
            webView.setWebViewClient(new MyBrowser());
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
        } else {
            url = getArguments().getString(getString(R.string.pass_url));
            webView.setWebViewClient(new MyBrowser());
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mContext = context;
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
