package com.narrowhawk.pocket.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.client.android.Intents;
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
    private String tipoInventario;
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
        tipoInventario = data.getString("TIPO_INVENTARIO");
        if (!tipoInventario.equals("Camadas")) {
            cajasSueltas.setEnabled(false);
            cajasSueltas.setBackgroundColor(Color.LTGRAY);
        }
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
                    Toast errorToast;
                    if (digito.getText().toString().equals("")) {
                        errorToast = Toast.makeText(ScanActivity.this, "Por favor ingrese dígito", Toast.LENGTH_LONG);
                        errorToast.show();
                    } else if (!digito.getText().toString().equals(".")) {
                        if (cajas.getText().toString().equals("")) {
                            errorToast = Toast.makeText(ScanActivity.this, "Por favor ingrese cajas", Toast.LENGTH_LONG);
                            errorToast.show();
                        } else if (cajasSueltas.getText().toString().equals("") && cajasSueltas.isEnabled()) {
                            errorToast = Toast.makeText(ScanActivity.this, "Por favor ingrese cajas sueltas", Toast.LENGTH_LONG);
                            errorToast.show();
                        } else {
                            sendData(v);
                        }
                    } else {
                        sendData(v);
                    }
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

    public void initializeScanner(View view) {
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
        if (result != null) {
            if (result.getContents() == null) {
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
        String myUrl = "https://fdc3cc72568d.ngrok.io/Sua.Inventario.Api/api/v1/ConteoSega/xPosicion";

        JSONObject body = new JSONObject();
        body.put("id", json.getInt("id"));
        body.put("idDocumento", json.getInt("idDocumento"));
        body.put("usuarioInventario", legajo);
        body.put("digito", digito.getText());
        body.put("cajasSueltas", cajasSueltas.getText());
        body.put("tipoInventario", json.getString("tipoInventario"));
        body.put("observaciones", observaciones.getText());
        body.put("camadas", cajas.getText().toString().matches("") ? 0 : cajas.getText());
        body.put("articulo", articulo.getText());
        body.put("etiqueta", json.getString("etiqueta"));
        body.put("ubicacion", json.getString("ubicacion"));
        body.put("ean13", json.getString("ean13"));
        body.put("descripcion", json.getString("descripcion"));
        body.put("registroTotal", posiciones);
        body.put("registroCargado", contador);

        String bodyString = body.toString();

        String result;
        HttpRequest putRequest = new HttpRequest("PUT", bodyString);
        result = putRequest.execute(myUrl).get();

        // Incremento al ID siguiente
        contador++;
        //validar que no me pasé, si me pasé porque fue la última, VER QUE HACEMOS
        if (contador > posiciones) {
            Toast.makeText(this, "Conteo finalizado.", Toast.LENGTH_LONG).show();
            Intent dataIntent = new Intent(this, MainActivity.class);
            startActivity(dataIntent);
        }

        //Actualizo data en la pantalla
        json = jsonArray.getJSONObject(contador - 1);
        conteo.setText("Conteo " + contador + "/" + posiciones);
        ubication.setText(json.getString("ubicacion") + " - " + contador);
        digito.setText("");
        articulo.setText("");
        cajas.setText("");
        cajasSueltas.setText("");
        observaciones.setText("");
        if (!json.getString("tipoInventario").equals("Camadas")) {
            cajasSueltas.setEnabled(false);
            cajasSueltas.setBackgroundColor(Color.LTGRAY);
        } else {
            cajasSueltas.setEnabled(true);
            cajasSueltas.setBackgroundColor(Color.WHITE);
        }
    }
}
