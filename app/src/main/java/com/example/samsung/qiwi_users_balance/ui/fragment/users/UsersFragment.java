package com.example.samsung.qiwi_users_balance.ui.fragment.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersFragmentView;
import com.example.samsung.qiwi_users_balance.ui.activity.balances.BalancesActivity;
import com.example.samsung.qiwi_users_balance.ui.fragment.ServiceFragment;
import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersFragment extends MvpAppCompatFragment implements UsersFragmentView {

    public static final String TAG = "UsersFragment";

    @InjectPresenter
    UsersPresenter mUsersPresenter;

    @BindView(R.id.btnExcheng)
    Button btnExcheng;
    @BindView(R.id.rvList)
    android.support.v7.widget.RecyclerView rvUsers;

    private boolean mDualPlane;
    private int mUserID = 0;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Bundle args = new Bundle();
            args.putInt(App.CALL_FROM, App.CALL_FROM_PRIM_FRAGMENT);
            args.putInt(App.FRAG_NUMBER, R.id.flPrimFragment);
            args.putInt(App.SERV_VERSION, App.LOAD_FRAG);

            try {
                mUsersPresenter.onClicExcheng();
                //Открываем прогресс-бар загрузки
                showProgressBar(args);
            } catch (DBIsNotDeletedException | DBCursorIsNullException e) {
                e.printStackTrace();
                App.getDequeMsg().pushMsg(e.getMessage());
            } catch (Exception e2) {
                e2.printStackTrace();
                App.getDequeMsg().pushMsg(e2.getMessage());
            }
            args.putInt(App.SERV_VERSION, App.MESS_FRAG);
            showMsg(args);
            rvUsers.getAdapter().notifyDataSetChanged();
        }
    };

    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public static UsersFragment newInstance(int userId) {
        UsersFragment fragment = new UsersFragment();

        Bundle args = new Bundle();
        args.putInt(App.USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    public int getUsersId() {
        return mUserID;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View balancesFragment = getActivity().findViewById(R.id.flSecFragment);
        mDualPlane = balancesFragment != null && balancesFragment.getVisibility() == RecyclerView.VISIBLE;

        if (savedInstanceState != null) {
            mUserID = savedInstanceState.getInt(App.USER_ID, 0);
        }

        ButterKnife.bind(this, view);

        while (!App.getQiwiUsersListCreated()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        ListQiwiUsersAdapter adapter = new ListQiwiUsersAdapter(mUsersPresenter.getDataset());
        adapter.SetOnItemClickListener(new ListQiwiUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Обновить экран
                Toast.makeText(view.getContext(), "Balances of user \"" + mUsersPresenter.getDataset().get(position).getName() + "\'", Toast.LENGTH_SHORT).show();
                showUsersBalances(position);
            }
        });
        rvUsers.setAdapter(adapter);
        rvUsers.setHasFixedSize(true); //Фиксируем размер списка

        btnExcheng.setOnClickListener(mOnClickListener);

        if (mDualPlane) {
            showUsersBalances(mUserID);
        }
    }

    @Override
    public void showProgressBar(Bundle args) {

        openServiceFragment(args);
    }

    @Override
    public void showMsg(Bundle args) {

        if (App.getDequeMsg().isEmpty()) {
            return;
        } else {

            openServiceFragment(args);
        }
    }

    private void openServiceFragment(Bundle args) {

        int fragmentsVersion = R.id.flPrimFragment;

        if (args.getInt(App.CALL_FROM) == App.CALL_FROM_SECOND_FRAGMENT) {

            fragmentsVersion = R.id.flSecFragment;
        }

        ServiceFragment serviceFragment = null;
        try {
            serviceFragment = (ServiceFragment) getActivity().getSupportFragmentManager().findFragmentById(fragmentsVersion);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (serviceFragment == null) {

            serviceFragment = ServiceFragment.newInstance(args);
        }
        getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentsVersion, serviceFragment).commit();
    }

    @Override
    public void showUsersBalances(int userId) {

        mUserID = userId;

        if (mDualPlane) {
            rvUsers.setId(userId);

            BalancesFragment balancesFragment = null;
            try {
                balancesFragment = (BalancesFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.flSecFragment);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            if (balancesFragment == null || balancesFragment.getUsersId() != userId) {

                balancesFragment = BalancesFragment.newInstance(userId);
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flSecFragment, balancesFragment).commit();
        } else {
            startActivity(new Intent(getActivity().getBaseContext(), BalancesActivity.class).putExtra(App.USER_ID, userId));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(App.USER_ID, mUserID);
    }
}
