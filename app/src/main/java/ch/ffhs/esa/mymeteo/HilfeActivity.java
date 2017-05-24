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
import android.view.View;

public class HilfeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilfe);

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

    public void startWebCam(View v) {
        Intent intent = new Intent(this, WebCamActivity.class);
        startActivity(intent);
    }

    public void startImageCrawler(View v) {
        Intent intent = new Intent(this, ImageCrawlerActivity.class);
        intent.putExtra("locationName","Zurich,ch");
        startActivity(intent);
    }

    public void startSBB(View v) {
        Intent intent = new Intent(this, SBBActivity.class);
        //intent.putExtra("locationFrom","Zurich");
        //intent.putExtra("locationFrom","Roma");
        startActivity(intent);
    }

    public void startFlug(View v) {
        Intent intent = new Intent(this, FlugActivity.class);
        //intent.putExtra("latFrom","XY");
        //intent.putExtra("longFrom","XY");
        //intent.putExtra("latTo","XY");
        //intent.putExtra("longTo","XY");
        startActivity(intent);
    }

    public void Route(View v) {
        Intent intent = new Intent(this, RouteActivity.class);
        startActivity(intent);
    }
}
