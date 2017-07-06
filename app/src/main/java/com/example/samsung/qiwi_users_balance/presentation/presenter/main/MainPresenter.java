package com.example.samsung.qiwi_users_balance.presentation.presenter.main;


import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.presentation.view.main.MainView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;
import com.example.samsung.qiwi_users_balance.ui.fragment.users.UsersFragment;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public MainPresenter() {
    }

    public void showUsersFragment(FragmentManager fm) {

        getViewState().showUsersFragment(fm);
    }

    public void showBalancesFragment(FragmentManager fm, int userId) {

        getViewState().showBalancesFragment(fm, userId);
    }
}
