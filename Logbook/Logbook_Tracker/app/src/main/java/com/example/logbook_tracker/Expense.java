package com.example.logbook_tracker;

import java.util.UUID;

public class Expense {
    private String id;
    private String name;
    private double amount;
    private String date;

    public Expense(String name, double amount, String date) {
        this.id = UUID.randomUUID().toString(); // Unique ID for finding/editing
        this.name = name;
        this.amount = amount;
        this.date = date;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
