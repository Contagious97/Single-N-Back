package se.kth.anderslm.ttt.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Android text-to-speech
 * NB! deallocate/allocate in onPause/onResume
 */
public class TextToSpeechUtil {

    private TextToSpeech textToSpeech = null;
    private static final int utteranceId = 42;

    public void initialize(Context appContext) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(appContext,
                    status -> {
                        if (status == TextToSpeech.SUCCESS) {
                            textToSpeech.setLanguage(Locale.UK);
                        }
                    });
        }
    }

    public void speakNow(String utterance) {
        if (textToSpeech != null) {
            textToSpeech.speak(utterance, TextToSpeech.QUEUE_FLUSH,
                    null, "" + utteranceId);
        }
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}

