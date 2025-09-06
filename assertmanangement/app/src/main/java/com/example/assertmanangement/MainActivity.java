package com.example.assertmanangement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Replace with your API URL
    private static final String API_URL = "https://oracleapex.com/ords/holdingtechsa/userAssert/auser";

    private ListView assetsListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> assetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ListView and Adapter
        assetsListView = findViewById(R.id.assetsListView);
        assetList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assetList);
        assetsListView.setAdapter(adapter);

        // Call AsyncTask to fetch assets
        new GetAssetsTask().execute();

        // Set up button to add new asset
        Button addButton = findViewById(R.id.addAssetButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch AddAssetActivity (you need to create this activity)
                Intent intent = new Intent(MainActivity.this, AddAssetActivity.class);
                startActivity(intent);
            }
        });
    }

    private class GetAssetsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                } else {
                    Log.e(TAG, "GET request failed: Response Code " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in GET request: " + e.getMessage());
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Parse JSON response and update list
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray items = jsonResponse.getJSONArray("items");
                assetList.clear(); // Clear previous data

                for (int i = 0; i < items.length(); i++) {
                    JSONObject asset = items.getJSONObject(i);
                    int assetId = asset.getInt("asset_id");
                    String assetTag = asset.getString("asset_tag");
                    String assetName = asset.getString("asset_name");

                    String displayAsset = "ID: " + assetId + ", Tag: " + assetTag + ", Name: " + assetName;
                    assetList.add(displayAsset);
                }
                adapter.notifyDataSetChanged(); // Refresh ListView
            } catch (Exception e) {
                Log.e(TAG, "Exception in parsing JSON: " + e.getMessage());
            }
        }
    }
}
