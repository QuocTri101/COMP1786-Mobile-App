package com.example.courseworkexpensetrckr;

import android.Manifest;

import android.content.pm.PackageManager;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MakeProjectActivity extends AppCompatActivity {
    private EditText etProjectId, etProjectName, etDescription, etStartDate, etEndDate, etManager, etBudget, etSpecialReq;
    private Spinner spinnerStatus;
    private Button btnSaveProject;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_make_project);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        String dbUrl = getString(R.string.firebase_db_url);
        databaseReference = FirebaseDatabase.getInstance(dbUrl).getReference("projects");
        etProjectId = findViewById(R.id.etProjectId);
        etProjectName = findViewById(R.id.etProjectName);
        etDescription = findViewById(R.id.etDescription);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));
        etManager = findViewById(R.id.etManager);
        etBudget = findViewById(R.id.etBudget);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        etSpecialReq = findViewById(R.id.etSpecialReq);
        btnSaveProject = findViewById(R.id.btnSaveProject);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        btnSaveProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    fetchLocationAndSave();
                }
            }
        });
    }
    private boolean validateInputs() {
        String id = etProjectId.getText().toString().trim();
        String name = etProjectName.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        if (id.isEmpty()) {
            etProjectId.setError("Please enter the project ID");
            return false;
        }
        if (name.isEmpty()) {
            etProjectName.setError("Please enter the project name");
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("Please enter a description");
            return false;
        }
        if (etStartDate.getText().toString().trim().isEmpty()) {
            etStartDate.setError("Please enter a start date");
            return false;
        }
        if (etEndDate.getText().toString().trim().isEmpty()) {
            etEndDate.setError("Please enter an end date");
            return false;
        }
        if (etManager.getText().toString().trim().isEmpty()) {
            etManager.setError("Please enter the manager's name");
            return false;
        }
        if (budgetStr.isEmpty()) {
            etBudget.setError("Please enter the budget");
            return false;
        }
        return true;
    }
    private void fetchLocationAndSave() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                String locationString = "Location not found";
                if (location != null) {
                    locationString = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
                }
                saveProjectToDatabase(locationString);
            });
        } else {
            saveProjectToDatabase("Permission Denied by User");
        }
    }
    private void saveProjectToDatabase(String gpsLocation) {
        String id = etProjectId.getText().toString().trim();
        String name = etProjectName.getText().toString().trim();
        double budget = Double.parseDouble(etBudget.getText().toString().trim());
        String specialReq = etSpecialReq.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();
        String manager = etManager.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String client = gpsLocation;
        Project newProject = new Project(id, name, desc, start, end, manager, status, budget, specialReq, client, false);
        databaseReference.child(id).setValue(newProject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MakeProjectActivity.this, "Project Saved to Cloud!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MakeProjectActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void showDatePickerDialog(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MakeProjectActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Month is 0-indexed in Java (Jan = 0), so we add 1!
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    targetEditText.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }
}