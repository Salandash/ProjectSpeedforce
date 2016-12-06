/**
 * @file ProfileActivity.java
 * @brief Fuente de la clase ProfileActivity.
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Clase que representa el Activity del perfil de usuario.
 */
public class ProfileActivity extends AppCompatActivity {

    /**
     * Método que crea el Activity y le pone la vista.
     * @param savedInstanceState Objeto que contiene el estado frizado del Activity anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    /*
    public void reattachFragment(){

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.profile_fragment_id);
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }
    */

    /*
    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment profileFragment = new ProfileFragment();
        ft.add(profileFragment, "profile");
        ft.attach(profileFragment);
        ft.commit();
    }
    */
}
