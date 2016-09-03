/**
 * @file ProfileFragment.java
 * @brief Fuente de la clase ProfileFragment.
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Clase que representa el Fragment del perfil de usuario.
 */
public class ProfileFragment extends Fragment implements OnClickListener{

    /**
     * TextViews para los campos del perfil.
     */
    TextView profileLargeTextView, profileIdUsuario, profileNombres, profileApellidos, profileEmail, profileSexo, profileFechanac,
        profileCiudad, profileTelefono, profileAltura, profilePeso, profileStatus;

    /**
     * Helper de la base de datos.
     */
    DB_Controller dbController;

    /**
     * Botón para terminar el Activity en el que está desplegado Fragment.
     */
    Button goBackButton;

    /**
     * Botón para iniciar el Activity de edición de perfil.
     */
    ImageButton editImgButton;

    /**
     * Constructor de la clase.
     */
    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileLargeTextView = (TextView) view.findViewById(R.id.profile_nombres_apellidos_textview_id);
        profileIdUsuario = (TextView) view.findViewById(R.id.profile_username_textview_id);
        profileNombres = (TextView) view.findViewById(R.id.profile_nombre_textview_id);
        profileApellidos = (TextView) view.findViewById(R.id.profile_apellido_textview_id);
        profileEmail = (TextView) view.findViewById(R.id.profile_email_textview_id);
        profileSexo = (TextView) view.findViewById(R.id.profile_sexo_textview_id);
        profileFechanac = (TextView) view.findViewById(R.id.profile_fechanac_textview_id);
        profileCiudad = (TextView) view.findViewById(R.id.profile_ciudad_textview_id);
        profileTelefono = (TextView) view.findViewById(R.id.profile_telefono_textview_id);
        profileAltura = (TextView) view.findViewById(R.id.profile_altura_textview_id);
        profilePeso = (TextView) view.findViewById(R.id.profile_peso_textview_id);
        profileStatus = (TextView) view.findViewById(R.id.profile_status_textview_id);

        editImgButton = (ImageButton) view.findViewById(R.id.btn_editProfile_id);
        editImgButton.setOnClickListener(this);
        goBackButton = (Button) view.findViewById(R.id.btn_finishProfile_id);
        goBackButton.setOnClickListener(this);

        dbController = new DB_Controller(this.getActivity(), "", null, 1);
        dbController.populateProfile(profileLargeTextView,
                profileIdUsuario,
                profileNombres,
                profileApellidos,
                profileEmail,
                profileSexo,
                profileFechanac,
                profileCiudad,
                profileTelefono,
                profileAltura,
                profilePeso,
                profileStatus);

        return view;
    }

    /**
     * Método que ejecuta las funcionalidades de los inputs en la vista del Fragment.
     * @param view Vista que recibe el input.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_finishProfile_id:
                getActivity().finish();
                break;

            case R.id.btn_editProfile_id:
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
                break;

        }
    }
}
