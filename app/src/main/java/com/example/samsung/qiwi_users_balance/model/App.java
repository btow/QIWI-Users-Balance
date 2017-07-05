package com.example.samsung.qiwi_users_balance.model;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private static App mApp;
    private static ManagerControllerDB mManagerControllerDB;
    private static String mPrimDbName;
    private static ManagerMessagersDialogs mDequeMsg;
    private static List<QiwiUsers> mQiwiUsersList;
    private static boolean mQiwiUsersListCreated;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        ControllerDB controllerDB = new ControllerDB(getBaseContext());
        controllerDB.openWritableDatabase();
        mManagerControllerDB.putControllerDB(controllerDB);
        mPrimDbName = controllerDB.getDbName();
        mQiwiUsersList = new ArrayList<>();
        mQiwiUsersListCreated = false;
    }

    public static void setQiwiUsersList(List<QiwiUsers> qiwiUsersList) {
        mQiwiUsersList = qiwiUsersList;
    }

    public static void setQiwiUsersListCreated(final boolean state) {
        mQiwiUsersListCreated = state;
    }

    public static void createControllerDB(final String dbName) {
        ControllerDB controllerDB = new ControllerDB(getApp().getBaseContext(), dbName);
        controllerDB.openWritableDatabase();
        mManagerControllerDB.putControllerDB(controllerDB);
    }

    public static App getApp() {
        return mApp;
    }

    public static ManagerControllerDB getManagerControllerDB() {
        return mManagerControllerDB;
    }

    public static String getPrimDbName() {
        return mPrimDbName;
    }

    public static ManagerMessagersDialogs getDequeMsg() {
        return mDequeMsg;
    }

    public static List<QiwiUsers> getQiwiUsersList() {
        return mQiwiUsersList;
    }

    public static boolean getQiwiUsersListCreated() {
        return mQiwiUsersListCreated;
    }
}
