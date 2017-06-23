package com.example.samsung.qiwi_users_balance.model.exceptions;

public class BDCursorIsEmptyException extends Exception {

    public BDCursorIsEmptyException() {
        super("DB cursor is empty");
    }
}
