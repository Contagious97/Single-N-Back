package se.kth.mohosm.ttt;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

public class SettingsActivity extends AppCompatActivity {
//    private SeekBar seekBar;
//
//    private TextView seekBarTextView;

    //seekbar data
//    private int seekBarValue;
//    private final int seekBarIncrements = 200;
//    private SeekBarPreference seekBarPreference;
//
//    private TextView valueOfNTextView;
//
//    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
                setContentView(R.layout.settings_activity);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.settings_activity);
//
//        getSupportActionBar().setTitle("Settings");




//        if (findViewById(R.id.settings) != null){
//            if (savedInstanceState == null){
//                getFragmentManager().beginTransaction().add(R.id.settings,
//                        new SettingsFragment()).commit();
//            }
//        }



//        seekBar = (SeekBar) findViewById(R.id.seekbar);
//        valueOfNTextView = (TextView) findViewById(R.id.nr_events_view);
//        seekBarTextView = (TextView) findViewById(R.id.nr_of_events_view);
//        dropdown = (Spinner) findViewById(R.id.spinner);
//
//        String[] items = new String[]{"10 Events", "20 Events", "30 Events", "40 Events"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                seekBarTextView.setText(progress + " seconds between events");
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.settings, new SettingsFragment())
//                    .commit();
//        }
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }


//    public static class SettingsFragment extends PreferenceFragmentCompat {
//        @Override
//        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey);
//        }
//    }
}