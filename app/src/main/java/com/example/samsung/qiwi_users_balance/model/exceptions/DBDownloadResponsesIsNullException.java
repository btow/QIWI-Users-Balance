package com.example.samsung.qiwi_users_balance.model.exceptions;

import java.io.IOException;

public class DBDownloadResponsesIsNullException extends IllegalStateException {

    public DBDownloadResponsesIsNullException(final String msg) {
        super(msg);
    }
}
