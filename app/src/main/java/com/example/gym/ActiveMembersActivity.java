package com.example.gym;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActiveMembersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper db;
    MemberAdapter adapter;
    List<Member> memberList = new ArrayList<>();
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_members);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearch); // ✅ FIXED

        db = new DatabaseHelper(this);

        adapter = new MemberAdapter(this, memberList);
        recyclerView.setAdapter(adapter);

        loadActiveMembers();

        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){}
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void loadActiveMembers() {

        memberList.clear();
        Cursor cursor = db.getActiveMembers();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                memberList.add(new Member(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiryDate"))
                ));
            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void filter(String text){

        memberList.clear();
        Cursor cursor = db.getActiveMembers();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));

                if (name.toLowerCase().contains(text.toLowerCase())) {

                    memberList.add(new Member(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            name,
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("expiryDate"))
                    ));
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveMembers();
    }
}