package com.example.samsung.qiwi_users_balance.presentation.view;

import android.os.Bundle;

import com.arellomobile.mvp.MvpView;

public interface ServiceView extends MvpView {

    public void showCallingScreen();

    public void setAppArguments();

    public void showNewMsg();
}
