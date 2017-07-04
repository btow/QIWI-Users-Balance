package com.example.samsung.qiwi_users_balance.component;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.ControllerDB;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ComponentInstrumentedTest {

    private static final int DB_VERSION = 1;
    // Context of the app under test.
    private static Context appContext = InstrumentationRegistry.getTargetContext();
    private final static String DB_NAME = "qiwisUsers";

    private final static String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name";
    private final static String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
            + TABLE_QIWI_USERS_ID + " integer primary key, "
            + TABLE_QIWI_USERS_NAME + " text)";


    public static List<QiwiUsers> expDataset() {

        List<QiwiUsers> expDataset = new ArrayList<>();
        expDataset.add(new QiwiUsers(0, "Marisa"));
        expDataset.add(new QiwiUsers(1, "Madeline"));
        expDataset.add(new QiwiUsers(2, "Galloway"));
        expDataset.add(new QiwiUsers(3, "Sophie"));
        expDataset.add(new QiwiUsers(4, "Lori"));
        expDataset.add(new QiwiUsers(5, "Becker"));
        expDataset.add(new QiwiUsers(6, "Martha"));
        expDataset.add(new QiwiUsers(7, "Cohen"));
        expDataset.add(new QiwiUsers(8, "Duffy"));
        expDataset.add(new QiwiUsers(9, "Russell"));
        return expDataset;
    }

    public static SQLiteDatabase createDatabase(final String dbName) {

        appContext.deleteDatabase(dbName);
        SQLiteDatabase cdb = appContext.openOrCreateDatabase(dbName, 0, null);
        cdb.setVersion(DB_VERSION);
        cdb.execSQL(sqlCommand);

        ContentValues cv = new ContentValues();

        for (QiwiUsers expQiwiUser :
                expDataset()) {
            cv.clear();
            cv.put(TABLE_QIWI_USERS_ID, expQiwiUser.getId());
            cv.put(TABLE_QIWI_USERS_NAME, expQiwiUser.getName());
            cdb.insert(TABLE_QIWI_USERS, null, cv);
        }
        return cdb;
    }

    private static void comparisonFailure(final String cleanMessage,
                                          final Object expected,
                                          final Object actual)
            throws AssertionError {

        String formatted = "";
        if (cleanMessage != null && !cleanMessage.equals("")) {
            formatted = cleanMessage + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString)) {
            formatted += "expected: "
                    + formatClassAndValue(expected, expectedString)
                    + " but was: " + formatClassAndValue(actual, actualString);
        } else {
            formatted += "expected:<" + expectedString + "> but was:<"
                    + actualString + ">";
        }
        if (formatted == null) {
            throw new AssertionError();
        }
        throw new AssertionError(formatted);
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }


    public static void assertEquals(final List<QiwiUsers> expDataset,
                                     final List<QiwiUsers> actDataset) {
        boolean isComparisonFailure = false;
        String cleanMessage = "The dataset is null - ";
        if (expDataset == null || actDataset == null) {
            isComparisonFailure = true;
        } else if (expDataset.size() != actDataset.size()) {
            cleanMessage = "The sizes of datasets isn't equals - ";
            isComparisonFailure = true;
        } else if (expDataset.size() == 0) {
            cleanMessage = "The size expeted and actual datasets is 0 - ";
            isComparisonFailure = true;
        } else {
            int index = 0;
            for (QiwiUsers expQiwiUser :
                    expDataset) {
                QiwiUsers actQiwiUser = actDataset.get(index);
                if (expQiwiUser.getId() != actQiwiUser.getId()
                        || !expQiwiUser.getName().equals(actQiwiUser.getName())) {
                    cleanMessage = "The datasets in position [" + index + "] isn't equals - ";
                    isComparisonFailure = true;
                }
                index++;
            }
            if (!isComparisonFailure) return;
        }
        if (isComparisonFailure) {

            comparisonFailure(cleanMessage,
                    expDataset,
                    actDataset);
        }
    }

    public static void assertEquals(final SQLiteDatabase expDB,
                                     final SQLiteDatabase actDB) {
        SQLiteDatabase exp_DB = SQLiteDatabase.openDatabase(expDB.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        SQLiteDatabase act_DB = SQLiteDatabase.openDatabase(actDB.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        boolean isComparisonFailure = false;
        String cleanMessage = "The ControllerDB is null - ";
        if (exp_DB == null) {
            cleanMessage = "EXP: " + cleanMessage;
            isComparisonFailure = true;
        } else if (act_DB == null) {
            cleanMessage = "ACT: " + cleanMessage;
            isComparisonFailure = true;
        } else if (exp_DB.getVersion() != act_DB.getVersion()) {
            cleanMessage = "The version of databases isn't equals - ";
            isComparisonFailure = true;
        } else {
            Cursor expCursor = exp_DB.query(TABLE_QIWI_USERS, null, null, null, null, null, null);
            Cursor actCursor = act_DB.query(TABLE_QIWI_USERS, null, null, null, null, null, null);

            if (expCursor == null) {
                cleanMessage = "The table " + TABLE_QIWI_USERS + " in EXP DataBase does not exist - ";
                isComparisonFailure = true;
            } else if (actCursor == null) {
                cleanMessage = "The table " + TABLE_QIWI_USERS + " in ACT DataBase does not exist - ";
                isComparisonFailure = true;
            } else if (!expCursor.moveToFirst()) {
                cleanMessage = "The records in table " + TABLE_QIWI_USERS + " of EXP DataBase does not exist - ";
                isComparisonFailure = true;
            } else if (!actCursor.moveToFirst()) {
                cleanMessage = "The records in table " + TABLE_QIWI_USERS + " of ACT DataBase does not exist - ";
                isComparisonFailure = true;
            } else {
                do {
                    int index = expCursor.getInt(0);
                    if (index != actCursor.getInt(0)
                            || !expCursor.getString(1).equals(actCursor.getString(1))) {
                        cleanMessage = "The records of DataBases table " + TABLE_QIWI_USERS + " in position [" + index + "] isn't equals - ";
                        isComparisonFailure = true;
                    }
                    if (!actCursor.moveToNext() && actCursor.getCount() < index) {
                        cleanMessage = "In the ACT database there is no corresponding position [" + ++index + "] recording - ";
                        isComparisonFailure = true;
                    }
                } while (expCursor.moveToNext());
                exp_DB.close();
                act_DB.close();
                if (!isComparisonFailure) return;
            }
        }
        if (isComparisonFailure) {

            comparisonFailure(cleanMessage,
                    expDB,
                    actDB);
        }
    }
}
