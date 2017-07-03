package com.example.samsung.qiwi_users_balance.model.exceptions;

import android.content.Context;

import com.example.samsung.qiwi_users_balance.R;

import java.io.IOException;

public class DBCursorIsNullException extends NullPointerException {

    public DBCursorIsNullException(final String msg) {
        super(msg);
    }
}
