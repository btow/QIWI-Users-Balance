package com.example.samsung.qiwi_users_balance.ui.fragment.balances;

import android.os.Bundle;
import android.os.health.UidHealthStats;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.exceptions.ResultIsEmptyException;
import com.example.samsung.qiwi_users_balance.presentation.presenter.balances.BalancesPresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.balances.BalancesView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.samsung.qiwi_users_balance.R.id.rvUsers;

public class BalancesFragment extends MvpAppCompatFragment implements BalancesView {

    public static final String TAG = "BalancesFragment";
    @InjectPresenter
    BalancesPresenter mBalancesPresenter;

    @BindView(R.id.rvBalances)
    RecyclerView rvBalances;

    public static BalancesFragment newInstance(final int usersId) {
        BalancesFragment fragment = new BalancesFragment();

        Bundle args = new Bundle();
        args.putInt("usersId", usersId);
        fragment.setArguments(args);

        return fragment;
    }

    public int getUsersId() {
        return getArguments().getInt("usersId", 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        mBalancesPresenter.setFragmentManager(getActivity().getFragmentManager());
        ButterKnife.bind(rvBalances);

        do {
            try {
                mBalancesPresenter.createListQiwiUsersBalances(getContext(), rvBalances);
            } catch (ResultIsEmptyException e) {
                e.printStackTrace();
                mBalancesPresenter.showDialog(e.getMessage());
            }
        } while (mBalancesPresenter.getExceptions());

        return inflater.inflate(R.layout.fragment_balances, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
