package com.example.samsung.qiwi_users_balance.model;

import android.app.Application;

public class App extends Application {

    private static App app;
    private static ManagerControllerDB managerControllerDB;
    private static String primDbName;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        ControllerDB controllerDB = new ControllerDB(getBaseContext());
        controllerDB.openWritableDatabase();
        managerControllerDB.putControllerDB(controllerDB);
        primDbName = controllerDB.getDbName();
    }

    public static void createControllerDB(final String dbName) {
        ControllerDB controllerDB = new ControllerDB(getApp().getBaseContext(), dbName);
        controllerDB.openWritableDatabase();
        managerControllerDB.putControllerDB(controllerDB);
    }

    public static App getApp() {
        return app;
    }

    public static ManagerControllerDB getManagerControllerDB() {
        return managerControllerDB;
    }

    public static String getPrimDbName() {
        return primDbName;
    }
}
