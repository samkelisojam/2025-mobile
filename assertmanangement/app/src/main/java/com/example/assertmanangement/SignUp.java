package com.example.assertmanangement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUp extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText, fullNameEditText, emailEditText;
    private EditText roleIdEditText, statusEditText, staffNumberEditText, studentNumberEditText, saIdEditText;
    private Button registerButton;
    private TextView errorTextView;

    private static final String API_URL = "https://oracleapex.com/ords/holdingtechsa/campus-users/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        roleIdEditText = findViewById(R.id.roleIdEditText);
        statusEditText = findViewById(R.id.statusEditText);
        staffNumberEditText = findViewById(R.id.staffNumberEditText);
        studentNumberEditText = findViewById(R.id.studentNumberEditText);
        saIdEditText = findViewById(R.id.saIdEditText);
        registerButton = findViewById(R.id.registerButton);
        errorTextView = findViewById(R.id.errorTextView);

        // Set up register button click listener
        registerButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String roleId = roleIdEditText.getText().toString().trim();
        String status = statusEditText.getText().toString().trim();
        String staffNumber = staffNumberEditText.getText().toString().trim();
        String studentNumber = studentNumberEditText.getText().toString().trim();
        String saId = saIdEditText.getText().toString().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty() ||
            roleId.isEmpty() || status.isEmpty() || staffNumber.isEmpty() || studentNumber.isEmpty() ||
            saId.isEmpty()) {
            errorTextView.setText("All fields are required.");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        // Execute registration in background
        new RegisterUserTask(username, password, fullName, email, roleId, status, staffNumber, studentNumber, saId).execute();
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, Boolean> {
        private String username, password, fullName, email, roleId, status, staffNumber, studentNumber, saId;

        public RegisterUserTask(String username, String password, String fullName, String email,
                                String roleId, String status, String staffNumber, String studentNumber, String saId) {
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.email = email;
            this.roleId = roleId;
            this.status = status;
            this.staffNumber = staffNumber;
            this.studentNumber = studentNumber;
            this.saId = saId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON object
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                jsonObject.put("fullName", fullName);
                jsonObject.put("email", email);
                jsonObject.put("roleId", roleId);
                jsonObject.put("status", status);
                jsonObject.put("staffNumber", staffNumber);
                jsonObject.put("studentNumber", studentNumber);
          

                jsonObject.put("saId", saId);

                // Write JSON data to output stream
                OutputStream os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                // Get response code
                int responseCode = conn.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_CREATED; // Check if the user was created successfully
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Return false in case of error
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(SignUp.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                errorTextView.setText("Registration failed. Please try again.");
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
