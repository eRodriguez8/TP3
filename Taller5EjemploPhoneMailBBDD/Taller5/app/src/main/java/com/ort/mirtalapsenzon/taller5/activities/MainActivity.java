package com.ort.mirtalapsenzon.taller5.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.ort.mirtalapsenzon.taller5.R;
import com.ort.mirtalapsenzon.taller5.basededatos.BaseDeDatos;
import com.ort.mirtalapsenzon.taller5.entities.Persona;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnCLick =  (Button) findViewById(R.id.btnPag2);
       final TextInputLayout txtNombre = (TextInputLayout) findViewById(R.id.nombre);
       final TextInputLayout txtApellido = (TextInputLayout) findViewById(R.id.apellido);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL  , "cosme.fulanito@fox.com");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DUFF");
                emailIntent.putExtra(Intent.EXTRA_TEXT   , "Mi Vieja Mula ya no es lo que era...");
                emailIntent.setType("plain/text");

               try{ //que pasa si no hay ninguna app de correo????
                startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));

            }catch (Exception e){ //ActivityNOtFoudExc....

//Toast...
               }

               }
        });


        btnCLick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //   if (valido){

                try {
                    Persona persona = new Persona(txtNombre.getEditText().getText().toString(),txtApellido.getEditText().getText().toString());

                        Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("nombre", txtNombre.getEditText().getText().toString());
                    mBundle.putString("apellido", txtApellido.getEditText().getText().toString());

                    BaseDeDatos baseDeDatos =new BaseDeDatos(getApplicationContext(),"ejemplo",null,1);
                    SQLiteDatabase conn = baseDeDatos.getWritableDatabase();
                    intent.putExtras(mBundle);

                    String nombre = txtNombre.getEditText().getText().toString();
                    String apellido = txtApellido.getEditText().getText().toString();

                    conn.execSQL("INSERT INTO ejemplo (nombre,apellido) values ('"+nombre+"','"+apellido+"')");

                    conn.close();
                    startActivity(intent);




                }catch (IllegalArgumentException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT);
                }



            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"Op 1",Toast.LENGTH_SHORT).show();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {
            Toast.makeText(getApplicationContext(),"Op 2",Toast.LENGTH_SHORT).show();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings3) {
            Toast.makeText(getApplicationContext(),"Op 3",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
