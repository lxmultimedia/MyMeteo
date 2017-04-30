package ch.ffhs.esa.mymeteo;

import android.Manifest;
import android.Manifest.permission;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ch.ffhs.esa.mymeteo.listContent.ForeCastContent;
import data.Channel;
import data.Day;
import data.ForeCast;
import data.Item;
import service.WeatherServiceCallback;
import service.YahooWeatherService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, WeatherServiceCallback, ForeCastListFragment.OnListFragmentInteractionListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_CODE = 1;
    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    String locationName = "";

    float testLat = 47;
    float testLon = 8.5f;

    private YahooWeatherService service;
    private ProgressDialog dialog;

    public ListView listForeCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Send by E-Mail Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getPosition();

                String link = "http://maps.google.com/maps?q=" + MainActivity.this.latLng.latitude + "," + MainActivity.this.latLng.longitude + "";

                Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                        "mailto", "abc@gmail.com", null));
                emailIntent.setType("text/html");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        Html.fromHtml("Google Map: <br>" + link + "<br><br><a href=\"" + link +
                                "\">Ich befinde mich hier</a>"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Meine Position");
                startActivity(Intent.createChooser(emailIntent, "email"));
            }
        });

        // Navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Google Map
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);


            }
        }

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);

        getLocation();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Intent intent = new Intent(this, ImageCrawler.class);
            intent.putExtra("locationName",locationName);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, WebCam.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);

        buildGoogleApiClient();

        //mGoogleApiClient.connect();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void getPosition() {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more label.
            return;
        }

        LocationManager locationManager;
        String provider;
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more label.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();

        //zoom to current position:
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 1));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    // Adds a marker to the map.
    public void addMarker(View v) {

        testLat += 0.1;
        testLon += 0.2;

        Marker newMarker;
        LatLng newLatLng = new LatLng(testLat, testLon);

        // Add the marker at the clicked location, and add the next-available label
        // from the array of alphabetical characters.
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(newLatLng);
        markerOptions.title("New Marker, Test");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        newMarker = mGoogleMap.addMarker(markerOptions);
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 11));

    }


    public void getLocation() {

        getPosition();

        String localityCountry = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        if (latLng == null) {
            return;
        }

        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            android.location.Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            String locality = obj.getLocality();
            this.locationName = locality;
            String countryCode = obj.getCountryCode();
            localityCountry = locality + "," + countryCode;

            TextView tvLocation = (TextView) findViewById(R.id.PositionWert);
            tvLocation.setText(localityCountry);

            Log.v("IGA", "Address" + add);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        getWeatherForecast(localityCountry);

    }

    private void getWeatherForecast(String localityCountry) {
        dialog = new ProgressDialog(this);
        service = new YahooWeatherService(this);

        dialog.setMessage("Laden...");
        dialog.show();

        service.refreshWeather(localityCountry);

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();

        String location = service.getLocation();
        Item item = channel.getItem();

        ForeCast forecast = item.getForeCast();

        Day day0 = forecast.getDay0();
        Day day1 = forecast.getDay1();
        Day day2 = forecast.getDay2();
        Day day3 = forecast.getDay3();
        Day day4 = forecast.getDay4();
        Day day5 = forecast.getDay5();
        Day day6 = forecast.getDay6();
        Day day7 = forecast.getDay7();
        Day day8 = forecast.getDay8();
        Day day9 = forecast.getDay9();

        String iconbase = "http://l.yimg.com/a/i/us/we/52/";

        // Day 0
        String temperature0 = "- " + day0.getTempLow() + ", + " + day0.getTempHigh();
        String text0 = day0.getText();
        String icon0 = iconbase + day0.getCode() + ".gif";
        String forecast0 = temperature0 + ", " + text0;

        // Day 1
        String temperature1 = "- " + day1.getTempLow() + ", + " + day1.getTempHigh();
        String text1 = day1.getText();
        String icon1 = iconbase + day1.getCode() + ".gif";
        String forecast1 = temperature1 + ", " + text1;

        // Day 2
        String temperature2 = "- " + day2.getTempLow() + ", + " + day2.getTempHigh();
        String text2 = day2.getText();
        String icon2 = iconbase + day2.getCode() + ".gif";
        String forecast2 = temperature2 + ", " + text2;

        // Day 3
        String temperature3 = "- " + day3.getTempLow() + ", + " + day3.getTempHigh();
        String text3 = day3.getText();
        String icon3 = iconbase + day3.getCode() + ".gif";
        String forecast3 = temperature3 + ", " + text3;

        // Day 4
        String temperature4 = "- " + day4.getTempLow() + ", + " + day4.getTempHigh();
        String text4 = day4.getText();
        String icon4 = iconbase + day4.getCode() + ".gif";
        String forecast4 = temperature4 + ", " + text4;

        // Day 5
        String temperature5 = "- " + day5.getTempLow() + ", + " + day5.getTempHigh();
        String text5 = day5.getText();
        String icon5 = iconbase + day5.getCode() + ".gif";
        String forecast5 = temperature5 + ", " + text5;

        // Day 6
        String temperature6 = "- " + day6.getTempLow() + ", + " + day6.getTempHigh();
        String text6 = day6.getText();
        String icon6 = iconbase + day6.getCode() + ".gif";
        String forecast6 = temperature6 + ", " + text6;

        // Day 7
        String temperature7 = "- " + day7.getTempLow() + ", + " + day7.getTempHigh();
        String text7= day7.getText();
        String icon7 = iconbase + day7.getCode() + ".gif";
        String forecast7 = temperature7 + ", " + text7;

        // Day 8
        String temperature8 = "- " + day8.getTempLow() + ", + " + day8.getTempHigh();
        String text8 = day8.getText();
        String icon8 = iconbase + day8.getCode() + ".gif";
        String forecast8 = temperature8 + ", " + text8;

        // Day 9
        String temperature9 = "- " + day9.getTempLow() + ", + " + day9.getTempHigh();
        String text9 = day9.getText();
        String icon9 = iconbase + day9.getCode() + ".gif";
        String forecast9 = temperature9 + ", " + text9;


        ForeCastContent.clear();

        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("0",icon0,forecast0));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("1",icon1,forecast1));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("2",icon2,forecast2));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("3",icon3,forecast3));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("4",icon4,forecast4));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("5",icon5,forecast5));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("6",icon6,forecast6));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("7",icon7,forecast7));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("8",icon8,forecast8));
        ForeCastContent.addItem(new ForeCastContent.ForeCastItem("9",icon9,forecast9));

        RecyclerView rv = (RecyclerView) findViewById(R.id.listForeCast);
        rv.setAdapter(new MyforecastDayRecyclerViewAdapter(ForeCastContent.ITEMS,null));
        rv.invalidate();
    }

    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListFragmentInteraction(ForeCastContent.ForeCastItem item) {

    }
}
