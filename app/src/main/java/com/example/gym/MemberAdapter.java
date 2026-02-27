package com.example.gym;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.*;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    Context context;
    List<Member> memberList;
    DatabaseHelper db;

    public MemberAdapter(Context context, List<Member> memberList) {
        this.context = context;
        this.memberList = memberList;
        db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.member_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Member member = memberList.get(position);

        holder.tvName.setText(member.getName());
        holder.tvPhone.setText("Phone: " + member.getPhone());
        holder.tvExpiry.setText("Expiry: " + member.getExpiryDate());

        // Status Logic
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiry = sdf.parse(member.getExpiryDate());
            Date today = new Date();

            long diff = expiry.getTime() - today.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            if (days < 0) {
                holder.tvStatus.setText("Expired");
                holder.tvStatus.setTextColor(Color.RED);
            }
            else if (days <= 3) {
                holder.tvStatus.setText("Expiring Soon");
                holder.tvStatus.setTextColor(Color.parseColor("#E65100"));
            }
            else {
                holder.tvStatus.setText("Active");
                holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // DELETE
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Member")
                    .setMessage("Are you sure you want to delete this member?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteMember(member.getId());
                        memberList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // EDIT
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditMemberActivity.class);
            intent.putExtra("id", member.getId());
            context.startActivity(intent);
        });

        if (db.isExpiringSoon(member.getExpiryDate())) {
            holder.tvStatus.setText("Expiring Soon");
            holder.tvStatus.setTextColor(Color.parseColor("#E65100"));
        }

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPhone, tvExpiry, tvStatus;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
