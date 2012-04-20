package ru.spbau.ourpedometer;

import android.speech.tts.TextToSpeech;

/**
 * User: Alina
 * Date: 20.04.12
 * Time: 14:30
 */
public class Speaker {
    private TextToSpeech textToSpeech;

    public void speak(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }
}
