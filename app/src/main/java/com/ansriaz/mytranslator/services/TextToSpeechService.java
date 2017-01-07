package com.ansriaz.mytranslator.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.ansriaz.mytranslator.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;

/**
 * Created by ansriaz on 04/01/2017.
 */

public class TextToSpeechService implements TextToSpeech.OnInitListener {

    Context context;
//    TextToSpeech tts;
    String lang;
    String textToSpeak;
    private static AsyncHttpClient client = new AsyncHttpClient();

    private MediaPlayer mMediaPlayer;

    public TextToSpeechService(Context context) {
        this.context = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }
        });

        // Android Built-in TextToSpeech library
//        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    tts.setLanguage(Locale.US);
//                }
//            }
//        });
    }

    public void Play(String stringToSpeak, String language) {
        if(language.equals("ur")){
            Toast.makeText(context, context.getString(R.string.langNotFound),Toast.LENGTH_LONG).show();
            return;
        }
        String url = "http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&client=tw-ob&q="
                + stringToSpeak +"&tl="+language;
        try {
            // Android built-in TextToSpeech
            //tts.speak(stringToSpeak, TextToSpeech.QUEUE_FLUSH, null);

            // TextToSpeech via url
            mMediaPlayer.setDataSource(context, Uri.parse(url));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            mMediaPlayer.reset();
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.langNotFound),Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            mMediaPlayer.reset();
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.langNotFound),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            mMediaPlayer.reset();
            e.printStackTrace();
            Toast.makeText(context, "Error occurred.",Toast.LENGTH_LONG).show();
        }
    }

    public void setLanguage(String lang) {
        this.lang = lang;
    }

    public void setText(String textToSpeak) {
        Log.i("textToSpeak",textToSpeak);
        this.textToSpeak = textToSpeak;
    }

    public void PlayVoice(AsyncHttpResponseHandler asyncHttpResponseHandler){
        String url = "http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&textlen=32&client=tw-ob&q=hello&tl=En-gb";
//        url = "http://translate.google.com/translate_tts?tl=" +
//                lang + "&client=t&key=AIzaSyB9g8rE8UefsoN6PJfJYAkDqfLJDCNvzoE&q=" + textToSpeak;
        url = "http://translate.google.com/translate_tts?ie=UTF-8&total=1&idx=0&client=tw-ob&q=" + textToSpeak + "&tl="+lang;
        Log.i("URL",url);
        client.get(url, null, asyncHttpResponseHandler);
    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR) {
//            tts.setLanguage(Locale.US);
        } else {
            Log.e("ERROR","TTS doesn't work");
        }
    }
}
