package com.example.samsung.qiwi_users_balance.model.exceptions;

import java.io.IOException;

public class DBIsNotDeletedException extends RuntimeException {

    public DBIsNotDeletedException(final String msg) {
        super(msg);
    }
}
