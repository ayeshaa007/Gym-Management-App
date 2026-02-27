package com.example.gym;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TotalRevenueActivity extends AppCompatActivity {

    TextView tvRevenue;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_revenue);

        tvRevenue = findViewById(R.id.tvRevenue);
        db = new DatabaseHelper(this);

        loadRevenue();
    }

    private void loadRevenue() {
        int paid = db.getTotalRevenuePaid();
        int unpaid = db.getTotalRevenueUnpaid();

        tvRevenue.setText("Revenue (Paid): ₹" + paid + "\nAssets (Unpaid): ₹" + unpaid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRevenue();
    }
}