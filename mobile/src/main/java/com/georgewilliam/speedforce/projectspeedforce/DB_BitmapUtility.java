/**
 * @file DB_BitmapUtility.java
 * @brief Fuente de la clase DB_BitmapUtility
 */
package com.georgewilliam.speedforce.projectspeedforce;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Clase para el manejo de Bitmaps entre actividades y bases de datos.
 */
public class DB_BitmapUtility {

    /**
     * Convierte un Bitmap en un Arreglo de Bytes.
     * @param bitmap Bitmap con la imagen a convertir en arreglo de bytes.
     * @return Arreglo de bytes de la imagen.
     */
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Convierte un Arreglo de Bytes en un Bitmap.
     * @param image Arreglo de bytes a convertir en Bitmap.
     * @return Bitmap con imagen contenida en el arreglo de bytes.
     */
    public static Bitmap getBitmapFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Convierte un string base 64 a un Bitmap.
     * @param str64 String base 64 a convertir.
     * @return Bitmap resultante.
     */
    public static Bitmap getBitmapFromString64(String str64) {
        byte[] image = getBytesFromString64(str64);
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Convierte bitmap a un string de base 64.
     * @param bitmap a convertir.
     * @return String de base 64 resultante.
     */
    public static String getString64FromBitmap(Bitmap bitmap) {
        byte[] bytes = getBytesFromBitmap(bitmap);
        return getString64FromBytes(bytes);
    }

    /**
     * Convierte un arreglo de bytes a un string de base 64.
     * @param bytes Arreglo de bytes a convertir a string base 64.
     * @return String de base 64 resultante.
     */
    public static String getString64FromBytes(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Convierte un string base 64 a un arreglo de bytes.
     * @param str64 String a convertir a arreglo de bytes.
     * @return Arreglo de bytes resultante.
     */
    public static byte[] getBytesFromString64(String str64) {
        return  Base64.decode(str64, Base64.DEFAULT);
    }

}
