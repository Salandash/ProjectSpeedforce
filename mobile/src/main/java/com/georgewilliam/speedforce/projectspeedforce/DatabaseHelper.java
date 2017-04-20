package com.georgewilliam.speedforce.projectspeedforce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SFDB00.db";

    private static final String USER_TABLE = "TB_USUARIOS";
    private static final String USER_USERNAME = "USUARIO";
    private static final String USER_PASSWORD = "CONTRASENA";
    private static final String USER_EMAIL = "EMAIL";
    private static final String USER_NAME = "NOMBRE";
    private static final String USER_LASTNAME = "APELLIDO";
    private static final String USER_GENDER = "SEXO";
    private static final String USER_BIRTHDATE = "FECHANACIMIENTO";
    private static final String USER_CITYNAME = "CIUDAD";
    private static final String USER_COUNTRYNAME = "PAIS";
    private static final String USER_TELEPHONE = "TELEFONO";
    private static final String USER_HEIGHT = "ALTURA";
    private static final String USER_WEIGHT = "PESO";
    private static final String USER_BIKERTYPE = "CICLISTA";

    private static final String SESSION_TABLE = "TB_SESIONES";
    private static final String SESSION_SESSIONID = "SESIONID";
    private static final String SESSION_USERID = "USUARIO";
    private static final String SESSION_CLIMATECONDITIONID = "CONDICIONCLIMATICA";
    private static final String SESSION_AVERAGEBPM = "RITMOCARDIACO";
    private static final String SESSION_ROUTEID = "RUTAID";
    private static final String SESSION_COORDINATES = "COORDENADAS";
    private static final String SESSION_CITYNAME = "CIUDAD";
    private static final String SESSION_COUNTRYNAME = "PAIS";
    private static final String SESSION_STARTTIME = "TIEMPOINICIO";
    private static final String SESSION_ENDTIME = "TIEMPOFINAL";
    private static final String SESSION_DISTANCE = "DISTANCIA";
    private static final String SESSION_BURNTCALORIES = "CALORIAS";
    private static final String SESSION_RELATIVEHUMIDITY = "HUMEDAD";
    private static final String SESSION_TEMPERATURE = "TEMPERATURA";
    private static final String SESSION_TRAININGTYPEID = "TIPOENTRENAMIENTO";
    private static final String SESSION_SESSIONSTATUSID = "STATUS";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
        //"LocalDB03.db"
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " // 0
                + USER_USERNAME + " TEXT UNIQUE, " // 1
                + USER_PASSWORD + " TEXT, " // 2
                + USER_EMAIL + " TEXT, " // 3
                + USER_NAME + " TEXT, " // 4
                + USER_LASTNAME + " TEXT, " // 5
                + USER_GENDER + " TEXT, " // 6
                + USER_BIRTHDATE + " TEXT, " // 7
                + USER_CITYNAME + " TEXT, " // 8
                + USER_COUNTRYNAME + " TEXT, " // 9
                + USER_TELEPHONE + " TEXT, " // 10
                + USER_HEIGHT + " REAL, " // 11
                + USER_WEIGHT + " REAL, " // 12
                + USER_BIKERTYPE + " TEXT);" // 13
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + SESSION_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " // 0
                + SESSION_SESSIONID + " TEXT UNIQUE, " // 1
                + SESSION_USERID + " TEXT, " // 2
                + SESSION_CLIMATECONDITIONID + " TEXT, " // 3
                + SESSION_AVERAGEBPM + " REAL, " // 4
                + SESSION_ROUTEID + " TEXT, " // 5
                + SESSION_COORDINATES + " TEXT, " // 6
                + SESSION_CITYNAME + " TEXT, " // 7
                + SESSION_COUNTRYNAME + " TEXT, " // 8
                + SESSION_STARTTIME + " TEXT, " // 9
                + SESSION_ENDTIME + " TEXT, " // 10
                + SESSION_DISTANCE + " REAL, " // 11
                + SESSION_BURNTCALORIES + " REAL, " // 12
                + SESSION_RELATIVEHUMIDITY + " REAL, " // 13
                + SESSION_TEMPERATURE + " REAL, " // 14
                + SESSION_TRAININGTYPEID + " TEXT, " // 15
                + SESSION_SESSIONSTATUSID + " TEXT);" // 16
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE + ";");

        onCreate(sqLiteDatabase);
    }

    public void insertUser(JSONObject json) {

        ContentValues contentValues = new ContentValues();

        try {
            contentValues.put(USER_USERNAME, json.getString("Username"));
            contentValues.put(USER_PASSWORD, json.getString("Password"));
            contentValues.put(USER_EMAIL, json.getString("Email"));
            contentValues.put(USER_NAME, json.getString("Name"));
            contentValues.put(USER_LASTNAME, json.getString("LastName"));
            contentValues.put(USER_GENDER, json.getString("Sex"));
            contentValues.put(USER_BIRTHDATE, json.getString("BirthDate"));
            contentValues.put(USER_CITYNAME, json.getString("CityName"));
            contentValues.put(USER_COUNTRYNAME, json.getString("CountryName"));
            contentValues.put(USER_TELEPHONE, json.getString("TelephoneNumber"));
            contentValues.put(USER_HEIGHT, json.getDouble("Height"));
            contentValues.put(USER_WEIGHT, json.getDouble("Weight"));
            contentValues.put(USER_BIKERTYPE, json.getString("BikerType"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "DatabaseHelper.insertUser");
        }


        this.getWritableDatabase().insertOrThrow(USER_TABLE, "", contentValues);
    }

    public void insertSession(JSONObject json) {

        ContentValues contentValues = new ContentValues();

        try {
            contentValues.put(SESSION_SESSIONID, json.getString("SessionID"));
            contentValues.put(SESSION_USERID, json.getString("UserID"));
            contentValues.put(SESSION_CLIMATECONDITIONID, json.getString("ClimateConditionID"));
            contentValues.put(SESSION_AVERAGEBPM, json.getDouble("AverageBPM"));
            contentValues.put(SESSION_ROUTEID, json.getString("RouteID"));
            contentValues.put(SESSION_COORDINATES, json.getJSONArray("Coordinates").toString());
            contentValues.put(SESSION_CITYNAME, json.getString("CityName"));
            contentValues.put(SESSION_COUNTRYNAME, json.getString("CountryName"));
            contentValues.put(SESSION_STARTTIME, json.getString("StartTime"));
            contentValues.put(SESSION_ENDTIME, json.getString("EndTime"));
            contentValues.put(SESSION_DISTANCE, json.getDouble("Distance"));
            contentValues.put(SESSION_BURNTCALORIES, json.getDouble("BurntCalories"));
            contentValues.put(SESSION_RELATIVEHUMIDITY, json.getDouble("RelativeHumidity"));
            contentValues.put(SESSION_TEMPERATURE, json.getDouble("Temperature"));
            contentValues.put(SESSION_TRAININGTYPEID, json.getString("TrainingTypeID"));
            contentValues.put(SESSION_SESSIONSTATUSID, json.getString("SessionStatusID"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "DatabaseHelper.insertSession");
        }


        this.getWritableDatabase().insertOrThrow(SESSION_TABLE, "", contentValues);
    }

    public JSONObject getUser(String username) {

        Cursor cursor = this.getReadableDatabase()
                .rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + USER_USERNAME + "='" + username + "';", null);
        cursor.moveToFirst();

        JSONObject json = new JSONObject();
        try {
            json.put("Username", cursor.getString(1));
            json.put("Password", cursor.getString(2));
            json.put("Role", "Atleta");
            json.put("Email", cursor.getString(3));
            json.put("Name", cursor.getString(4));
            json.put("LastName", cursor.getString(5));
            json.put("Sex", cursor.getString(6));
            json.put("BirthDate", cursor.getString(7));
            json.put("CityName", cursor.getString(8));
            json.put("CountryName", cursor.getString(9));
            json.put("TelephoneNumber", cursor.getString(10));
            json.put("Height", cursor.getDouble(11));
            json.put("Weight", cursor.getDouble(12));
            json.put("BikerType", cursor.getString(13));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "DatabaseHelper.getUser");
        }

        cursor.close();

        return json;
    }


    public JSONObject getSession(String sessionID) {

        Cursor cursor = this.getReadableDatabase()
                .rawQuery("SELECT * FROM " + SESSION_TABLE + " WHERE " + SESSION_SESSIONID + "='" + sessionID + "';", null);
        cursor.moveToFirst();

        JSONObject json = new JSONObject();
        try {
            json.put("SessionID", cursor.getString(1));
            json.put("UserID", cursor.getString(2));
            json.put("ClimateConditionID", cursor.getString(3));
            json.put("AverageBPM", cursor.getDouble(4));
            json.put("RouteID", cursor.getString(5));
            json.put("Coordinates", new JSONArray(cursor.getString(6)));
            json.put("CityName", cursor.getString(7));
            json.put("CountryName", cursor.getString(8));
            json.put("StartTime", cursor.getString(9));
            json.put("EndTime", cursor.getString(10));
            json.put("Distance", cursor.getDouble(11));
            json.put("BurntCalories", cursor.getDouble(12));
            json.put("RelativeHumidity", cursor.getDouble(13));
            json.put("Temperature", cursor.getDouble(14));
            json.put("TrainingTypeID", cursor.getString(15));
            json.put("SessionStatusID", cursor.getString(16));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "DatabaseHelper.getSession");
        }

        cursor.close();

        return json;
    }

    public JSONArray listSessions() {
        Cursor cursor = this.getReadableDatabase()
                .rawQuery("SELECT * FROM " + SESSION_TABLE + " WHERE " + SESSION_SESSIONSTATUSID + "='Pendiente';", null);

        JSONArray jsonArray = new JSONArray();
        JSONObject json;
        try {
            while(cursor.moveToNext()) {
                json = new JSONObject();
                json.put("SessionID", cursor.getString(1));
                json.put("TrainingTypeID", cursor.getString(15));
                jsonArray.put(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "DatabaseHelper.listSessions");
        }

        cursor.close();

        return jsonArray;
    }

    public void updateUser(JSONObject json) {

        try {
            String username = json.getString("Username");

            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_NAME + "='"+ json.getString("Name") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_LASTNAME + "='"+ json.getString("LastName") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_GENDER + "='"+ json.getString("Sex") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_BIRTHDATE + "='"+ json.getString("BirthDate") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_CITYNAME + "='"+ json.getString("CityName") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_COUNTRYNAME + "='"+ json.getString("CountryName") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_TELEPHONE + "='"+ json.getString("TelephoneNumber") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_HEIGHT + "='"+ json.getDouble("Height") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_WEIGHT + "='"+ json.getDouble("Weight") +"' WHERE " + USER_USERNAME + "='" + username + "';");
            this.getWritableDatabase().execSQL("UPDATE " + USER_TABLE + " SET " + USER_BIKERTYPE + "='"+ json.getString("BikerType") +"' WHERE " + USER_USERNAME + "='" + username + "';");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "DatabaseHelper.updateUser");
        }
    }

    public void updateSession(JSONObject json) {

        try {
            String sessionID = json.getString("SessionID");

            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_USERID + "='"+ json.getString("UserID") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_CLIMATECONDITIONID + "='"+ json.getString("ClimateConditionID") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_AVERAGEBPM + "='"+ json.getString("AverageBPM") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_STARTTIME + "='"+ json.getString("StartTime") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_ENDTIME + "='"+ json.getString("EndTime") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_DISTANCE + "='"+ json.getString("Distance") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_BURNTCALORIES + "='"+ json.getString("BurntCalories") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_RELATIVEHUMIDITY + "='"+ json.getString("RelativeHumidity") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_TEMPERATURE + "='"+ json.getString("Temperature") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
            this.getWritableDatabase().execSQL("UPDATE " + SESSION_TABLE + " SET " + SESSION_SESSIONSTATUSID + "='"+ json.getString("SessionStatusID") +"' WHERE " + SESSION_SESSIONID + "='" + sessionID + "';");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "DatabaseHelper.updateSession");
        }

    }

    public void deleteUser(String username) {
        this.getWritableDatabase().delete(USER_TABLE, USER_USERNAME + "='" + username + "'", null);
    }

    public void deleteSession(String sessionID) {
        this.getWritableDatabase().delete(SESSION_TABLE, SESSION_SESSIONID + "='" + sessionID + "'", null);
    }

}
