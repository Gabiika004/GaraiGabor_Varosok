package com.example.varosok;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private String url = "https://retoolapi.dev/Muu0fn/varosok";
    private ListView listView;
    public static List<City> city = new ArrayList<>();
    private CityAdapter cityAdapter;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
        RequestTask task = new RequestTask();
        task.execute();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Visszalépés a MainActivity-re
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
                // A ListActivity bezárása
                finish();
            }
        });
    }

    public void init() {
        listView = findViewById(R.id.citylistView);
        cityAdapter = new CityAdapter();
        listView.setAdapter(cityAdapter);
        buttonBack = findViewById(R.id.buttonBack);
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                response = RequestHandler.get(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson gson = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(ListActivity.this,
                        "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError:", response.getContent());
            } else {
                City[] cities = gson.fromJson(response.getContent(), City[].class);
                city.clear();
                city.addAll(Arrays.asList(cities));
                cityAdapter.notifyDataSetChanged();
                Toast.makeText(ListActivity.this, "Sikeres városok lekérdezés", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CityAdapter extends ArrayAdapter<City> {

        public CityAdapter() {
            super(ListActivity.this, R.layout.city_list_items, city);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.city_list_items, null, false);

            TextView textViewId = view.findViewById(R.id.textViewId);
            TextView textViewName = view.findViewById(R.id.textViewName);
            TextView textViewOrszag = view.findViewById(R.id.textViewOrszag);
            TextView textViewLakossag= view.findViewById(R.id.textViewLakossag);
            TextView textViewChange= view.findViewById(R.id.textViewChange);
            TextView textViewDelete= view.findViewById(R.id.textViewDelete);

            City actualCity = city.get(position);

            textViewId.setText(String.valueOf(actualCity.getId()));
            textViewName.setText(actualCity.getNev());
            textViewOrszag.setText(actualCity.getOrszag());
            textViewLakossag.setText(String.valueOf(actualCity.getLakossag()));

            textViewChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Az aktuális város adatainak átadása az Intentnek
                    City actualCity = city.get(position);
                    Intent intent = new Intent(ListActivity.this, UpdateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("selected_city", (Parcelable) actualCity);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCity(position);
                }
            });

            return view;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        RequestTask task = new RequestTask();
        task.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int cityIdToDelete = params[0];
            try {
                RequestHandler.delete(url + "/" + cityIdToDelete);
            } catch (IOException e) {
                Log.e("DeleteTask", "Error while deleting city: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Törlés visszajelzése
            Toast.makeText(ListActivity.this, "Város törölve", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteCity(int position) {
        City cityToDelete = city.get(position);
        int cityId = cityToDelete.getId();

        // Törlés végrehajtása
        new DeleteTask().execute(cityId);

        // Frissítjük a helyi listát
        city.remove(position);
        cityAdapter.notifyDataSetChanged();
    }

}
