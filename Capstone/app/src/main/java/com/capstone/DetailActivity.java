package com.capstone;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String url = getIntent().getStringExtra(getString(R.string.pass_url));

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.pass_url),url);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);

        //Call the fragment
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
            transaction.add(R.id.movie_detail_container, fragment);
            transaction.commit();
        }
    }
}
