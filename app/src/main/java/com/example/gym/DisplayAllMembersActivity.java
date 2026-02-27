package com.example.gym;



import android.database.Cursor;
import android.os.Bundle;
import android.text.*;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import java.util.*;

public class DisplayAllMembersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseHelper db;
    MemberAdapter adapter;
    List<Member> memberList = new ArrayList<>();
    List<Member> fullList = new ArrayList<>();
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_members);

        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.etSearch);

        db = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMembers();

        adapter = new MemberAdapter(this, memberList);
        recyclerView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){}
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void loadMembers() {
        Cursor cursor = db.getAllMembers();
        while (cursor.moveToNext()) {
            Member member = new Member(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(7)
            );
            memberList.add(member);
            fullList.add(member);
        }
    }

    private void filter(String text){
        memberList.clear();

        Cursor cursor = db.getAllMembers();

        while(cursor.moveToNext()){
            String name = cursor.getString(1);

            if(name.toLowerCase().contains(text.toLowerCase())){
                memberList.add(new Member(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(7)
                ));
            }
        }

        adapter.notifyDataSetChanged();
    }
}