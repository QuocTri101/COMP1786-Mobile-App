package com.example.courseworkexpensetrckr;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ExpenseDetailsActivity extends AppCompatActivity {
    private EditText etId, etDate, etAmount, etClaimant, etDesc, etLocation;
    private Spinner spinCurrency, spinType, spinMethod, spinStatus;
    private Button btnUpdate, btnDelete;

    private DatabaseReference expenseRef;
    private String currentProjectId, currentExpenseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expense_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        currentProjectId = getIntent().getStringExtra("PROJECT_ID");
        currentExpenseId = getIntent().getStringExtra("EXPENSE_ID");
        String dbUrl = getString(R.string.firebase_db_url);
        expenseRef = FirebaseDatabase.getInstance(dbUrl)
                .getReference("projects").child(currentProjectId).child("expenses").child(currentExpenseId);
        etId = findViewById(R.id.etDetExpenseId);
        etDate = findViewById(R.id.etDetExpenseDate);
        etAmount = findViewById(R.id.etDetExpenseAmount);
        spinCurrency = findViewById(R.id.spinnerDetExpenseCurrency);
        etClaimant = findViewById(R.id.etDetExpenseClaimant);
        spinType = findViewById(R.id.spinnerDetExpenseType);
        spinMethod = findViewById(R.id.spinnerDetPaymentMethod);
        spinStatus = findViewById(R.id.spinnerDetPaymentStatus);
        etDesc = findViewById(R.id.etDetExpenseDesc);
        etLocation = findViewById(R.id.etDetExpenseLocation);
        btnUpdate = findViewById(R.id.btnUpdateExpense);
        btnDelete = findViewById(R.id.btnDeleteExpense);
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(ExpenseDetailsActivity.this, (view, year, month, day) -> {
                etDate.setText(day + "/" + (month + 1) + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        loadExpenseData();
        btnUpdate.setOnClickListener(v -> updateExpense());
        btnDelete.setOnClickListener(v -> deleteExpense());
    }
    private void loadExpenseData() {
        expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Expense exp = snapshot.getValue(Expense.class);
                    if (exp != null) {
                        etId.setText(exp.getExpenseId());
                        etDate.setText(exp.getDate());
                        etAmount.setText(String.valueOf(exp.getAmount()));
                        setSpinnerToValue(spinCurrency, exp.getCurrency());
                        etClaimant.setText(exp.getClaimant());
                        etDesc.setText(exp.getDescription());
                        etLocation.setText(exp.getLocation());
                        setSpinnerToValue(spinType, exp.getType());
                        setSpinnerToValue(spinMethod, exp.getPaymentMethod());
                        setSpinnerToValue(spinStatus, exp.getPaymentStatus());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpenseDetailsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }
    private void updateExpense() {
        if (etDate.getText().toString().trim().isEmpty() ||
                etAmount.getText().toString().trim().isEmpty() ||
                etClaimant.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = etDate.getText().toString().trim();
        double amount = Double.parseDouble(etAmount.getText().toString().trim());
        String currency = spinCurrency.getSelectedItem().toString();
        String type = spinType.getSelectedItem().toString();
        String method = spinMethod.getSelectedItem().toString();
        String claimant = etClaimant.getText().toString().trim();
        String status = spinStatus.getSelectedItem().toString();
        String desc = etDesc.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        Expense updatedExpense = new Expense(currentExpenseId, currentProjectId, date, amount, currency, type, method, claimant, desc, status, location);
        expenseRef.setValue(updatedExpense).addOnSuccessListener(aVoid -> {
            Toast.makeText(ExpenseDetailsActivity.this, "Expense Updated!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> Toast.makeText(ExpenseDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void deleteExpense() {
        expenseRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ExpenseDetailsActivity.this, "Expense Deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}