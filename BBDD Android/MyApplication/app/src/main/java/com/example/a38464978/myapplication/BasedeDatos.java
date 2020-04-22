package com.example.a38464978.myapplication;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BasedeDatos extends SQLiteOpenHelper {
	private static final String TABLE_CONTACTS = "Ejemplo";
	// string con la instruccion para crear la base de datos
	String sqlCreate = "CREATE TABLE Ejemplo(_id INTEGER PRIMARY KEY AUTOINCREMENT, codigo INTEGER, nombre TEXT)";

	public BasedeDatos(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// se crea la base de datos
		db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// En este ejemplo se pierden todos los datos de la tabla
		// se deber�a hacer un bkup
		db.execSQL("DROP TABLE IF EXISTS Ejemplo");
		db.execSQL(sqlCreate);
	}

	public List<Ejemplo> getAllContacts() {

		List<Ejemplo> ejemploList = new ArrayList<Ejemplo>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " order by codigo";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// limpio el array antes de cargarlo con los datos de la tabla
		GridActivity.ArrayofName.clear();
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Ejemplo ejemplo = new Ejemplo();
				ejemplo.set_id(Integer.parseInt(cursor.getString(0)));
				ejemplo.setCodigo(Integer.parseInt(cursor.getString(1)));
				ejemplo.setNombre(cursor.getString(2));

				// utilizo esto para luego mostar solo los campos deseados en el
				// adaptardor del gridview
				String string = cursor.getString(1) + " " + cursor.getString(2);

				GridActivity.ArrayofName.add(string);

				// Adding contact to list
				ejemploList.add(ejemplo);
			} while (cursor.moveToNext());
		}
		cursor.close();
		// return contact list
		return ejemploList;
	}

}