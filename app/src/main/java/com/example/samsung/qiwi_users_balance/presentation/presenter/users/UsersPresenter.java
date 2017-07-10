package com.example.samsung.qiwi_users_balance.presentation.presenter.users;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.ControllerDB;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.ManagerControllerDB;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsersBalances;
import com.example.samsung.qiwi_users_balance.model.exceptions.CreateListQiwiUsersException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersFragmentView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

import static com.example.samsung.qiwi_users_balance.model.ManagerControllerDB.getControllerDB;

@InjectViewState
public class UsersPresenter extends MvpPresenter<UsersFragmentView> {

    private SQLiteDatabase mDb;

    public UsersPresenter() {

    }

    public void setDb(SQLiteDatabase db) {
        if (mDb == null) {
            this.mDb = db;
        }
    }

    public String collGetNameDB(SQLiteDatabase origDB) {
        return getNameDB(origDB);
    }

    public List<QiwiUsers> getDataset() {

            return App.getQiwiUsersList();
    }

    public List<QiwiUsers> createListQiwiUsers() throws DBCursorIsNullException {

        boolean isRun = false;
        App.setQiwiUsersListCreated(isRun);

        List<QiwiUsers> qiwiUsersList = new ArrayList<>();
        ControllerDB controllerDB = getControllerDB(App.getPrimDbName()
        );
        //Создаём списк из БД
        int couner = 0, maxTrys = 2;
        do {
            if (!controllerDB.DBisOpen()) {
                controllerDB.openWritableDatabase();
            }
            Cursor cursor = controllerDB.getCursor();

            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    do {
                        qiwiUsersList.add(new QiwiUsers(cursor.getInt(0), cursor.getString(1)));
                    } while (cursor.moveToNext());
                    isRun = true;
                } else {
                    if (cursor.getCount() == 0) {
                        try {
                            controllerDB.downloadData(ControllerAPI.getAPI().getUsers().execute());
                        } catch (Exception e) {
                            e.printStackTrace();
                            cursor.close();
                            String msg = App.getApp().getString(R.string.error_loading_response_in_db)
                                    + e.getMessage() + ": more 100 iterations";
                            throw new CreateListQiwiUsersException(msg);
                        }
                    }
                }
            } else {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                throw new DBCursorIsNullException(
                        App.getApp().getString(R.string.error_when_writing_data_from_the_response_db)
                                + " " + App.getApp().getString(R.string.db_cursor_is_null)
                );
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (couner > maxTrys) {
                String msg = App.getApp().getString(R.string.error_loading_response_in_db)
                        + ": run more " + maxTrys + " trys";
                App.getDequeMsg().pushMsg(msg);
                throw new CreateListQiwiUsersException(msg);
            }
            couner++;

        } while (!isRun);

        controllerDB.close();
        App.setQiwiUsersListCreated(isRun);

        return qiwiUsersList;
    }

    public void onClicExcheng() throws Exception {

        Bundle args = new Bundle();
        args.putInt(App.CALL_FROM, App.CALL_FROM_PRIM_FRAGMENT);
        getViewState().showProgressBar(args);
    }

    private String getNameDB(SQLiteDatabase origDB) {

        char[] charPathDB = origDB.getPath().toCharArray();
        String nameDB = "";
        for (int index = charPathDB.length - 1; index > -1; index--) {
            if (charPathDB[index] == '/') {
                break;
            } else {
                nameDB = String.valueOf(charPathDB[index]) + nameDB;
            }
        }

        return nameDB;
    }
}
