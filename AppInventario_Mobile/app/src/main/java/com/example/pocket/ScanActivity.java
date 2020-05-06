package com.example.pocket;

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

import org.json.JSONObject;

public class ScanActivity extends AppCompatActivity {
    public static String PERSONNAME_KEY = "PersonName";
    public static String ID_KEY = "ID";
    public static String STATUS_KEY = "Status";
    public static String STATUS_BOOL_KEY = "StatusBool";
    public static int LASTNAME_ARRAY_KEY = 1;
    public static int FIRSTNAME_ARRAY_KEY = 2;
    public static int ID_ARRAY_KEY = 4;
    public static String CHAR_SPLIT = "@";
    public static int STATUS_VALUE = 0;
    public static String SCAN_ERROR_TEXT = "Hubo un error al escanear. Por favor, intentelo nuevamente.";
    private Button btnScan;
    private TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        final Bundle data = getIntent().getExtras();
        welcome = findViewById(R.id.welcome);
        welcome.setText("Bienvenido/a " + data.getString(MainActivity.LEGAJO));
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
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PDF_417);
        integrator.setPrompt("Escanear Elemento");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
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
        String personName;
        String lastName;
        String firstName;
        String id;
        String status = "";
        Boolean statusBool;
        JSONObject array;
        String[] parsedData = data.split(CHAR_SPLIT);

        if (parsedData.length == 9) // New Argentinian ID
        {
            lastName = parsedData[LASTNAME_ARRAY_KEY];
            firstName = parsedData[FIRSTNAME_ARRAY_KEY];
            id = parsedData[ID_ARRAY_KEY];
        }
        else // Old Argentinian ID
        {
            lastName = parsedData[4];
            firstName = parsedData[5];
            id = parsedData[1].trim();
        }
        personName = lastName + " " + firstName;statusBool = STATUS_VALUE > 0;

        Intent dataIntent = new Intent(this, ResultsActivity.class);
        Bundle dataBundle = new Bundle();

        dataBundle.putString(PERSONNAME_KEY, personName);
        dataBundle.putString(ID_KEY, id);
        dataBundle.putString(STATUS_KEY, status);
        dataBundle.putBoolean(STATUS_BOOL_KEY, statusBool);

        dataIntent.putExtras(dataBundle);

        startActivity(dataIntent);
    }
}
