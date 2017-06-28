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
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.User;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsEmptyException;
import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
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
    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name";
    private Context mCxt;
    private String mMsg;
    private List<QiwiUsers> mDataset;
    private FragmentManager mFrm;
    private RecyclerView mRvUsers;
    private boolean isExceptions = false;

    public void setCxt(Context cxt) {
        this.mCxt = cxt;
        this.mMsg = cxt.getString(R.string.response_is_null);
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

    public List<QiwiUsers> getDataset() {
        return mDataset;
    }

    public boolean getExceptions() {
        return isExceptions;
    }

    public void showDialog() {
        DialogFragment mDialogFragment = new Dialog();
        Bundle args = new Bundle();
        args.putString("msg", mMsg);
        mDialogFragment.setArguments(args);
        mDialogFragment.show(mFrm, "mDialogFragment");
    }

    public void createListQiwiUsers() throws DBCursorIsEmptyException {

        int DB_VERSION = 1;

        if (mDataset == null) {
            mDataset = new ArrayList<>();
        } else {
            mDataset.clear();
        }
        //Создаём списк из БД и если БД - пустая, то создаём её
        SQLiteDatabase db = new DBHelper(mCxt, DB_NAME, DB_VERSION).getWritableDatabase();

        Cursor cursor = db.query(TABLE_QIWI_USERS, null, null, null, null, null, null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    mDataset.add(new QiwiUsers(cursor.getInt(0), cursor.getString(1)));
                } while (cursor.moveToNext());
                isExceptions = false;
            }
        } else {
            db.close();
            throw new DBCursorIsEmptyException(mCxt);
        }
        cursor.close();
        db.close();
    }

    public void onClicExcheng() {
        //Создаём резервную копию БД
        try {
            copyDB("copy_" + DB_NAME, DB_NAME);
            isExceptions = false;
        } catch (DBCursorIsEmptyException e) {
            e.printStackTrace();
            isExceptions = true;
            mMsg = e.getMessage();
            showDialog();
        }
        //Удаляем БД
        mCxt.deleteDatabase(DB_NAME);
        //Обновляем содержание БД и список
        try {
            createListQiwiUsers();
            isExceptions = false;
        } catch (DBCursorIsEmptyException e) {
            e.printStackTrace();
            mMsg = e.getMessage();
            showDialog();
            try {
                copyDB(DB_NAME, "copy_" + DB_NAME);
            } catch (DBCursorIsEmptyException e1) {
                e1.printStackTrace();
                mMsg = e1.getMessage();
                showDialog();
            }
        }
    }

    //After testing to change the access modifier to "private"-------------------------------------->
    public
    void copyDB(String copy_db_name, final String db_name)
            throws DBCursorIsEmptyException {
        SQLiteDatabase db = mCxt.openOrCreateDatabase(db_name, 0, null);
        mCxt.deleteDatabase(copy_db_name);
        SQLiteDatabase cdb = mCxt.openOrCreateDatabase(copy_db_name, 0, null);
        String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
                + TABLE_QIWI_USERS_ID + " integer primary key, "
                + TABLE_QIWI_USERS_NAME + " text)";
        cdb.execSQL(sqlCommand);
        Cursor cursor = db.query(TABLE_QIWI_USERS, null, null, null, null, null, null);
        final ContentValues cv = new ContentValues();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, cursor.getInt(0));
                        cv.put(TABLE_QIWI_USERS_NAME, cursor.getString(1));
                        cdb.insert(TABLE_QIWI_USERS, null, cv);
                    } while (cursor.moveToNext());
                }
                isExceptions = false;
            } else {
                isExceptions = true;
                throw new DBCursorIsEmptyException(mCxt);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            mMsg = e.getMessage();
            showDialog();
        } finally {
            cdb.close();
        }
    }

    //After testing to change the access modifier to "private"--------------------------------->
    public
    Callback<List<JsonQiwisUsers>> listCallback(final SQLiteDatabase db) {

        return new Callback<List<JsonQiwisUsers>>() {

            JsonQiwisUsers jsonQiwisUsers = new JsonQiwisUsers();
            final ContentValues cv = new ContentValues();

            @Override
            public void onResponse(@Nullable Call<List<JsonQiwisUsers>> call,
                                   @Nullable Response<List<JsonQiwisUsers>> response) {

                if (response == null) {
                    isExceptions = true;
                } else if (response.body() == null) {
                    isExceptions = true;
                    mMsg = mCxt.getString(R.string.responses_body_is_null);
                } else {
                    isExceptions = false;
                    //Обработка
                    jsonQiwisUsers = response.body().get(0);
                    if (jsonQiwisUsers.getResultCode() == 0) {
                        try {
                            for (User qiwisUser :
                                    jsonQiwisUsers.getUsers()) {
                                cv.clear();
                                cv.put(TABLE_QIWI_USERS_ID, qiwisUser.getId());
                                cv.put(TABLE_QIWI_USERS_NAME, qiwisUser.getName());
                                db.insert(TABLE_QIWI_USERS, null, cv);
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

            @Override
            public void onFailure(@Nullable Call<List<JsonQiwisUsers>> call,
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

            String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
                    + TABLE_QIWI_USERS_ID + " integer primary key, "
                    + TABLE_QIWI_USERS_NAME + " text)";
            db.execSQL(sqlCommand); //создание БД

            //Открываем прогресс-бар загрузки

            ControllerAPI.getAPI().getUsers().enqueue(listCallback(db));

            //Закрываем прогрксс-бар загрузки

            if (isExceptions) {
                showDialog();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
