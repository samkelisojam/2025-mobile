package com.example.assertmanangement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddAssetActivity extends AppCompatActivity {

    private EditText assetTagEditText, assetNameEditText, roomEditText, conditionEditText, locationEditText, notesEditText, submittedByEditText;
    private Button submitButton;

    private static final String POST_ASSET_URL = "https://oracleapex.com/ords/holdingtechsa/admin/Assert/allassert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);
        setTitle("Add Asset");

        // Initialize UI components
        assetTagEditText = findViewById(R.id.assetTagEditText);
        assetNameEditText = findViewById(R.id.assetNameEditText);
        roomEditText = findViewById(R.id.roomEditText);
        conditionEditText = findViewById(R.id.conditionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        notesEditText = findViewById(R.id.notesEditText);
        submittedByEditText = findViewById(R.id.submittedByEditText);
        submitButton = findViewById(R.id.submitButton);

        // Handle submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String assetTag = assetTagEditText.getText().toString().trim();
                String assetName = assetNameEditText.getText().toString().trim();
                String room = roomEditText.getText().toString().trim();
                String condition = conditionEditText.getText().toString().trim();
                String location = locationEditText.getText().toString().trim();
                String notes = notesEditText.getText().toString().trim();
                String submittedByStr = submittedByEditText.getText().toString().trim();

                if (assetTag.isEmpty() || assetName.isEmpty() || room.isEmpty() || condition.isEmpty() || location.isEmpty() || submittedByStr.isEmpty()) {
                    Toast.makeText(AddAssetActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int submittedBy;
                try {
                    submittedBy = Integer.parseInt(submittedByStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddAssetActivity.this, "Invalid submitted_by number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Run AsyncTask to post asset
                new PostAssetTask().execute(assetTag, assetName, room, condition, location, notes, "N", submittedBy);
            }
        });
    }

    // AsyncTask for posting asset
    private class PostAssetTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            String assetTag = (String) params[0];
            String assetName = (String) params[1];
            String room = (String) params[2];
            String condition = (String) params[3];
            String location = (String) params[4];
            String notes = (String) params[5];
            String verified = (String) params[6];
            int submittedBy = (int) params[7];

            try {
                URL url = new URL(POST_ASSET_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON object with asset data
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("asset_tag", assetTag);
                jsonObject.put("asset_name", assetName);
                jsonObject.put("room", room);
                jsonObject.put("condition", condition);
                jsonObject.put("location", location);
                jsonObject.put("notes", notes);
                jsonObject.put("verified", verified);
                jsonObject.put("submitted_by", submittedBy);

                // Write JSON to request body
                OutputStream os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(AddAssetActivity.this, "Asset posted successfully!", Toast.LENGTH_SHORT).show();
                // Optionally, clear fields or finish activity
            } else {
                Toast.makeText(AddAssetActivity.this, "Failed to post asset.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
