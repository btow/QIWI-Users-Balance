package com.example.samsung.qiwi_users_balance.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBDownloadResponsesIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBDownloadResponsesResultCodeException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotRecordInsertException;
import com.example.samsung.qiwi_users_balance.ui.fragment.users.UsersFragment;

import retrofit2.Response;

public class ControllerDB extends UsersFragment {

    private static final int DB_VERSION = 1;
    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "_id",
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

    public ControllerDB(Context cxt) {
        this.mCxt = cxt;
        this.mDbName = "qiwisUsers";
    }

    public ControllerDB(Context cxt, final String dbName) {
        this.mCxt = cxt;
        this.mDbName = dbName;
    }

    public String getDbName() {
        return mDbName;
    }

    public void openWritableDatabase() {
        dbHelper = new DBHelper(mCxt, mDbName, DB_VERSION);
        if (mDb == null || !mDb.isOpen()) {
            mDb = dbHelper.getWritableDatabase();
        }
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
            JsonQiwisUsers jsonQiwisUsers = listResponse.body();

            if (jsonQiwisUsers.getResultCode() == 0) {

                if (!mDb.isOpen()) {
                    mDb = mCxt.openOrCreateDatabase(mDbName, 0, null);
                }
                ContentValues cv = new ContentValues();
                mDb.beginTransaction();
                try {
                    for (User user :
                            jsonQiwisUsers.getUsers()) {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, user.getId());
                        cv.put(TABLE_QIWI_USERS_NAME, user.getName());
                        putRecord(cv);
                    }
                    mDb.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = e.getMessage() + ": "
                            + getString(R.string.error_when_writing_data_from_the_response_db);
                    throw new Exception(msg);
                } finally {
                    mDb.endTransaction();
                }
            } else {
                String msg = mCxt.getString(R.string.result_code) + jsonQiwisUsers.getResultCode()
                        + mCxt.getString(R.string.message) + jsonQiwisUsers.getMessage();
                throw new DBDownloadResponsesResultCodeException(msg);
            }
        } else {
            String msg = mCxt.getString(R.string.the_response_is_null);
            throw new DBDownloadResponsesIsNullException(msg);
        }
    }

    public void copyDB(final String copyDbName)
            throws Exception {

        if (!DBisOpen()) openWritableDatabase();

        ControllerDB copyControllerDB = new ControllerDB(mCxt, copyDbName);
        copyControllerDB.openWritableDatabase();

        Cursor cursor = getCursor();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ContentValues cv = new ContentValues();
                try {
                    do {
                        cv.clear();
                        cv.put(TABLE_QIWI_USERS_ID, cursor.getInt(0));
                        cv.put(TABLE_QIWI_USERS_NAME, cursor.getString(1));
                        copyControllerDB.putRecord(cv);
                    } while (cursor.moveToNext());
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = getString(R.string.error_when_copying_data) + e.getMessage();
                    throw new DBIsNotRecordInsertException(msg);
                } finally {
                    copyControllerDB.close();
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

    private void putRecord(ContentValues cv) {

        Cursor qwerysResult = mDb.query(
                TABLE_QIWI_USERS,
                new String[]{TABLE_QIWI_USERS_ID, TABLE_QIWI_USERS_NAME},
                TABLE_QIWI_USERS_ID + " = ?",
                new String[]{cv.getAsString(TABLE_QIWI_USERS_ID)},
                null, null, null
        );

        if (qwerysResult.getCount() == 0) {
            mDb.insert(TABLE_QIWI_USERS, null, cv);
        } else {
            mDb.update(TABLE_QIWI_USERS, cv,
                    TABLE_QIWI_USERS_ID + " = ?",
                    new String[]{cv.getAsString(TABLE_QIWI_USERS_ID)});
        }
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
