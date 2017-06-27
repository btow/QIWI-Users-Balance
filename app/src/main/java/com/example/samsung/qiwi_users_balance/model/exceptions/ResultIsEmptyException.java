package com.example.samsung.qiwi_users_balance.model.exceptions;

import android.content.Context;

import com.example.samsung.qiwi_users_balance.R;

public class ResultIsEmptyException extends Exception {

    public ResultIsEmptyException(final Context cxt) {
        super(cxt.getString(R.string.query_result_is_empty));
    }
}
