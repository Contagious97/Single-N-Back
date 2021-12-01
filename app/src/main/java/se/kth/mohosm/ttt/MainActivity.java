package se.kth.mohosm.ttt;

import static se.kth.mohosm.ttt.model.TicLogic.Player;
import static se.kth.mohosm.ttt.model.TicLogic.SIZE;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Timer;
import java.util.TimerTask;

import se.kth.mohosm.ttt.model.GameLogic;
import se.kth.mohosm.ttt.model.TicLogic;
import se.kth.mohosm.ttt.utils.AnimationUtils;
import se.kth.mohosm.ttt.utils.TextToSpeechUtil;
import se.kth.mohosm.ttt.utils.UiUtils;

public class MainActivity extends AppCompatActivity {

    private TicLogic ticLogic;

    private GameLogic gameLogic;

    private ImageView[] imageViews;
    private Drawable crossDrawable, noughtDrawable, blueDrawable;

    private TextToSpeechUtil textToSpeechUtil;

    private Timer msgTimer;
    private Handler handler;

    private int noOfMsgs;
    private static final String TAG = "MainActivity";
    private int score;
    private final int rounds = 6;
    private boolean visualStimuliPressed;
    private boolean gameStarted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ui stuff
        setContentView(R.layout.activity_main);
        imageViews = loadReferencesToImageViews();
        findViewById(R.id.restart_btn).setOnClickListener(v -> onGameRestart());
        findViewById(R.id.position_match_btn).setOnClickListener(v -> onGuess());
        findViewById(R.id.start_btn).setOnClickListener(v -> onGameStart());

        textToSpeechUtil = new TextToSpeechUtil();  // also part of the user interface(!)
        // load drawables (images)
        Resources resources = getResources();

        blueDrawable = ResourcesCompat.getDrawable(resources,R.drawable.img_blue,null);

        msgTimer = null;
        handler = new Handler();
        gameLogic = new GameLogic(2,rounds);
        score = 0;
        gameStarted = false;
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

    // Initialize the text-to-speech service - we do this initialization
    // in onResume because we shutdown the service in onPause
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"On resume");
        if (gameStarted){
            boolean started = startTimer();
            if (!started)
                Log.i(TAG,"Timer already started");
        }
        textToSpeechUtil.initialize(getApplicationContext());
    }

    private void onGameStart(){
        gameLogic.start();
        startTimer();
        gameStarted = true;
        score = 0;
    }

    public void onGameRestart() {

        textToSpeechUtil.speakNow("Restarting");
        cancelTimer();
        gameLogic.reset();
        gameLogic.start();
        startTimer();
        score = 0;
    }

    public void onGuess(){

        visualStimuliPressed = true;

        /*if (gameLogic.isCorrectGuess()){
            score++;
        }*/
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

        return imgViews;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // to prevent the staus bar from reappearing in landscape mode when,
        // for example, a dialog is shown
        Log.i(TAG,"Window focus changed");
        if(hasFocus) UiUtils.setStatusBarHiddenInLandscapeMode(this);
    }

    private class MsgTimerTask extends TimerTask {
        public void run() {
            noOfMsgs++;

            if (visualStimuliPressed){
                if (gameLogic.isCorrectGuess()){
                    score++;
                }

                visualStimuliPressed = false;
            }

            if (gameLogic.gameOver()) {

                textToSpeechUtil.speakNow("Your score: " + score);
                Log.i(TAG,"Score: "+score);
                cancelTimer();
            } else {
                gameLogic.makeMove();
                String msg = "Curr pos: " + gameLogic.getPosition();
                Log.i("MsgTask", msg);
                if (gameLogic.getPrevValue() > -1)
                    imageViews[gameLogic.getPrevValue()].setImageDrawable(null);

                imageViews[gameLogic.getPosition()].setImageDrawable(blueDrawable);
            }


            // post message to main thread
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"Nr msgs" + noOfMsgs);

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
            msgTimer.schedule(new MsgTimerTask(), 4000, 2000);
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