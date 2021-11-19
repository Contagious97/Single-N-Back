package se.kth.anderslm.ttt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

import se.kth.anderslm.ttt.model.TicLogic;
import static se.kth.anderslm.ttt.model.TicLogic.*;

public class MainActivity extends AppCompatActivity {

    private TicLogic ticLogic;

    private ImageView[] imageViews;
    private Drawable crossDrawable, noughtDrawable;

    private TextToSpeech textToSpeach;
    private static final int utteranceId = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViews = loadReferencesToImageViews();
        // load drawables (images)
        Resources resources = getResources();
        crossDrawable = ResourcesCompat.getDrawable(resources, R.drawable.cross, null);
        noughtDrawable = ResourcesCompat.getDrawable(resources, R.drawable.nought, null);
        findViewById(R.id.restartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGameRestart();
            }
        });

        ticLogic = TicLogic.getInstance(); // singleton
        updateImageViews(); // game might already be started, so update image views
    }

    // NB! Cancel the current and queued utterances, then shut down the service to
    // de-allocate resources
    @Override
    protected void onPause() {
        if (textToSpeach != null) {
            textToSpeach.stop();
            textToSpeach.shutdown();
        }
        super.onPause();
    }

    // Initialize the text-to-speech service - we do this initialization
    // in onResume because we shutdown the service in onPause
    @Override
    protected void onResume() {
        super.onResume();
        textToSpeach = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            textToSpeach.setLanguage(Locale.UK);
                        }
                    }
                });
    }

    public void onImageViewTap(View view) {
        // the image views in the activity_main file is marked with tags, 0-8
        int index = Integer.parseInt(view.getTag().toString());
        // calculate corresponding row and column
        int row = index / SIZE;
        int col = index % SIZE;
        Log.i("Tag", "" + row + "," + col);

        // update logic
        if (ticLogic.isLegalMove(row, col)) {
            ticLogic.makeMove(row, col);
            // update the image views
            updateImageViews();
            // TODO: Animate view

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
                TicActivityUtils.createDialog(this,"Game ower", msg).show();
                sayIt(msg);
            }
        }
    }

    public void onGameRestart() {
        ticLogic.reset();
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setImageDrawable(null);
        }
        sayIt("Restarting");
    }

    // text-to-speech - dellocate/allocate in onPause/onResume
    private void sayIt(String utterance) {
        textToSpeach.speak(utterance, TextToSpeech.QUEUE_FLUSH,
                null, "" + utteranceId);
    }

    // ui helpers
    private void updateImageViews() {
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
                imageViews[r * SIZE + c].setImageDrawable(img); // index in array imageViews
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
        View.OnClickListener imgViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageViewTap(view);
            }
        };
        for(int i = 0; i <imgViews.length; i++){
            imgViews[i].setOnClickListener(imgViewListener);
        }
        return imgViews;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // to prevent the status bar to reappear in landscape mode when,
        // for example, a dialog is shown
        if(hasFocus) TicActivityUtils.setStatusBarHiddenInLandscapeMode(this);
        Log.i("DEBUG","onWindowFocusChanged");
    }
}