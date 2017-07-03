package com.example.samsung.qiwi_users_balance.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBDownloadResponsesIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBDownloadResponsesResultCodeException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotRecordInsertException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBRenameException;

import java.io.IOException;

import retrofit2.Response;

public class ControllerDB extends MvpAppCompatFragment {

    private static final int DB_VERSION = 1;
    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name",
            sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
                    + TABLE_QIWI_USERS_ID + " integer primary key, "
                    + TABLE_QIWI_USERS_NAME + " text)";
    private String mDbName;
    private Context mCxt;
    private DBHelper dbHelper;
    private SQLiteDatabase mDb;

    public ControllerDB() {
        this.mCxt = getContext();
        this.mDbName = "qiwisUsers";
    }

    public String getDbName() {
        return mDbName;
    }

    public void setDbName(final String dbName) throws DBRenameException {
        if (mDb != null) {
            throw new DBRenameException(getString(R.string.to_rename_an_existing_database_is_prohibited));
        }
        mDbName = dbName;
    }

    public void openWritableDatabase() {
        dbHelper = new DBHelper(mCxt, mDbName, DB_VERSION);
        mDb = dbHelper.getWritableDatabase();
    }

    public void openReadableDatabase() {
        dbHelper = new DBHelper(mCxt, mDbName, DB_VERSION);
        mDb = dbHelper.getReadableDatabase();
    }

    public void close() {
        if (dbHelper != null) dbHelper.close();
    }

    public boolean delete() {
        close();
        return mCxt.deleteDatabase(mDbName);
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    public boolean DBisOpen() {
        return mDb.isOpen();
    }

    public Cursor getCursor() {
        return mDb.query(TABLE_QIWI_USERS, null, null, null, null, null, null);
    }

    public void downloadData(Response<JsonQiwisUsers> listResponse) throws Exception {

        if (listResponse != null) {
            JsonQiwisUsers actJsonQiwisUsersList = listResponse.body();

            if (actJsonQiwisUsersList.getResultCode() == 0) {

                if (!mDb.isOpen()) mDb = mCxt.openOrCreateDatabase(mDbName, 0, null);
                ContentValues cv = new ContentValues();

                try {
                    for (User user :
                            actJsonQiwisUsersList.getUsers()) {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, user.getId());
                        cv.put(TABLE_QIWI_USERS_NAME, user.getName());
                        mDb.insert(TABLE_QIWI_USERS, null, cv);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = e.getMessage() + ": "
                            + getString(R.string.error_when_writing_data_from_the_response_db);
                    throw new Exception(msg);
                }
            } else {
                String msg = mCxt.getString(R.string.result_code) + actJsonQiwisUsersList.getResultCode()
                        + mCxt.getString(R.string.message) + actJsonQiwisUsersList.getMessage();
                throw new DBDownloadResponsesResultCodeException(msg);
            }
        } else {
            String msg = mCxt.getString(R.string.the_response_is_null);
            throw new DBDownloadResponsesIsNullException(msg);
        }
    }

    public void copyDB(ControllerDB copyControllerDB)
            throws Exception {

        String copy_db_name = copyControllerDB.getDbName();
        if (!DBisOpen()) openWritableDatabase();

        if (copyControllerDB.delete()) {
            copyControllerDB = new ControllerDB();
            copyControllerDB.setDbName(copy_db_name);
            copyControllerDB.openWritableDatabase();
        } else {
            String msg = copy_db_name + ": " + mCxt.getString(R.string.the_database_is_not_deleted);
            throw new DBIsNotDeletedException(msg);
        }

        Cursor cursor = getCursor();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ContentValues cv = new ContentValues();
                try {
                    do {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, cursor.getInt(0));
                        cv.put(TABLE_QIWI_USERS_NAME, cursor.getString(1));
                        copyControllerDB.insert(cv);
                    } while (cursor.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = getString(R.string.error_when_copying_data) + e.getMessage();
                    throw new DBIsNotRecordInsertException(msg);
                } finally {
                    copyControllerDB.close();
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                String msg = mCxt.getString(R.string.query_result_is_empty);
                throw new Exception(msg);
            }
        } else {
            String msg = getString(R.string.error_when_copying_data)
                    + getString(R.string.db_cursor_is_null);
            if (cursor != null) {
                cursor.close();
            }
            throw new DBCursorIsNullException(msg);
        }
    }

    private void insert(ContentValues cv) {
        mDb.insert(getDbName(), null, cv);
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
