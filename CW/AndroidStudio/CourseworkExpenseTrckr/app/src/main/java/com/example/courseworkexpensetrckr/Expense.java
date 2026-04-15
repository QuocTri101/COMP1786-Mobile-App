package com.example.courseworkexpensetrckr;

public class Expense {
    public Expense() {
    }
    private String expenseId;
    private String projectId;
    private String date;
    private double amount;
    private String currency;
    private String type;
    private String paymentMethod;
    private String claimant;
    private String paymentStatus;
    private String description;
    private String location;
    public Expense(String expenseId, String projectId, String date, double amount, String currency, String type, String paymentMethod, String claimant, String description, String paymentStatus, String location) {
        this.expenseId = expenseId;
        this.projectId = projectId;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.claimant = claimant;
        this.description = description;
        this.paymentStatus = paymentStatus;
        this.location = location;
    }

    public String getExpenseId() {return expenseId;}
    public void setExpenseId(String expenseId) {this.expenseId = expenseId;}
    public String getProjectId() {return projectId;}

    public void setProjectId(String projectId) {this.projectId = projectId;}
    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}
    public double getAmount() {return amount;}
    public void setAmount(double amount) {this.amount = amount;}
    public String getCurrency() {return currency;}
    public void setCurrency(String currency) {this.currency = currency;}
    public String getType() {return type;}

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getClaimant() {
        return claimant;
    }

    public void setClaimant(String claimant) {
        this.claimant = claimant;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
