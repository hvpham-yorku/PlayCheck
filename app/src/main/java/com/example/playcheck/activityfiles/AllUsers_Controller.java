package com.example.playcheck.activityfiles;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playcheck.R;
import com.example.playcheck.puremodel.Organizer;
import com.example.playcheck.puremodel.Player;
import com.example.playcheck.puremodel.Referee;
import com.example.playcheck.puremodel.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllUsers_Controller extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private String currentCategory = "All";
    
    private TextView tabAll, tabOrganizers, tabReferees, tabPlayers;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_users);
        
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupSearch();
        loadUsers();
    }

    private void initViews() {
        rvUsers = findViewById(R.id.rvUsers);
        tabAll = findViewById(R.id.tabAll);
        tabOrganizers = findViewById(R.id.tabOrganizers);
        tabReferees = findViewById(R.id.tabReferees);
        tabPlayers = findViewById(R.id.tabPlayers);
        etSearch = findViewById(R.id.etSearch);
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(filteredUsers, this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }

    private void setupClickListeners() {
        tabAll.setOnClickListener(v -> selectCategory("All"));
        tabOrganizers.setOnClickListener(v -> selectCategory("Organizer"));
        tabReferees.setOnClickListener(v -> selectCategory("Referee"));
        tabPlayers.setOnClickListener(v -> selectCategory("Player"));

        // Bottom Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> navigateTo(Homepage_Controller.class));
        findViewById(R.id.navNews).setOnClickListener(v -> handleNavClick("Notifications"));
        findViewById(R.id.navClipboard).setOnClickListener(v -> navigateTo(Video_Review_Page_Plus_Stats_controller.class));
        findViewById(R.id.navBack).setOnClickListener(v -> finish());
        findViewById(R.id.navProfile).setOnClickListener(v -> navigateToUserProfile());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUsers() {
        User.fetchAllUsers().thenAccept(users -> {
            runOnUiThread(() -> {
                allUsers.clear();
                allUsers.addAll(users);
                applyFilters();
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> 
                Toast.makeText(this, "Failed to load users: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
            );
            return null;
        });
    }

    private void selectCategory(String category) {
        currentCategory = category;
        updateTabStyles();
        applyFilters();
    }

    private void updateTabStyles() {
        resetTabStyles();
        TextView selectedTab;
        switch (currentCategory) {
            case "Organizer": selectedTab = tabOrganizers; break;
            case "Referee": selectedTab = tabReferees; break;
            case "Player": selectedTab = tabPlayers; break;
            default: selectedTab = tabAll; break;
        }
        
        selectedTab.setBackgroundResource(R.drawable.bg_circle_white);
        selectedTab.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.black));
        selectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    private void resetTabStyles() {
        int darkGray = ContextCompat.getColor(this, android.R.color.black);
        tabAll.setBackground(null);
        tabAll.setTextColor(darkGray);
        tabOrganizers.setBackground(null);
        tabOrganizers.setTextColor(darkGray);
        tabReferees.setBackground(null);
        tabReferees.setTextColor(darkGray);
        tabPlayers.setBackground(null);
        tabPlayers.setTextColor(darkGray);
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase();
        
        List<User> filtered = allUsers.stream()
            .filter(user -> currentCategory.equals("All") || user.getClassType().equals(currentCategory))
            .filter(user -> {
                String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                return fullName.contains(query);
            })
            .collect(Collectors.toList());
            
        filteredUsers.clear();
        filteredUsers.addAll(filtered);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, User_Profile_Page_Others_Perspective_Controller.class);
        intent.putExtra("USER_ID", user.getUid());
        startActivity(intent);
    }

    private void navigateToUserProfile() {
        User.getCurrentUser().thenAccept(user -> {
            runOnUiThread(() -> {
                if (user instanceof Organizer) {
                    navigateTo(General_Profile_Page_Controller.class);
                } else if (user instanceof Player) {
                    navigateTo(Player_Profile_Page_Controller.class);
                } else if (user instanceof Referee) {
                    navigateTo(Referee_Profile_Page_Controller.class);
                }
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    private void handleNavClick(String navItem) {
        Toast.makeText(this, "Navigating to " + navItem, Toast.LENGTH_SHORT).show();
    }
}
