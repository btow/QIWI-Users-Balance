package com.example.samsung.qiwi_users_balance.model;

import android.app.Application;
import android.os.Bundle;

import com.example.samsung.qiwi_users_balance.R;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static final String
            USER_ID = "user_id",
            FRAG_NUMBER = "frag_number",
            SERV_VERSION = "serv_version",
            CALL_FROM = "call_from";

    public static final int
            CALL_FROM_MAIN_ACTIVITY = 0,
            CALL_FROM_BALANCES_ACTIVITY = 1,
            CALL_FROM_PRIM_FRAGMENT = 2,
            CALL_FROM_SECOND_FRAGMENT = 3,
            LOAD_FRAG = R.layout.fragment_loading,
            MESS_FRAG = R.layout.fragment_message,
            USERS_FRAMENT = 6,
            BALANCES_FRAGMENT = 7,
            LOADING_FRAGMENT = 8,
            MESSAGE_FRAGMENT = 9;

    private static App mApp;
    private static ManagerControllerDB mManagerControllerDB;
    private static String mPrimDbName;
    private static ManagerMessagersDialogs mDequeMsg;
    private static List<QiwiUsers> mQiwiUsersList;
    private static boolean mQiwiUsersListCreated;
    private static boolean mUsedTwoFragmentLayout;
    private static int mUsedPrimFragmentsVersion;
    private static int mUsedSecondFragmentsVersion;
    private static Bundle mArguments;

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
        mUsedTwoFragmentLayout = false;
        mUsedPrimFragmentsVersion = LOADING_FRAGMENT;
        mUsedSecondFragmentsVersion = LOADING_FRAGMENT;
    }

    public static void setQiwiUsersList(List<QiwiUsers> qiwiUsersList) {
        mQiwiUsersList = qiwiUsersList;
    }

    public static void setQiwiUsersListCreated(final boolean state) {
        mQiwiUsersListCreated = state;
    }

    public static void setUsedTwoFragmentLayout(final boolean usedTwoFragmentLayout) {
        mUsedTwoFragmentLayout = usedTwoFragmentLayout;
    }

    public static void setUsedPrimFragmentsVersion(final int fragmentsVersion) {
        mUsedPrimFragmentsVersion = fragmentsVersion;
    }

    public static void setUsedSecondFragmentsVersion(final int fragmentsVersion) {
        mUsedSecondFragmentsVersion = fragmentsVersion;
    }

    public static void setArguments(Bundle arguments) {
        mArguments = arguments;
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

    public static boolean getUsedTwoFragmentLayout() {
        return mUsedTwoFragmentLayout;
    }

    public static int getUsedPrimFragmentsVersion() {
        return mUsedPrimFragmentsVersion;
    }

    public static int getUsedSecondFragmentsVersion() {
        return mUsedSecondFragmentsVersion;
    }

    public static Bundle getArguments() {
        return mArguments;
    }
}
