package com.example.gym;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "members";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullName TEXT, " +
                "phone TEXT, " +
                "joinDate TEXT, " +
                "membershipPlan TEXT, " +
                "feeAmount REAL, " +
                "paymentStatus TEXT, " +
                "expiryDate TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ADD MEMBER
    public void addMember(String name, String phone, String joinDate,
                          String plan, double fee, String paymentStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String joinDateDB = convertToDBDate(joinDate);
        String expiryDate = calculateExpiry(joinDateDB, plan);

        values.put("fullName", name);
        values.put("phone", phone);
        values.put("joinDate", joinDateDB);
        values.put("membershipPlan", plan);
        values.put("feeAmount", fee);
        values.put("paymentStatus", paymentStatus);
        values.put("expiryDate", expiryDate);

        db.insert(TABLE_NAME, null, values);
    }

    // UPDATE MEMBER
    public void updateMember(int id, String name, String phone,
                             String joinDate, String plan,
                             double fee, String paymentStatus) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String joinDateDB = convertToDBDate(joinDate);
        String expiryDate = calculateExpiry(joinDateDB, plan);

        values.put("fullName", name);
        values.put("phone", phone);
        values.put("joinDate", joinDateDB);
        values.put("membershipPlan", plan);
        values.put("feeAmount", fee);
        values.put("paymentStatus", paymentStatus);
        values.put("expiryDate", expiryDate);

        db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)});
    }

    // DELETE MEMBER
    public void deleteMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    // GET ALL MEMBERS
    public Cursor getAllMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC", null);
    }

    // TOTAL MEMBERS COUNT
    public int getTotalMembers() {
        Cursor cursor = getAllMembers();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // ACTIVE MEMBERS
    public Cursor getActiveMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE expiryDate >= ?", new String[]{today});
    }

    // EXPIRED MEMBERS
    public Cursor getExpiredMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE expiryDate < ?", new String[]{today});
    }

    // TOTAL REVENUE (PAID)
    public int getTotalRevenuePaid() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(feeAmount) FROM " + TABLE_NAME + " WHERE paymentStatus='Paid'", null);
        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    // TOTAL ASSETS (UNPAID)
    public int getTotalRevenueUnpaid() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(feeAmount) FROM " + TABLE_NAME + " WHERE paymentStatus='Unpaid'", null);
        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    // CONVERT JOIN DATE TO yyyy-MM-dd FORMAT
    private String convertToDBDate(String date) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdfOutput.format(sdfInput.parse(date));
        } catch (Exception e) {
            return date;
        }
    }

    // CALCULATE EXPIRY DATE
    public String calculateExpiry(String joinDate, String plan) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(joinDate));

            if (plan.equals("1 Month")) cal.add(Calendar.MONTH, 1);
            else if (plan.equals("3 Months")) cal.add(Calendar.MONTH, 3);
            else if (plan.equals("6 Months")) cal.add(Calendar.MONTH, 6);

            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return joinDate;
        }
    }


    // CHECK IF MEMBER IS EXPIRING WITHIN 3 DAYS
    public boolean isExpiringSoon(String expiryDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date expiry = sdf.parse(expiryDate);
            Date today = new Date();

            long diff = expiry.getTime() - today.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            return days >= 0 && days <= 3;

        } catch (Exception e) {
            return false;
        }
    }
    public int getActiveMembersCount() {
        Cursor cursor = getActiveMembers();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getExpiredMembersCount() {
        Cursor cursor = getExpiredMembers();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}