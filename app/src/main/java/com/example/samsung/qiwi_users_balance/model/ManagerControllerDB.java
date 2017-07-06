package com.example.samsung.qiwi_users_balance.model;

import java.util.HashMap;
import java.util.Map;

public class ManagerControllerDB {

    private static Map<String, ControllerDB> tmpControllerDBs = new HashMap<>();

    public static void putControllerDB(ControllerDB controllerDB) {
        tmpControllerDBs.put(controllerDB.getDbName(), controllerDB);
    }

    public static Map<String, ControllerDB> getAllControllerDBs() {
        return tmpControllerDBs;
    }

    public static ControllerDB getControllerDB(final int index) throws Exception {

        int cur = 0, maxIndex = tmpControllerDBs.size() - 1;
        String msg = "getControllerDB(): index = " + index + ": The index exceeds the array size";
        if (index > maxIndex) {
            throw new Exception(msg);
        }
        ControllerDB returned = null;
        for (Map.Entry<String, ControllerDB> curControllerDB :
                tmpControllerDBs.entrySet()) {
            if (cur == index) {
                returned = curControllerDB.getValue();
                break;
            }
            cur++;
            if (cur > maxIndex) {
                msg = "getControllerDB(): index = " + index + ": Going beyond the bounds of the array";
                throw new Exception(msg);
            }
        }
        return returned;
    }

    public static ControllerDB getControllerDB(final String dbName) {
        return tmpControllerDBs.get(dbName);
    }
}