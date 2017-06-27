package com.example.samsung.qiwi_users_balance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonQiwisUsersBalances {

    @SerializedName("result_code")
    @Expose
    private Integer resultCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("balances")
    @Expose
    private List<Balance> balances = null;

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

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

}
