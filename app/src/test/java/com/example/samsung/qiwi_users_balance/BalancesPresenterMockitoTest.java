package com.example.samsung.qiwi_users_balance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsersBalances;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsersBalances;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static org.junit.Assert.assertNotNull;

/**
 * Created by samsung on 29.06.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class BalancesPresenterMockitoTest {

    // Context of the app under test.
    private UsersPresenter actUsersPresenter = new UsersPresenter();

    @Mock
    Context fakeContext;

    @Test
    public void getBalancesResponseMocTest() throws Exception {

        MockitoAnnotations.initMocks(this);
        for (int id = 0; id < 10; id++) {
            Response<JsonQiwisUsersBalances> listResponse = ControllerAPI.getAPI().getBalancesById(id).execute();
            assertNotNull(listResponse);
        }
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

    private static void assertEquals(final List<QiwiUsersBalances> expDataset,
                                     final List<QiwiUsersBalances> actDataset) {
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
            for (QiwiUsersBalances expQiwiUsersBalances :
                    expDataset) {
                QiwiUsersBalances actQiwiUsersBalances = actDataset.get(index);
                if (expQiwiUsersBalances.getAmount() != actQiwiUsersBalances.getAmount()
                        || !expQiwiUsersBalances.getCurrency().equals(actQiwiUsersBalances.getCurrency())) {
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
}
