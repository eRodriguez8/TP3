package com.example.a38464978.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EditarActivity extends Activity {
	private Button botonEditar;
	private TextView textoCodigo;
	private TextView textoNombre;
	
	private SQLiteDatabase db;
	private BasedeDatos b;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar);
		
		botonEditar = (Button) findViewById(R.id.editar);
		textoCodigo = (TextView) findViewById(R.id.codigoEdit);
		textoNombre = (TextView) findViewById(R.id.nombreEdit);
		
		// creamos la base de datos
		b = new BasedeDatos(this, "Ejemplo", null, 2);
		// la abrimos en modo escritura
		db = b.getWritableDatabase();
		
		//obtengo los campos que vienen del intent
		
		Bundle bundle = this.getIntent().getExtras();
		
		bundle.getInt("_id");
		final int id = bundle.getInt("_id");;
		int codigo = bundle.getInt("codigo");;
		String nombre =  bundle.getString("nombre");
		
		//seteo los valores en los text view
		textoCodigo.setText(Integer.toString(codigo));
		textoNombre.setText(nombre);
		
		botonEditar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String codigo = textoCodigo.getText().toString();
				String nombre = textoNombre.getText().toString();
				
				if (!codigo.isEmpty()) {
					//edito
					db.execSQL("Update Ejemplo set codigo="+codigo+",nombre='"+nombre+"' where _id="+id);
					db.close();
					Toast.makeText(getApplicationContext(),"se edito el registro con id: "+id , Toast.LENGTH_SHORT).show();
					Intent i = new Intent(getApplicationContext(),GridActivity.class);
					startActivity(i);
				}else{
					//emito msg cod vacio
					Toast.makeText(getApplicationContext(),"el codigo no puede estar vacio" , Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editar, menu);
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
