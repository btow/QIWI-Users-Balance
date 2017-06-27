package com.example.samsung.qiwi_users_balance.presentation.presenter.users;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.QiwisUsersAPI;
import com.example.samsung.qiwi_users_balance.model.User;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsEmptyException;
import com.example.samsung.qiwi_users_balance.presentation.ControllerAPI;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;
import com.example.samsung.qiwi_users_balance.ui.fragment.Dialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class UsersPresenter extends MvpPresenter<UsersView> {

    private final String DB_NAME = "qiwisUswrs";
    private final int DB_VERSION = 1;
    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name";
    private final String MSG = "msg";
    public List<QiwiUsers> mDataset;
    private SQLiteDatabase db;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DialogFragment mDialogFragment;
    private FragmentManager mFrm;
    private boolean isExceptions = false;

    public void setFragmentManager(final FragmentManager frm) {
        this.mFrm = frm;
    }

    public boolean getExceptions() {
        return isExceptions;
    }

    public void showDialog(final String msg) {
        mDialogFragment = new Dialog();
        Bundle args = new Bundle();
        args.putString(MSG, msg);
        mDialogFragment.setArguments(args);
        mDialogFragment.show(mFrm, "mDialogFragment");
        mDialogFragment.dismiss();
        isExceptions = true;
    }

    public void createListQiwiUsers(final Context cxt, RecyclerView rvUsers)
            throws DBCursorIsEmptyException {

        if (mDataset == null) {
            mDataset = new ArrayList<QiwiUsers>();
        }
        //Создаём списк из БД и если БД - пустая, то создаём её
        db = new DBHelper(cxt, DB_NAME, DB_VERSION).getWritableDatabase();

        Cursor cursor = db.query(TABLE_QIWI_USERS, null, null, null, null, null, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    mDataset.add(new QiwiUsers(cursor.getInt(0), cursor.getString(1)));
                } while (cursor.moveToNext());
            }
            isExceptions = false;
        } else {
            db.close();
            throw new DBCursorIsEmptyException(cxt);
        }
        db.close();

        mLayoutManager = new LinearLayoutManager(cxt);
        rvUsers.setLayoutManager(mLayoutManager);
        mAdapter = new ListQiwiUsersAdapter(mDataset);
        rvUsers.setAdapter(mAdapter);
        rvUsers.setHasFixedSize(true); //Фиксируем размер списка
    }

    public void onClicExcheng(final Context cxt, RecyclerView rvUsers) {
        //Создаём резервную копию БД
        try {
            copyDB(cxt, "copy_" + DB_NAME, DB_NAME, DB_VERSION);
            isExceptions = false;
        } catch (DBCursorIsEmptyException e) {
            e.printStackTrace();
            showDialog(e.getMessage());
        }
        //Удаляем БД
        cxt.deleteDatabase(DB_NAME);
        //Обновляем содержание БД и список
        try {
            createListQiwiUsers(cxt, rvUsers);
            isExceptions = false;
        } catch (DBCursorIsEmptyException e) {
            e.printStackTrace();
            showDialog(e.getMessage());
            try {
                copyDB(cxt, DB_NAME, "copy_" + DB_NAME, DB_VERSION);
            } catch (DBCursorIsEmptyException e1) {
                e1.printStackTrace();
                showDialog(e.getMessage());
            }
        }
    }

    private void copyDB(Context cxt, String copy_db_name,
                        final String db_name, final int db_version)
    throws DBCursorIsEmptyException {
        SQLiteDatabase cdb = cxt.openOrCreateDatabase(copy_db_name, db_version, null);
        String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
                + TABLE_QIWI_USERS_ID + " integer primary key, "
                + TABLE_QIWI_USERS_NAME + " text)";
        cdb.execSQL(sqlCommand);
        Cursor cursor = db.query(TABLE_QIWI_USERS, null, null, null, null, null, null);
        final ContentValues cv = new ContentValues();
        cdb.beginTransaction();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID,cursor.getInt(0));
                        cv.put(TABLE_QIWI_USERS_NAME,cursor.getString(1));
                        cdb.insert(TABLE_QIWI_USERS, null, cv);
                    } while (cursor.moveToNext());
                }
                isExceptions = false;
            } else {
                isExceptions = true;
                throw new DBCursorIsEmptyException(cxt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showDialog(e.getMessage());
        } finally {
            cdb.endTransaction();
        }
    }

    class DBHelper extends SQLiteOpenHelper {

        private String DB_NAME;
        private int DB_VERSION;

        public DBHelper(final Context context,
                        final String DB_NAME,
                        final int DB_VERSION) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {

            final ContentValues cv = new ContentValues();
            String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
                    + TABLE_QIWI_USERS_ID + " integer primary key, "
                    + TABLE_QIWI_USERS_NAME + " text)";
            db.execSQL(sqlCommand); //создание БД

            QiwisUsersAPI qiwisUsersAPI = ControllerAPI.getAPI();
            //Открываем анимацию загрузки

            qiwisUsersAPI.getUsers().enqueue(new Callback<List<JsonQiwisUsers>>() {
                @Override
                public void onResponse(Call<List<JsonQiwisUsers>> call, Response<List<JsonQiwisUsers>> response) {

                    JsonQiwisUsers jsonQiwisUser = response.body().get(0);

                    if (jsonQiwisUser.getResultCode() == 0) {
                        db.beginTransaction();
                        try {
                            for (User qiwisUser :
                                    jsonQiwisUser.getUsers()) {
                                cv.clear();
                                cv.put(TABLE_QIWI_USERS_ID, qiwisUser.getId());
                                cv.put(TABLE_QIWI_USERS_NAME, qiwisUser.getName());
                                db.insert(TABLE_QIWI_USERS, null, cv);
                            }
                            isExceptions = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Открываем сообщение об ошибке
                            showDialog(e.getMessage());
                        } finally {
                            db.endTransaction();
                        }
                    } else {
                        String e = jsonQiwisUser.getMessage();
                        //Открываем сообщение об ошибке
                        showDialog(e);
                    }
                }

                @Override
                public void onFailure(Call<List<JsonQiwisUsers>> call, Throwable t) {
                    String e = t.toString();
                    //Открываем сообщение об ошибке
                    showDialog(e);
                }
            });
            //Закрываем анимацию загрузки

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            return;
        }
    }
}
