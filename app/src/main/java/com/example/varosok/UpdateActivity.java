package com.example.varosok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;

public class UpdateActivity extends AppCompatActivity {

    private String url = "https://retoolapi.dev/Muu0fn/varosok";
    private Button buttonModosit;
    private Button buttonVissza;

    private EditText editTextNev;
    private EditText editTextOrszag;
    private EditText editTextLakossag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        init();

        // Bundle kinyerése az Intentből
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            City actualCity = bundle.getParcelable("selected_city");
            if (actualCity != null) {
                // Az adatok beállítása a megfelelő EditText-ekben
                editTextNev.setText(actualCity.getNev());
                editTextOrszag.setText(actualCity.getOrszag());
                editTextLakossag.setText(String.valueOf(actualCity.getLakossag()));
            }
        }

        // Módosítás gomb eseménykezelője
        buttonModosit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modositasGombClick();
            }
        });

        // Vissza gomb eseménykezelője
        buttonVissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        // EditTextek inicializálása
        editTextNev = findViewById(R.id.editTextNev);
        editTextOrszag = findViewById(R.id.editTextOrszag);
        editTextLakossag = findViewById(R.id.editTextLakossag);

        // Buttonok inicializálása
        buttonModosit = findViewById(R.id.buttonModosit);
        buttonVissza = findViewById(R.id.buttonVissza);
    }
    private void modositasGombClick() {
        // Város adatainak lekérése az EditText-ekből
        String nev = editTextNev.getText().toString().trim();
        String orszag = editTextOrszag.getText().toString().trim();
        String lakossagStr = editTextLakossag.getText().toString().trim();

        // Ellenőrzés, hogy minden mező ki legyen töltve
        if (nev.isEmpty() || orszag.isEmpty() || lakossagStr.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show();
        } else {
            try {
                // Lakosság számának konvertálása integer-re
                int lakossag = Integer.parseInt(lakossagStr);
                // Új város létrehozása a felhasználó által megadott adatokkal
                City modositandoVaros = new City(nev, orszag, lakossag);
                // AsyncTask példány létrehozása és végrehajtása az új város adatainak átadásával
                RequestTask task = new RequestTask();
                task.execute(modositandoVaros);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "A lakosság mező csak számot tartalmazhat", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class RequestTask extends AsyncTask<City, Void, Void> {

        @Override
        protected Void doInBackground(City... cities) {
            City modifiedCity = cities[0]; // Módosítandó város adatainak lekérése

            Gson gson = new Gson();
            try {
                // Módosítandó város adatainak elküldése a szerverre
                RequestHandler.put(url + "/" + modifiedCity.getId(), gson.toJson(modifiedCity));
            } catch (IOException e) {
                Log.e("RequestTask", "Hiba a város módosításánál: " + e.getMessage());
                throw new RuntimeException("Hiba a város módosításánál", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Visszajelzés az adatok módosításáról
            Toast.makeText(UpdateActivity.this, "Város módosítva", Toast.LENGTH_SHORT).show();
            // Visszalépés a ListActivity-re
            Intent intent = new Intent(UpdateActivity.this, ListActivity.class);
            startActivity(intent);
        }
    }

}