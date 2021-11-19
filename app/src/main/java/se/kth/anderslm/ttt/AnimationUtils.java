package se.kth.anderslm.ttt;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class AnimationUtils {

    public static void fadeInImageView(View viewToFadeIn) {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setInterpolator(new LinearInterpolator());

        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float newAlpha = (float) valueAnimator.getAnimatedValue();
                viewToFadeIn.setAlpha(newAlpha);
            }
        });
        alphaAnimator.start();
    }
}
