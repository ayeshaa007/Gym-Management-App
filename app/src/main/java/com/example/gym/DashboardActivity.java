package com.example.gym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    TextView tvTotal, tvActive, tvExpired, tvRevenue;
    CardView cardTotal, cardActive, cardExpired, cardRevenue;
    FloatingActionButton fabAddMember;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = new DatabaseHelper(this);

        tvTotal = findViewById(R.id.tvTotal);
        tvActive = findViewById(R.id.tvActive);
        tvExpired = findViewById(R.id.tvExpired);
        tvRevenue = findViewById(R.id.tvRevenue);

        cardTotal = findViewById(R.id.cardTotal);
        cardActive = findViewById(R.id.cardActive);
        cardExpired = findViewById(R.id.cardExpired);
        cardRevenue = findViewById(R.id.cardRevenue);

        fabAddMember = findViewById(R.id.fabAddMember);

        loadDashboardStats();

        fabAddMember.setOnClickListener(v ->
                startActivity(new Intent(this, AddMemberActivity.class)));

        cardTotal.setOnClickListener(v ->
                startActivity(new Intent(this, DisplayAllMembersActivity.class)));

        cardActive.setOnClickListener(v ->
                startActivity(new Intent(this, ActiveMembersActivity.class)));

        cardExpired.setOnClickListener(v ->
                startActivity(new Intent(this, ExpiredMembersActivity.class)));

        cardRevenue.setOnClickListener(v ->
                startActivity(new Intent(this, TotalRevenueActivity.class)));
    }

    private void loadDashboardStats() {

        tvTotal.setText(String.valueOf(db.getTotalMembers()));

        tvActive.setText(String.valueOf(db.getActiveMembersCount()));

        tvExpired.setText(String.valueOf(db.getExpiredMembersCount()));

        tvRevenue.setText("₹ " + db.getTotalRevenuePaid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();  // Auto refresh when returning
    }
}