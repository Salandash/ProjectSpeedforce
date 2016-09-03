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
import android.widget.EditText;
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
        super(context, "LocalSpeedforceDB.db", factory, version);
    }

    /**
     * Método que crea la base de datos y sus tablas.
     * @param sqLiteDatabase Objeto que representa la base de datos.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE TB_USUARIO(ID INTEGER PRIMARY KEY AUTOINCREMENT, ID_USUARIO TEXT UNIQUE, NOMBRES TEXT, APELLIDOS TEXT, EMAIL TEXT, SEXO TEXT, FECHANAC TEXT, CIUDAD TEXT, TELEFONO TEXT, ALTURA TEXT, PESO TEXT, STATUS TEXT);");
        //                                                              0                          1                     2             3                4           5           6               7           8               9           10          11
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
     * @param altura Texto de la altura.
     * @param peso Texto del peso.
     * @param status Texto del status.
     */
    public void insert_usuario(String id_usuario, String nombres, String apellidos, String email, String sexo, String fechanac,
                               String ciudad, String telefono, String altura, String peso, String status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_USUARIO", id_usuario);
        contentValues.put("NOMBRES", nombres);
        contentValues.put("APELLIDOS", apellidos);
        contentValues.put("EMAIL", email);
        contentValues.put("SEXO", sexo);
        contentValues.put("FECHANAC", fechanac);
        contentValues.put("CIUDAD", ciudad);
        contentValues.put("TELEFONO", telefono);
        contentValues.put("ALTURA", altura);
        contentValues.put("PESO", peso);
        contentValues.put("STATUS", status);
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
     * @param altura Texto de la altura.
     * @param peso Texto del peso.
     * @param status Texto del status.
     */
    public void update_usuario(String id_usuario, String nombres, String apellidos, String email, String sexo, String fechanac,
                               String ciudad, String telefono, String altura, String peso, String status) {
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET NOMBRES='"+nombres+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET APELLIDOS='"+apellidos+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET EMAIL='"+email+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET SEXO='"+sexo+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET FECHANAC='"+fechanac+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET CIUDAD='"+ciudad+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET TELEFONO='"+telefono+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET ALTURA='"+altura+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET PESO='"+peso+"' WHERE ID_USUARIO='"+id_usuario+"'");
        this.getWritableDatabase().execSQL("UPDATE TB_USUARIO SET STATUS='"+status+"' WHERE ID_USUARIO='"+id_usuario+"'");
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


        // <TB_USUARIO table rows> //
        // 0. ID
        // 1. ID_USUARIO
        // 2. NOMBRES
        // 3. APELLIDOS
        //
        ////////////////////
        //
        // 4. Email
        // 5. Sexo
        // 6. F.Nac.
        // 7. Ciudad
        // 8. Telefono
        // 9. Altura
        // 10. Peso
        // 11. Estatus
        //
        // Foto
        // Contrasena
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
     * @param altura Texto de la altura.
     * @param peso Texto del peso.
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
                                TextView altura,
                                TextView peso,
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
        altura.setText("");
        peso.setText("");
        status.setText("");

        textNombresApellidos.append(cursor.getString(2)+" "+cursor.getString(3));
        id_usuario.append(cursor.getString(1));
        nombres.append(cursor.getString(2));
        apellidos.append(cursor.getString(3));
        email.append(cursor.getString(4));
        sexo.append(cursor.getString(5));
        fechanac.append(cursor.getString(6));
        ciudad.append(cursor.getString(7));
        telefono.append(cursor.getString(8));
        altura.append(cursor.getString(9));
        peso.append(cursor.getString(10));
        status.append(cursor.getString(11));

        cursor.close();
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
     * @param altura Texto de la altura.
     * @param peso Texto del peso.
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
                                    EditText altura,
                                    EditText peso,
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
        altura.setText("");
        peso.setText("");
        status.setText("");

        id_usuario.append(cursor.getString(1));
        nombres.append(cursor.getString(2));
        apellidos.append(cursor.getString(3));
        email.append(cursor.getString(4));
        sexo.append(cursor.getString(5));
        fechanac.append(cursor.getString(6));
        ciudad.append(cursor.getString(7));
        telefono.append(cursor.getString(8));
        altura.append(cursor.getString(9));
        peso.append(cursor.getString(10));
        status.append(cursor.getString(11));

        cursor.close();
    }

}
