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
import com.narrowhawk.pocket.activities.AnyOrientationCaptureActivity;
import com.narrowhawk.pocket.activities.ResultsActivity;

public class ScanActivity extends AppCompatActivity {
    public static final String BARCODE = "";
    public static String SCAN_ERROR_TEXT = "Hubo un error al escanear. Por favor, intentelo nuevamente.";
    private Button btnScan;
    private TextView welcome;
    private TextView document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        final Bundle data = getIntent().getExtras();
        welcome = findViewById(R.id.welcome);
        welcome.setText("Bienvenido/a " + data.getString("LEGAJO"));
        document = findViewById(R.id.document);
        document.setText("Documento " + data.getString("DOCUMENTO"));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.btnScan = findViewById(R.id.btnLogin);
        this.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeScanner(v);
            }
        });
    }

    public void initializeScanner(View view)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
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
                this.BuildAndSendData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void BuildAndSendData(String data)
    {
        Intent dataIntent = new Intent(this, ResultsActivity.class);
        Bundle dataBundle = new Bundle();

        dataBundle.putString(BARCODE, data);

        dataIntent.putExtras(dataBundle);

        startActivity(dataIntent);
    }
}
