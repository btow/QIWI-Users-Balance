package com.example.samsung.qiwi_users_balance.model.exceptions;

import java.io.IOException;

public class DBRenameException extends IllegalStateException {

    public DBRenameException(final String msg) {
        super(msg);
    }
}
