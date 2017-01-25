package com.example.proky.popmovie;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity  extends AppCompatPreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set action bar title according to appropriate selected language
        getSupportActionBar().setTitle(R.string.title_activity_settings);

        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);
        setContentView(R.layout.activity_settings);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sorting_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_language_key)));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Attaches a listener so the summary is always updated with the preference value
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

        Log.v("preference key:", preference.getKey());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        Log.v("stringValue:", stringValue);

        if(stringValue.equals("en-US")) {
            LocaleHelper.setLocale(this, "en-US");
            Log.v("lang", LocaleHelper.getLanguage(this));
        } else {
            LocaleHelper.setLocale(this, "ru");
            Log.v("lang", LocaleHelper.getLanguage(this));
        }

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            Log.v("prefIndex:", Integer.toString(prefIndex));

            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }
}
