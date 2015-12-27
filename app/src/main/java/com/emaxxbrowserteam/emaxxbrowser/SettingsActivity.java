package com.emaxxbrowserteam.emaxxbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.emaxxbrowserteam.emaxxbrowser.loader.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackson on 27.12.2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        Preference button = (Preference)findPreference(getString(R.string.pref_clear_cache));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FileUtils.clearCache(SettingsActivity.this.getCacheDir());
                Toast.makeText(SettingsActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
                SettingsActivity.this.startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                return true;
            }
        });
        button = (Preference)findPreference(getString(R.string.pref_upload_all));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FileUtils.clearCache(SettingsActivity.this.getCacheDir());
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                i.putExtra(getString(R.string.refresh_all_extra), true);
                SettingsActivity.this.startActivity(i);
                return true;
            }
        });
        ListPreference l = (ListPreference) findPreference(getString(R.string.pref_color_theme));
        List<String> list = new ArrayList<>();
        try {
            int i = 0;
            for (String s : getAssets().list("www")) {
                if (s.endsWith(".css")) {
                    list.add(s.replace(".css", ""));
                }
                i++;
            }
        } catch (IOException e) {
            Log.e(TAG, "Can't access assets: " + e);
        }
        CharSequence arr[] = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        l.setEntries(arr);
        l.setEntryValues(arr);
        Log.d(TAG, "value is " + l.getValue());
        //l.setValueIndex(0);
    }

    private static String TAG = "SettingsActivity.java";
}
