package ch.ffhs.esa.mymeteo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.loopj.android.http.*;
import org.json.JSONException;
import org.json.JSONObject;
import android.webkit.*;

public class WebCam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_cam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        testRestRequestWebCam();
    }

    public void testRestRequestWebCam() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Mashape-Key","PviZuRayZZmshZBV6WwwGTcFPttAp1u9y7ZjsnQ6u8UxbPR8Wu");
        client.get("https://webcamstravel.p.mashape.com/webcams/list/region=ch.zh?show=webcams:url&bbox=47.368650,8.539183,47.368650,8.539183",
                new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                //TextView _response = (TextView) findViewById(R.id.txtResponse);
                //_response.setText("started");
                System.out.println("WebCam Request started");
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new String(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TextView _response = (TextView) findViewById(R.id.txtResponse);
                //_response.setText(jsonObject.toString());

                String webcamurl = "";
                try {
                    webcamurl = jsonObject.getJSONObject("result").getJSONArray("webcams").getJSONObject(0).
                            getJSONObject("url").getJSONObject("current").getString("mobile");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //_response.setText(webcamurl);
                System.out.println("WebCam Request successful");


                WebView webview = (WebView) findViewById(R.id.webcambrowser);
                webview.setInitialScale(1);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setLoadWithOverviewMode(true);
                webview.getSettings().setUseWideViewPort(true);
                webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webview.setScrollbarFadingEnabled(false);
                webview.loadUrl(webcamurl);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new String(errorResponse));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                //TextView _response = (TextView) findViewById(R.id.txtResponse);
                //_response.setText(jsonObject.toString());
                System.out.println("WebCam Request failed");
            }

            @Override
            public void onRetry(int retryNo) {
                //TextView _response = (TextView) findViewById(R.id.txtResponse);
                //_response.setText(retryNo);
                System.out.println("WebCam Request retry");
            }
        });
    }

}
