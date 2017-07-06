package com.example.samsung.qiwi_users_balance.presentation.view.main;

import android.support.v4.app.FragmentManager;

import com.arellomobile.mvp.MvpView;

public interface MainView extends MvpView {

    public void showUsersFragment(FragmentManager fm);

    public void showBalancesFragment(FragmentManager fm, int userId);

}
