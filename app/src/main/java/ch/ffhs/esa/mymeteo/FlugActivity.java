package ch.ffhs.esa.mymeteo;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.opencsv.CSVReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FlugActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public String locationFrom = "";
    public String locationTo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbb);

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

        getData();

    }


    private void getData() {

        float radiusDistance = 30;
        //Zürich
        float fLat1 = 47.366F;
        float fLong1 = 8.550F;

        //Paris
        float fLat2 = 48.864F;
        float fLong2 = 2.349F;


        String codeStart = getAirportCode(radiusDistance, fLong1, fLat1);
        String codeEnd = getAirportCode(radiusDistance, fLong2, fLat2);

        String url = "https://www.google.ch/flights/?gl=ch#search;f=" + codeStart + ";t=" + codeEnd + ";";


        WebView webview = (WebView) findViewById(R.id.webcambrowsersbb);
        webview.setInitialScale(1);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setScrollbarFadingEnabled(false);
        webview.loadUrl(url);

    }


    private String getAirportCode(float distance, float fLong, float fLat) {

        String urlString = "http://aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance="+distance+";"+fLong+","+fLat+"";

        String response = "";

        try {
            response = new AirportCodeTask().execute(urlString).get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return response;
    }


    private class AirportCodeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String response = "";

            try {
                URL url = new URL(params[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("Station");

                for (int i = 0; i < nodeList.getLength(); i++) {

                    Node node = nodeList.item(i);

                    Element fstElmnt = (Element) node;
                    NodeList nameList = fstElmnt.getElementsByTagName("station_id");
                    Element nameElement = (Element) nameList.item(0);

                    String iataCode = getIataCode(nameElement.getTextContent().toString());
                    if(iataCode!="") { iataCode = "," + iataCode; }
                    response += iataCode;

                }
            } catch (Exception e) {
                System.out.println("XML Pasing Excpetion = " + e);
            }

            if(response.length() > 1) {  response = response.replaceFirst(",","");  }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
        }
    }

    private String getIataCode(String icaoCode) throws IOException {
        String iataCode = "";

        CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.icao_iata)), ';');

        String [] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine[1].equals(icaoCode)) {
                iataCode = nextLine[0];
                break;
            }
        }
        return iataCode;
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
