package com.example.samsung.qiwi_users_balance.ui.activity.balances;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.ui.activity.main.MainActivity;
import com.example.samsung.qiwi_users_balance.ui.fragment.balances.BalancesFragment;
import com.example.samsung.qiwi_users_balance.ui.fragment.users.UsersFragment;

public class BalancesActivity extends AppCompatActivity {


    public static Intent getIntent(final Context context, final Bundle arg) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(arg);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().densityDpi >= 600) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        BalancesFragment balancesFragment = new BalancesFragment();
        balancesFragment.setArguments(getIntent().getExtras().getBundle(App.USER_ID));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.flPrimFragment, balancesFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.flPrimFragment, balancesFragment).commit();
        }
    }
}
