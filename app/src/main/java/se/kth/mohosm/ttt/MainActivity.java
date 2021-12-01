package se.kth.mohosm.ttt;

import static se.kth.mohosm.ttt.model.TicLogic.Player;
import static se.kth.mohosm.ttt.model.TicLogic.SIZE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

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

    private SeekBar seekBar;

    private TextView seekBarTextView;

    private TextView valueOfNTextView;

    private Spinner dropdown;

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

        updateImageViews(null); // game might already be started, so update image views
    }

    public void launchSettingsActivity(View view) {
        Log.d(LOG_TAG, "Settings in list clicked!");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_view,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(LOG_TAG,"Inside the onOptionsIntemSelected");
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
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
                UiUtils.createDialog(this,"Game ower", msg).show();
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
                if(imageViews[index]== tappedView) {
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
        if(hasFocus) UiUtils.setStatusBarHiddenInLandscapeMode(this);
    }
}