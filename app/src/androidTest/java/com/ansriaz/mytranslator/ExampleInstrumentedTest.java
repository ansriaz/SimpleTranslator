package com.ansriaz.mytranslator;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;

import javax.net.ssl.SSLEngineResult;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.conn.HttpHostConnectException;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest extends Mockito {

    String mStringToBetyped;
    String url = "https://glosbe.com/gapi/translate?from=eng&dest=ita&format=json&phrase=espresso&pretty=true";

    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useActivityInTest() {
        Activity activity = activityTestRule.getActivity();
    }

    @Before
    public void initValidString() {
        mStringToBetyped = "Espresso";
    }

    @Test
    public void autoCompTest() {

        onView(withId(R.id.spDefaultLanguage)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("English"))).perform(click());

        onView(withId(R.id.spTranslateLanguage)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Italian"))).perform(click());

        onView(withId(R.id.autoCompleteTextView))
                .perform(typeText(mStringToBetyped), closeSoftKeyboard());

        onView(withId(R.id.btnPlay)).perform(click());
        onView(withId(R.id.btnTranslate)).perform(click());
        onView(withId(R.id.tvWordToTranslateWikiLink)).perform(click());
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.ansriaz.mytranslator", appContext.getPackageName());
    }

    @Test
    public void should_return_true_if_the_status_api_works_properly() throws ClientProtocolException, IOException {
        //given:
        HttpClient httpClient = mock(HttpClient.class);
        HttpGet httpGet = mock(HttpGet.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        //and:
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(httpGet)).thenReturn(httpResponse);

        //and:
        StatusApiClient client = new StatusApiClient(url);

        //when:
        boolean status = client.getStatus();
        Log.i("stauts",Boolean.toString(status));

        //then:
        Assert.assertTrue(true);
    }

    @Test
    public void should_return_false_if_status_api_do_not_respond() throws ClientProtocolException, IOException {
        //given:
        HttpClient httpClient = mock(HttpClient.class);
        HttpGet httpGet = mock(HttpGet.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        //and:
        when(httpClient.execute(httpGet)).thenThrow(HttpHostConnectException.class);

        //and:
        StatusApiClient client = new StatusApiClient(httpClient, httpGet);

        //when:
        boolean status = client.getStatus();
        Log.i("stauts",Boolean.toString(status));

        //then:
        Assert.assertFalse(status);
    }

    public class StatusApiClient {

        private String targetUrl = "";
        private HttpClient client = null;
        HttpGet httpGet = null;

        public StatusApiClient(HttpClient client, HttpGet httpGet) {
            this.client = client;
            this.httpGet = httpGet;
        }

        public StatusApiClient(String targetUrl) {
            this.targetUrl = targetUrl;
            this.client = HttpClientBuilder.create().build();
            this.httpGet = new HttpGet(targetUrl);
        }

        public boolean getStatus() {
            BufferedReader rd = null;
            boolean status = false;
            try{
                Log.d("Requesting status: ", targetUrl);


                HttpResponse response = client.execute(httpGet);

                if(Integer.toString(response.getStatusLine().getStatusCode()) == SSLEngineResult.Status.OK.toString()) {
                    Log.d("Is online.","true");
                    status = true;
                }

            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (rd != null) {
                    try{
                        rd.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }

            return status;
        }
    }
}
