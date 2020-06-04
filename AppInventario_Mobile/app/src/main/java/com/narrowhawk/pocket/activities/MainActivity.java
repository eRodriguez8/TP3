package com.narrowhawk.pocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.narrowhawk.pocket.requests.HttpRequest;
import com.narrowhawk.pocket.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        this.btnLogin = findViewById(R.id.btnSend);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login(v);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(View view) throws ExecutionException, InterruptedException, JSONException {
        user = findViewById(R.id.legajo);
        legajo = user.getText().toString();
        if(legajo.length() == 0) {
            Toast errorToast = Toast.makeText(MainActivity.this, "Por favor ingrese su número de legajo.", Toast.LENGTH_SHORT);
            errorToast.show();
        } else {
            String myUrl = "https://0d93ee4e32f0.ngrok.io/Sua.Inventario.Api/api/v1/ConteoSega/" + legajo;
            String result;
            HttpRequest getRequest = new HttpRequest("GET", null);
            result = getRequest.execute(myUrl).get();

            if(result == null) {
                Toast errorToast = Toast.makeText(MainActivity.this, "Legajo " + legajo + " no contiene documento asociado", Toast.LENGTH_SHORT);
                errorToast.show();
            } else {
                JSONObject documentJson = new JSONObject(result);
                JSONArray jsonArray = documentJson.getJSONArray("posiciones");
                String document = documentJson.getString("documento");
                int lContadas = documentJson.getInt("lContadas");
                int lTotales = documentJson.getInt("lTotales");

                JSONObject json = jsonArray.getJSONObject(lContadas); //lContadas

                Intent dataIntent = new Intent(MainActivity.this, ScanActivity.class);
                Bundle dataBundle = new Bundle();
                dataIntent.putExtra("DOCUMENTO", document);
                dataIntent.putExtra("LCONTADAS", lContadas);
                dataIntent.putExtra("LEGAJO", legajo);
                dataIntent.putExtra("ID_POS_INICIAL", json.getInt("id"));
                dataIntent.putExtra("UBICACION_INICIAL", json.getString("ubicacion"));
                dataIntent.putExtra("POSICIONES", lTotales);
                dataIntent.putExtra("RESULT", result);
                startActivity(dataIntent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
