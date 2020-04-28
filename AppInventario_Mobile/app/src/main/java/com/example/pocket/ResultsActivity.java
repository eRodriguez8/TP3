package com.example.pocket;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Bundle;

public class ResultsActivity extends AppCompatActivity {

    private TextView personName;
    private TextView id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results2);

        final Bundle data = getIntent().getExtras();
        personName = findViewById(R.id.personName);
        id = findViewById(R.id.idValue);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Por favor espere");
        progress.setCancelable(false);
        progress.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progress.dismiss();
                personName.setText(data.getString(MainActivity.PERSONNAME_KEY));
                id.setText(data.getString(MainActivity.ID_KEY));
            }
        }, 0);
    }
}
