package com.example.samsung.qiwi_users_balance.ui.fragment.users;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.exceptions.BDCursorIsEmptyException;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersFragment extends MvpAppCompatFragment implements UsersView {

    public static final String TAG = "UsersFragment";
    @InjectPresenter
    private UsersPresenter mUsersPresenter;

    @BindView(R.id.btnExcheng)
    private Button btnExcheng;
    @BindView(R.id.rvUsers)
    private RecyclerView rvUsers;

    public List<QiwiUsers> mDataset;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        ButterKnife.bind(btnExcheng);
        btnExcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsersPresenter.onClicExcheng(getContext());
            }
        });

        try {
            mUsersPresenter.createListQiwiUsers(getContext(), mDataset);
        } catch (BDCursorIsEmptyException e) {
            e.printStackTrace();
        }

        ButterKnife.bind(rvUsers);
        rvUsers.setHasFixedSize(true); //Фиксируем размер списка

        mLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(mLayoutManager);
        mAdapter = new ListQiwiUsersAdapter(mDataset);
        rvUsers.setAdapter(mAdapter);

        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
