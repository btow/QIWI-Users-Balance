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
    private List<QiwiUsers> mDataset;
    private FragmentManager mFrm;
    private RecyclerView mRvUsers;
    private boolean isExceptions = false;

    public void setCxt(Context cxt) {
        this.mCxt = cxt;
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

    public void showDialog(final String msg) {
        DialogFragment mDialogFragment = new Dialog();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        mDialogFragment.setArguments(args);
        mDialogFragment.show(mFrm, "mDialogFragment");
        mDialogFragment.dismiss();
        isExceptions = true;
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
            copyDB(mCxt, "copy_" + DB_NAME, DB_NAME);
            isExceptions = false;
        } catch (DBCursorIsEmptyException e) {
            e.printStackTrace();
            showDialog(e.getMessage());
        }
        //Удаляем БД
        mCxt.deleteDatabase(DB_NAME);
        //Обновляем содержание БД и список
        try {
            createListQiwiUsers();
            mRvUsers.getAdapter().notifyDataSetChanged();
            isExceptions = false;
        } catch (DBCursorIsEmptyException e) {
            e.printStackTrace();
            showDialog(e.getMessage());
            try {
                copyDB(mCxt, DB_NAME, "copy_" + DB_NAME);
            } catch (DBCursorIsEmptyException e1) {
                e1.printStackTrace();
                showDialog(e.getMessage());
            }
        }
    }

    private void copyDB(Context cxt, String copy_db_name, final String db_name)
            throws DBCursorIsEmptyException {
        SQLiteDatabase db = cxt.openOrCreateDatabase(db_name, 0, null);
        mCxt.deleteDatabase(copy_db_name);
        SQLiteDatabase cdb = cxt.openOrCreateDatabase(copy_db_name, 0, null);
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
                        cv.put(TABLE_QIWI_USERS_ID, cursor.getInt(0));
                        cv.put(TABLE_QIWI_USERS_NAME, cursor.getString(1));
                        cdb.insert(TABLE_QIWI_USERS, null, cv);
                    } while (cursor.moveToNext());
                }
                isExceptions = false;
            } else {
                isExceptions = true;
                throw new DBCursorIsEmptyException(cxt);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            showDialog(e.getMessage());
        } finally {
            cdb.endTransaction();
            cdb.close();
        }
    }

    private class DBHelper extends SQLiteOpenHelper {

        private Context mCxt;
        private String exMsg;
        private boolean isException;
        private JsonQiwisUsers jsonQiwisUser = new JsonQiwisUsers();

        DBHelper(Context context,
                 final String DB_NAME,
                 final int DB_VERSION) {
            super(context, DB_NAME, null, DB_VERSION);
            this.mCxt = context;
            this.exMsg  = this.mCxt.getString(R.string.response_is_null);
            this.isException = false;
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {

            final ContentValues cv = new ContentValues();
            String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
                    + TABLE_QIWI_USERS_ID + " integer primary key, "
                    + TABLE_QIWI_USERS_NAME + " text)";
            db.execSQL(sqlCommand); //создание БД

            Callback<List<JsonQiwisUsers>> listCallback = new Callback<List<JsonQiwisUsers>>() {

                @Override
                public void onResponse(@Nullable Call<List<JsonQiwisUsers>> call,
                                       @Nullable Response<List<JsonQiwisUsers>> response) {

                    if (response == null) {
                        isException = true;
                    } else if (response.body() == null) {
                        isException = true;
                        exMsg = mCxt.getString(R.string.responses_body_is_null);
                    } else {
                        isException = false;
                        //Обработка
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
                                isException = true;
                                //Создаём сообщение об ошибке
                                exMsg = e.getMessage();
                            } finally {
                                db.endTransaction();
                            }
                        } else {
                            isException = true;
                            //Создаём сообщение об ошибке
                            exMsg = jsonQiwisUser.getMessage();
                        }
                    }
                }

                @Override
                public void onFailure(@Nullable Call<List<JsonQiwisUsers>> call,
                                      @Nullable Throwable t) {
                    isException = true;

                    if (t != null) exMsg = t.getMessage();
                }
            };

            //Открываем прогресс-бар загрузки

            ControllerAPI.getAPI().getUsers().enqueue(listCallback);

            //Закрываем прогрксс-бар загрузки
            if (isException) {
                showDialog(exMsg);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
