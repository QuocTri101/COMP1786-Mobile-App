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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDetailsActivity extends AppCompatActivity {

    private String currentProjectId;
    private RecyclerView recyclerView;
    private EditText etId, etName, etDesc, etStart, etEnd, etManager, etBudget, etSpecial;
    private Spinner spinStatus;
    private Button btnUpdate, btnDelete;
    private ExpAdapter adapter;
    private List<Expense> expenseList;
    private DatabaseReference projectRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        currentProjectId = getIntent().getStringExtra("PROJECT_ID");
        String dbUrl = getString(R.string.firebase_db_url);
        projectRef = FirebaseDatabase.getInstance(dbUrl).getReference("projects").child(currentProjectId);
        etId = findViewById(R.id.etDetProjectId);
        etName = findViewById(R.id.etDetProjectName);
        etDesc = findViewById(R.id.etDetDescription);
        etStart = findViewById(R.id.etDetStartDate);
        etEnd = findViewById(R.id.etDetEndDate);
        etManager = findViewById(R.id.etDetManager);
        etBudget = findViewById(R.id.etDetBudget);
        spinStatus = findViewById(R.id.spinnerDetStatus);
        etSpecial = findViewById(R.id.etDetSpecialReq);
        btnUpdate = findViewById(R.id.btnUpdateProject);
        btnDelete = findViewById(R.id.btnDeleteProject);
        etStart.setOnClickListener(v -> showDatePicker(etStart));
        etEnd.setOnClickListener(v -> showDatePicker(etEnd));
        loadProjectData();
        btnUpdate.setOnClickListener(v -> updateProject());
        btnDelete.setOnClickListener(v -> deleteProject());
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(ProjectDetailsActivity.this, AddExpenseActivity.class);
                intent.putExtra("PROJECT_ID", currentProjectId);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.recyclerViewExpenses);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        expenseList = new ArrayList<>();
        adapter = new ExpAdapter(expenseList, currentProjectId);
        recyclerView.setAdapter(adapter);

        projectRef.child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    expenseList.add(expense);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void showDatePicker(EditText targetEditText) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            targetEditText.setText(day + "/" + (month + 1) + "/" + year);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadProjectData() {
        projectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etId.setText(currentProjectId);
                    etName.setText(snapshot.child("name").getValue(String.class));
                    etDesc.setText(snapshot.child("description").getValue(String.class));
                    etStart.setText(snapshot.child("startDate").getValue(String.class));
                    etEnd.setText(snapshot.child("endDate").getValue(String.class));
                    etManager.setText(snapshot.child("manager").getValue(String.class));
                    etSpecial.setText(snapshot.child("specialReq").getValue(String.class));

                    Double budget = snapshot.child("budget").getValue(Double.class);
                    if (budget != null) etBudget.setText(String.valueOf(budget));

                    String status = snapshot.child("status").getValue(String.class);
                    if (status != null) {
                        for (int i = 0; i < spinStatus.getCount(); i++) {
                            if (spinStatus.getItemAtPosition(i).toString().equalsIgnoreCase(status)) {
                                spinStatus.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateProject() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", etName.getText().toString().trim());
        updates.put("description", etDesc.getText().toString().trim());
        updates.put("startDate", etStart.getText().toString().trim());
        updates.put("endDate", etEnd.getText().toString().trim());
        updates.put("manager", etManager.getText().toString().trim());
        updates.put("status", spinStatus.getSelectedItem().toString());
        updates.put("specialReq", etSpecial.getText().toString().trim());

        String budgetStr = etBudget.getText().toString().trim();
        if (!budgetStr.isEmpty()) {
            updates.put("budget", Double.parseDouble(budgetStr));
        }

        projectRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
            Toast.makeText(ProjectDetailsActivity.this, "Project Updated!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> Toast.makeText(ProjectDetailsActivity.this, "Update Failed", Toast.LENGTH_SHORT).show());
    }

    private void deleteProject() {
        projectRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ProjectDetailsActivity.this, "Project Deleted!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}