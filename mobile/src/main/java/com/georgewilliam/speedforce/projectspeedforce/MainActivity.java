/**
 * @file MainActivity.java
 * @brief Fuente de la clase MainActivity.
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Clase que representa el Activity de punto de entrada de la aplicación móvil.
 * Actualmente se usa como clase de prueba para probar las demás funcionalidades de la aplicación.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    private ActionBarDrawerToggle drawerToggle;

    EditText id_usuario, nombres, apellidos;
    TextView largeTextView;
    DB_Controller controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        id_usuario = (EditText)findViewById(R.id.id_usuario_input);
        nombres = (EditText)findViewById(R.id.nombres_input);
        apellidos = (EditText)findViewById(R.id.apellidos_input);

        largeTextView = (TextView) findViewById(R.id.largeText);

        controller = new DB_Controller(this, "", null, 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }



    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_main_map_id:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nav_training_id:
                fragmentClass = ProfileEditFragment.class;
                break;
//            case R.id.nav_third_fragment:
//                fragmentClass = ProfileEditFragment.class;
//                break;
            default:
                fragmentClass = ProfileFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = (LinearLayout) findViewById(R.id.content_frame);
        inflater.inflate(R.layout.activity_main, container);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }



    public void btn_click(View view) {
        switch(view.getId()) {

            case R.id.btn_add:
                try {}catch (SQLiteException e){
                    Toast.makeText(MainActivity.this, "Ya existe este ID de usuario.", Toast.LENGTH_SHORT).show();
                }
                controller.insert_usuario(
                        id_usuario.getText().toString(),
                        nombres.getText().toString(),
                        apellidos.getText().toString(),
                        " ",
                        " ",
                        " ",
                        " ",
                        " ",
                        " "
                );
                break;

            case R.id.btn_delete:
                controller.delete_usuario(id_usuario.getText().toString());
                break;

            case R.id.btn_update:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("¿Desea guardar cambios?");

                //EditText new_nombres = new EditText(this);
                //dialog.setView(new_nombres);

                dialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        controller.update_usuario(
                                id_usuario.getText().toString(),
                                nombres.getText().toString(),
                                apellidos.getText().toString(),
                                " ",
                                " ",
                                " ",
                                " ",
                                " ",
                                " "
                        );
                    }
                });

                dialog.show();

                break;

            case R.id.btn_list:
                controller.list_all_usuario(largeTextView);
                break;

            case R.id.btn_to_profile_id:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_to_maps_id:
                Intent mapsIntent = new Intent(this, MapsActivity.class);
                startActivity(mapsIntent);
                break;
        }
    }
}
