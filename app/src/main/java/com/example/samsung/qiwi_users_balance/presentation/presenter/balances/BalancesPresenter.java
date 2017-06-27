package com.example.samsung.qiwi_users_balance.presentation.presenter.balances;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.samsung.qiwi_users_balance.model.Balance;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsersBalances;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersBalancesAdapter;
import com.example.samsung.qiwi_users_balance.model.QiwiUsersBalances;
import com.example.samsung.qiwi_users_balance.model.QiwisUsersAPI;
import com.example.samsung.qiwi_users_balance.model.exceptions.ResultIsEmptyException;
import com.example.samsung.qiwi_users_balance.presentation.ControllerAPI;
import com.example.samsung.qiwi_users_balance.presentation.view.balances.BalancesView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.ui.fragment.Dialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class BalancesPresenter extends MvpPresenter<BalancesView> {
    public List<QiwiUsersBalances> mDataset;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DialogFragment mDialogFragment;
    private final String MSG = "msg";
    private FragmentManager mFrm;
    private boolean isExceptions = false;
    private int mUsersId;

    public void setUsersId(final int usersId) {
        this.mUsersId = usersId;
    }

    public void setFragmentManager(final FragmentManager frm) {
        this.mFrm = frm;
    }

    public boolean getExceptions() {
        return isExceptions;
    }

    public int getUsersID() {
        return mUsersId;
    }

    public void showDialog(final String msg) {
        mDialogFragment = new Dialog();
        Bundle args = new Bundle();
        args.putString(MSG, msg);
        mDialogFragment.setArguments(args);
        mDialogFragment.show(mFrm, "mDialogFragment");
        mDialogFragment.dismiss();
        isExceptions = true;
    }

    public void createListQiwiUsersBalances(final Context cxt, final RecyclerView rvUsersBalances)
            throws ResultIsEmptyException {

        if (mDataset == null) {
            mDataset = new ArrayList<QiwiUsersBalances>();
        }
        //Открываем програсс-диалог
        
        //Получаем списк из запроса
        try {
            mDataset.clear();
            QiwisUsersAPI qiwisUsersAPI = ControllerAPI.getAPI();
            qiwisUsersAPI.getBalancesById(mUsersId).enqueue(new Callback<List<JsonQiwisUsersBalances>>() {
                @Override
                public void onResponse(Call<List<JsonQiwisUsersBalances>> call,
                                       Response<List<JsonQiwisUsersBalances>> response) {

                    JsonQiwisUsersBalances jsonQiwisUsersBalances = response.body().get(0);

                    if (jsonQiwisUsersBalances.getResultCode() != 0) {
                        showDialog(jsonQiwisUsersBalances.getMessage());
                    } else {
                        for (Balance balance :
                                jsonQiwisUsersBalances.getBalances()) {
                            mDataset.add(new QiwiUsersBalances(balance.getCurrency(), balance.getAmount()));
                        }
                        isExceptions = false;
                    }
                }

                @Override
                public void onFailure(Call<List<JsonQiwisUsersBalances>> call, Throwable t) {
                    showDialog(t.getMessage());
                }
            });
            isExceptions = false;
        } catch (Exception e) {
            e.printStackTrace();
            showDialog(e.getMessage());
            throw new ResultIsEmptyException(cxt);
        }
        //Закрываем прогресс-диалог

        mLayoutManager = new LinearLayoutManager(cxt);
        rvUsersBalances.setLayoutManager(mLayoutManager);
        mAdapter = new ListQiwiUsersBalancesAdapter(mDataset);
        rvUsersBalances.setAdapter(mAdapter);
        rvUsersBalances.setHasFixedSize(true); //Фиксируем размер списка
    }

    public void onClicExcheng(final Context cxt, RecyclerView rvUsersBalances) {
        //Создаём резервную копию списка
        List<QiwiUsersBalances> mNewDataset = new ArrayList<QiwiUsersBalances>();
        mNewDataset.addAll(mDataset);
        //Обновляем список
        do {
            try {
                mDataset.clear();
                createListQiwiUsersBalances(cxt, rvUsersBalances);
                isExceptions = false;
            } catch (ResultIsEmptyException e) {
                e.printStackTrace();
                mDataset.addAll(mNewDataset);
                showDialog(e.getMessage());
            }
        } while (isExceptions);
    }

}
