package com.example.samsung.qiwi_users_balance.presentation.presenter.users;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.ControllerDB;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.exceptions.CreateListQiwiUsersException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

import static com.example.samsung.qiwi_users_balance.model.ManagerControllerDB.getControllerDB;

@InjectViewState
public class UsersPresenter extends MvpPresenter<UsersView> {

    private SQLiteDatabase mDb;
    private ControllerDBTask controllerDBTask;

    public UsersPresenter() {

        controllerDBTask = new ControllerDBTask();
        controllerDBTask.execute();
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
                            App.getDequeMsg().pushMsg(
                                    App.getApp().getString(R.string.error_loading_response_in_db)
                                            + e.getMessage());
//                            return null;
                        }
                    }
                }
            } else {
                cursor.close();
                throw new DBCursorIsNullException(
                        App.getApp().getString(R.string.error_when_writing_data_from_the_response_db)
                                + " " + App.getApp().getString(R.string.db_cursor_is_null)
                );
            }
            cursor.close();
        } while (!isRun);

        controllerDB.close();
        App.setQiwiUsersListCreated(isRun);
        return qiwiUsersList;
    }

    public void onClicExcheng() throws Exception {

        ControllerDB controllerDB = getControllerDB(
                App.getPrimDbName()
        );
        //Создаём резервную копию БД
        try {
            String dbName = "copy_db";
            App.createControllerDB(dbName);
            ControllerDB copyControllerDB = getControllerDB(dbName);
            copyControllerDB.openWritableDatabase();
            try {
                controllerDB.copyDB(copyControllerDB);
            } catch (DBIsNotDeletedException e) {
                e.printStackTrace();
                throw new DBIsNotDeletedException(
                        App.getApp().getString(R.string.error_while_backing_up_database)
                                + e.getMessage()
                );
            }
            //Удаляем БД
            if (controllerDB.delete()) {
                //Обновляем содержание БД и список
                try {
                    App.setQiwiUsersList(createListQiwiUsers());
                    if (App.getQiwiUsersListCreated()) {
                        throw new CreateListQiwiUsersException(
                                App.getApp().getString(R.string.error_when_updating_database) + " " +
                                        App.getApp().getString(R.string.create_list_of_qiwis_users_is_not_performed)
                        );
                    }
                } catch (DBCursorIsNullException e) {
                    e.printStackTrace();
                    //Восттанавливаем в случае неудачного обновления
                    try {
                        try {
                            copyControllerDB.copyDB(controllerDB);
                        } catch (DBIsNotDeletedException e1) {
                            e1.printStackTrace();
                            throw new DBIsNotDeletedException(
                                    App.getApp().getString(R.string.error_restoring_db_from_backup)
                                            + e1.getMessage()
                            );
                        }
                        if (!copyControllerDB.delete()) {
                            throw new DBIsNotDeletedException(
                                    copyControllerDB.getDbName() + ": "
                                            + App.getApp().getString(R.string.the_database_is_not_deleted)
                            );
                        }
                    } catch (DBCursorIsNullException e1) {
                        e1.printStackTrace();
                        throw new DBCursorIsNullException(
                                App.getApp().getString(R.string.error_restoring_db_from_backup)
                                        + e1.getMessage()
                        );
                    }
                    throw new DBCursorIsNullException(
                            App.getApp().getString(R.string.error_when_updating_database)
                                    + e.getMessage()
                    );
                }
            } else {
                throw new DBIsNotDeletedException(
                        controllerDB.getDbName() + ": " +
                                App.getApp().getString(R.string.the_database_is_not_deleted)
                );
            }
        } catch (DBCursorIsNullException e) {
            e.printStackTrace();
            App.getDequeMsg().pushMsg(
                    App.getApp().getString(R.string.error_while_backing_up_database)
                            + e.getMessage()
            );
        }
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

    private class ControllerDBTask extends AsyncTask<Void, Void, List<QiwiUsers>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Открываем прогресс-бар загрузки
            getViewState().showProgressBar();
        }

        @Override
        protected List<QiwiUsers> doInBackground(Void... params) {

            Map<String, ControllerDB> allConrollersBDs = App.getManagerControllerDB().getAllControllerDBs();
            ControllerDB controllerDB = allConrollersBDs.get(App.getPrimDbName());
            Response<JsonQiwisUsers> response = null;
            try {
                response = ControllerAPI.getAPI().getUsers().execute();
            } catch (IOException e) {
                e.printStackTrace();
                App.getDequeMsg().pushMsg(e.getMessage());
            }
            try {
                controllerDB.downloadData(response);
            } catch (Exception e) {
                e.printStackTrace();
                App.getDequeMsg().pushMsg(e.getMessage());
            }

            App.setQiwiUsersList(createListQiwiUsers());
            try {
                if (App.getQiwiUsersList() == null) {
                    App.getDequeMsg().pushMsg(
                            App.getApp().getString(R.string.the_dataset_is_null)
                    );
                }
            } catch (DBCursorIsNullException e) {
                e.printStackTrace();
                App.getDequeMsg().pushMsg(e.getMessage());
            }
            return App.getQiwiUsersList();
        }

        @Override
        protected void onPostExecute(List<QiwiUsers> result) {
            super.onPostExecute(result);
            //Закрываем прогрксс-бар загрузки
            getViewState().dismissProgressBar();
            //Показываем сообщения
            getViewState().showMsg();
            App.setQiwiUsersList(result);
        }
    }
}
