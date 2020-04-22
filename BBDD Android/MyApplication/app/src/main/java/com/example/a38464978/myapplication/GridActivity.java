package com.example.a38464978.myapplication;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class GridActivity extends Activity {
	private SQLiteDatabase db;
	private BasedeDatos b;
	public static ArrayList<String> ArrayofName = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
	
		// creamos la base de datos
		b = new BasedeDatos(this, "Ejemplo", null, 2);
		// la abrimos en modo lectura
		db = b.getReadableDatabase();
		
		//obtengo todos los registros de la tabla en un arraylist    
		 final ArrayList<Ejemplo> ejemplo = (ArrayList<Ejemplo>) b.getAllContacts();
	        //Array que asociaremos al adaptador
	        GridView gridview = (GridView) findViewById(R.id.gridView1);// crear el
	        // gridview a partir del elemento del xml gridview
	//paso al adaptador solo los datos que quiero mostrar en la grilla
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_1, ArrayofName);
	        //seteo el adatador a la grilla
	        gridview.setAdapter(adapter);

	        gridview.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v,
	                int position, long id) {
	           //voy a mostrar el row_id de la tabla
	            	Toast.makeText(getApplicationContext(),"el row_id es: " +ejemplo.get(position).get_id(), Toast.LENGTH_SHORT).show();
	            	db.close();
	           //creo el intent para ir a edicion
	            	Intent i = new Intent(getApplicationContext(), EditarActivity.class);
	            // cargo los valores que le paso al intent
	            	//id
	            	i.putExtra("_id", ejemplo.get(position).get_id());
	            	i.putExtra("codigo", ejemplo.get(position).getCodigo());
	            	i.putExtra("nombre", ejemplo.get(position).getNombre());
	            startActivity(i);
	            }
	        });     
db.close();	        
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
