package com.emaxxbrowserteam.emaxxbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.emaxxbrowserteam.emaxxbrowser.loader.FileUtils;

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
                return true;
            }
        });
    }
}
