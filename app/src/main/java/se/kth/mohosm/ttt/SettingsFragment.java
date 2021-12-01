package se.kth.mohosm.ttt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import java.util.ArrayList;

import se.kth.mohosm.ttt.model.GameSettings;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String LOG_TAG =
            SettingsFragment.class.getSimpleName();


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
        SwitchPreferenceCompat audioSwitch = findPreference("audio_stimuli");
        SwitchPreferenceCompat patternSwitch = findPreference("pattern_stimuli");

        assert audioSwitch != null;
        audioSwitch.setDefaultValue(false);
        assert patternSwitch != null;
        patternSwitch.setDefaultValue(false);

        SeekBarPreference seekBar = findPreference("time_interval_component");

        Log.d(LOG_TAG, "This is the preference of time_interval_component max value:" + seekBar.getMax());
        seekBar.setUpdatesContinuously(true);
        seekBar.setSeekBarIncrement(100);
        seekBar.setShowSeekBarValue(true);
        seekBar.setMax(5000);
        seekBar.setMin(500);
        seekBar.setValue(2500);

        ListPreference drop = findPreference("dropdown");
        drop.setEntries(new String[]{"10", "20", "30", "40"});
        drop.setEntryValues(new String[]{"10", "20", "30", "40"});
        drop.setDefaultValue("30");

        Preference listPreference = getPreferenceManager().findPreference("dropdown");
        assert listPreference != null;
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedNrOfEvents = (String) newValue;
            GameSettings.setNrOfEvents(Integer.parseInt(selectedNrOfEvents));
            Log.d(LOG_TAG, String.valueOf(GameSettings.getNrOfEvents()));
            Log.d(LOG_TAG, selectedNrOfEvents + " inside the onPreferenceChange");
            return true;
        });

        seekBar.setOnPreferenceChangeListener((preference, newValue) -> {
            int selectedTimeBetweenEvents = (Integer) newValue;
            GameSettings.setTimeBetweenEvents(selectedTimeBetweenEvents);
            Log.d(LOG_TAG, GameSettings.getTimeBetweenEvents() + " Inside of listener");
            return true;
        });

        audioSwitch.setOnPreferenceClickListener(preference -> {
            audioSwitch.setChecked(audioSwitch.isChecked());

            GameSettings.setAudioStimuli(audioSwitch.isChecked());
            Log.d(LOG_TAG,String.valueOf(GameSettings.isAudioStimuli()));
            return true;
        });

        patternSwitch.setOnPreferenceClickListener(preference -> {
            patternSwitch.setChecked(patternSwitch.isChecked());

            GameSettings.setPatternStimuli(patternSwitch.isChecked());
            Log.d(LOG_TAG,String.valueOf(GameSettings.isPatternStimuli()));
            return true;
        });
    }
}