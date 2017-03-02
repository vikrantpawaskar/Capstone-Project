package com.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.capstone.sync.NewsSyncUtils;

public class MainActivity extends AppCompatActivity {
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start tracking the app
        ((MyApplication)getApplication()).startTracking();

        //Check internet connection
        ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE));
        if(!(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())){
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
        transaction.replace(R.id.news_container, new MainFragment());
        transaction.commit();

        if(findViewById(R.id.news_detail_container)!=null){
            mTwoPane = true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.news_detail_container, new DetailFragment()).commit();
            }
            else{
                mTwoPane = false;
                getSupportActionBar().setElevation(0f);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, getString(R.string.settings), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}