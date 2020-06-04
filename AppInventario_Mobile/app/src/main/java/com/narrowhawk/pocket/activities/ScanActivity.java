package com.narrowhawk.pocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.narrowhawk.pocket.R;
import com.narrowhawk.pocket.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ScanActivity extends AppCompatActivity {
    public static final String BARCODE = "";
    public static String SCAN_ERROR_TEXT = "Hubo un error al escanear. Por favor, intentelo nuevamente.";
    private Button btnScanDigit;
    private Button btnScanArticle;
    private Button btnSend;
    private String legajo;
    private TextView user;
    private TextView document;
    private TextView ubication;
    private TextView conteo;
    private TextView articulo;
    private TextView digito;
    private TextView cajas;
    private TextView cajasSueltas;
    private TextView observaciones;
    private int contador;
    private int posiciones;
    private JSONObject documentJson = null;
    private JSONArray jsonArray = null;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        final Bundle data = getIntent().getExtras();
        legajo = data.getString("LEGAJO");
        user = findViewById(R.id.user);
        user.setText(legajo);
        document = findViewById(R.id.document);
        document.setText(data.getString("DOCUMENTO"));
        contador = data.getInt("LCONTADAS") + 1; //lo que recibo más 1
        ubication = findViewById(R.id.ubication);
        ubication.setText(data.getString("UBICACION_INICIAL") + " - " + contador);
        conteo = findViewById(R.id.conteo);
        posiciones = data.getInt("POSICIONES");
        conteo.setText("Conteo " + contador + "/" + posiciones);
        articulo = findViewById(R.id.txtArticle);
        digito = findViewById(R.id.txtDigit);
        cajas = findViewById(R.id.txtBoxes);
        cajasSueltas = findViewById(R.id.txtEmptyBoxes);
        observaciones = findViewById(R.id.txtObservations);
        try {
            documentJson = new JSONObject(data.getString("RESULT"));
            jsonArray = documentJson.getJSONArray("posiciones");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.btnScanDigit = findViewById(R.id.btnScanDigit);
        this.btnScanDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                initializeScanner(v);
            }
        });
        this.btnScanArticle = findViewById(R.id.btnScanArticle);
        this.btnScanArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                initializeScanner(v);
            }
        });
        this.btnSend = findViewById(R.id.btnSend);
        this.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendData(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initializeScanner(View view)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_128);
        integrator.setPrompt("Escanear Elemento");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, SCAN_ERROR_TEXT, Toast.LENGTH_LONG).show();
            } else {
                try {
                    this.BuildAndSendData(result.getContents());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void BuildAndSendData(String data) throws JSONException {
        JSONObject json = jsonArray.getJSONObject(contador - 1);
        if (flag) {
            articulo.setText(data);
        } else {
            digito.setText(data);
            if (data.equals(json.getString("etiqueta"))) {
                articulo.setText(json.getString("ean13") + " - " + json.getString("descripcion"));
            }
        }
    }

    private void sendData(View view) throws JSONException, ExecutionException, InterruptedException {
        // Llega la data del id actual
        JSONObject json = jsonArray.getJSONObject(contador - 1);

        // La procesamos y enviamos a la API
        String myUrl = "https://0d93ee4e32f0.ngrok.io/Sua.Inventario.Api/api/v1/ConteoSega/xPosicion";

        Toast.makeText(this, "antes del body", Toast.LENGTH_LONG).show();

        JSONObject body = new JSONObject();
        body.put("id", json.getInt("id"));
        body.put("idDocumento", json.getInt("idDocumento"));
        body.put("usuarioInventario", legajo);
        body.put("digito", digito.getText());
        body.put("cajas", cajas.getText());
        body.put("cajasSueltas", cajasSueltas.getText());
        body.put("tipoInventario", json.getString("tipoInventario"));
        body.put("observaciones", observaciones.getText());
        body.put("camadas", json.getInt("camadas"));
        body.put("articulo", articulo.getText());
        body.put("etiqueta", json.getString("etiqueta"));
        body.put("ubicacion", json.getString("ubicacion"));
        body.put("ean13", json.getString("ean13"));
        body.put("descripcion", json.getString("descripcion"));
        body.put("registroTotal", 2);//json.getInt("posiciones")); // ACA VER PORQUE PINCHA
        body.put("registroCargado", 1);//json.getInt("contador")); // ACA VER PORQUE PINCHA

        String bodyString = body.toString();


        String result;
        HttpRequest putRequest = new HttpRequest("PUT", bodyString);
        result = putRequest.execute(myUrl).get();

        Toast.makeText(this, "Resultado PUT : " + result, Toast.LENGTH_LONG).show();


        // Incremento al ID siguiente
        contador++;
        //validar que no me pasé, si me pasé porque fue la última, VER QUE HACEMOS
        if (contador > posiciones) {
            Toast.makeText(this, "terminé", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(this, String.valueOf(json.getInt("id")), Toast.LENGTH_LONG).show();

        //Actualizo data en la pantalla
        json = jsonArray.getJSONObject(contador - 1);
        conteo.setText("Conteo " + contador + "/" + posiciones);
        ubication.setText(json.getString("ubicacion") + " - " + contador);
        digito.setText("");
        articulo.setText("");
        cajas.setText("");
        cajasSueltas.setText("");
        observaciones.setText("");


        /*Intent dataIntent = new Intent(this, ResultsActivity.class);
        Bundle dataBundle = new Bundle();

        dataBundle.putString(BARCODE, data);

        dataIntent.putExtras(dataBundle);

        startActivity(dataIntent);*/
    }
}
