/**
 * @file MainActivity.java
 * @brief Fuente de la clase MainActivity.
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Clase que representa el Activity de punto de entrada de la aplicación móvil.
 * Actualmente se usa como clase de prueba para probar las demás funcionalidades de la aplicación.
 */
public class MainActivity extends AppCompatActivity {
    EditText id_usuario, nombres, apellidos;
    TextView largeTextView;
    DB_Controller controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_usuario = (EditText)findViewById(R.id.id_usuario_input);
        nombres = (EditText)findViewById(R.id.nombres_input);
        apellidos = (EditText)findViewById(R.id.apellidos_input);

        largeTextView = (TextView) findViewById(R.id.largeText);


        controller = new DB_Controller(this, "", null, 1);
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
