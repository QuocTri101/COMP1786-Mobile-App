package com.example.courseworkexpensetrckr;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etExpenseId, etExpenseDate, etExpenseAmount, etExpenseClaimant, etExpenseDesc, etExpenseLocation;
    private Spinner spinnerExpenseCurrency, spinnerExpenseType, spinnerPaymentMethod, spinnerPaymentStatus;
    private Button btnSaveExpense;
    private DatabaseReference databaseReference;
    private String currentProjectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_expense);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        currentProjectId = getIntent().getStringExtra("PROJECT_ID");
        String dbUrl = getString(R.string.firebase_db_url);
        databaseReference = FirebaseDatabase.getInstance(dbUrl)
                .getReference("projects").child(currentProjectId).child("expenses");
        etExpenseId = findViewById(R.id.etExpenseId);
        etExpenseDate = findViewById(R.id.etExpenseDate);
        etExpenseAmount = findViewById(R.id.etExpenseAmount);
        spinnerExpenseCurrency = findViewById(R.id.spinnerExpenseCurrency);
        etExpenseClaimant = findViewById(R.id.etExpenseClaimant);
        spinnerExpenseType = findViewById(R.id.spinnerExpenseType);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        spinnerPaymentStatus = findViewById(R.id.spinnerPaymentStatus);
        etExpenseDesc = findViewById(R.id.etExpenseDesc);
        etExpenseLocation = findViewById(R.id.etExpenseLocation);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        etExpenseDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(AddExpenseActivity.this, (view, year, month, day) -> {
                etExpenseDate.setText(day + "/" + (month + 1) + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveExpenseToFirebase();
                }
            }
        });
    }
    private boolean validateInputs() {
        if (etExpenseId.getText().toString().trim().isEmpty()) { etExpenseId.setError("Required"); return false; }
        if (etExpenseDate.getText().toString().trim().isEmpty()) { etExpenseDate.setError("Required"); return false; }
        if (etExpenseAmount.getText().toString().trim().isEmpty()) { etExpenseAmount.setError("Required"); return false; }
        if (etExpenseClaimant.getText().toString().trim().isEmpty()) { etExpenseClaimant.setError("Required"); return false; }
        return true;
    }

    private void saveExpenseToFirebase() {
        String expId = etExpenseId.getText().toString().trim();
        String date = etExpenseDate.getText().toString().trim();
        double amount = Double.parseDouble(etExpenseAmount.getText().toString().trim());
        String currency = spinnerExpenseCurrency.getSelectedItem().toString();
        String type = spinnerExpenseType.getSelectedItem().toString();
        String method = spinnerPaymentMethod.getSelectedItem().toString();
        String claimant = etExpenseClaimant.getText().toString().trim();
        String status = spinnerPaymentStatus.getSelectedItem().toString();
        String desc = etExpenseDesc.getText().toString().trim();
        String location = etExpenseLocation.getText().toString().trim();

        Expense newExpense = new Expense(expId, currentProjectId, date, amount, currency, type, method, claimant, desc, status, location);

        databaseReference.child(expId).setValue(newExpense)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddExpenseActivity.this, "Expense Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddExpenseActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}