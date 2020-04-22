package com.ort.mirtalapsenzon.taller5.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.GridView;
import android.widget.Toast;
import android.net.Uri;
import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.ort.mirtalapsenzon.taller5.R;
import com.ort.mirtalapsenzon.taller5.basededatos.BaseDeDatos;
import com.ort.mirtalapsenzon.taller5.entities.Persona;

public class DetailActivity extends AppCompatActivity {

    public static ArrayList<String> arrayOfPersona = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnVolver = (Button) findViewById(R.id.btnVolver);
        Button btnDial = (Button) findViewById(R.id.btnDial);

        TextView txtDetalle = (TextView) findViewById(R.id.lbResultado);
        TextView txtResultBBDD = (TextView) findViewById(R.id.resultBBDD);

        String nombre = getIntent().getExtras().getString("nombre");
        String apellido = getIntent().getExtras().getString("apellido");

        BaseDeDatos  mDbHelper= new BaseDeDatos(getApplicationContext(),"ejemplo",null,1);
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM ejemplo",null);
String datos ="";
       cursor.moveToFirst();
        do {

            String nombreBBDD = cursor.getString(1);
            String apellidoBBDD = cursor.getString(2);
            datos=" "+datos+","+nombreBBDD+" "+apellidoBBDD;

        }while (cursor.moveToNext());

        txtDetalle.setText(txtDetalle.getText().toString()+ nombre + " " + apellido);
        txtResultBBDD.setText(datos);
/*grilla*/
       //obtengo todos los registros de la tabla en un arraylist
        final ArrayList<Persona> persona = (ArrayList<Persona>) mDbHelper.getAllContacts();
        //Array que asociaremos al adaptador
        GridView gridview = (GridView) findViewById(R.id.gridBBDD);// crear el
        // gridview a partir del elemento del xml gridview
        //paso al adaptador solo los datos que quiero mostrar en la grilla

        // gridview a partir del elemento del xml gridview
        //paso al adaptador solo los datos que quiero mostrar en la grilla
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayOfPersona);
        //seteo el adatador a la grilla
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //voy a mostrar el row_id de la tabla
                Toast.makeText(getApplicationContext(),"el row_id es: " +persona.get(position).getId(), Toast.LENGTH_SHORT).show();
                db.close();
                //creo el intent para ir a edicion
                Intent i = new Intent(getApplicationContext(), EditarActivity.class);
                // cargo los valores que le paso al intent
                //id
                i.putExtra("_id", persona.get(position).getId());
                i.putExtra("Nombre", persona.get(position).getNombre());
                i.putExtra("Apellido", persona.get(position).getApellido());
                startActivity(i);
            }
        });
        db.close();


        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:5551234");
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
        Toast toast = Toast.makeText(getApplicationContext(),"Hola!, Como estas??",Toast.LENGTH_LONG);
        toast.show();
    }

}
