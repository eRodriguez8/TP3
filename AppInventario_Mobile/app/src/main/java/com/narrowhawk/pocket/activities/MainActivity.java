package com.narrowhawk.pocket.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.narrowhawk.pocket.requests.HttpRequest;
import com.narrowhawk.pocket.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private JSONArray jsonArray;
    private Button btnLogin;
    private TextInputLayout user;
    private String legajo;
    private JSONObject documentJson;
    private String document;
    private int lContadas;
    private int lTotales;
    private String result;
    private int documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        user = findViewById(R.id.legajo);
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
        legajo = user.getEditText().getText().toString();
        if (legajo.length() == 0) {
            user.setError("Por favor ingrese su número de legajo.");
        } else {
            String myUrl = "https://0cff4270f79d.ngrok.io/Sua.Inventario.Api/api/v1/ConteoSega/" + legajo;
            HttpRequest getRequest = new HttpRequest("GET", null);
            result = getRequest.execute(myUrl).get();

            if (result == null) {
                Toast errorToast = Toast.makeText(MainActivity.this, "Legajo " + legajo + " no contiene documento asociado", Toast.LENGTH_SHORT);
                errorToast.show();
            } else {
                documentJson = new JSONObject(result);
                try {
                    jsonArray = documentJson.getJSONArray("posiciones");
                    document = documentJson.getString("documento");
                    documentId = documentJson.getInt("id");
                    lContadas = documentJson.getInt("lContadas");
                    lTotales = documentJson.getInt("lTotales");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (lContadas > 0) {
                    showContinueDialog();
                } else {
                    try {
                        goToScanActivity(lContadas);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void showContinueDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_continue, null));
        builder.setPositiveButton("Sí, continuar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    goToScanActivity(lContadas);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        })
                .setNegativeButton("No, empezar de cero", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String myUrl = "https://0cff4270f79d.ngrok.io/Sua.Inventario.Api/api/v1/ConteoSega/" + documentId;

                        HttpRequest putRequest = new HttpRequest("PUT", "");
                        try {
                            putRequest.execute(myUrl).get();
                            goToScanActivity(0);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
    }

    public void goToScanActivity(int index) throws JSONException {
        JSONObject json = jsonArray.getJSONObject(index); //lContadas

        Intent dataIntent = new Intent(MainActivity.this, ScanActivity.class);
        dataIntent.putExtra("DOCUMENTO", document);
        dataIntent.putExtra("LCONTADAS", index); //lContadas
        dataIntent.putExtra("LEGAJO", legajo);
        dataIntent.putExtra("ID_POS_INICIAL", json.getInt("id"));
        dataIntent.putExtra("UBICACION_INICIAL", json.getString("ubicacion"));
        dataIntent.putExtra("TIPO_INVENTARIO", json.getString("tipoInventario"));
        dataIntent.putExtra("POSICIONES", lTotales);
        dataIntent.putExtra("RESULT", result);
        startActivity(dataIntent);
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
