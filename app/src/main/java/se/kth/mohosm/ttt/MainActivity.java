package se.kth.mohosm.ttt;

import static se.kth.mohosm.ttt.model.GameLogic.SIZE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import java.util.Timer;
import java.util.TimerTask;

import se.kth.mohosm.ttt.model.GameLogic;
import se.kth.mohosm.ttt.model.GameSettings;
import se.kth.mohosm.ttt.utils.TextToSpeechUtil;

public class MainActivity extends AppCompatActivity {


    private GameLogic gameLogic;

    private ImageView[] imageViews;
    private Drawable blueDrawable;
    private TextView currentEventsCounterTV;
    private TextView valueOfNTV;
    private TextView selectedStimuliTV;
    private TextView timeBetweenEventsTV;
    private TextView scoreTV;

    private Button position_match_btn;
    private Button audio_match_btn;

    private TextToSpeechUtil textToSpeechUtil;

    private Timer msgTimer;
    private Handler handler;

    private int noOfMsgs;
    private static final String TAG = "MainActivity";
    private int score;
    private boolean visualStimuliPressed;
    private boolean auditoryStimuliPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize UI components
        setContentView(R.layout.activity_main);
        imageViews = loadReferencesToImageViews();
        findViewById(R.id.restart_btn).setOnClickListener(v -> onGameRestart());
        findViewById(R.id.position_match_btn).setOnClickListener(v -> onVisualGuess());
        findViewById(R.id.start_btn).setOnClickListener(v -> onGameStart());
        findViewById(R.id.audio_match_btn).setOnClickListener(v -> onAuditoryGuess());

        textToSpeechUtil = new TextToSpeechUtil();  // also part of the user interface(!)
        currentEventsCounterTV = (TextView) findViewById(R.id.curr_nr_events_text);
        valueOfNTV = (TextView) findViewById(R.id.value_of_n);
        selectedStimuliTV = (TextView) findViewById(R.id.sel_stimuli_text);
        timeBetweenEventsTV = (TextView) findViewById(R.id.time_events_text);
        scoreTV = (TextView) findViewById(R.id.current_score);

        position_match_btn = (Button) findViewById(R.id.position_match_btn);
        audio_match_btn = (Button) findViewById(R.id.audio_match_btn);
        // load drawables (images)
        Resources resources = getResources();
        blueDrawable = ResourcesCompat.getDrawable(resources,R.drawable.img_blue,null);

        //load preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String dropdown = preferences.getString("dropdown", "");




        //default and initial gameSettings
        GameSettings.setAudioStimuli(false);
        GameSettings.setPatternStimuli(true);
        GameSettings.setNrOfEvents(16);
        GameSettings.setTimeBetweenEvents(2500);
        GameSettings.setValueOfN(2);

        currentEventsCounterTV.setText(R.string.number_of_events);
        currentEventsCounterTV.append(" 0 of " + GameSettings.getNrOfEvents());
        valueOfNTV.setText(R.string.value_of_n);
        valueOfNTV.append("  " + GameSettings.getValueOfN());
        selectedStimuliTV.setText(R.string.selected_stimuli);
        selectedStimuliTV.append(" Visual Stimuli");
        timeBetweenEventsTV.setText(R.string.time_between_events);
        timeBetweenEventsTV.append(GameSettings.getTimeBetweenEvents() + " ms");

        msgTimer = null;
        handler = new Handler();
        gameLogic = GameLogic.getInstance();
        score = 0;
        updateImageViews(null);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // get data from textViews
        String valueOfN = valueOfNTV.getText().toString();
        String currentEventsCounter = currentEventsCounterTV.getText().toString();
        String timeBetweenEvents = timeBetweenEventsTV.getText().toString();
        String selectedStimuli = selectedStimuliTV.getText().toString();

        // tests so we can see that the right data is being saved in the later stage
        Log.d(TAG, valueOfN);
        Log.d(TAG, currentEventsCounter);
        Log.d(TAG, timeBetweenEvents);
        Log.d(TAG, selectedStimuli);

