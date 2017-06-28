package com.example.samsung.qiwi_users_balance.model;

public class QiwiUsers {

    private Integer mId;
    private String mName;

    public QiwiUsers(final Integer id, final String name) {
        this.mId = id;
        this.mName = name;
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setId(final Integer id) {
        this.mId = mId;
    }

    public void setName(final String name) {
        this.mName = name;
    }

    @Override
    public String toString() {

        if (this == null) return "";
        return super.toString();
    }
}
