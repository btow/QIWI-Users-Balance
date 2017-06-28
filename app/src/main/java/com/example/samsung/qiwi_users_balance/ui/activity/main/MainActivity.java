package com.example.samsung.qiwi_users_balance.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.presentation.presenter.main.MainPresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.main.MainView;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    public static final String TAG = "MainActivity";
    @InjectPresenter
    MainPresenter mMainPresenter;

    private final String UID = "usersId";
    private int usersId = 0;

    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            usersId = savedInstanceState.getInt(UID);
        }

        mMainPresenter.showUsers(getSupportFragmentManager());
//        mMainPresenter.showBalances(getSupportFragmentManager(), usersId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(UID, usersId);
    }
}
