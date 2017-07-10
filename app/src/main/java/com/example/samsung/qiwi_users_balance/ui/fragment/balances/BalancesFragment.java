package com.example.samsung.qiwi_users_balance.ui.fragment.balances;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersBalancesAdapter;
import com.example.samsung.qiwi_users_balance.presentation.view.balances.BalancesView;
import com.example.samsung.qiwi_users_balance.presentation.presenter.balances.BalancesPresenter;

import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BalancesFragment extends MvpAppCompatFragment implements BalancesView {

    public static final String TAG = "BalancesFragment";
    @InjectPresenter
    BalancesPresenter mBalancesPresenter;

    @BindView(R.id.btnExcheng)
    Button btnExcheng;
    @BindView(R.id.rvList)
    RecyclerView rvBalances;

    public static BalancesFragment newInstance(final int usersId) {
        BalancesFragment fragment = new BalancesFragment();

        Bundle args = new Bundle();
        args.putInt(App.USER_ID, usersId);
        fragment.setArguments(args);

        return fragment;
    }

    public int getUsersId() {
        return getArguments().getInt(App.USER_ID, 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mBalancesPresenter.setCxt(getContext());
        mBalancesPresenter.setFragmentManager(getActivity().getSupportFragmentManager());
        //Открываем програсс-диалог

        try {
            if (mBalancesPresenter.createListQiwiUsersBalances()) mBalancesPresenter.showDialog();
        } catch (Exception e) {
            e.printStackTrace();
            mBalancesPresenter.showDialog();
        }
        //Закрываем прогресс-диалог


        btnExcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Открываем програсс-диалог

                try {
                    mBalancesPresenter.onClicExcheng();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!mBalancesPresenter.getExceptions()) rvBalances.getAdapter().notifyDataSetChanged();
                //Закрываем прогресс-диалог

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvBalances.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new ListQiwiUsersBalancesAdapter(mBalancesPresenter.getDataset());
        rvBalances.setAdapter(mAdapter);
        rvBalances.setHasFixedSize(true); //Фиксируем размер списка

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void dismissProgressBar() {

    }

    @Override
    public void showMsg() {

    }
}