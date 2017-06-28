package com.example.samsung.qiwi_users_balance.ui.fragment.users;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsEmptyException;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersFragment extends MvpAppCompatFragment implements UsersView {

    public static final String TAG = "UsersFragment";
    @InjectPresenter
    UsersPresenter mUsersPresenter;

    @BindView(R.id.btnExcheng)
    Button btnExcheng;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;

    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mUsersPresenter.setCxt(getContext());
        mUsersPresenter.setFragmentManager(getActivity().getFragmentManager());
        mUsersPresenter.setRvUsers(rvUsers);
        btnExcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsersPresenter.onClicExcheng();
            }
        });
        do {
            try {
                mUsersPresenter.createListQiwiUsers();
            } catch (DBCursorIsEmptyException e) {
                e.printStackTrace();
                mUsersPresenter.showDialog(e.getMessage());
            }
        } while (mUsersPresenter.getExceptions());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new ListQiwiUsersAdapter(mUsersPresenter.getDataset());
        rvUsers.setAdapter(mAdapter);
        rvUsers.setHasFixedSize(true); //Фиксируем размер списка

    }
}
