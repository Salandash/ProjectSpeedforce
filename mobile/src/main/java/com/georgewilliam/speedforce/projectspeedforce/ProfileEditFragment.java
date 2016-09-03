/**
 * @file ProfileFragment.java
 * @brief Fuente de la clase ProfileFragment.
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Clase que representa el Fragment de edición de perfil de usuario.
 */
public class ProfileEditFragment extends Fragment implements OnClickListener {

    /**
     * Campos de los inputs de los datos del perfil.
     */
    EditText inputNombres, inputApellidos, inputEmail, inputSexo, inputFechanac, inputCiudad,
            inputTelefono, inputAltura, inputPeso, inputStatus;

    /**
     * TextView para el nombre de usuario.
     */
    TextView labelIdUsuario;

    /**
     * Helper de la base de datos.
     */
    DB_Controller dbController;

    /**
     * Botón para guardar los nuevos datos introducidos para el perfil.
     */
    Button saveButton;

    /**
     * Botón para terminar el Activity en el que está desplegado Fragment.
     */
    Button goBackButton;

    /**
     * Constructor de la clase.
     */
    public ProfileEditFragment() {
        // Required empty public constructor
    }

    /**
     * Método que crea el Fragment.
     * @param savedInstanceState Objeto que provee data de la instacia previa al Fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Método que crea la vista del Fragment
     * @param inflater Objeto inflador que despliega la vista del Fragment.
     * @param container Objeto que contiene la vista a desplegar.
     * @param savedInstanceState Objeto que provee data de la instacia previa al Fragment.
     * @return Vista desplegada del Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        inputNombres = (EditText) view.findViewById(R.id.profile_edit_nombres_input_id);
        inputApellidos = (EditText) view.findViewById(R.id.profile_edit_apellidos_input_id);
        inputEmail = (EditText) view.findViewById(R.id.profile_edit_email_input_id);
        inputSexo = (EditText) view.findViewById(R.id.profile_edit_sexo_input_id);
        inputFechanac = (EditText) view.findViewById(R.id.profile_edit_fechanac_input_id);
        inputCiudad = (EditText) view.findViewById(R.id.profile_edit_ciudad_input_id);
        inputTelefono = (EditText) view.findViewById(R.id.profile_edit_telefono_input_id);
        inputAltura = (EditText) view.findViewById(R.id.profile_edit_altura_input_id);
        inputPeso = (EditText) view.findViewById(R.id.profile_edit_peso_input_id);
        inputStatus = (EditText) view.findViewById(R.id.profile_edit_status_input_id);

        labelIdUsuario = (TextView) view.findViewById(R.id.profile_edit_username_label_id);

        saveButton = (Button) view.findViewById(R.id.btn_saveEditedProfile_id);
        saveButton.setOnClickListener(this);
        goBackButton = (Button) view.findViewById(R.id.btn_finishProfileEdit_id);
        goBackButton.setOnClickListener(this);

        dbController = new DB_Controller(this.getActivity(), "", null, 1);
        dbController.populateProfileEdit(labelIdUsuario,
                inputNombres,
                inputApellidos,
                inputEmail,
                inputSexo,
                inputFechanac,
                inputCiudad,
                inputTelefono,
                inputAltura,
                inputPeso,
                inputStatus);

        return view;
    }

    /**
     * Método que ejecuta las funcionalidades de los inputs en la vista del Fragment.
     * @param view Vista que recibe el input.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_finishProfileEdit_id:
                getActivity().finish();
                break;

            case R.id.btn_saveEditedProfile_id:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("¿Desea guardar cambios?");
                dialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbController.update_usuario(labelIdUsuario.getText().toString(),
                                inputNombres.getText().toString(),
                                inputApellidos.getText().toString(),
                                inputEmail.getText().toString(),
                                inputSexo.getText().toString(),
                                inputFechanac.getText().toString(),
                                inputCiudad.getText().toString(),
                                inputTelefono.getText().toString(),
                                inputAltura.getText().toString(),
                                inputPeso.getText().toString(),
                                inputStatus.getText().toString());
                        getActivity().finish();
                    }
                });
                dialog.show();
                break;
        }

    }
}
