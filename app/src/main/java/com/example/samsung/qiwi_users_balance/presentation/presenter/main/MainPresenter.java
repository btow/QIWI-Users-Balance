package com.example.samsung.qiwi_users_balance.presentation.presenter.main;


import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.presentation.view.main.MainView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private MainPresenterTask mMainPresenterTask;

    public MainPresenter() {

        mMainPresenterTask = new MainPresenterTask();
        mMainPresenterTask.execute();
    }

    public void showUsersFragment(FragmentManager fm) {
        getViewState().showUsersFragment(fm);
    }

    public void showBalancesFragment(FragmentManager fm, int userId) {
        getViewState().showBalancesFragment(fm, userId);
    }

    public void showLoadFragment(FragmentManager fm, final Bundle args) {
        getViewState().showProgressBar(fm, args);
    }

    public void showMsgFragment(FragmentManager fm, final Bundle args) {
        getViewState().showMessageFragment(fm, args);
    }

    private class MainPresenterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            FragmentManager mFragmentManager = null;
            getViewState().getFragmentManager(mFragmentManager);
            getViewState().setTypeActivityLayout();

            if (App.getUsedTwoFragmentLayout()) {

                Bundle args = new Bundle();

                switch (App.getUsedSecondFragmentsVersion()) {

                    case App.BALANCES_FRAGMENT:
                        args.clear();
                        int userId = 0;
                        getViewState().getUserId(userId);
                        args.putInt(App.USER_ID, userId);
                        showBalancesFragment(mFragmentManager, userId);
                    case App.LOADING_FRAGMENT:
                        args.clear();
                        args.putInt(App.CALL_FROM, App.CALL_FROM_MAIN_ACTIVITY);
                        args.putInt(App.FRAG_NUMBER, R.id.flSecFragment);
                        showLoadFragment(mFragmentManager, args);
                        break;
                    case App.MESSAGE_FRAGMENT:
                        args.clear();
                        args.putInt(App.CALL_FROM, App.CALL_FROM_MAIN_ACTIVITY);
                        args.putInt(App.FRAG_NUMBER, R.id.flSecFragment);
                        showMsgFragment(mFragmentManager, args);
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
