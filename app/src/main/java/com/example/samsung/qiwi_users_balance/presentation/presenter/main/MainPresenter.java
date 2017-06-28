package com.example.samsung.qiwi_users_balance.presentation.presenter.main;


import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.presentation.view.main.MainView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import android.support.v4.app.FragmentManager;
import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;
import com.example.samsung.qiwi_users_balance.ui.fragment.users.UsersFragment;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public void showUsers(FragmentManager fm) {

        UsersFragment users = (UsersFragment) fm.findFragmentById(R.id.flUsersList);

        if (users == null) {
            users = UsersFragment.newInstance();
            fm.beginTransaction().replace(R.id.flUsersList, users).commit();
        }
    }

    public void showBalances(FragmentManager fm, final int usersId) {

        BalancesFragment balances = (BalancesFragment) fm.findFragmentById(R.id.flUsersBalancesList);

        if (balances == null || balances.getUsersId() != usersId) {
            balances = BalancesFragment.newInstance(usersId);
            fm.beginTransaction().replace(R.id.flUsersBalancesList, balances).commit();
        }
    }
}
