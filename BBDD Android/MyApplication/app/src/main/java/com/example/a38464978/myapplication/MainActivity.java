package com.example.a38464978.myapplication;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button botonIngreso;
	private Button botonConsulta;
	private TextView textoCodigo;
	private TextView textoNombre;
	
	private SQLiteDatabase db;
	private BasedeDatos b;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		botonIngreso = (Button) findViewById(R.id.ingreso);
		botonConsulta = (Button) findViewById(R.id.verdatos);
		textoCodigo = (TextView) findViewById(R.id.codigo);
		textoNombre = (TextView) findViewById(R.id.nombre);
	
		// creamos la base de datos
		b = new BasedeDatos(this, "Ejemplo", null, 1);
		// la abrimos en modo escritura
		db = b.getWritableDatabase();
		
		botonIngreso.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// recuperamos los valores de los campos de texto
				String codigo = textoCodigo.getText().toString();
				String descripcion = textoNombre.getText().toString();
				
				if (!codigo.isEmpty()) {
					// creamos un ContentValue 
					ContentValues nuevoRegistro = new ContentValues();
					// insertamos los datos en el ContentValues
					nuevoRegistro.put("codigo", codigo);
					nuevoRegistro.put("nombre", descripcion);
					
					// insertamos en la base
					db.insert("Ejemplo", null, nuevoRegistro);
				
				
					// limpiamos los TextView
					textoCodigo.setText("");
					textoNombre.setText("");
					}else{
				
				Toast.makeText(getApplicationContext(),"el codigo no puede estar vacio" , Toast.LENGTH_SHORT).show();
					}
			}
		});
		
		botonConsulta.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent i = new Intent(getApplicationContext(), GridActivity.class);
				startActivity(i);
			}
		});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}