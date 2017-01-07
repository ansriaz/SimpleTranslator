package com.ansriaz.mytranslator;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ansriaz.mytranslator.db.AutoCompleteTextDBHelpher;
import com.ansriaz.mytranslator.db.LanguageDBHelper;
import com.ansriaz.mytranslator.services.TextToSpeechService;
import com.ansriaz.mytranslator.services.TranslationService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    String langFrom, langTo, textToTranslate;
    AutoCompleteTextView autoCompleteTextView;
    Spinner spDefaultLanguage;
    Spinner spTranslateLanguage;
    Button btnTranslate;
    Button btnPlay;
    LinearLayout container;
    TextToSpeech tts;
    ScrollView scrollView;
    TextView tvWordToTranslate, tvWordToTranslateWikiLink;

    LanguageDBHelper languageDBHelper;
    AutoCompleteTextDBHelpher autoCompleteTextDBHelpher;
    ArrayList languages;
    ArrayAdapter<String> autoCompleteAdapter;

    TextToSpeechService textToSpeech;
    TranslationService translationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Services initialization
        translationService = new TranslationService();
        textToSpeech = new TextToSpeechService(MainActivity.this);

        // Database helpers
        languageDBHelper = new LanguageDBHelper(this);
        autoCompleteTextDBHelpher = new AutoCompleteTextDBHelpher(this);
        languages = languageDBHelper.getAllLanguages();

        // View fields
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        spDefaultLanguage = (Spinner) findViewById(R.id.spDefaultLanguage);
        spTranslateLanguage = (Spinner) findViewById(R.id.spTranslateLanguage);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        container = (LinearLayout) findViewById(R.id.llContainer);
        btnTranslate = (Button) findViewById(R.id.btnTranslate);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        tvWordToTranslate = (TextView) findViewById(R.id.tvWordToTranslate);
        tvWordToTranslateWikiLink = (TextView) findViewById(R.id.tvWordToTranslateWikiLink);

        // Setting adapters for autoCompletefield and spinners
        setAdapters();
        setValues();
    }

    protected void setAdapters() {

        ArrayList<String> array = new ArrayList<>();
        array.addAll(autoCompleteTextDBHelpher.getAllDataFromDb());

        autoCompleteAdapter = new
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array);

        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.setThreshold(1);

        ArrayAdapter<String> spDefaultLanguageArrayAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, languages);
        spDefaultLanguageArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDefaultLanguage.setAdapter(spDefaultLanguageArrayAdapter);

        ArrayAdapter<String> spTranslateLanguageArrayAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, languages);
        spTranslateLanguageArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTranslateLanguage.setAdapter(spTranslateLanguageArrayAdapter);
    }

    protected void setValues() {

        spDefaultLanguage.setOnItemSelectedListener(this);
        spTranslateLanguage.setOnItemSelectedListener(this);

        // Default selected languages
        spDefaultLanguage.setSelection(1);
        spTranslateLanguage.setSelection(4);

        btnTranslate.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        tvWordToTranslateWikiLink.setOnClickListener(this);
    }

    // Converting response string into pretty GSON=>JSON object to parse and create view
    public void convertResponse(String response)
    {
        // first remove all the old children
        if(((LinearLayout) container).getChildCount() > 0)
            ((LinearLayout) container).removeAllViews();

        // Parse response and create view acordingly
        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(response);

            JsonArray tuc = jsonObject.getAsJsonArray("tuc");
            Log.i("tuc", tuc.toString());

            if (tuc.size() > 0) {

                int translationsCount = 1;
                for (JsonElement t_obj : tuc) {
                    View phraseChileView = getLayoutInflater().inflate(R.layout.linear_row, null);

                    TextView tvLanguage = (TextView) phraseChileView.findViewById(R.id.heading);
                    TextView tvTranslatedText = (TextView) phraseChileView.findViewById(R.id.detail);

                    JsonObject tubObj1 = t_obj.getAsJsonObject();

                    if (tubObj1.has("phrase")) {
                        JsonObject phrase = tubObj1.getAsJsonObject("phrase");
                        Log.i("tuc", phrase.toString());
                        tvLanguage.setText("Translation " + translationsCount + ": " + phrase.get("language").toString().replace("\"", ""));
                        tvTranslatedText.setText(phrase.get("text").toString().replace("\"", ""));
                        container.addView(phraseChileView);

                        if (tubObj1.has("meanings")) {
                            JsonArray meanings = tubObj1.getAsJsonArray("meanings");
                            if (meanings.size() > 0){
                                int i = 1;
                                Log.i("meanings", meanings.toString());

                                for (JsonElement meaning : meanings) {
                                    View child = getLayoutInflater().inflate(R.layout.linear_row, null);

                                    TextView tvHeading = (TextView) child.findViewById(R.id.heading);
                                    TextView tvText = (TextView) child.findViewById(R.id.detail);

                                    tvHeading.setText(Integer.toString(i) + ") Language => " + meaning.getAsJsonObject().get("language").toString() + ":");
                                    tvText.setText(meaning.getAsJsonObject().get("text").toString().replace("\"", ""));

                                    container.addView(child);
                                    i++;
                                }
                            }
                        }
                        if(tubObj1.has("authors")){
                            JsonArray authors = tubObj1.getAsJsonArray("authors");
                            if (authors.size() > 0){
                                if(jsonObject.has("authors")) {
                                    JsonObject authorsInResponseObject = jsonObject.getAsJsonObject("authors");
                                    String author = authors.get(0).toString();
                                    Log.i("authors",author);
                                    if (authorsInResponseObject.has(author)) {
                                        JsonObject auth = authorsInResponseObject.getAsJsonObject(author);
                                        Log.i("auth",auth.get("id").toString());
                                        if(auth.has("id")){
                                            View child = getLayoutInflater().inflate(R.layout.linear_row, null);
                                            TextView tvHeading = (TextView) child.findViewById(R.id.heading);
                                            final TextView tvText = (TextView) child.findViewById(R.id.detail);

                                            tvHeading.setText("Author => " + auth.get("id").toString());
                                            if(!auth.get("U").isJsonNull()) {
                                                String link = auth.get("U").toString().replace("\"", "");
                                                if (link.contains("http")) {
                                                    tvText.setTextColor(Color.BLUE);
                                                    tvText.setText(link);
                                                    tvText.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tvText.getText().toString()));
                                                            startActivity(browserIntent);
                                                        }
                                                    });
                                                }
                                            }
                                            container.addView(child);
                                        }
                                    }
                                }
                            }
                        }
                        View v = new View(this);
                        v.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                5
                        ));
                        v.setPadding(5,0,0,5);
                        v.setBackgroundColor(Color.parseColor("#d3d3d3"));

                        container.addView(v);
                        translationsCount++;
                    }
                }
            } else {
                View child = getLayoutInflater().inflate(R.layout.no_data_found, null);
                TextView tvNoDataFound = (TextView) child.findViewById(R.id.tvNoDataFound);
                tvNoDataFound.setGravity(Gravity.CENTER);
                container.addView(child);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnTranslate:
                textToTranslate = autoCompleteTextView.getText().toString();
                boolean status = autoCompleteTextDBHelpher.insertAutoCompleteText(textToTranslate);
                Log.i("string added",Boolean.toString(status));
//                if(status){
                    autoCompleteAdapter.add(textToTranslate);
//                }
                autoCompleteAdapter.notifyDataSetChanged();

                tvWordToTranslate.setText(textToTranslate);

                tvWordToTranslateWikiLink.setVisibility(View.VISIBLE);

                translationService.setLanguageFrom(langFrom);
                translationService.setLanguageTo(langTo);
                translationService.setTranslateText(encodeURIComponent(textToTranslate));
                translationService.SendHttpGetRequest(new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("RESPONSE",response.toString());
                        convertResponse(response.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                        // Pull out the first event on the public timeline
                    }
                });
                break;

            case R.id.btnPlay:
                String str = autoCompleteTextView.getText().toString();
                if(!str.isEmpty()){
//                    tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
//                    status = autoCompleteTextDBHelpher.insertAutoCompleteText(str);
//                    Log.i("string added",Boolean.toString(status));
//                    if(status){
//                        autoCompleteAdapter.add(textToTranslate);
//                    }
//                    autoCompleteAdapter.notifyDataSetChanged();
                    textToSpeech.Play(str, languageDBHelper.getISO2Name(langFrom));
                }
                break;

            case R.id.tvWordToTranslateWikiLink:
                String url = "https://"+languageDBHelper.getISO2Name(langFrom)+".wikipedia.org/wiki/" + encodeURIComponent(textToTranslate);
                Log.i("wiki url",url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                break;

            default:
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch(adapterView.getId()) {
            case R.id.spDefaultLanguage:
                int position = i + 1;
                langFrom = languageDBHelper.getLanguageCode(position);
                Log.i("langFrom",langFrom);
                if(langFrom.equals(langTo)){
                    if( position+1 >= languageDBHelper.numberOfRows()){
                        position = 1;
                    } else {
                        position++;
                    }
                    langTo = languageDBHelper.getLanguageCode(position);
                    Log.i("langTo",langTo);
                    spTranslateLanguage.setSelection(position);
                }
                Log.i("spDefaultLanguage",langFrom);
                break;
            case R.id.spTranslateLanguage:
                int p = i + 1;
                langTo = languageDBHelper.getLanguageCode(p);
                Log.i("langTo",langTo);
                if(langTo.equals(langFrom)){
                    if( p+1 >= languageDBHelper.numberOfRows()){
                        p = 1;
                    } else {
                        p++;
                    }
                    langFrom = languageDBHelper.getLanguageCode(p);
                    Log.i("langFrom",langFrom);
                    spDefaultLanguage.setSelection(p);
                }

                Log.i("spTranslateLanguage",langTo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
}
