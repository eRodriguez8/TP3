package com.ort.mirtalapsenzon.taller5.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ort.mirtalapsenzon.taller5.R;

public class EditarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView nombre = (TextView) findViewById(R.id.txtnombre);
        TextView apellido = (TextView) findViewById(R.id.txtapellido);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        String nombreInt = getIntent().getExtras().getString("Nombre");
        String apellidoInt = getIntent().getExtras().getString("Apellido");

        nombre.setText(nombre.getText().toString()+ nombreInt);
        apellido.setText(apellido.getText().toString()+ apellidoInt);


    }

}
