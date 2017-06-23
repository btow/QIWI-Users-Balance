package com.example.samsung.qiwi_users_balance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonQiwisUsers {

    @SerializedName("result_code")
    @Expose
    private Integer resultCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
