package com.example.samsung.qiwi_users_balance.model;

public class QiwiUsersBalances {

    private String mCurrency;
    private Float mAmount;

    public QiwiUsersBalances(final String currency, final Float amount) {
        this.mCurrency = currency;
        this.mAmount = amount;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public Float getAmount() {
        return mAmount;
    }

    public void setCurrency(final String currency) {
        this.mCurrency = currency;
    }

    public void setAmount(final Float amount) {
        this.mAmount = amount;
    }
}
