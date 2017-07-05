package com.example.samsung.qiwi_users_balance.ui.fragment.users;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.model.ControllerDB;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.ManagerControllerDB;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;
import com.example.samsung.qiwi_users_balance.ui.fragment.Dialog;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @BindView(R.id.clpbLoading)
    ContentLoadingProgressBar clpbLoading;

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

        while (!App.getQiwiUsersListCreated()) {
            if (clpbLoading.getVisibility() == ContentLoadingProgressBar.GONE)
                showProgressBar();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dismissProgressBar();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new ListQiwiUsersAdapter(mUsersPresenter.getDataset());
        rvUsers.setAdapter(mAdapter);
        rvUsers.setHasFixedSize(true); //Фиксируем размер списка

        btnExcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Открываем прогресс-бар загрузки
                showProgressBar();
                try {
                    mUsersPresenter.onClicExcheng();
                } catch (DBIsNotDeletedException e) {
                    e.printStackTrace();
                    App.getDequeMsg().pushMsg(e.getMessage());
                } catch (DBCursorIsNullException e1) {
                    e1.printStackTrace();
                    App.getDequeMsg().pushMsg(e1.getMessage());
                } catch (Exception e2) {
                    e2.printStackTrace();
                    App.getDequeMsg().pushMsg(e2.getMessage());
                }
                showDialog();
//                rvUsers.getAdapter().notifyDataSetChanged();
                //Закрываем прогрксс-бар загрузки
                dismissProgressBar();
                //Открываем список
                showUsersList();
            }
        });
    }

    @Override
    public void showUsersList() {

        rvUsers.setVisibility(RecyclerView.VISIBLE);

    }

    @Override
    public void showProgressBar() {
        rvUsers.setVisibility(RecyclerView.GONE);
        clpbLoading.setVisibility(ContentLoadingProgressBar.VISIBLE);
    }

    @Override
    public void dismissProgressBar() {
        clpbLoading.setVisibility(ContentLoadingProgressBar.GONE);
        rvUsers.setVisibility(RecyclerView.VISIBLE);
    }

    @Override
    public void showDialog() {

        DialogFragment mDialogFragment = new Dialog();
        Bundle args = new Bundle();
        while (!App.getDequeMsg().isEmpty()) {
            args.clear();
            args.putString("msg", App.getDequeMsg().outMsg());
            mDialogFragment.setArguments(args);
            mDialogFragment.show(getActivity().getFragmentManager(), "mDialogFragment");
        }
    }
}
