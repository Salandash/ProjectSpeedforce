/**
 * @file DB_Controller.java
 * @brief Fuente de la clase DB_Controller
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Clase que representa el helper de la base de datos local de la aplicación móvil.
 */
public class DB_Controller extends SQLiteOpenHelper {

    /**
     * Constructor de la clase.
     * @param context Objeto que representa el contexto que utiliza la base de datos.
     * @param name String del nombre de la base de datos.
     * @param factory Objeto para crear un objeto cursor para la base de datos.
     * @param version Entero que representa la versión de la base de datos.
     */
    public DB_Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "LocalDB03.db", factory, version);
        //"LocalDB03.db"
    }

    /**
     * Método que crea las tablas y campos de la base de datos.
     * @param sqLiteDatabase Objeto que representa la base de datos.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE TB_USUARIO(ID INTEGER PRIMARY KEY AUTOINCREMENT, "//0
                + "ID_USUARIO TEXT UNIQUE, "//1
                + "NOMBRES TEXT, "//2
                + "APELLIDOS TEXT, "//3
                + "EMAIL TEXT, "//4
                + "CONTRASENA TEXT, "//5
                + "SEXO TEXT, "//6
                + "FECHANACIMIENTO TEXT, "//7
                + "ID_CIUDAD TEXT, "//8
                + "NUMEROTELEFONO TEXT, "//9
                + "ID_STATUS TEXT, "//10
                + "FOTO TEXT);"//11
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_ENTRENADORES(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_ENTRENADOR TEXT UNIQUE, "
                + "BCERTIFICADO BLOB, "
                + "ID_USUARIO TEXT);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_CIUDADES(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_CIUDAD TEXT UNIQUE, "
                + "DESCRIPCION TEXT);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_SESIONESENTRENAMIENTO(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_SESION TEXT UNIQUE, "
                + "ID_ATLETA TEXT, "
                + "ID_CONDICIONCLIMATICA TEXT, "
                + "RITMOCARDIACOMEDIO TEXT, "
                + "ID_RUTA TEXT, "
                + "MOMENTOINICIO TEXT, "
                + "MOMENTOTERMINO TEXT, "
                + "DURACION INTEGER, "
                + "DISTANCIARECORRIDA INTEGER);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_ATLETA(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_USUARIO TEXT UNIQUE, "
                + "ALTURA TEXT, "
                + "ID_ATLETA TEXT, "
                + "PESO TEXT, "
                + "ID_BICICLETA TEXT);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_ATLETAENTRENADOR(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_ATLETA TEXT, "
                + "ID_ENTRENADOR TEXT);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_RUTA(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_RUTA TEXT UNIQUE, "
                + "ID_ATLETAENTRENADOR TEXT, "
                + "PUNTOPARTIDA TEXT, "
                + "PUNTOTERMINO TEXT, "
                + "ID_CIUDAD TEXT);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_BICICLETAS(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_BICICLETA TEXT UNIQUE, "
                + "PESOKG TEXT, "
                + "MATERIAL TEXT, "
                + "TAMANO INTEGER, "
                + "TAMANOGOMAS INTEGER, "
                + "FABRICANTEGOMAS TEXT, "
                + "FABRICANTE TEXT);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_CONDICIONCLIMATICA(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_CONDICIONCLIMATICA TEXT UNIQUE, "
                + "DESCRIPCION TEXT, "
                + "TEMPERATURA INTEGER, "
                + "HUMEDAD INTEGER);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_STATUS(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_STATUS TEXT UNIQUE, "
                + "DESCRIPCION TEXT);"
        );

        sqLiteDatabase.execSQL("CREATE TABLE TB_DATASYNC(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_DATASYNC TEXT UNIQUE, "
                + "FECHASYNC TEXT, "
                + "ID_ATLETA TEXT);"
        );
    }

    /**
     * Método que actualiza la estructura de la base de datos.
     * @param sqLiteDatabase Objeto que representa la base de datos.
     * @param i Entero que representa la versión antigua de la base de datos.
     * @param i1 Entero que representa la versión nueva de la base de datos.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_USUARIO");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_ENTRENADORES");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_CIUDADES");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_SESIONESENTRENAMIENTO");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_ATLETA");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_ATLETAENTRENADOR");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_RUTA");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_BICICLETAS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_CONDICIONCLIMATICA");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_STATUS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TB_DATASYNC");
        onCreate(sqLiteDatabase);
    }


    // CRUD methods


    /**
     * Método para introducir datos a la tabla de usuario.
     * @param id_usuario Texto del nombre de usuario.
     * @param nombres Texto de los nombres.
     * @param apellidos Texto de los apellidos.
     * @param email Texto del Email.
     * @param sexo Texto del sexo.
     * @param fechanac Texto de la fecha de nacimiento.
     * @param ciudad Texto de la ciudad.
     * @param telefono Texto del telefono.
     * @param status Texto del status.
     */
    public void insert_usuario(String id_usuario, String nombres, String apellidos, String email, String sexo, String fechanac,
                               String ciudad, String telefono, String status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_USUARIO", id_usuario);
        contentValues.put("NOMBRES", nombres);
        contentValues.put("APELLIDOS", apellidos);
        contentValues.put("EMAIL", email);
        contentValues.put("SEXO", sexo);
        contentValues.put("FECHANACIMIENTO", fechanac);
        contentValues.put("ID_CIUDAD", ciudad);
        contentValues.put("NUMEROTELEFONO", telefono);
        contentValues.put("ID_STATUS", status);
        this.getWritableDatabase().insertOrThrow("TB_USUARIO", "", contentValues);
    }


    /**
     * Método para eliminar un usuario de la tabla del usuario.
     * @param id_usuario Texto del nombre de usuario, del usuario a eliminar.
     */
    public void delete_usuario(String id_usuario) {
        this.getWritableDatabase().delete("TB_USUARIO", "ID_USUARIO='" + id_usuario + "'", null);
    }

    /**
     * Método para actualizar datos de un usuario en la tabla de usuario.
     * @param id_usuario Texto del nombre de usuario, del usuario a actualizar.
     * @param nombres Texto de los nombres.
     * @param apellidos Texto de los apellidos.
     * @param email Texto del Email.
     * @param sexo Texto del sexo.
     * @param fechanac Texto de la fecha de nacimiento.
     * @param ciudad Texto de la ciudad.
     * @param telefono Texto del telefono.
     * @param status Texto del status.
     */
    public void update_usuario(String id_usuario, String nombres, String apellidos, String email, String sexo, String fechanac,
                               String ciudad, String telefono, String status) {
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET NOMBRES='"+nombres+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET APELLIDOS='"+apellidos+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET EMAIL='"+email+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET SEXO='"+sexo+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET FECHANACIMIENTO='"+fechanac+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET ID_CIUDAD='"+ciudad+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET NUMEROTELEFONO='"+telefono+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET ID_STATUS='"+status+"' WHERE ID_USUARIO='"+id_usuario+"'");
    }

    /**
     * Inserta una imagen de perfil a la tabla de usuarios.
     * @param id_usuario ID del usuario a cambiar foto.
     * @param imageBitmap Bitmap que contiene la imagen de perfil.
     */
    public void updateProfilePicture(String id_usuario, Bitmap imageBitmap) {
        String imageStr64 = DB_BitmapUtility.getString64FromBitmap(imageBitmap);
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET FOTO='"+imageStr64+"' WHERE ID_USUARIO='"+id_usuario+"'");
    }

    /**
     * Método para listar todos los usuario de la tabla de usuario (nombre de usuario, nombres y apellidos).
     * @param txtv TextView a poblar.
     */
    public void list_all_usuario(TextView txtv) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TB_USUARIO", null);
        txtv.setText("");
        while(cursor.moveToNext()) {
            txtv.append(cursor.getString(1)+"\n"
                    +cursor.getString(2)+" "+cursor.getString(3)

                    +"\n\n");
        }
        cursor.close();

    }


    /**
     * Método para poblar la pantalla del perfil.
     * @param textNombresApellidos Texto de los nombres y apellidos.
     * @param id_usuario Texto del nombre de usuario.
     * @param nombres Texto de los nombres.
     * @param apellidos Texto de los apellidos.
     * @param email Texto del Email.
     * @param sexo Texto del sexo.
     * @param fechanac Texto de la fecha de nacimiento.
     * @param ciudad Texto de la ciudad.
     * @param telefono Texto del telefono.
     * @param status Texto del status.
     */
    public void populateProfile(TextView textNombresApellidos,
                                TextView id_usuario,
                                TextView nombres,
                                TextView apellidos,
                                TextView email,
                                TextView sexo,
                                TextView fechanac,
                                TextView ciudad,
                                TextView telefono,
                                TextView status) {

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TB_USUARIO", null);
        cursor.moveToFirst();

        textNombresApellidos.setText("");
        id_usuario.setText("");
        nombres.setText("");
        apellidos.setText("");
        email.setText("");
        sexo.setText("");
        fechanac.setText("");
        ciudad.setText("");
        telefono.setText("");
        status.setText("");

        textNombresApellidos.append(cursor.getString(2)+" "+cursor.getString(3));
        id_usuario.append(cursor.getString(1));
        nombres.append(cursor.getString(2));
        apellidos.append(cursor.getString(3));
        email.append(cursor.getString(4));
        sexo.append(cursor.getString(6));
        fechanac.append(cursor.getString(7));
        ciudad.append(cursor.getString(8));
        telefono.append(cursor.getString(9));
        status.append(cursor.getString(10));

        cursor.close();
    }

    /**
     * Obtiene la imagen de perfil del usuario de la base de datos
     * @param image ImageView donde se carga la imagen.
     * @return False si no hay imagen en la base de datos.
     */
    public boolean getProfilePicture(ImageView image) {

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TB_USUARIO", null);
        cursor.moveToFirst();
        String imageStr64 = cursor.getString(11);
        cursor.close();

        if(imageStr64 == null)
            return false;

        Bitmap imageBitmap = DB_BitmapUtility.getBitmapFromString64(imageStr64);
        image.setImageBitmap(imageBitmap);
        return true;
    }

    /**
     * Método para poblar la pantalla de edición de perfil.
     * @param id_usuario TextView del nombre de usuario, del usuario a editar.
     * @param nombres Texto de los nombres.
     * @param apellidos Texto de los apellidos.
     * @param email Texto del Email.
     * @param sexo Texto del sexo.
     * @param fechanac Texto de la fecha de nacimiento.
     * @param ciudad Texto de la ciudad.
     * @param telefono Texto del telefono.
     * @param status Texto del status.
     */
    public void populateProfileEdit(TextView id_usuario,
                                    EditText nombres,
                                    EditText apellidos,
                                    EditText email,
                                    EditText sexo,
                                    EditText fechanac,
                                    EditText ciudad,
                                    EditText telefono,
                                    EditText status) {

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM TB_USUARIO", null);
        cursor.moveToFirst();

        id_usuario.setText("");
        nombres.setText("");
        apellidos.setText("");
        email.setText("");
        sexo.setText("");
        fechanac.setText("");
        ciudad.setText("");
        telefono.setText("");
        status.setText("");

        id_usuario.append(cursor.getString(1));
        nombres.append(cursor.getString(2));
        apellidos.append(cursor.getString(3));
        email.append(cursor.getString(4));
        sexo.append(cursor.getString(6));
        fechanac.append(cursor.getString(7));
        ciudad.append(cursor.getString(8));
        telefono.append(cursor.getString(9));
        status.append(cursor.getString(10));

        cursor.close();
    }

}
