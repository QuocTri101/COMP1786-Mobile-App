package com.example.logbook_temperatureapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

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

        EditText etTemperature = findViewById(R.id.etTemperature);
        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = findViewById(R.id.spinnerTo);
        Button btnConvert = findViewById(R.id.btnConvert);
        TextView tvResult = findViewById(R.id.tvResult);

        String[] options = {"Celsius", "Fahrenheit", "Kelvin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        btnConvert.setOnClickListener(v -> {
            String inputText = etTemperature.getText().toString();

            if (inputText.isEmpty()) {
                etTemperature.setError("Please enter a value");
                return;
            }

            double inputVal;
            try {
                inputVal = Double.parseDouble(inputText);
            } catch (NumberFormatException e) {
                etTemperature.setError("Please enter a valid number");
                return;
            }

            String fromUnit = spinnerFrom.getSelectedItem().toString();
            String toUnit = spinnerTo.getSelectedItem().toString();

            double result = convertTemperature(inputVal, fromUnit, toUnit);

            if (isBelowAbsoluteZero(inputVal, fromUnit)) {
                etTemperature.setError("Temperature below absolute zero!");
                tvResult.setText("Invalid Input");
            } else {
                tvResult.setText(String.format(Locale.US, "Result: %.2f %s", result, getUnitSymbol(toUnit)));
            }
        });
    }

    private double convertTemperature(double value, String from, String to) {
        double celsius = value;

        if (from.equals("Fahrenheit")) {
            celsius = (value - 32) * 5 / 9;
        } else if (from.equals("Kelvin")) {
            celsius = value - 273.15;
        }

        if (to.equals("Fahrenheit")) {
            return (celsius * 9 / 5) + 32;
        } else if (to.equals("Kelvin")) {
            return celsius + 273.15;
        } else {
            return celsius;
        }
    }

    private boolean isBelowAbsoluteZero(double value, String unit) {
        if (unit.equals("Celsius")) return value < -273.15;
        if (unit.equals("Fahrenheit")) return value < -459.67;
        if (unit.equals("Kelvin")) return value < 0;
        return false;
    }

    private String getUnitSymbol(String unit) {
        if (unit.equals("Celsius")) return "°C";
        if (unit.equals("Fahrenheit")) return "°F";
        if (unit.equals("Kelvin")) return "K";
        return "";
    }
}