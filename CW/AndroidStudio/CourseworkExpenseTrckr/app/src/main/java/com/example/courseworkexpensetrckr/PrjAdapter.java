package com.example.courseworkexpensetrckr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PrjAdapter extends RecyclerView.Adapter<PrjAdapter.ProjectViewHolder>{
    private List<Project> projectList;
    private OnFavoriteClickListener favoriteClickListener;

    public PrjAdapter(List<Project> projectList, OnFavoriteClickListener listener) {
        this.projectList = projectList;
        this.favoriteClickListener = listener;
    }
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Project project);
    }
    public void setFilteredList(List<Project> filteredList) {
        this.projectList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.tvName.setText(project.getName());
        holder.tvId.setText("ID: " + project.getProjectId());
        holder.tvBudget.setText("Budget: $" + project.getBudget());
        String status = project.getStatus();
        holder.tvStatus.setText("Status: " + status);

        if ("Active".equalsIgnoreCase(status)) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#1976D2"));
        } else if ("Completed".equalsIgnoreCase(status)) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#388E3C"));
        } else if ("On Hold".equalsIgnoreCase(status)) {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
        } else {
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#757575"));
        }
        String location = project.getClientInfo();
        if (location != null && !location.isEmpty()) {
            holder.tvLocation.setText("Location: " + location);
        } else {
            holder.tvLocation.setText("Location: Not available");
        }
        if (project.isFavorite()) {
            holder.ivStar.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.ivStar.setImageResource(android.R.drawable.btn_star_big_off);
        }
        holder.ivStar.setOnClickListener(v -> {
            favoriteClickListener.onFavoriteClick(project);
        });
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ProjectDetailsActivity.class);
            intent.putExtra("PROJECT_ID", project.getProjectId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvBudget, tvLocation, tvStatus;
        ImageView ivStar;
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemProjectName);
            tvId = itemView.findViewById(R.id.tvItemProjectId);
            tvBudget = itemView.findViewById(R.id.tvItemProjectBudget);
            tvLocation = itemView.findViewById(R.id.tvItemProjectLocation);
            ivStar = itemView.findViewById(R.id.ivFavoriteStar);
            tvStatus = itemView.findViewById(R.id.tvItemProjectStatus);
        }
    }
}
