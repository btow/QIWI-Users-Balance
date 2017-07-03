package com.example.samsung.qiwi_users_balance.model.exceptions;

import java.io.IOException;

public class DBDownloadResponsesResultCodeException extends IllegalStateException {

    public DBDownloadResponsesResultCodeException(final String msg) {
        super(msg);
    }
}
