package se.kth.anderslm.ttt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // callback methods for user controls (button and image views)
    public void playerTap(View view) {

    }

    public void onGameRestart(View view) {

    }
}