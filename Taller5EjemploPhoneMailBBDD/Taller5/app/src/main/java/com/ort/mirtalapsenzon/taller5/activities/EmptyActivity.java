package com.ort.mirtalapsenzon.taller5.activities;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ort.mirtalapsenzon.taller5.R;
import com.ort.mirtalapsenzon.taller5.basededatos.BaseDeDatos;

public class EmptyActivity extends AppCompatActivity {
    private Button botonEditar;
    private EditText textoApellido;
    private EditText textoNombre;

    private SQLiteDatabase db;
    private BaseDeDatos b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);


    }
}
