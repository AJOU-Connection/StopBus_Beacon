package com.example.kimheeyeon.beacontest;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
//speach
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Speach extends Activity implements OnInitListener {
    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_activity);

        myTTS = new TextToSpeech(this, this);
    }

    public void onInit(int status) {
        String myText1 = "안녕하세요 안드로이드 블로그 녹두장군 입니다.";
        String myText2 = "말하는 스피치 입니다.";
        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
        myTTS.speak(myText2, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }
}
