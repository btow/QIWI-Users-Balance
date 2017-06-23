package com.example.samsung.qiwi_users_balance.model;

public class QiwiUsers {

    private int mId;
    private String mName;

    public QiwiUsers(final int id, final String name) {
        this.mId = id;
        this.mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setId(final int id) {
        this.mId = mId;
    }

    public void setName(final String name) {
        this.mName = name;
    }
}
