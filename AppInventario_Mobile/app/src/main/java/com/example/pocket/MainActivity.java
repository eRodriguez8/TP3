package com.example.pocket;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String LEGAJO = "";
    public static int STATUS_VALUE = 0;
    public static String SCAN_ERROR_TEXT = "Hubo un error al escanear. Por favor, intentelo nuevamente.";
    private Button btnLogin;
    private EditText legajo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
    }

    public void login(View view)
    {
        legajo = findViewById(R.id.legajo);
        if(legajo.getText().toString().length() == 0) {
            Toast errorToast = Toast.makeText(MainActivity.this, "Por favor ingrese su número de legajo.", Toast.LENGTH_SHORT);
            errorToast.show();
        } else {
            Intent dataIntent = new Intent(MainActivity.this, ScanActivity.class);
            Bundle dataBundle = new Bundle();
            dataBundle.putString(LEGAJO, legajo.getText().toString());
            dataIntent.putExtras(dataBundle);
            startActivity(dataIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
