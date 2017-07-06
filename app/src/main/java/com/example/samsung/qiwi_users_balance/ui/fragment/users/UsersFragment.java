package com.example.samsung.qiwi_users_balance.ui.fragment.users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;
import com.example.samsung.qiwi_users_balance.ui.activity.balances.BalancesActivity;
import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter.OnItemClickListener;

public class UsersFragment extends MvpAppCompatFragment implements UsersView {

    public static final String TAG = "UsersFragment";

    @InjectPresenter
    UsersPresenter mUsersPresenter;

    @BindView(R.id.btnExcheng)
    Button btnExcheng;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @BindView(R.id.llMsg)
    LinearLayout llMsg;
    @BindView(R.id.tvMsg)
    TextView tvMsg;
    @BindView(R.id.btnRepeat)
    Button btnRepeat;

    private boolean mDualPlane;
    private int mUserID = 0;

    private OnClickListener mOnClickListener = new OnClickListener() {
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
            showMsg();
            rvUsers.getAdapter().notifyDataSetChanged();
            //Закрываем прогрксс-бар загрузки
            dismissProgressBar();
        }
    };

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

        View balancesFragment = getActivity().findViewById(R.id.flUsersBalancesList);
        mDualPlane = balancesFragment != null && balancesFragment.getVisibility() == VISIBLE;

        if (savedInstanceState != null) {
            mUserID = savedInstanceState.getInt(App.getUid(), 0);
        }

        ButterKnife.bind(this, view);

        while (!App.getQiwiUsersListCreated()) {
            if (pbLoading.getVisibility() != ProgressBar.VISIBLE) {
                showProgressBar();
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dismissProgressBar();

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
        btnRepeat.setOnClickListener(mOnClickListener);

        if (mDualPlane) {
            showUsersBalances(mUserID);
        }
    }

    @Override
    public void showProgressBar() {

        pbLoading.setVisibility(ProgressBar.VISIBLE);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismissProgressBar() {
        pbLoading.setVisibility(ProgressBar.GONE);
        rvUsers.setVisibility(RecyclerView.VISIBLE);
    }

    @Override
    public void showMsg() {

        while (!App.getDequeMsg().isEmpty()) {
            pbLoading.setVisibility(ProgressBar.GONE);
            rvUsers.setVisibility(RecyclerView.GONE);
            llMsg.setVisibility(LinearLayout.VISIBLE);
            tvMsg.setVisibility(TextView.VISIBLE);
            tvMsg.setText(App.getDequeMsg().outMsg());
            btnRepeat.setVisibility(Button.VISIBLE);
        }
        rvUsers.setVisibility(RecyclerView.VISIBLE);
        llMsg.setVisibility(LinearLayout.GONE);
        tvMsg.setVisibility(TextView.GONE);
        btnRepeat.setVisibility(Button.GONE);

    }

    @Override
    public void showUsersBalances(int userId) {

        mUserID = userId;

        if (mDualPlane) {
            rvUsers.setId(userId);

            BalancesFragment balancesFragment
                    = (BalancesFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.flUsersBalancesList);

            if (balancesFragment == null || balancesFragment.getUsersId() != userId) {

                balancesFragment = BalancesFragment.newInstance(userId);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flUsersBalancesList, balancesFragment).commit();
            }
        } else {
            startActivity(new Intent(getActivity().getBaseContext(), BalancesActivity.class).putExtra(App.getUid(), userId));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(App.getUid(), mUserID);
    }
}
