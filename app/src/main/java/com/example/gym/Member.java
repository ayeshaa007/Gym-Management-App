package com.example.gym;

public class Member {

    int id;
    String name, phone, expiryDate;

    public Member(int id, String name, String phone, String expiryDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.expiryDate = expiryDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getExpiryDate() { return expiryDate; }
}
