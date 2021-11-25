package se.kth.anderslm.ttt.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.view.View;

public class UiUtils {

    public static Dialog createDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", (dialog, id) -> {
        });
        return builder.create();
    }

    public static void setStatusBarHiddenInLandscapeMode(Activity activity) {
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
}
