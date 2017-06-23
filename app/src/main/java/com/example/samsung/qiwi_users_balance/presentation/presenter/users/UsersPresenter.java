package com.example.samsung.qiwi_users_balance.presentation.presenter.users;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.QiwisUsersAPI;
import com.example.samsung.qiwi_users_balance.model.User;
import com.example.samsung.qiwi_users_balance.model.exceptions.BDCursorIsEmptyException;
import com.example.samsung.qiwi_users_balance.presentation.ControllerAPI;
import com.example.samsung.qiwi_users_balance.presentation.view.users.UsersView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

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
    private SQLiteDatabase db;

    public void createListQiwiUsers(final Context cxt, List<QiwiUsers> users)
            throws BDCursorIsEmptyException {
        //Создаём списк из БД и если БД - пустая, то создаём её
        db = new DBHelper(cxt, DB_NAME, DB_VERSION).getWritableDatabase();

        Cursor cursor = db.query(TABLE_QIWI_USERS, null, null, null, null, null, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    users.add(new QiwiUsers(cursor.getInt(0),cursor.getString(1)));
                } while (cursor.moveToNext());
            }
        } else {
            throw new BDCursorIsEmptyException();
        }
        db.close();
    }

    public void onClicExcheng(final Context cxt, List<QiwiUsers> users) {
        //Обновляем содержание БД и список
        db.beginTransaction();
        try {
            //Очищаем БД
        } finally {
            db.endTransaction();
        }
        db = new DBHelper(cxt, DB_NAME, DB_VERSION).getWritableDatabase();

    }

    class DBHelper extends SQLiteOpenHelper{

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
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            db.endTransaction();
                        }
                    } else {
                        String e = jsonQiwisUser.getMessage();
                        //Открыть сообщение об ошибке
                    }
                }

                @Override
                public void onFailure(Call<List<JsonQiwisUsers>> call, Throwable t) {
                    //Открыть сообщение об ошибке
                }
            });
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
