package se.kth.mohosm.ttt;

import static se.kth.mohosm.ttt.model.TicLogic.Player;
import static se.kth.mohosm.ttt.model.TicLogic.SIZE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import se.kth.mohosm.ttt.model.GameSettings;
import se.kth.mohosm.ttt.model.TicLogic;
import se.kth.mohosm.ttt.utils.AnimationUtils;
import se.kth.mohosm.ttt.utils.TextToSpeechUtil;
import se.kth.mohosm.ttt.utils.UiUtils;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();

    private TicLogic ticLogic;

    private ImageView[] imageViews;
    private Drawable crossDrawable, noughtDrawable;

    private TextToSpeechUtil textToSpeechUtil;

    private TextView currentEventsCounterTV;
    private TextView valueOfNTV;
    private TextView selectedStimuliTV;
    private TextView timeBetweenEventsTV;

    @SuppressLint({"CutPasteId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ui stuff
        setContentView(R.layout.activity_main);
        imageViews = loadReferencesToImageViews();
        findViewById(R.id.start_btn).setOnClickListener(v -> onGameRestart());
        textToSpeechUtil = new TextToSpeechUtil();  // also part of the user interface(!)
        // load drawables (images)
        Resources resources = getResources();
        crossDrawable = ResourcesCompat.getDrawable(resources, R.drawable.cross, null);
        noughtDrawable = ResourcesCompat.getDrawable(resources, R.drawable.nought, null);

        ticLogic = TicLogic.getInstance(); // singleton

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String dropdown = preferences.getString("dropdown", "");

        currentEventsCounterTV = (TextView) findViewById(R.id.curr_nr_events_text);
        valueOfNTV = (TextView) findViewById(R.id.value_of_n);
        selectedStimuliTV = (TextView) findViewById(R.id.sel_stimuli_text);
        timeBetweenEventsTV = (TextView) findViewById(R.id.time_events_text);

        //default and initial gameSettings
        GameSettings.setAudioStimuli(false);
        GameSettings.setPatternStimuli(false);
        GameSettings.setNrOfEvents(30);
        GameSettings.setTimeBetweenEvents(2500);
        GameSettings.setValueOfN(1);

        currentEventsCounterTV.setText("Current number of events: " + "0 of " + GameSettings.getNrOfEvents());
        valueOfNTV.setText("Value of n: " + GameSettings.getValueOfN());
        selectedStimuliTV.setText("Selected: No stimuli selected");
        timeBetweenEventsTV.setText("Time between events: " + GameSettings.getTimeBetweenEvents() + "ms");

        //checks whether phone is flipped or not
        isFlipped(savedInstanceState);


//        if (GameSettings.isAudioStimuli()){
//            selectedStimuli.setText("Selected: Audio stimuli");
//        }
//        if(GameSettings.isPatternStimuli()){
//            selectedStimuli.setText("Selected: Pattern stimuli");
//        }
//        else{
//            selectedStimuli.setText("Selected: No stimuli selected");
//        }
        updateImageViews(null); // game might already be started, so update image views
    }

    /**
     * Saves the states, meaning saving the data so that it can be used in portrait/land mode
     *
     * @param savedInstanceState
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // get data from textViews
        String valueOfN = valueOfNTV.getText().toString();
        String currentEventsCounter = currentEventsCounterTV.getText().toString();
        String timeBetweenEvents = timeBetweenEventsTV.getText().toString();
        String selectedStimuli = selectedStimuliTV.getText().toString();

        // tests so we can see that the right data is being saved in the later stage
        Log.d(LOG_TAG, valueOfN);
        Log.d(LOG_TAG, currentEventsCounter);
        Log.d(LOG_TAG, timeBetweenEvents);
        Log.d(LOG_TAG, selectedStimuli);

        savedInstanceState.putString("value_of_n", valueOfN);
        savedInstanceState.putString("curr_nr_events_text", currentEventsCounter);
        savedInstanceState.putString("time_events_text", timeBetweenEvents);
        savedInstanceState.putString("sel_stimuli_text", selectedStimuli);
    }


    /**
     * Checks if the phone has flipped from portrait to horizon and vice versa
     * If indeed flipped save the current state so that it does not perish when the application stops, creates and starts
     *
     * @param savedInstanceState
     */
    public void isFlipped(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            // GameSettings

            String savedValueOfN = savedInstanceState.getString("value_of_n");
            String savedCurrentEventsCounter = savedInstanceState.getString("curr_nr_events_text");
            String savedSelectedStimuli = savedInstanceState.getString("time_events_text");
            String savedTimeBetweenEvents = savedInstanceState.getString("sel_stimuli_text");
            // tests so we can see that the right data is being set in the textViews
            Log.d(LOG_TAG, savedValueOfN);
            Log.d(LOG_TAG, savedCurrentEventsCounter);
            Log.d(LOG_TAG, savedSelectedStimuli);
            Log.d(LOG_TAG, savedTimeBetweenEvents);

            currentEventsCounterTV.setText(savedCurrentEventsCounter);
            valueOfNTV.setText(savedValueOfN);
            selectedStimuliTV.setText(savedSelectedStimuli);
            timeBetweenEventsTV.setText(savedTimeBetweenEvents);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(LOG_TAG, "Inside the onOptionsIntemSelected");
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // NB! Cancel the current and queued utterances, then shut down the service to
    // de-allocate resources
    @Override
    protected void onPause() {
        textToSpeechUtil.shutdown();
        super.onPause();
    }

    // Initialize the text-to-speech service - we do this initialization
    // in onResume because we shutdown the service in onPause
    @Override
    protected void onResume() {
        super.onResume();
        textToSpeechUtil.initialize(getApplicationContext());
    }

    public void onImageViewTap(View tappedView) {
        // the image views in the activity_main file is marked with tags, 0-8
        int index = Integer.parseInt(tappedView.getTag().toString());
        // calculate corresponding row and column
        int row = index / SIZE;
        int col = index % SIZE;
        Log.i("Tag", "" + row + "," + col);

        // update logic
        if (ticLogic.isLegalMove(row, col)) {
            ticLogic.makeMove(row, col);
            // update the image views
            updateImageViews(tappedView);
            if (ticLogic.isDecided()) {
                String msg;
                switch (ticLogic.getWinner()) {
                    case CROSS:
                        msg = "Cross won, congratulations";
                        break;
                    case NOUGHT:
                        msg = "Nought won, congratulations";
                        break;
                    default:
                        msg = "A draw, try again";
                }
                UiUtils.createDialog(this, "Game ower", msg).show();
                textToSpeechUtil.speakNow(msg);
            }
        }
    }

    public void onGameRestart() {
        ticLogic.reset();
        for (ImageView imageView : imageViews) {
            imageView.setImageDrawable(null);
        }
        textToSpeechUtil.speakNow("Restarting");
    }

    // ui helpers
    private void updateImageViews(View tappedView) {
        Player[][] board = ticLogic.getCopyOfBoard();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Drawable img = null;
                switch (board[r][c]) {
                    case CROSS:
                        img = crossDrawable;
                        break;
                    case NOUGHT:
                        img = noughtDrawable;
                        break;
                    case NONE:
                        img = null;
                }
                int index = r * SIZE + c;
                imageViews[index].setImageDrawable(img); // index in array imageViews
                if (imageViews[index] == tappedView) {
                    AnimationUtils.fadeInImageView(tappedView);
                }
            }
        }
    }

    // load references to, and add listener on, all image views
    private ImageView[] loadReferencesToImageViews() {
        // well, it would probably be easier (for a larger matrix) to create
        // the views in Java code and then add them to the appropriate layout
        ImageView[] imgViews = new ImageView[SIZE * SIZE];
        imgViews[0] = findViewById(R.id.imageView0);
        imgViews[1] = findViewById(R.id.imageView1);
        imgViews[2] = findViewById(R.id.imageView2);
        imgViews[3] = findViewById(R.id.imageView3);
        imgViews[4] = findViewById(R.id.imageView4);
        imgViews[5] = findViewById(R.id.imageView5);
        imgViews[6] = findViewById(R.id.imageView6);
        imgViews[7] = findViewById(R.id.imageView7);
        imgViews[8] = findViewById(R.id.imageView8);
        // add listener
        View.OnClickListener imgViewListener = view -> onImageViewTap(view);
        for (ImageView imgView : imgViews) {
            imgView.setOnClickListener(imgViewListener);
        }
        return imgViews;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // to prevent the staus bar from reappearing in landscape mode when,
        // for example, a dialog is shown
        if (hasFocus) UiUtils.setStatusBarHiddenInLandscapeMode(this);
    }
}