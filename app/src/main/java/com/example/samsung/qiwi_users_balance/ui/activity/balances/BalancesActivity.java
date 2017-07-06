package com.example.samsung.qiwi_users_balance.ui.activity.balances;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;

public class BalancesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().densityDpi >= 600) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            BalancesFragment balancesFragment = new BalancesFragment();
            balancesFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, balancesFragment).commit();
        }
    }
}
