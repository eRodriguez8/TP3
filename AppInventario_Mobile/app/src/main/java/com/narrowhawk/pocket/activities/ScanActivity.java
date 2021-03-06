package com.narrowhawk.pocket.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.narrowhawk.pocket.R;
import com.narrowhawk.pocket.requests.HttpRequest;
import com.narrowhawk.pocket.utils.CustomHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ScanActivity extends AppCompatActivity {
    public static final String BARCODE = "";
    public static String SCAN_ERROR_TEXT = "Hubo un error al escanear. Por favor, intentelo nuevamente.";
    private ImageButton btnScanDigit;
    private ImageButton btnScanArticle;
    private ImageButton btnSend;
    private String legajo;
    private String tipoInventario;
    private TextView user;
    private TextView document;
    private TextView ubication;
    private TextView conteo;
    private TextInputLayout articulo;
    private TextInputLayout digito;
    private TextInputLayout cajas;
    private TextInputLayout cajasSueltas;
    private TextInputLayout observaciones;
    private int contador;
    private int posiciones;
    private JSONObject documentJson = null;
    private JSONArray jsonArray = null;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Thread.setDefaultUncaughtExceptionHandler(new CustomHandler(this));

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
                    btnSendOnCreate(v);
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

    private void btnSendOnCreate(View v) throws InterruptedException, ExecutionException, JSONException {
        if (digito.getEditText().getText().toString().equals("")) {
            digito.setError("Ingrese dígito.");
        } else if (!digito.getEditText().getText().toString().equals(".")) {
            if (cajas.getEditText().getText().toString().equals("")) {
                cajas.setError("Ingrese cajas.");
            } else if (cajasSueltas.getEditText().getText().toString().equals("") && cajasSueltas.isEnabled()) {
                cajasSueltas.setError("Ingrese cajas sueltas.");
            } else {
                //int error = 8/0;
                sendData(v);
            }
        } else {
            sendData(v);
        }
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
            articulo.getEditText().setText(data);
        } else {
            digito.getEditText().setText(data);
            if (data.equals(json.getString("etiqueta"))) {
                articulo.getEditText().setText(json.getString("ean13") + " - " + json.getString("descripcion"));
            }
        }
    }

    private void sendData(View view) throws JSONException, ExecutionException, InterruptedException {
        // Llega la data del id actual
        JSONObject json = jsonArray.getJSONObject(contador - 1);

        // La procesamos y enviamos a la API
        String myUrl = "https://2860aa3af213.ngrok.io/Sua.Inventario.Api/api/v1/ConteoSega/xPosicion";

        JSONObject body = new JSONObject();
        body.put("id", json.getInt("id"));
        body.put("idDocumento", json.getInt("idDocumento"));
        body.put("usuarioInventario", legajo);
        body.put("digito", digito.getEditText().getText());
        body.put("cajasSueltas", cajasSueltas.getEditText().getText());
        body.put("tipoInventario", json.getString("tipoInventario"));
        body.put("observaciones", observaciones.getEditText().getText());
        body.put("camadas", cajas.getEditText().getText().toString().matches("") ? 0 : cajas.getEditText().getText());
        body.put("articulo", articulo.getEditText().getText());
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
        digito.getEditText().setText("");
        articulo.getEditText().setText("");
        cajas.getEditText().setText("");
        cajasSueltas.getEditText().setText("");
        observaciones.getEditText().setText("");
        if (!json.getString("tipoInventario").equals("Camadas")) {
            cajasSueltas.setEnabled(false);
            cajasSueltas.setBackgroundColor(Color.LTGRAY);
        } else {
            cajasSueltas.setEnabled(true);
            cajasSueltas.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
