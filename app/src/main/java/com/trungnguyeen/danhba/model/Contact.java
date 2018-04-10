package com.trungnguyeen.danhba.model;

import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by trungnguyeen on 4/9/18.
 */

public class Contact {

    private String id;
    private String name;
    private String phoneNumber;

    public Contact() {
    }

    public Contact(String name, String sdt) {
        this.name = name;
        this.phoneNumber = sdt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCharOfName(String name){
        name = name.trim();
        String[] tmp = name.split(" ");
        Log.i(TAG, "getCharOfName: " + tmp[tmp.length -1]);
        return String.valueOf(tmp[tmp.length - 1].charAt(0)).toUpperCase();
    }

    @Override
    public String toString() {
        return name + "  " + phoneNumber;
    }
}
