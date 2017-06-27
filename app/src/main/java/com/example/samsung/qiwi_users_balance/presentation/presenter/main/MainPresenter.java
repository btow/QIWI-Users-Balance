package com.example.samsung.qiwi_users_balance.presentation.presenter.main;


import android.content.Context;

import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.presentation.view.main.MainView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public void showBalances(android.support.v4.app.FragmentManager fm, final int usersId) {

        BalancesFragment balances = (BalancesFragment) fm.findFragmentById(R.id.fcUsersBalancesList);

        if (balances == null || balances.getUsersId() != usersId) {
            balances = BalancesFragment.newInstance(usersId);
            fm.beginTransaction().replace(R.id.fcUsersBalancesList, balances).commit();
        }
    }
}
