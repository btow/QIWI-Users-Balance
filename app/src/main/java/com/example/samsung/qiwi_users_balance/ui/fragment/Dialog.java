package com.example.samsung.qiwi_users_balance.ui.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;

public class Dialog extends DialogFragment implements DialogInterface.OnClickListener {

    private final String MSG = "msg";

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder adb  = new AlertDialog.Builder(App.getApp().getBaseContext())
                .setTitle(getString(R.string.attention))
                .setMessage(getArguments().getString(MSG))
                .setPositiveButton(R.string.repeat, this)
                .setNegativeButton(R.string.close_application, this);
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        switch (i) {

            case DialogInterface.BUTTON_POSITIVE:
                Toast.makeText(getActivity().getBaseContext(),
                        getString(R.string.to_abort),
                        Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }
    }
}
