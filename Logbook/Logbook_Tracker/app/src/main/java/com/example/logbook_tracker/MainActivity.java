package com.example.logbook_tracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener{

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        expenseList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExpenseAdapter(expenseList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> showExpenseDialog(-1));
    }
    private void showExpenseDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_expense, null);
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editAmount = dialogView.findViewById(R.id.editAmount);
        EditText editDate = dialogView.findViewById(R.id.editDate);
        editDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, yearSelected, monthOfYear, dayOfMonth) -> {
                        // Format the date to MM/dd/yyyy
                        String formattedDate = String.format(Locale.US, "%02d/%02d/%d",
                                (monthOfYear + 1), dayOfMonth, yearSelected);
                        editDate.setText(formattedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });
        if (position >= 0) {
            Expense expense = expenseList.get(position);
            editName.setText(expense.getName());
            editAmount.setText(String.valueOf(expense.getAmount()));
            editDate.setText(expense.getDate());
            builder.setTitle("Edit Expense");
        } else {
            builder.setTitle("Add Expense");
        }

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String amountStr = editAmount.getText().toString().trim();
            String date = editDate.getText().toString().trim();

            if (name.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                editAmount.setError("Enter a valid positive number");
                return;
            }
            if (!isValidDate(date)) {
                editDate.setError("Use format MM/dd/yyyy");
                return;
            }

            if (position >= 0) {
                Expense expense = expenseList.get(position);
                expense.setName(name);
                expense.setAmount(amount);
                expense.setDate(date);
                adapter.notifyItemChanged(position);
            } else {
                expenseList.add(new Expense(name, amount, date));
                adapter.notifyItemInserted(expenseList.size() - 1);
            }

            dialog.dismiss();
        });
    }
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public void onEditClick(int position) {
        showExpenseDialog(position);
    }

    @Override
    public void onDeleteClick(int position) {
        expenseList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, expenseList.size());
    }
}