package com.example.samsung.qiwi_users_balance.presentation.view.balances;

import com.arellomobile.mvp.MvpView;

public interface BalancesView extends MvpView {

    public void showProgressBar();

    public void dismissProgressBar();

    public void showMsg();
}
