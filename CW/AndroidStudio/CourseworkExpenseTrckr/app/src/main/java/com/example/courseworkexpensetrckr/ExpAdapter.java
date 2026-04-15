package com.example.courseworkexpensetrckr;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import java.util.List;

public class ExpAdapter extends RecyclerView.Adapter<ExpAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private String projectId;

    public ExpAdapter(List<Expense> expenseList, String projectId) {
        this.expenseList = expenseList;
        this.projectId = projectId;
    }
    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvType.setText(expense.getType() + " - " + expense.getDescription());
        holder.tvDate.setText("Date: " + expense.getDate());
        holder.tvAmount.setText("Amount: " + expense.getAmount() + " " + expense.getCurrency());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ExpenseDetailsActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            intent.putExtra("EXPENSE_ID", expense.getExpenseId());
            v.getContext().startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {return expenseList.size();}
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDate, tvAmount;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvExpType);
            tvDate = itemView.findViewById(R.id.tvExpDate);
            tvAmount = itemView.findViewById(R.id.tvExpAmount);
        }
    }
}
