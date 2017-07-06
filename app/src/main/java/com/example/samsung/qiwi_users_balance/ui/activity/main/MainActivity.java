package com.example.samsung.qiwi_users_balance.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.presentation.presenter.main.MainPresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.main.MainView;
import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;
import com.example.samsung.qiwi_users_balance.ui.fragment.users.UsersFragment;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    public static final String TAG = "MainActivity";
    @InjectPresenter
    MainPresenter mMainPresenter;

    private int mUserId = 0;

    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mUserId = savedInstanceState.getInt(App.getUid());
        }

        if ((FrameLayout) findViewById(R.id.flUsersBalancesList) != null) {

            mMainPresenter.showBalancesFragment(getSupportFragmentManager(), mUserId);
        }
        mMainPresenter.showUsersFragment(getSupportFragmentManager());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(App.getUid(), mUserId);
    }

    @Override
    public void showUsersFragment(FragmentManager fm) {

        UsersFragment users = (UsersFragment) fm.findFragmentById(R.id.flUsersList);

        if (users == null) {
            users = UsersFragment.newInstance();
            fm.beginTransaction().replace(R.id.flUsersList, users).commit();
        }

    }

    @Override
    public void showBalancesFragment(FragmentManager fm, int userId) {

        BalancesFragment balances = (BalancesFragment) fm.findFragmentById(R.id.flUsersBalancesList);

        if (balances == null) {
            balances = BalancesFragment.newInstance(userId);
            fm.beginTransaction().replace(R.id.flUsersBalancesList, balances).commit();
        }

    }
}
