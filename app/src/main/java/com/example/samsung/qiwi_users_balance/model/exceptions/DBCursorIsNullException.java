package com.example.samsung.qiwi_users_balance.model.exceptions;

import android.content.Context;

import com.example.samsung.qiwi_users_balance.R;

public class DBCursorIsNullException extends Exception {

    public DBCursorIsNullException(final Context cxt) {
        super(cxt.getString(R.string.db_cursor_is_null));
    }
}
