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

import static com.example.samsung.qiwi_users_balance.model.ManagerControllerDB.getTmpControllerDB;

@InjectViewState
public class UsersPresenter extends MvpPresenter<UsersView> {

    private SQLiteDatabase mDb;
    private ControllerDB mControllerDB;
    private String mMsg;
    private List<QiwiUsers> mDataset;
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

    public void setMsg(final String msg) {
        this.mMsg = msg;
    }

    public String collGetNameDB(SQLiteDatabase origDB) {
        return getNameDB(origDB);
    }

    public List<QiwiUsers> getDataset() {
        return mDataset;
    }

    public boolean createListQiwiUsers() throws DBCursorIsNullException {

        boolean isRun = false;

        if (mDataset == null) {
            mDataset = new ArrayList<>();
        } else {
            mDataset.clear();
        }
        //Создаём списк из БД
        do {
            if (!mControllerDB.DBisOpen()) {
                mControllerDB.openWritableDatabase();
            }
            Cursor cursor = mControllerDB.getCursor();

            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    do {
                        mDataset.add(new QiwiUsers(cursor.getInt(0), cursor.getString(1)));
                    } while (cursor.moveToNext());
                    isRun = true;
                } else {
                    if (cursor.getCount() == 0) {
                        try {
                            mControllerDB.downloadData(ControllerAPI.getAPI().getUsers().execute());
                        } catch (Exception e) {
                            e.printStackTrace();
                            getViewState().setMsg(R.string.error_loading_response_in_db);
                            mMsg += e.getMessage();
                            cursor.close();
                            getViewState().showDialog(e.getMessage());
                        }
                    }
                }
            } else {
                cursor.close();
                getViewState().setMsg(R.string.db_cursor_is_null);
                String tmpMsg = String.copyValueOf(mMsg.toCharArray());
                getViewState().setMsg(R.string.error_when_writing_data_from_the_response_db);
                mMsg = mMsg + " " + tmpMsg;
                throw new DBCursorIsNullException(mMsg);
            }
            cursor.close();
        } while (!isRun);

        mControllerDB.close();
        return isRun;
    }

    public void onClicExcheng() throws Exception {
        //Создаём резервную копию БД
        try {
            String dbName = "copy_db";
            App.createControllerDB(dbName);
            ControllerDB copyControllerDB = getTmpControllerDB(dbName);
            copyControllerDB.openWritableDatabase();
            try {
                mControllerDB.copyDB(copyControllerDB);
            } catch (DBIsNotDeletedException e) {
                e.printStackTrace();
                getViewState().setMsg(R.string.error_while_backing_up_database);
                mMsg += e.getMessage();
                throw new DBIsNotDeletedException(mMsg);
            }
            //Удаляем БД
            if (mControllerDB.delete()) {
                //Обновляем содержание БД и список
                try {
                    if (!createListQiwiUsers()) {
                        getViewState().setMsg(R.string.create_list_of_qiwis_users_is_not_performed);
                        String tmpMsg = String.copyValueOf(mMsg.toCharArray());
                        getViewState().setMsg(R.string.error_when_updating_database);
                        mMsg = mMsg + " " + tmpMsg;
                        throw new CreateListQiwiUsersException(mMsg);
                    }
                } catch (DBCursorIsNullException e) {
                    e.printStackTrace();
                    getViewState().setMsg(R.string.error_when_updating_database);
                    mMsg += e.getMessage();
                    //Восттанавливаем в случае неудачного обновления
                    try {
                        try {
                            copyControllerDB.copyDB(mControllerDB);
                        } catch (DBIsNotDeletedException e1) {
                            e1.printStackTrace();
                            getViewState().setMsg(R.string.error_restoring_db_from_backup);
                            mMsg += e1.getMessage();
                            throw new DBIsNotDeletedException(mMsg);
                        }
                        if (!copyControllerDB.delete()) {
                            getViewState().setMsg(R.string.the_database_is_not_deleted);
                            mMsg = copyControllerDB.getDbName() + ": " + mMsg;
                            throw new DBIsNotDeletedException(mMsg);
                        }
                    } catch (DBCursorIsNullException e1) {
                        e1.printStackTrace();
                        getViewState().setMsg(R.string.error_restoring_db_from_backup);
                        mMsg += e1.getMessage();
                    }
                    throw new DBCursorIsNullException(mMsg);
                }
            } else {
                getViewState().setMsg(R.string.the_database_is_not_deleted);
                mMsg = mControllerDB.getDbName() + ": " + mMsg;
                throw new DBIsNotDeletedException(mMsg);
            }
        } catch (DBCursorIsNullException e) {
            e.printStackTrace();
            getViewState().setMsg(R.string.error_while_backing_up_database);
            mMsg += e.getMessage();
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

    private class ControllerDBTask extends AsyncTask<Void, Void, ControllerDB> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Открываем прогресс-бар загрузки
            getViewState().showProgressBar();
        }

        @Override
        protected ControllerDB doInBackground(Void... params) {

            Map<String, ControllerDB> allConrollersBDs = App.getManagerControllerDB().getAllControllerDBs();
            mControllerDB = allConrollersBDs.get(App.getPrimDbName());
            Cursor cursor = mControllerDB.getCursor();
            Response<JsonQiwisUsers> response = null;
            try {
                response = ControllerAPI.getAPI().getUsers().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (cursor.getCount() != response.body().getUsers().size()) {
                try {
                    mControllerDB.downloadData(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            return mControllerDB;
        }

        @Override
        protected void onPostExecute(ControllerDB result) {
            super.onPostExecute(result);

            try {
                if (!createListQiwiUsers()) getViewState().showDialog(mMsg);
            } catch (DBCursorIsNullException e) {
                e.printStackTrace();
                getViewState().showDialog(e.getMessage());
            }
            //Закрываем прогрксс-бар загрузки
            getViewState().dismissProgressBar();
            mDb = result.getDb();
        }
    }
}
