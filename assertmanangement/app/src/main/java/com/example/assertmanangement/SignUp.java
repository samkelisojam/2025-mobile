package com.example.assertmanangement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUp extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText, fullNameEditText, emailEditText;
    private EditText statusEditText, staffNumberEditText, studentNumberEditText, saIdEditText;
    private Spinner roleSpinner;
    private Button registerButton;
    private TextView errorTextView;

    private static final String API_URL = "https://oracleapex.com/ords/holdingtechsa/campus-users/users/";

    // Corresponds to dropdown positions: admin, student, lecture, staff
    private final int[] roleIds = {1, 2, 3, 4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        statusEditText = findViewById(R.id.statusEditText);
        staffNumberEditText = findViewById(R.id.staffNumberEditText);
        studentNumberEditText = findViewById(R.id.studentNumberEditText);
        saIdEditText = findViewById(R.id.saIdEditText);
        registerButton = findViewById(R.id.registerButton);
        errorTextView = findViewById(R.id.errorTextView);

        // Set up Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Admin", "Student", "Lecture", "Staff"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        int roleId = roleIds[roleSpinner.getSelectedItemPosition()];
        String status = statusEditText.getText().toString().trim();
        String staffNumber = staffNumberEditText.getText().toString().trim();
        String studentNumber = studentNumberEditText.getText().toString().trim();
        String saId = saIdEditText.getText().toString().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty() ||
                status.isEmpty() || staffNumber.isEmpty() || studentNumber.isEmpty() ||
                saId.isEmpty()) {
            errorTextView.setText("All fields are required.");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }
        if (saId.length() != 13 || !saId.matches("\\d{13}")) {
            errorTextView.setText("SA ID must be exactly 13 digits.");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        new RegisterUserTask(username, password, fullName, email, roleId, status, staffNumber, studentNumber, saId).execute();
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, Boolean> {
        private String username, password, fullName, email, status, staffNumber, studentNumber, saId;
        private int roleId;

        public RegisterUserTask(String username, String password, String fullName, String email,
                                int roleId, String status, String staffNumber, String studentNumber, String saId) {
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

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                jsonObject.put("full_name", fullName);
                jsonObject.put("email", email);
                jsonObject.put("role_id", roleId);
                jsonObject.put("status", status);
                jsonObject.put("staff_number", staffNumber);
                jsonObject.put("student_number", studentNumber);
                jsonObject.put("sa_id", saId);

                OutputStream os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_CREATED;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(SignUp.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                errorTextView.setText("Registration failed. Please try again.");
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
