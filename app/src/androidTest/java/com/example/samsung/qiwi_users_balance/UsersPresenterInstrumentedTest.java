package com.example.samsung.qiwi_users_balance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.ListQiwiUsersAdapter;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.User;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import org.junit.Assert;
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
public class UsersPresenterInstrumentedTest {

    private static final String S_TABLE_QIWI_USERS = "qiwi_users";
    private final String DB_NAME = "qiwisUsers";

    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name";
    private String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
            + TABLE_QIWI_USERS_ID + " integer primary key, "
            + TABLE_QIWI_USERS_NAME + " text)";


    // Context of the app under test.
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private UsersPresenter actUsersPresenter = new UsersPresenter();

    @Test
    public void getNameDBTest() throws Exception {

        appContext.deleteDatabase(DB_NAME);
        SQLiteDatabase expDB = createDatabase(DB_NAME);
        expDB.execSQL(sqlCommand);
        String actNameDB = actUsersPresenter.collGetNameDB(expDB);
        Assert.assertEquals(DB_NAME, actNameDB);
        appContext.deleteDatabase(DB_NAME);
    }

    @Test
    public void downloadDataTest() throws Exception {

        appContext.deleteDatabase(DB_NAME);
        appContext.deleteDatabase("exp_" + DB_NAME);
        appContext.deleteDatabase("act_" + DB_NAME);
        SQLiteDatabase actDB = appContext.openOrCreateDatabase("act_" + DB_NAME, 0 ,null);
        actDB.execSQL(sqlCommand);
        actUsersPresenter.setDb(actDB);
        actUsersPresenter.collDownloadData(ControllerAPI.getAPI().getUsers().execute());
        assertEquals(createDatabase("exp_" + DB_NAME), actDB);
    }

    @Test
    public void copyDBTest() throws Exception {

        actUsersPresenter.setCxt(appContext);
        appContext.deleteDatabase(DB_NAME);
        appContext.deleteDatabase("exp_" + DB_NAME);
        SQLiteDatabase expDb = createDatabase("exp_" + DB_NAME);
        SQLiteDatabase actDb = appContext.openOrCreateDatabase(DB_NAME, 0, null);
        actDb.execSQL(sqlCommand);
        actUsersPresenter.collCopyDB(expDb, actDb);
        assertEquals(expDb, actDb);
        expDb.close();
        actDb.close();

    }

    @Test
    public void createListQiwiUsersWithoutBDTest() throws Exception {

        actUsersPresenter.setCxt(appContext);
        appContext.deleteDatabase(DB_NAME);
        actUsersPresenter.createListQiwiUsers();
        assertEquals(expDataset(), actUsersPresenter.getDataset());
    }

    @Test
    public void createListQiwiUsersWithDBTest() throws Exception {

        actUsersPresenter.setCxt(appContext);
        appContext.deleteDatabase(DB_NAME);
        actUsersPresenter.setDb(createDatabase(DB_NAME));
        actUsersPresenter.createListQiwiUsers();
        assertEquals(expDataset(), actUsersPresenter.getDataset());
    }

    @Test
    public void onClicExchengTest() throws Exception {

        actUsersPresenter.setCxt(appContext);
        appContext.deleteDatabase(DB_NAME);
        SQLiteDatabase expDB = appContext.openOrCreateDatabase(DB_NAME, 0, null);
        expDB.execSQL(sqlCommand);
        actUsersPresenter.setDb(expDB);
        actUsersPresenter.onClicExcheng();
        assertEquals(expDataset(), actUsersPresenter.getDataset());
    }

    @SuppressWarnings("deprecation")
    private static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private List<QiwiUsers> expDataset() {

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

    private SQLiteDatabase createDatabase(final String dbName) {
        SQLiteDatabase cdb = appContext.openOrCreateDatabase(dbName, 0, null);
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

    private static void assertEquals(final List<QiwiUsers> expDataset,
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

    private static void assertEquals(final SQLiteDatabase expDB,
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
            Cursor expCursor = exp_DB.query(S_TABLE_QIWI_USERS, null, null, null, null, null, null);
            Cursor actCursor = act_DB.query(S_TABLE_QIWI_USERS, null, null, null, null, null, null);

            if (expCursor == null) {
                cleanMessage = "The table " + S_TABLE_QIWI_USERS + " in EXP DataBase does not exist - ";
                isComparisonFailure = true;
            } else if (actCursor == null) {
                cleanMessage = "The table " + S_TABLE_QIWI_USERS + " in ACT DataBase does not exist - ";
                isComparisonFailure = true;
            } else if (!expCursor.moveToFirst()) {
                cleanMessage = "The records in table " + S_TABLE_QIWI_USERS + " of EXP DataBase does not exist - ";
                isComparisonFailure = true;
            } else if (!actCursor.moveToFirst()) {
                cleanMessage = "The records in table " + S_TABLE_QIWI_USERS + " of ACT DataBase does not exist - ";
                isComparisonFailure = true;
            } else {
                do {
                    int index = expCursor.getInt(0);
                    if (index != actCursor.getInt(0)
                            || !expCursor.getString(1).equals(actCursor.getString(1))) {
                        cleanMessage = "The records of DataBases table " + S_TABLE_QIWI_USERS + " in position [" + index + "] isn't equals - ";
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
            if (isComparisonFailure) {

                comparisonFailure(cleanMessage,
                        expDB,
                        actDB);
            }
        }
    }
}
