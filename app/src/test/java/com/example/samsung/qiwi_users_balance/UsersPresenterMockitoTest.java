package com.example.samsung.qiwi_users_balance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static org.mockito.Mockito.*;

/**
 * Created by samsung on 29.06.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class UsersPresenterMockitoTest {

    private static final String S_TABLE_QIWI_USERS = "qiwi_users";
    private final String DB_NAME = "qiwisUsers";

    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name";
    private String sqlCommand = "create table " + TABLE_QIWI_USERS + " ("
            + TABLE_QIWI_USERS_ID + " integer primary key, "
            + TABLE_QIWI_USERS_NAME + " text)";

    // Context of the app under test.
    private UsersPresenter actUsersPresenter = new UsersPresenter();

    @Mock
    Context fakeContext;

    @Test
    public void getUsersResponseMocTest() throws Exception {

        MockitoAnnotations.initMocks(this);
        Response<JsonQiwisUsers> listResponse = ControllerAPI.getAPI().getUsers().execute();
        assertNotNull(listResponse);
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

    private SQLiteDatabase createDatabase() {
        SQLiteDatabase cdb = fakeContext.openOrCreateDatabase(DB_NAME, 0, null);
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
        boolean isComparisonFailure = false;
        String cleanMessage = "The DB is null - ";
        if (expDB == null || actDB == null) {
            isComparisonFailure = true;
        } else if (expDB.getVersion() != actDB.getVersion()) {
            cleanMessage = "The version of databases isn't equals - ";
            isComparisonFailure = true;
        } else {
            Cursor expCursor = expDB.query(S_TABLE_QIWI_USERS, null, null, null, null, null, null);
            Cursor actCursor = actDB.query(S_TABLE_QIWI_USERS, null, null, null, null, null, null);

            if (expCursor == null || actCursor == null) {
                cleanMessage = "The table " + S_TABLE_QIWI_USERS + " in DataBase does not exist - ";
                isComparisonFailure = true;
            } else if (!expCursor.moveToFirst() || !actCursor.moveToFirst()) {
                cleanMessage = "The records in table " + S_TABLE_QIWI_USERS + " of DataBase does not exist - ";
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
                        cleanMessage = "In the actual database there is no corresponding position [" + ++index + "] recording - ";
                        isComparisonFailure = true;
                    }
                } while (expCursor.moveToNext());
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
