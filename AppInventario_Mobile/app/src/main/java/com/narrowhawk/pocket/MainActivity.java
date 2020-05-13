package com.narrowhawk.pocket;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public static final String LEGAJO = "";
    public static final String DOCUMENTO = "";
    public static int STATUS_VALUE = 0;
    public static String SCAN_ERROR_TEXT = "Hubo un error al escanear. Por favor, intentelo nuevamente.";
    private Button btnLogin;
    private EditText user;
    private String legajo;

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
                try {
                    login(v);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(View view) throws ExecutionException, InterruptedException {
        user = findViewById(R.id.legajo);
        legajo = user.getText().toString();
        if(legajo.length() == 0) {
            Toast errorToast = Toast.makeText(MainActivity.this, "Por favor ingrese su número de legajo.", Toast.LENGTH_SHORT);
            errorToast.show();
        } else {
            //Some url endpoint that you may have
            String myUrl = "https://c4f99f75.ngrok.io/Sua.Inventario.Api/api/v1/ConteoSega/" + legajo;
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            HttpGetRequest getRequest = new HttpGetRequest();
            //Perform the doInBackground method, passing in our url
            result = getRequest.execute(myUrl).get();

            if(result == null) {
                Toast errorToast = Toast.makeText(MainActivity.this, "Legajo " + legajo + " no contiene documento asociado", Toast.LENGTH_SHORT);
                errorToast.show();
            } else {
                Intent dataIntent = new Intent(MainActivity.this, ScanActivity.class);
                Bundle dataBundle = new Bundle();
                dataIntent.putExtra("DOCUMENTO", result);
                dataIntent.putExtra("LEGAJO", legajo);
                startActivity(dataIntent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
