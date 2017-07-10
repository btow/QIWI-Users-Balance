package com.example.samsung.qiwi_users_balance.presentation.view.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.arellomobile.mvp.MvpView;

public interface MainView extends MvpView {

    public void getFragmentManager(FragmentManager fm);

    public void getUserId(int userId);

    public void showUsersFragment(FragmentManager fm);

    public void showBalancesFragment(FragmentManager fm, int userId);

    public void showProgressBar(FragmentManager fm, final Bundle args);

    public void showMessageFragment(FragmentManager fm, final Bundle args);

    public void setTypeActivityLayout();
}
