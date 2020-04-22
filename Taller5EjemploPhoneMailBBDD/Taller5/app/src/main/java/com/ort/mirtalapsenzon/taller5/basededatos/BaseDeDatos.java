package com.ort.mirtalapsenzon.taller5.basededatos;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.ort.mirtalapsenzon.taller5.activities.DetailActivity;
import com.ort.mirtalapsenzon.taller5.entities.Persona;

public class BaseDeDatos extends SQLiteOpenHelper {
 private String tabla = "create table if not exists ejemplo (\n" +
         "_ID integer PRIMARY KEY autoincrement,\n" +
         "nombre text,\n" +
         "apellido text )";

    public BaseDeDatos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     db.execSQL(tabla);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE EJEMPLO");
        db.execSQL(tabla);

    }

    public List<Persona> getAllContacts() {

        List<Persona> personaList = new ArrayList<Persona>();
        // Select All Query
        String selectQuery = "SELECT  * FROM ejemplo order by apellido";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // limpio el array antes de cargarlo con los datos de la tabla
        DetailActivity.arrayOfPersona.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Persona persona = new Persona(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));

                // utilizo esto para luego mostar solo los campos deseados en el
                // adaptardor del gridview
                String string = cursor.getString(1) + " " + cursor.getString(2);

                DetailActivity.arrayOfPersona.add(string);

                // Adding contact to list
                personaList.add(persona);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return personaList;
    }


}