        savedInstanceState.putString("value_of_n", valueOfN);
        savedInstanceState.putString("curr_nr_events_text", currentEventsCounter);
        savedInstanceState.putString("time_events_text", timeBetweenEvents);
        savedInstanceState.putString("sel_stimuli_text", selectedStimuli);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "Inside the onOptionsIntemSelected");
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateScore(){
        scoreTV.setText(R.string.score_text);
        scoreTV.append(" ");
        scoreTV.append(String.valueOf(score));
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
        Log.i(TAG,"On pause");
        textToSpeechUtil.shutdown();
        super.onPause();
        cancelTimer();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    // Initialize the text-to-speech service - we do this initialization
    // in onResume because we shutdown the service in onPause
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"On resume");

        Log.i(TAG,"Game started?: " + gameLogic.isGameStarted());
        if (gameLogic.isGameStarted()){
            updateCurrentEventCounter();
            valueOfNTV.setText(R.string.value_of_n + GameSettings.getValueOfN());
            selectedStimuliTV.setText(R.string.selected_stimuli);
            timeBetweenEventsTV.setText(R.string.time_between_events + GameSettings.getTimeBetweenEvents());
            timeBetweenEventsTV.append(" ms");
            boolean started = startTimer();
            if (!started)
                Log.i(TAG,"Timer already started");

        }

        if (GameSettings.isPatternStimuli())
            audio_match_btn.setVisibility(View.INVISIBLE);
        else position_match_btn.setVisibility(View.INVISIBLE);



        textToSpeechUtil.initialize(getApplicationContext());
    }

    private void updateCurrentEventCounter(){
        currentEventsCounterTV.setText(R.string.number_of_events);
        currentEventsCounterTV.append(" ");
        int currentRound = gameLogic.getCurrPosition();
        if (currentRound > 29)
            currentRound = 30;
        currentEventsCounterTV.append(String.valueOf(currentRound) + "of ");
        currentEventsCounterTV.append(String.valueOf(GameSettings.getNrOfEvents()));
    }
    private void updateImageViews(View view){
        if (gameLogic.isGameStarted()){
            if (gameLogic.getPosition() > -1)
                imageViews[gameLogic.getPosition()].setImageDrawable(blueDrawable);
        } else resetVisualStimuli();
    }

    private void resetVisualStimuli(){
        for (ImageView imageView:
             imageViews) {
            imageView.setImageDrawable(null);
        }
    }

    private void onGameStart(){
        gameLogic.start();
        startTimer();
        score = 0;
        resetVisualStimuli();
    }

    public void onGameRestart() {

        textToSpeechUtil.speakNow("Restarting");
        cancelTimer();
        gameLogic.reset();
        //gameLogic.start();
        startTimer();
        score = 0;
        resetVisualStimuli();
    }

    public void onVisualGuess(){

        visualStimuliPressed = true;

        /*if (gameLogic.isCorrectGuess()){
            score++;
        }*/
    }

    public void onAuditoryGuess(){
        auditoryStimuliPressed = true;
    }


    // load references to, and add listener on, all image views
    private ImageView[] loadReferencesToImageViews() {

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

        return imgViews;
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i(TAG,"Window focus changed");
        if(hasFocus) UiUtils.setStatusBarHiddenInLandscapeMode(this);
    }*/

    private class MsgTimerTask extends TimerTask {
        public void run() {
            noOfMsgs++;

            if (gameLogic.isGameStarted()){
                if (visualStimuliPressed){
                    if (gameLogic.isCorrectGuess()){
                        score++;
                    }

                    visualStimuliPressed = false;
                } else if (auditoryStimuliPressed){
                    if (gameLogic.isCorrectGuess())
                        score++;
                    auditoryStimuliPressed = false;
                }

                if (gameLogic.gameOver()) {

                    textToSpeechUtil.speakNow("Your score: " + score);
                    Log.i(TAG,"Score: "+score);
                    cancelTimer();
                } else {
                    gameLogic.makeMove();
                    if (gameLogic.isAuditory()){
                        String letter = String.valueOf(gameLogic.getLetter());
                        textToSpeechUtil.speakNow(letter);
                        Log.i(TAG,"Letter: " + letter);
                    } else {
                        String msg = "Curr pos: " + gameLogic.getPosition();
                        Log.i("MsgTask", msg);
                        if (gameLogic.getPrevValue() > -1)
                            imageViews[gameLogic.getPrevValue()].setImageDrawable(null);

                        imageViews[gameLogic.getPosition()].setImageDrawable(blueDrawable);
                    }
                }
            }

            // post message to main thread
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"Nr msgs" + noOfMsgs);
                    updateCurrentEventCounter();
                    updateScore();
                    //msgTimer.schedule(new MsgTimerTask(),50,500);
                }
            });
        }
    }

    private boolean startTimer() {
        if (msgTimer == null) { // first, check if task is already running
            noOfMsgs = 0;
            msgTimer = new Timer();
            // schedule a new task: task , delay, period (milliseconds)
            msgTimer.schedule(new MsgTimerTask(), 3000, GameSettings.getTimeBetweenEvents());

            return true; // new task started
        }
        return false;
    }

    private boolean cancelTimer() {
        if (msgTimer != null) {
            msgTimer.cancel();
            msgTimer = null;
            Log.i("MsgTask", "timer canceled");
            return true; // task canceled
        }
        return false;
    }
}