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
        audioSwitch.setChecked(GameSettings.isAudioStimuli());
        assert patternSwitch != null;
        patternSwitch.setChecked(GameSettings.isPatternStimuli());

        SeekBarPreference seekBar = findPreference("time_interval_component");

        Log.d(LOG_TAG, "This is the preference of time_interval_component max value:" + seekBar.getMax());
        seekBar.setUpdatesContinuously(true);
        seekBar.setSeekBarIncrement(100);
        seekBar.setShowSeekBarValue(true);
        seekBar.setMax(5000);
        seekBar.setMin(500);
        seekBar.setValue(GameSettings.getTimeBetweenEvents());

        ListPreference listEventRounds = findPreference("list");
        listEventRounds.setEntries(new String[]{"10", "20", "30", "40"});
        listEventRounds.setEntryValues(new String[]{"10", "20", "30", "40"});
        listEventRounds.setDefaultValue(GameSettings.getNrOfEvents());

        DropDownPreference dropdownNValue = findPreference("dropdown");
        dropdownNValue.setEntries(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        dropdownNValue.setEntryValues(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        dropdownNValue.setDefaultValue(GameSettings.getValueOfN());


        dropdownNValue.setOnPreferenceChangeListener((preference, newValue) -> {
            String valueOfN = (String) newValue;
            GameSettings.setValueOfN(Integer.parseInt(valueOfN));
            Log.d(LOG_TAG, String.valueOf(GameSettings.getValueOfN()));
            return true;
        });

        listEventRounds.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedNrOfEvents = (String) newValue;
            GameSettings.setNrOfEvents(Integer.parseInt(selectedNrOfEvents));
            Log.d(LOG_TAG, String.valueOf(GameSettings.getNrOfEvents()));
            return true;
        });

        seekBar.setOnPreferenceChangeListener((preference, newValue) -> {
            int selectedTimeBetweenEvents = (Integer) newValue;
            GameSettings.setTimeBetweenEvents(selectedTimeBetweenEvents);
            Log.d(LOG_TAG, GameSettings.getTimeBetweenEvents() + " Inside of listener");
            return true;
        });

        audioSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                boolean newValue = (boolean) o;

                audioSwitch.setChecked(newValue);
                patternSwitch.setChecked(!newValue);

                GameSettings.setAudioStimuli(newValue);
                GameSettings.setPatternStimuli(!newValue);

                return true;
            }


            /*if (patternSwitch.isEnabled()) {
                patternSwitch.setEnabled(false);
                audioSwitch.setEnabled(true);

            } else {
                patternSwitch.setEnabled(true);
                audioSwitch.setChecked(audioSwitch.isChecked());
                GameSettings.setAudioStimuli(audioSwitch.isChecked());
                Log.d(LOG_TAG, String.valueOf(GameSettings.isAudioStimuli()));
            }
            return true;*/
        });


        patternSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean newValue = (boolean) o;

                patternSwitch.setChecked(newValue);
                audioSwitch.setChecked(!newValue);

                GameSettings.setPatternStimuli(newValue);
                GameSettings.setAudioStimuli(!newValue);
                return true;
            }

           /*         patternSwitch.setChecked(!patternSwitch.isChecked());
            if (patternSwitch.isChecked()){
                Log.i(LOG_TAG,"visual checked");
                patternSwitch.setChecked(false);
            } else if(!patternSwitch.isChecked()){
                patternSwitch.setChecked(true);
            }
            GameSettings.setPatternStimuli(patternSwitch.isChecked());
            Log.i(LOG_TAG,"Visual stimuli? " + GameSettings.isPatternStimuli());

            if (audioSwitch.isEnabled()) {
                audioSwitch.setEnabled(false);
                patternSwitch.setChecked(true);

            } else {
                audioSwitch.setEnabled(true);
                patternSwitch.setChecked(patternSwitch.isChecked());
                GameSettings.setPatternStimuli(patternSwitch.isChecked());
                Log.d(LOG_TAG, String.valueOf(GameSettings.isPatternStimuli()));
            }
            return true;*/
        });
    }
}
