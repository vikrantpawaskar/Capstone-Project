package com.capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.util.Log;

import com.capstone.sync.NewsSyncUtils;

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
import java.util.ArrayList;

public class SettingsActivity extends PreferenceActivity implements SourceCallBack{
    private final String source_base_url = "https://newsapi.org/v1/sources?language=en";
    private ArrayList<String> entryList = new ArrayList<>();
    private ArrayList<String> valuesList = new ArrayList<>();
    private ListPreference listPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_xml);
        listPreference = (ListPreference) findPreference(getString(R.string.source_string));

        //Load data from the API using AsyncTask to populate the list
        if(valuesList.size()==0 || valuesList==null) {
            new FetchSources().execute();
        } else{
            //Convert the loaded ArrayList to CharSequence to be loaded in ListPreference
            CharSequence[] entrySequence = entryList.toArray(new CharSequence[entryList.size()]);
            CharSequence[] valueSequence = valuesList.toArray(new CharSequence[valuesList.size()]);

            //Load the values in ListPreference
            listPreference.setEntries(entrySequence);
            listPreference.setEntryValues(valueSequence);
            listPreference.setDefaultValue(valueSequence[0]);
        }

    }

    @Override
    public void addData() {
        //Convert the loaded ArrayList to CharSequence to be loaded in ListPreference
        CharSequence[] entrySequence = entryList.toArray(new CharSequence[entryList.size()]);
        CharSequence[] valueSequence = valuesList.toArray(new CharSequence[valuesList.size()]);

        //Load the values in ListPreference
        listPreference.setEntries(entrySequence);
        listPreference.setEntryValues(valueSequence);
        listPreference.setDefaultValue(valueSequence[0]);

        //Initialize an onPreferenceChangeListener
        Preference.OnPreferenceChangeListener listListen = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                    for(int i=0; i<valuesList.size(); i++){
                        if(valuesList.get(i).equals(newValue.toString())){
                            MainFragment.SettingsListener = 1;
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                return true;
            }
        };

        //Set the on preference change listener
        listPreference.setOnPreferenceChangeListener(listListen);
    }

    //Inner AsyncClass to load sources in the ListPreference
    public class FetchSources extends AsyncTask<Void, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String SourceJSONString;
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(source_base_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
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
                SourceJSONString = buffer.toString();
                //Log.d("JSON", SourceJSONString);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                        getSources(SourceJSONString);
                    } catch (final IOException e) {
                        Log.e("Error", "Error closing stream", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return SourceJSONString;
        }

        @Override
        protected void onPostExecute(String SourceJSONString) {
            super.onPostExecute(SourceJSONString);
            //The execution continues on the main thread.
            //It executes only when the arraylist is fully populated.
            addData();
        }


        //Decode the JSON String to get the required data
        private void getSources(String SourceJSONString) throws JSONException{
            JSONObject sourceObject = new JSONObject(SourceJSONString);
            JSONArray sourceArray = sourceObject.getJSONArray("sources");

            for (int i=0; i<sourceArray.length(); i++){
                JSONObject tempObject = sourceArray.getJSONObject(i);
                String entry = tempObject.getString("name");
                String value = tempObject.getString("id");

                //Add the entries and values in the arraylist
                entryList.add(entry);
                valuesList.add(value);
            }
        }
    }
}
