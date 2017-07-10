package com.example.samsung.qiwi_users_balance.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.presentation.presenter.ServicePresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.ServiceView;
import com.example.samsung.qiwi_users_balance.ui.activity.balances.BalancesActivity;
import com.example.samsung.qiwi_users_balance.ui.activity.main.MainActivity;
import com.example.samsung.qiwi_users_balance.ui.fragment.users.UsersFragment;

public class ServiceFragment extends MvpAppCompatFragment implements ServiceView {

    public static final String TAG = "ServiceFragment";

    @InjectPresenter
    ServicePresenter mServicePresenter;

    private ProgressBar pbLoading;
    private LinearLayout llMsg;
    private TextView tvMsg;
    private Button btnRepeat;
    private Button btnContinue;

    public static ServiceFragment newInstance(final Bundle args) {
        ServiceFragment fragment = new ServiceFragment();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(getArguments().getInt(App.SERV_VERSION), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments().getInt(App.SERV_VERSION) == App.LOAD_FRAG) {

            pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
            pbLoading.setIndeterminate(true);
        } else if (getArguments().getInt(App.SERV_VERSION) == App.MESS_FRAG) {

            llMsg = (LinearLayout) view.findViewById(R.id.llMsg);
            llMsg.setVisibility(LinearLayout.VISIBLE);
            tvMsg = (TextView) view.findViewById(R.id.tvMsg);
            btnRepeat = (Button) view.findViewById(R.id.btnRepeat);
            btnRepeat.setOnClickListener(mServicePresenter.onClickRepeat(view));
            btnContinue = (Button) view.findViewById(R.id.btnContinue);
            btnContinue.setOnClickListener(mServicePresenter.onClickRepeat(view));

            if (!App.getDequeMsg().isEmpty()) {
                tvMsg.setText(App.getDequeMsg().outMsg());
            } else {
                showCallingScreen();
            }
        }
    }

    @Override
    public void showCallingScreen() {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        int users_id = getArguments().getInt(App.USER_ID);
        int call_from = getArguments().getInt(App.CALL_FROM);
        int frag_number = getArguments().getInt(App.FRAG_NUMBER);

        switch (call_from) {

            case App.CALL_FROM_MAIN_ACTIVITY:
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra(App.USER_ID, users_id);
                startActivity(intent);
                break;
            case App.CALL_FROM_BALANCES_ACTIVITY:
                intent = new Intent(getContext(), BalancesActivity.class);
                intent.putExtra(App.USER_ID, users_id);
                startActivity(intent);
                break;
            case App.CALL_FROM_PRIM_FRAGMENT:
                UsersFragment users = (UsersFragment) fm.findFragmentById(R.id.flPrimFragment);

                if (users == null) {
                    users = UsersFragment.newInstance();
                }
                fm.beginTransaction().replace(R.id.flPrimFragment, users).commit();
                break;
            case App.CALL_FROM_SECOND_FRAGMENT:
                UsersFragment balances = (UsersFragment) fm.findFragmentById(R.id.flSecFragment);

                if (balances == null) {
                    balances = UsersFragment.newInstance(users_id);
                }
                fm.beginTransaction().replace(R.id.flSecFragment, balances).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void setAppArguments() {
        App.setArguments(getArguments());
    }

    @Override
    public void showNewMsg() {
        tvMsg.setText(App.getDequeMsg().outMsg());
        onResume();
    }
}
