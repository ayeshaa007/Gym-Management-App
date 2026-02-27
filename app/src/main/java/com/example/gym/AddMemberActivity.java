package com.example.gym;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMemberActivity extends AppCompatActivity {

    EditText etName, etPhone, etJoinDate, etFee;
    Spinner spinnerPlan, spinnerPayment;
    Button btnSave;
    DatabaseHelper db;

    String[] plans = {"1 Month", "3 Months", "6 Months"};
    String[] payments = {"Paid", "Unpaid"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etJoinDate = findViewById(R.id.etJoinDate);
        etFee = findViewById(R.id.etFee);
        spinnerPlan = findViewById(R.id.spinnerPlan);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        btnSave = findViewById(R.id.btnSave);

        spinnerPlan.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, plans));

        spinnerPayment.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, payments));

        // Date Picker
        etJoinDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        // Show date as dd/MM/yyyy for user
                        String displayDate = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
                        etJoinDate.setText(displayDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        btnSave.setOnClickListener(v -> saveMember());
    }

    private void saveMember() {
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String joinDate = etJoinDate.getText().toString();
        String plan = spinnerPlan.getSelectedItem().toString();
        String feeStr = etFee.getText().toString();
        String payment = spinnerPayment.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || joinDate.isEmpty() || feeStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double fee = Double.parseDouble(feeStr);

        // Convert joinDate to yyyy-MM-dd before saving
        String dbJoinDate = convertToDBDate(joinDate);

        db.addMember(name, phone, dbJoinDate, plan, fee, payment);

        Toast.makeText(this, "Member Added Successfully", Toast.LENGTH_SHORT).show();
        finish(); // go back
    }

    private String convertToDBDate(String date) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdfOutput.format(sdfInput.parse(date));
        } catch (Exception e) {
            return date;
        }
    }
}