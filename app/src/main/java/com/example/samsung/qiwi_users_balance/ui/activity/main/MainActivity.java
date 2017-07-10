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
import com.example.samsung.qiwi_users_balance.ui.fragment.ServiceFragment;
import com.example.samsung.qiwi_users_balance.ui.fragment.users.UsersFragment;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    public static final String TAG = "MainActivity";
    @InjectPresenter
    MainPresenter mMainPresenter;

    private int mUserId = 0;
    FragmentManager mFragmentManager;

    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }

    public static Intent getIntent(final Context context, final Bundle arg) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(arg);

        return intent;
    }

    protected void selectUsedPrimFragmentsVersion() {

        Bundle args = new Bundle();

        switch (App.getUsedPrimFragmentsVersion()) {

            case App.USERS_FRAMENT:
                mMainPresenter.showUsersFragment(mFragmentManager);
                break;
            case App.LOADING_FRAGMENT:
                args.clear();
                args.putInt(App.CALL_FROM, App.CALL_FROM_MAIN_ACTIVITY);
                args.putInt(App.FRAG_NUMBER, R.id.flPrimFragment);
                args.putInt(App.SERV_VERSION, App.LOAD_FRAG);
                mMainPresenter.showLoadFragment(mFragmentManager, args);
                break;
            case App.MESSAGE_FRAGMENT:
                args.clear();
                args.putInt(App.CALL_FROM, App.CALL_FROM_MAIN_ACTIVITY);
                args.putInt(App.FRAG_NUMBER, R.id.flPrimFragment);
                args.putInt(App.SERV_VERSION, App.MESS_FRAG);
                mMainPresenter.showMsgFragment(mFragmentManager, args);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {

            mUserId = savedInstanceState.getInt(App.USER_ID);
        }
        selectUsedPrimFragmentsVersion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectUsedPrimFragmentsVersion();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(App.USER_ID, mUserId);
    }

    @Override
    public void getFragmentManager(FragmentManager fm) {
        fm = mFragmentManager;
    }

    @Override
    public void getUserId(int userId) {
        userId = mUserId;
    }

    @Override
    public void setTypeActivityLayout() {
        App.setUsedTwoFragmentLayout((FrameLayout) findViewById(R.id.flSecFragment) != null);
    }

    @Override
    public void showUsersFragment(FragmentManager fm) {

        UsersFragment users = null;
        try {
            users = (UsersFragment) fm.findFragmentById(R.id.flPrimFragment);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (users == null) {
            users = UsersFragment.newInstance();
        }
        fm.beginTransaction().replace(R.id.flPrimFragment, users).commit();
    }

    @Override
    public void showBalancesFragment(FragmentManager fm, int userId) {

        UsersFragment balances = null;
        try {
            balances = (UsersFragment) fm.findFragmentById(R.id.flSecFragment);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        if (balances == null) {
            balances = UsersFragment.newInstance(userId);
        }
        fm.beginTransaction().replace(R.id.flSecFragment, balances).commit();
    }

    @Override
    public void showProgressBar(FragmentManager fm, Bundle args) {

        App.setArguments(args);
        ServiceFragment loading = null;
        try {
            loading = (ServiceFragment) fm.findFragmentById(args.getInt(App.FRAG_NUMBER));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        if (loading == null) {
            loading = ServiceFragment.newInstance(args);
        }
        fm.beginTransaction().replace(args.getInt(App.FRAG_NUMBER), loading).commit();
    }

    @Override
    public void showMessageFragment(FragmentManager fm, Bundle args) {

        ServiceFragment messag = null;
        try {
            messag = (ServiceFragment) fm.findFragmentById(args.getInt(App.FRAG_NUMBER));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        if (messag == null) {
            messag = ServiceFragment.newInstance(args);
        }
        fm.beginTransaction().replace(args.getInt(App.FRAG_NUMBER), messag).commit();
    }
}
