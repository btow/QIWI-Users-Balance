package com.example.samsung.qiwi_users_balance.ui.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.samsung.qiwi_users_balance.R;

public class Dialog extends DialogFragment implements DialogInterface.OnClickListener {

    private final String MSG = "msg";

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder adb  = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.attention))
                .setPositiveButton(getString(R.string.repeat), this)
                .setMessage(getArguments().getString(MSG));
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        return;
    }
}
