package com.example.varosok;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class InsertActivity extends AppCompatActivity {

    private String url = "https://retoolapi.dev/Muu0fn/varosok";
    private EditText editTextNev;
    private EditText editTextOrszag;
    private EditText editTextLakossag;
    private Button buttonFelvetel;
    private Button buttonVissza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        init();

        // Felvétel gomb eseménykezelője
        buttonFelvetel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                felvetelGombClick();
            }
        });

        // Vissza gomb eseménykezelője
        buttonVissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertActivity.this, MainActivity.class);
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
        buttonFelvetel = findViewById(R.id.buttonFelvetel);
        buttonVissza = findViewById(R.id.buttonVissza);
    }

    // Felvétel gombra kattintás eseménykezelője
    // Felvétel gombra kattintás eseménykezelője
    private void felvetelGombClick() {
        String nev = editTextNev.getText().toString().trim();
        String orszag = editTextOrszag.getText().toString().trim();
        String lakossagStr = editTextLakossag.getText().toString().trim();

        if (nev.isEmpty() || orszag.isEmpty() || lakossagStr.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show();
        } else {
            try {
                // Ellenőrizze, hogy a város már létezik-e
                if (isCityExist(nev)) {
                    Toast.makeText(this, "Ez a város már szerepel", Toast.LENGTH_SHORT).show();
                } else {
                    int lakossag = Integer.parseInt(lakossagStr);
                    City ujVaros = new City(nev, orszag, lakossag); // Új város létrehozása
                    // RequestTask példány létrehozása és végrehajtása az új város adatainak átadásával
                    RequestTask task = new RequestTask();
                    task.execute(ujVaros);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "A lakosság mező csak számot tartalmazhat", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Ellenőrzi, hogy a város már létezik-e
    private boolean isCityExist(String cityName) {
        for (City city : ListActivity.city) {
            if (city.getNev().equalsIgnoreCase(cityName)) {
                return true;
            }
        }
        return false;
    }

    private class RequestTask extends AsyncTask<City, Void, Void> {

        @Override
        protected Void doInBackground(City... cities) {
            // A cities tömb csak egy elemet tartalmaz, amely az új várost reprezentálja
            City ujVaros = cities[0];

            // Az új város adatainak elküldése a szervernek
            Gson gson = new Gson();
            try {
                RequestHandler.post(url, gson.toJson(ujVaros));
            } catch (IOException e) {
                // Hiba történt a kérés feldolgozása során
                Log.e("RequestTask", "Hiba történt a kérés feldolgozása során: " + e.getMessage());
                throw new RuntimeException("Hiba történt a kérés feldolgozása során", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Toast vagy más visszajelzés az adatok felvételéről
            Toast.makeText(InsertActivity.this, "Új város felvéve", Toast.LENGTH_SHORT).show();
            // Alaphelyzetbe állítás
            editTextNev.setText("");
            editTextOrszag.setText("");
            editTextLakossag.setText("");
        }
    }

}