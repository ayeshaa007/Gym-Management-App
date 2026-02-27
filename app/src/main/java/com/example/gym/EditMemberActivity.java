package com.example.gym;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditMemberActivity extends AppCompatActivity {

    EditText etName, etPhone, etJoinDate, etFee;
    Spinner spinnerPlan, spinnerPayment;
    Button btnUpdate;
    DatabaseHelper db;
    int memberId;

    String[] plans = {"1 Month", "3 Months", "6 Months"};
    String[] payments = {"Paid", "Unpaid"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etJoinDate = findViewById(R.id.etJoinDate);
        etFee = findViewById(R.id.etFee);
        spinnerPlan = findViewById(R.id.spinnerPlan);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        btnUpdate = findViewById(R.id.btnUpdate);

        spinnerPlan.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, plans));

        spinnerPayment.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, payments));

        memberId = getIntent().getIntExtra("id", -1);

        loadMemberData();

        // Date Picker
        etJoinDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        String displayDate = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
                        etJoinDate.setText(displayDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        btnUpdate.setOnClickListener(v -> updateMember());
    }

    @SuppressLint("Range")
    private void loadMemberData() {
        Cursor cursor = db.getAllMembers();
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("id")) == memberId) {

                etName.setText(cursor.getString(cursor.getColumnIndex("fullName")));
                etPhone.setText(cursor.getString(cursor.getColumnIndex("phone")));

                // Convert db date yyyy-MM-dd to dd/MM/yyyy for display
                String dbJoinDate = cursor.getString(cursor.getColumnIndex("joinDate"));
                etJoinDate.setText(convertToDisplayDate(dbJoinDate));

                etFee.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("feeAmount"))));

                String membershipPlan = cursor.getString(cursor.getColumnIndex("membershipPlan"));
                String payment = cursor.getString(cursor.getColumnIndex("paymentStatus"));

                spinnerPlan.setSelection(getIndex(spinnerPlan, membershipPlan));
                spinnerPayment.setSelection(getIndex(spinnerPayment, payment));
                break;
            }
        }
        cursor.close();
    }

    private void updateMember() {
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String joinDate = etJoinDate.getText().toString();
        String membershipPlan = spinnerPlan.getSelectedItem().toString();
        String feeStr = etFee.getText().toString();
        String payment = spinnerPayment.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || joinDate.isEmpty() || feeStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double fee = Double.parseDouble(feeStr);

        // Convert joinDate to yyyy-MM-dd before updating
        String dbJoinDate = convertToDBDate(joinDate);

        db.updateMember(memberId, name, phone, dbJoinDate, membershipPlan, fee, payment);

        Toast.makeText(this, "Member Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) return i;
        }
        return 0;
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

    private String convertToDisplayDate(String dbDate) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdfOutput.format(sdfInput.parse(dbDate));
        } catch (Exception e) {
            return dbDate;
        }
    }
}