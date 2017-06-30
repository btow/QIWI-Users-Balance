package com.example.samsung.qiwi_users_balance;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsersBalances;
import com.example.samsung.qiwi_users_balance.model.QiwiUsersBalances;
import com.example.samsung.qiwi_users_balance.presentation.presenter.balances.BalancesPresenter;

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
public class BalancesPresenterInstrumentedTest {

    // Context of the app under test.
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private BalancesPresenter actBalancesPresenter = new BalancesPresenter();
    private List<Response<JsonQiwisUsersBalances>> responses = new ArrayList<>();

    @Test
    public void responseHandlerTest() throws Exception {

        List<Response<JsonQiwisUsersBalances>> excListResponses = getResponsesJsonQiwisUsersBalances();

        for (int id = 0; id < 10; id++) {
            Response<JsonQiwisUsersBalances> jsonQiwisUsersBalancesResponse = getResponse(excListResponses, id);
            actBalancesPresenter.collResponseHandler(jsonQiwisUsersBalancesResponse);
            if (jsonQiwisUsersBalancesResponse.body().getResultCode() == 0) {
                assertEquals(expDataset(id), actBalancesPresenter.getDataset());
            } else {
                Assert.assertEquals(expExMsg(id), actBalancesPresenter.getExMsg());
            }
        }
    }

    @Test
    public void listCallbackTest() throws Exception {

        actBalancesPresenter.setCxt(appContext);

        for (int id = 0; id < 10; id++) {
            ControllerAPI.getAPI().getBalancesById(id).enqueue(actBalancesPresenter.collCallback());
            assertEquals(expDataset(id), actBalancesPresenter.getDataset());
        }

    }

    @Test
    public void createListQiwiUsersBalancesTest() throws Exception {

        actBalancesPresenter.setCxt(appContext);

        for (int id = 0; id < 10; id++) {

            actBalancesPresenter.setUsersId(id);
            actBalancesPresenter.createListQiwiUsersBalances();
            assertEquals(expDataset(id), actBalancesPresenter.getDataset());
        }
    }

    @Test
    public void onClicExchengTest() throws Exception {

        actBalancesPresenter.setCxt(appContext);

        for (int id = 0; id < 10; id++) {
            actBalancesPresenter.setUsersId(id);
            actBalancesPresenter.onClicExcheng();
            List<QiwiUsersBalances> expDataset = expDataset(id);
            List<QiwiUsersBalances> actDataset = actBalancesPresenter.getDataset();
            assertEquals(expDataset, actDataset);
        }
    }

    private Response<JsonQiwisUsersBalances> getResponse(final List<Response<JsonQiwisUsersBalances>> responses,
                                                         final int id) {
        return responses.get(id);
    }

    private List<Response<JsonQiwisUsersBalances>> getResponsesJsonQiwisUsersBalances() {

//-----------------------------------------------------------------------------------------
//---------------- Добавить проверку правильности заполнения ------------------------------
//-------------------------- листа ответов на запросы -------------------------------------
//        ----------------------------------------------------------------------------


        for (int id = 0; id < 10; id++) {
            try {
                responses.add(ControllerAPI.getAPI().getBalancesById(id).execute());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responses;
    }

    private List<String> expExMsg(final int id) {

        List<String> expExMsg = new ArrayList<>();
        switch (id) {
            case 1:
                expExMsg.add("result code: 300, message: ");
                break;
            case 3:
                expExMsg.add("result code: 300, message: ");
                break;
            case 6:
                expExMsg.add("result code: 300, message: ");
                break;
            case 9:
                expExMsg.add("result code: 300, message: ");
                break;
            default:
                break;
        }
        return expExMsg;
    }

    private List<QiwiUsersBalances> expDataset(final int id) {

        List<QiwiUsersBalances> expDataset = new ArrayList<>();
        switch (id) {
            case 0:
                expDataset.add(new QiwiUsersBalances("KZT", 3760.43f));
                expDataset.add(new QiwiUsersBalances("RUB", 1428.48f));
                expDataset.add(new QiwiUsersBalances("USD", 1267.87f));
                expDataset.add(new QiwiUsersBalances("KZT", 3936.91f));
                break;
            case 2:
                expDataset.add(new QiwiUsersBalances("EUR", 3647.86f));
                expDataset.add(new QiwiUsersBalances("EUR", 2347.36f));
                expDataset.add(new QiwiUsersBalances("EUR", 1979.86f));
                expDataset.add(new QiwiUsersBalances("USD", 1450.88f));
                break;
            case 4:
                expDataset.add(new QiwiUsersBalances("USD", 2229.95f));
                expDataset.add(new QiwiUsersBalances("KZT", 1337.18f));
                expDataset.add(new QiwiUsersBalances("RUB", 2033.76f));
                break;
            case 5:
                expDataset.add(new QiwiUsersBalances("KZT", 2416.32f));
                expDataset.add(new QiwiUsersBalances("USD", 3351.83f));
                break;
            case 7:
                expDataset.add(new QiwiUsersBalances("USD", 3750.88f));
                break;
            case 8:
                expDataset.add(new QiwiUsersBalances("KZT", 3453.49f));
                expDataset.add(new QiwiUsersBalances("EUR", 3883.45f));
                expDataset.add(new QiwiUsersBalances("RUB", 3726.43f));
                break;
            default:
                break;
        }
        return expDataset;
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
