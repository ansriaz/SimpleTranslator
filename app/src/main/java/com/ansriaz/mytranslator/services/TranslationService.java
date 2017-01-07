package com.ansriaz.mytranslator.services;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by ansriaz on 04/01/2017.
 */

public class TranslationService {

    String langFrom;
    String langTo;
    String textToTranslate;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public TranslationService(){

    }

    public void setLanguageFrom(String langFrom) {
        this.langFrom = langFrom;
    }

    public void setLanguageTo(String langTo) {
        this.langTo = langTo;
    }

    public String setTranslateText(String textToTranslate) {
        this.textToTranslate = textToTranslate;
        return textToTranslate;
    }

    public void SendHttpGetRequest(AsyncHttpResponseHandler asyncHttpResponseHandler){
        String url = "https://glosbe.com/gapi/translate?from=" + langFrom + "&dest=" +
                langTo + "&format=json&phrase=" + textToTranslate + "&pretty=true";
        Log.i("URL",url);
        client.get(url, null, asyncHttpResponseHandler);
    }
}
