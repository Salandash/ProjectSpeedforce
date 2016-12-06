/**
 * @file ProfileFragment.java
 * @brief Fuente de la clase ProfileFragment.
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Clase que representa el Fragment del perfil de usuario.
 */
public class ProfileFragment extends Fragment implements OnClickListener{

    /**
     * Identificador constante (fingerprint) que identifica la Solicitud de búsqueda de imagen (Intent).
     */
    public static final int IMAGE_GALLERY_REQUEST = 20;

    /**
     * ImageView para la foto del perfil.
     */
    ImageView imgPicture;

    /**
     * TextViews para los campos del perfil.
     */
    TextView profileLargeTextView,
            profileIdUsuario,
            profileNombres,
            profileApellidos,
            profileEmail,
            profileSexo,
            profileFechanac,
            profileCiudad,
            profileTelefono,
            profileStatus;

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
        profileStatus = (TextView) view.findViewById(R.id.profile_status_textview_id);

        editImgButton = (ImageButton) view.findViewById(R.id.btn_editProfile_id);
        editImgButton.setOnClickListener(this);
        goBackButton = (Button) view.findViewById(R.id.btn_finishProfile_id);
        goBackButton.setOnClickListener(this);
        imgPicture = (ImageView) view.findViewById(R.id.img_profile_picture_id);
        imgPicture.setOnClickListener(this);

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
                profileStatus);

        dbController.getProfilePicture(imgPicture);

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

            case R.id.img_profile_picture_id:
                // invoke the image gallery using an implict intent.
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                // where do we want to find the data?
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                // finally, get a URI representation
                Uri data = Uri.parse(pictureDirectoryPath);

                // set the data and type.  Get all image types.
                photoPickerIntent.setDataAndType(data, "image/*");

                // we will invoke this activity, and get something back from it.
                startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
                break;
        }
    }

    /**
     * Método que maneja la imagen obtenida por startActivityForResult() al seleccionar imagen de perfil.
     * @param requestCode Código idenrificador de la acción (Intent) de selección de imagen.
     * @param resultCode Código de resultado de la solicitud de selección de imagen.
     * @param data Data obtenida en la selección de imagen.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            // if we are here, everything processed successfully.
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // show the image to the user
                    imgPicture.setImageBitmap(image);

                    dbController.updateProfilePicture(profileIdUsuario.getText().toString(), image);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(getActivity(), "No se pudo abrir la imagen", Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
