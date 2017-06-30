package com.example.samsung.qiwi_users_balance.presentation.presenter.balances;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.Balance;
import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsersBalances;
import com.example.samsung.qiwi_users_balance.model.QiwiUsersBalances;
import com.example.samsung.qiwi_users_balance.model.QiwisUsersAPI;
import com.example.samsung.qiwi_users_balance.presentation.view.balances.BalancesView;
import com.example.samsung.qiwi_users_balance.ui.fragment.Dialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class BalancesPresenter extends MvpPresenter<BalancesView> {
    private List<QiwiUsersBalances> mDataset;
    private Context mCxt;
    private String exMsg;
    private FragmentManager mFrm;
    private boolean isExceptions = false;
    private int mUsersId;

    public void setCxt(Context cxt) {
        this.mCxt = cxt;
        this.exMsg = cxt.getString(R.string.response_is_null);
    }

    public void setUsersId(final int usersId) {
        this.mUsersId = usersId;
    }

    public void setFragmentManager(final FragmentManager frm) {
        this.mFrm = frm;
    }

    public String getExMsg() {
        return exMsg;
    }

    public List<QiwiUsersBalances> getDataset() {
        return mDataset;
    }

    public boolean getExceptions() {
        return isExceptions;
    }

    public int getUsersID() {
        return mUsersId;
    }

    public void collResponseHandler(@Nullable Response<JsonQiwisUsersBalances> response) {
        responseHandler(response);
    }

    public Callback<JsonQiwisUsersBalances> collCallback() {
        return callback();
    }

    public void showDialog() {
        DialogFragment mDialogFragment = new Dialog();
        Bundle args = new Bundle();
        args.putString("msg", exMsg);
        mDialogFragment.setArguments(args);
        mDialogFragment.show(mFrm, "mDialogFragment");
    }

    private void responseHandler(@Nullable Response<JsonQiwisUsersBalances> response) {

        if (response == null) {
            isExceptions = true;
            exMsg = mCxt.getString(R.string.response_is_null);
        } else if (response.body() == null) {
            isExceptions = true;
            exMsg = mCxt.getString(R.string.responses_body_is_null);
        } else {
            JsonQiwisUsersBalances jsonQiwisUsersBalances = response.body();

            if (jsonQiwisUsersBalances.getResultCode() != 0) {
                isExceptions = true;
                exMsg =
                        "result code: " + jsonQiwisUsersBalances.getResultCode().toString()
                        + ", message: " + jsonQiwisUsersBalances.getMessage();
            } else {
                for (Balance balance :
                        jsonQiwisUsersBalances.getBalances()) {
                    mDataset.add(new QiwiUsersBalances(balance.getCurrency(), balance.getAmount()));
                }
                isExceptions = false;
            }
        }
    }

    private Callback<JsonQiwisUsersBalances> callback() {

        return new Callback<JsonQiwisUsersBalances>() {

            @Override
            public void onResponse(@Nullable Call<JsonQiwisUsersBalances> call,
                                   @Nullable Response<JsonQiwisUsersBalances> response) {

                responseHandler(response);
            }

            @Override
            public void onFailure(@Nullable Call<JsonQiwisUsersBalances> call,
                                  @Nullable Throwable t) {
                isExceptions = true;
                if (t != null) exMsg = t.getMessage();
            }
        };
    }

    public boolean createListQiwiUsersBalances() throws Exception {

        if (mDataset == null) {
            mDataset = new ArrayList<>();
        } else {
            mDataset.clear();
        }

        //Получаем список из запроса
        try {
            mDataset.clear();
            QiwisUsersAPI qiwisUsersAPI = ControllerAPI.getAPI();
            qiwisUsersAPI.getBalancesById(mUsersId).enqueue(callback());
            isExceptions = false;
        } catch (Exception e) {
            e.printStackTrace();
            exMsg = e.getMessage();
            throw new Exception(e);
        }
        return isExceptions;
    }

    public void onClicExcheng() throws Exception {
        //Создаём резервную копию списка
        List<QiwiUsersBalances> mNewDataset = new ArrayList<>();
        mNewDataset.addAll(mDataset);
        //Обновляем список
        do {
            mDataset.clear();
            try {
                if (createListQiwiUsersBalances()) {
                    isExceptions = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                exMsg = e.getMessage();
                throw new Exception(e);
            }
            mDataset.addAll(mNewDataset);
        } while (isExceptions);
    }
}
