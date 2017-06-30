package com.example.samsung.qiwi_users_balance.presentation.presenter.users;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.User;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;
import com.example.samsung.qiwi_users_balance.ui.fragment.Dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class UsersPresenter extends MvpPresenter<UsersView> {

    private final String DB_NAME = "qiwisUsers";
    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name",
            sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
                    + TABLE_QIWI_USERS_ID + " integer primary key, "
                    + TABLE_QIWI_USERS_NAME + " text)";

    private Context mCxt;
    private String mMsg;
    private DBHelper dbHelper;
    private SQLiteDatabase mDb;
    private List<QiwiUsers> mDataset;
    private FragmentManager mFrm;
    private RecyclerView mRvUsers;
    private boolean isExceptions = false, isWarning = false;

    public void setCxt(Context cxt) {
        this.mCxt = cxt;
        this.mMsg = cxt.getString(R.string.response_is_null);
    }

    public void setDb(SQLiteDatabase db) {
        this.mDb = db;
    }

    public void setMsg(final String msg) {
        this.mMsg = msg;
    }

    public void setFragmentManager(final FragmentManager frm) {
        this.mFrm = frm;
    }

    public void setRvUsers(RecyclerView rvUsers) {
        this.mRvUsers = rvUsers;
    }

    public String collGetNameDB(SQLiteDatabase origDB) {
        return  getNameDB(origDB);
    }

    public List<QiwiUsers> getDataset() {
        return mDataset;
    }

    public boolean getExceptions() {
        return isExceptions;
    }

    public void collDownloadData(Response<JsonQiwisUsers> listResponse) {
        downloadData(listResponse);
    }

    public Callback<JsonQiwisUsers> collListCallback() {
        return listCallback();
    }

    public void collCopyDB(SQLiteDatabase origDB, SQLiteDatabase copyDB)
            throws DBCursorIsNullException {
        try {
            copyDB(origDB, copyDB);
        } catch (DBIsNotDeletedException e) {
            e.printStackTrace();
        }
    }

    public void showDialog() {

        DialogFragment mDialogFragment = new Dialog();
        Bundle args = new Bundle();
        args.putString("msg", mMsg);
        mDialogFragment.setArguments(args);
        mDialogFragment.show(mFrm, "mDialogFragment");
    }

    public boolean createListQiwiUsers() throws DBCursorIsNullException {

        int DB_VERSION = 1;

        if (mDataset == null) {
            mDataset = new ArrayList<>();
        } else {
            mDataset.clear();
        }
        //Создаём списк из БД и если БД - пустая, то сначала создаём её
        if (dbHelper == null) {
            dbHelper = new DBHelper(mCxt, DB_NAME, DB_VERSION);
        }
        if (mDb == null) {
            mDb = dbHelper.getWritableDatabase();
        }

        @SuppressWarnings("UnusedAssignment") Cursor cursor = null;

        do {
            cursor = mDb.query(TABLE_QIWI_USERS, null, null, null, null, null, null);

            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    do {
                        mDataset.add(new QiwiUsers(cursor.getInt(0), cursor.getString(1)));
                    } while (cursor.moveToNext());
                    isExceptions = false;
                } else {
//            ControllerAPI.getAPI().getUsers().enqueue(listCallback(mDb));
                    try {
                        downloadData(ControllerAPI.getAPI().getUsers().execute());
                    } catch (IOException e) {
                        e.printStackTrace();
                        isExceptions = true;
                        mMsg = mCxt.getString(R.string.error_loading_response_in_db) + e.getMessage();
                    }
                    cursor.close();
                    isExceptions = true;
                }
            } else {
                isExceptions = true;
                mMsg = mCxt.getString(R.string.error_when_writing_data_from_the_response_db)
                        + mCxt.getString(R.string.db_cursor_is_null);
                throw new DBCursorIsNullException(mCxt);
            }
            cursor.close();
        } while (isExceptions);

        mDb.close();
        return isExceptions;
    }

    public void onClicExcheng() throws DBIsNotDeletedException, DBCursorIsNullException{
        //Создаём резервную копию БД
        try {
            SQLiteDatabase copyDb = mCxt.openOrCreateDatabase("copy_" + DB_NAME, 0, null);
            try {
                copyDB(mDb, copyDb);
            } catch (DBIsNotDeletedException e) {
                e.printStackTrace();
                mMsg = mCxt.getString(R.string.error_while_backing_up_database) + e.getMessage();
                throw new DBIsNotDeletedException(mMsg);
            }
            //Удаляем БД
            if (closeAndDeleteDB(mDb)) {
                //Обновляем содержание БД и список
                try {
                    createListQiwiUsers();
                    isExceptions = false;
                } catch (DBCursorIsNullException e) {
                    e.printStackTrace();
                    isExceptions = true;
                    mMsg = mCxt.getString(R.string.error_when_updating_database) + e.getMessage();
                    //Восттанавливаем в случае неудачного обновления
                    try {
                        try {
                            copyDB(copyDb, mDb);
                        } catch (DBIsNotDeletedException e1) {
                            e1.printStackTrace();
                            isWarning = true;
                            mMsg = mCxt.getString(R.string.error_restoring_db_from_backup) + e1.getMessage();
                            throw new  DBIsNotDeletedException(mMsg);
                        }
                        if (!closeAndDeleteDB(copyDb)) {
                            isWarning = true;
                            mMsg = "copy_" + DB_NAME + ": " + mCxt.getString(R.string.the_database_is_not_deleted);
                            throw new DBIsNotDeletedException(mMsg);
                        }
                    } catch (DBCursorIsNullException e1) {
                        e1.printStackTrace();
                        isExceptions = true;
                        mMsg = mCxt.getString(R.string.error_restoring_db_from_backup) + e1.getMessage();
                    }
                    isWarning = true;
                    throw new DBCursorIsNullException(mCxt);
                }
            } else {
                isWarning = true;
                mMsg = DB_NAME + ": " + mCxt.getString(R.string.the_database_is_not_deleted);
                throw new DBIsNotDeletedException(mMsg);
            }
        } catch (DBCursorIsNullException e) {
            e.printStackTrace();
            isExceptions = true;
            mMsg = mCxt.getString(R.string.error_while_backing_up_database) + e.getMessage();
        }
    }

    private void copyDB(SQLiteDatabase origDB, SQLiteDatabase copyDB)
            throws DBCursorIsNullException, DBIsNotDeletedException {

        String orig_db_name = getNameDB(origDB), copy_db_name = getNameDB(copyDB);
        origDB = mCxt.openOrCreateDatabase(orig_db_name, 0, null);

        if (closeAndDeleteDB(copyDB)) {
            copyDB = mCxt.openOrCreateDatabase(copy_db_name, 0, null);
            copyDB.execSQL(sqlCommand);
            copyDB.setVersion(origDB.getVersion());
        } else {
            mMsg = copy_db_name + ": " + mCxt.getString(R.string.the_database_is_not_deleted);
            throw new DBIsNotDeletedException(mMsg);
        }

        Cursor cursor = origDB.query(TABLE_QIWI_USERS, null, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ContentValues cv = new ContentValues();
                try {
                    do {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, cursor.getInt(0));
                        cv.put(TABLE_QIWI_USERS_NAME, cursor.getString(1));
                        copyDB.insert(TABLE_QIWI_USERS, null, cv);
                    } while (cursor.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                    isExceptions = true;
                    mMsg = mCxt.getString(R.string.error_when_copying_data) + e.getMessage();
                } finally {
                    copyDB.close();
                    cursor.close();
                }
                isExceptions = false;
            } else {
                isExceptions = true;
                mMsg = mCxt.getString(R.string.query_result_is_empty);
                cursor.close();
            }
        } else {
            isExceptions = true;
            mMsg = mCxt.getString(R.string.error_when_copying_data)
                    + mCxt.getString(R.string.db_cursor_is_null);
            cursor.close();
            throw new DBCursorIsNullException(mCxt);
        }
    }

    private boolean closeAndDeleteDB(SQLiteDatabase db) {

        String db_name = getNameDB(db);
        int attemptCounter = 0;
        do {
            if (attemptCounter > 0) try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (attemptCounter > 10) {
                mMsg = db_name + ": " + mCxt.getString(R.string.the_database_is_not_deleted);
                return false;
            }
            db.close();
            attemptCounter++;
        } while (!mCxt.deleteDatabase(db_name));
        return true;
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

    private void downloadData(Response<JsonQiwisUsers> listResponse) {

        if (listResponse != null) {
            JsonQiwisUsers actJsonQiwisUsersList = listResponse.body();

            if (actJsonQiwisUsersList.getResultCode() == 0) {

                if (!mDb.isOpen()) mDb = mCxt.openOrCreateDatabase(DB_NAME, 0, null);
                ContentValues cv = new ContentValues();

                try {
                    for (User user :
                            actJsonQiwisUsersList.getUsers()) {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, user.getId());
                        cv.put(TABLE_QIWI_USERS_NAME, user.getName());
                        mDb.insert(TABLE_QIWI_USERS, null, cv);
                    }
                    isExceptions = false;
                } catch (Exception e) {
                    isExceptions = true;
                    mMsg = mCxt.getString(R.string.error_when_writing_data_from_the_response_db);
                }
            } else {
                isExceptions = true;
                mMsg = mCxt.getString(R.string.result_code) + actJsonQiwisUsersList.getResultCode()
                        + mCxt.getString(R.string.message) + actJsonQiwisUsersList.getMessage();
            }
        } else {
            isExceptions = true;
            mMsg = mCxt.getString(R.string.the_response_is_null);
        }
    }

    private void responseHandler(@Nullable Response<JsonQiwisUsers> response) {
        if (!mDb.isOpen()) mDb = mCxt.openOrCreateDatabase(DB_NAME, 0, null);

        if (response == null) {
            isExceptions = true;
        } else if (response.body() == null) {
            isExceptions = true;
            mMsg = mCxt.getString(R.string.responses_body_is_null);
        } else {
            isExceptions = false;
            //Обработка
            JsonQiwisUsers jsonQiwisUsers = response.body();
            if (jsonQiwisUsers.getResultCode() == 0) {
                try {
                    ContentValues cv = new ContentValues();

                    for (User qiwisUser :
                            jsonQiwisUsers.getUsers()) {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, qiwisUser.getId());
                        cv.put(TABLE_QIWI_USERS_NAME, qiwisUser.getName());
                        mDb.insert(TABLE_QIWI_USERS, null, cv);
                    }
                    isExceptions = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    isExceptions = true;
                    //Создаём сообщение об ошибке
                    mMsg = e.getMessage();
                }
            } else {
                isExceptions = true;
                //Создаём сообщение об ошибке
                mMsg = jsonQiwisUsers.getMessage();
            }
        }
    }

    private Callback<JsonQiwisUsers> listCallback() {

        return new Callback<JsonQiwisUsers>() {

            @Override
            public void onResponse(@Nullable Call<JsonQiwisUsers> call,
                                   @Nullable Response<JsonQiwisUsers> response) {

                responseHandler(response);
            }

            @Override
            public void onFailure(@Nullable Call<JsonQiwisUsers> call,
                                  @Nullable Throwable t) {
                isExceptions = true;

                if (t != null) mMsg = t.getMessage();
            }
        };
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context,
                 final String DB_NAME,
                 final int DB_VERSION) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(sqlCommand); //создание БД
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
