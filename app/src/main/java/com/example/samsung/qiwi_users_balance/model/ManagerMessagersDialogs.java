package com.example.samsung.qiwi_users_balance.model;

import java.util.ArrayList;

public class ManagerMessagersDialogs {

    private static ArrayList<String> dequeMsg = new ArrayList<>();

    public static void pushMsg(final String msg) {

        dequeMsg.add(msg);
    }

    public static String outMsg() {

        if (dequeMsg.isEmpty()) return null;
        int index = dequeMsg.size() - 1;
        String msg = dequeMsg.get(index);
        dequeMsg.remove(index);
        return msg;
    }

    public static boolean isEmpty() {
        return dequeMsg.isEmpty();
    }
}
