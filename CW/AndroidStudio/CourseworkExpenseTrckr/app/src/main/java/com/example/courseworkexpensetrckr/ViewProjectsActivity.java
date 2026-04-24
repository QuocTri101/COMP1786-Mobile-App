package com.example.courseworkexpensetrckr;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
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
import java.util.List;

public class ViewProjectsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PrjAdapter prjAdapter;
    private List<Project> projectList;
    private DatabaseReference databaseReference;
    private SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_projects);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.recyclerViewProjects);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectList = new ArrayList<>();
        prjAdapter = new PrjAdapter(projectList, new PrjAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(Project project) {
                boolean newStatus = !project.isFavorite();
                databaseReference.child(project.getProjectId()).child("favorite").setValue(newStatus);
            }
        });
        recyclerView.setAdapter(prjAdapter);

        String dbUrl = getString(R.string.firebase_db_url);
        databaseReference = FirebaseDatabase.getInstance(dbUrl).getReference("projects");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projectList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Project project = dataSnapshot.getValue(Project.class);
                    projectList.add(project);
                }
                if (searchView != null) {
                    filterList(searchView.getQuery().toString());
                } else {
                    prjAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProjectsActivity.this, "Failed to load projects.", Toast.LENGTH_SHORT).show();
            }
        });
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void filterList(String text) {
        List<Project> filteredList = new ArrayList<>();
        String query = text.toLowerCase().trim();
        String[] favWords = getResources().getStringArray(R.array.fav_keywords);
        if (java.util.Arrays.asList(favWords).contains(query)) {
            for (Project project : projectList) {
                if (project.isFavorite()) {
                    filteredList.add(project);
                }
            }
        } else {
            for (Project project : projectList) {
                if (project.getName().toLowerCase().contains(query) ||
                        project.getStatus().toLowerCase().contains(query) ||
                        project.getProjectId().toLowerCase().contains(query) ||
                        String.valueOf(project.getBudget()).contains(query)) {
                    filteredList.add(project);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
        prjAdapter.setFilteredList(filteredList);
    }
}