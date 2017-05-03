package ch.ffhs.esa.mymeteo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class WebCamActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_cam);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                System.out.println("WebCamActivity Request started");
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
                System.out.println("WebCamActivity Request successful");


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
                System.out.println("WebCamActivity Request failed");
            }

            @Override
            public void onRetry(int retryNo) {
                //TextView _response = (TextView) findViewById(R.id.txtResponse);
                //_response.setText(retryNo);
                System.out.println("WebCamActivity Request retry");
            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_suche) {

        } else if (id == R.id.nav_favoriten) {

        } else if (id == R.id.nav_topplaces) {

        } else if (id == R.id.nav_einstellungen) {

        } else if (id == R.id.nav_hilfe) {
            Intent intent = new Intent(this, HilfeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
