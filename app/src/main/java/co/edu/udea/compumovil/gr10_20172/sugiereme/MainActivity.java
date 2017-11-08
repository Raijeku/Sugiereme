package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DetailFragment.OnFragmentInteractionListener, GustosDarCategoriaFragment.OnFragmentInteractionListener, GustosDarFragment.OnFragmentInteractionListener, GustosRecibirCategoriaFragment.OnFragmentInteractionListener, GustosRecibirFragment.OnFragmentInteractionListener, CategoryFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_actualizar) {
            genericFragment = new CategoryFragment();
        } else if (id == R.id.nav_dar) {
            genericFragment = new GustosDarCategoriaFragment();
        } else if (id == R.id.nav_recibir) {
            genericFragment=new GustosRecibirCategoriaFragment();
        } else if (id == R.id.nav_postula) {

        } else if (id == R.id.nav_salir) {
            try {
                FirebaseAuth.getInstance().signOut();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onGDItemSelect(Element element) {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=DetailFragment.newInstance(element.getDescription(),element.getImage());
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onGRItemSelect(Element element) {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=DetailFragment.newInstance(element.getDescription(),element.getImage());
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onGRCItemSelected(Category category) {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=GustosDarFragment.newInstance(category.getTitle(),null);
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onGDCItemSelected(Category category) {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=GustosDarFragment.newInstance(category.getTitle(),null);
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onGRCRegresarClick() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(getSupportFragmentManager().findFragmentById(R.id.main_container));
        fragmentTransaction.commit();
    }

    @Override
    public void onGRCContinuarClick() {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=new GustosRecibirFragment();
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onGRRegresarClick() {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=new GustosRecibirCategoriaFragment();
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onGRContinuarClick() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(getSupportFragmentManager().findFragmentById(R.id.main_container));
        fragmentTransaction.commit();
    }

    @Override
    public void onGDCContinuarClick() {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=new GustosDarFragment();
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onGDCRegresarClick() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(getSupportFragmentManager().findFragmentById(R.id.main_container));
        fragmentTransaction.commit();
    }

    @Override
    public void onGDContinuarClick() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(getSupportFragmentManager().findFragmentById(R.id.main_container));
        fragmentTransaction.commit();
    }

    @Override
    public void onGDRegresarClick() {
        Fragment genericFragment=null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        genericFragment=new GustosDarCategoriaFragment();
        if (genericFragment!=null){
            fragmentTransaction.replace(R.id.main_container,genericFragment);
            fragmentTransaction.commit();
        }
    }
}
