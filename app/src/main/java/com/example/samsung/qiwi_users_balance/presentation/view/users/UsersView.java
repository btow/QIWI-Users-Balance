package com.example.samsung.qiwi_users_balance.presentation.view.users;

import android.content.Context;
import android.view.View;

import com.arellomobile.mvp.MvpView;
import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;

import java.util.List;

public interface UsersView extends MvpView {

    public void showProgressBar();

    public void dismissProgressBar();

    public void showMsg();

    public void showUsersBalances(final int userId);
}
