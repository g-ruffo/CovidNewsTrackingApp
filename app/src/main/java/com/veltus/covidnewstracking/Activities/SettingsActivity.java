package com.veltus.covidnewstracking.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.veltus.covidnewstracking.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    @Override
    public void onBackPressed() {
        /* On back press create new Intent and load the MainActivity class */
        Intent settingsIntent = new Intent(this, MainActivity.class);
        startActivity(settingsIntent);
    }


    public static class NewsSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private Preference sortBy;
        private Preference language;
        private Preference countries;
        private Preference region;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            /* Find the preference views and bind the set values */
            sortBy = findPreference(getString(R.string.settings_sort_by_key));
            bindPreferenceSummaryToValue(sortBy);

            language = findPreference(getString(R.string.settings_language_key));
            bindPreferenceSummaryToValue(language);

            countries = findPreference(getString(R.string.settings_countries_key));
            bindPreferenceSummaryToValue(countries);

            region = findPreference(getString(R.string.settings_region_key));
            bindPreferenceSummaryToValue(region);

            /* Find the reset to default preference and restore the default values when clicked */
            Preference restoreDefault = findPreference(getString(R.string.settings_restore_key));
            restoreDefault.setOnPreferenceClickListener(preference -> {
                restoreDefaultSettings(preference);
                return true;
            });

            /* Find the clear favoriteList preference and delete list when clicked */
            Preference clearFavoriteList = findPreference(getString(R.string.settings_clear_key));
            clearFavoriteList.setOnPreferenceClickListener(preference -> {
                clearAllFavoritesList(preference);
                return true;
            });

        }


        @Override
        /* Update the displayed preference summary after it has been changed */
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        /* Delete all values saved in SharedPreferences and set the values to default */
        private void restoreDefaultSettings(Preference preference) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(getString(R.string.settings_region_key));
            editor.remove(getString(R.string.settings_countries_key));
            editor.remove(getString(R.string.settings_language_key));
            editor.remove(getString(R.string.settings_sort_by_key));
            editor.commit();
            sortBy.setSummary(R.string.settings_sort_by_newest_label);
            language.setSummary(R.string.settings_language_english_label);
            countries.setSummary(R.string.settings_countries_canada_label);
            region.setSummary(R.string.settings_region_canada_label);

            Toast.makeText(preference.getContext(), preference.getContext().getString(R.string.defaults_restored), Toast.LENGTH_SHORT).show();

        }

        /* Clear the global favoriteList and remove all saved list items in SharedPreferences */
        private void clearAllFavoritesList(Preference preference) {
            MainActivity.getFavoritesList().clear();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("list_key");
            editor.commit();
            Toast.makeText(preference.getContext(), preference.getContext().getString(R.string.bookmarks_deleted), Toast.LENGTH_SHORT).show();
        }

    }
}

