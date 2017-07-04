package com.example.samsung.qiwi_users_balance.presentation.presenter.balances;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsersBalances;
import com.example.samsung.qiwi_users_balance.model.QiwiUsersBalances;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, Response<JsonQiwisUsersBalances>> responses = new HashMap<>();

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

    private static void assertEquals(final List expDataset,
                                     final List actDataset) {

        boolean isComparisonFailure = false;
        String cleanMessage = "The expected dataset is null - ";

        if (expDataset == null) {
            isComparisonFailure = true;
        } else if (actDataset == null) {
            cleanMessage = "The actual dataset is null - ";
            isComparisonFailure = true;
        } else if (expDataset.getClass() != actDataset.getClass()) {
            cleanMessage = "Lists from datasets are belong to different classes - ";
            isComparisonFailure = true;
        } else if (expDataset.size() != actDataset.size()) {
            cleanMessage = "The sizes of datasets isn't equals - ";
            isComparisonFailure = true;
        } else if (expDataset.size() == 0) {
            cleanMessage = "The size expeted and actual datasets is 0 - ";
            isComparisonFailure = true;
        } else {
            int index = 0;

            for (Object expObject :
                    expDataset) {
                switch (actDataset.getClass().toString()) {

                    case "String.class":
                        String expString = (String) expObject;
                        String actString = (String) actDataset.get(index);
                        if (!expString.equals(actString)) {
                            cleanMessage = "Lists<String> from datasets in position [" + index + "] isn't equals - ";
                            isComparisonFailure = true;
                        }
                        break;
                    case "QiwiUsersBalances.class":
                        QiwiUsersBalances expQiwiUsersBalances = (QiwiUsersBalances) expObject;
                        QiwiUsersBalances actQiwiUsersBalances = (QiwiUsersBalances) actDataset.get(index);
                        if (expQiwiUsersBalances.getAmount() != actQiwiUsersBalances.getAmount()
                                || !expQiwiUsersBalances.getCurrency().equals(actQiwiUsersBalances.getCurrency())) {
                            cleanMessage = "Lists<QiwiUsersBalances> from datasets in position [" + index + "] isn't equals - ";
                            isComparisonFailure = true;
                        }
                        break;
                    default:
                        break;
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

    @Test
    public void setContextTest() throws Exception {

        setActContext(appContext);
        Assert.assertEquals(appContext, actBalancesPresenter.getCxt());
    }

    @Test
    public void responseHandlerTest() throws Exception {

        setActContext(appContext);

        Map<Integer, Response<JsonQiwisUsersBalances>> excListResponses = getResponsesJsonQiwisUsersBalances();

        for (int id = 0; id < 10; id++) {
            Response<JsonQiwisUsersBalances> jsonQiwisUsersBalancesResponse = excListResponses.get(id);
            actBalancesPresenter.setUsersId(id);
            actBalancesPresenter.callResponseHandler(jsonQiwisUsersBalancesResponse);
            if (jsonQiwisUsersBalancesResponse.body().getResultCode() == 0) {
                assertEquals(expDataset(id), actBalancesPresenter.getDataset());
            } else {
                Assert.assertEquals(expExMsg(id), actBalancesPresenter.getExMsg());
            }
        }
    }

    @Test
    public void createListQiwiUsersBalancesTest() throws Exception {

        setActContext(appContext);

        for (int id = 0; id < 10; id++) {

            actBalancesPresenter.setUsersId(id);
            actBalancesPresenter.createListQiwiUsersBalances();
            switch (id) {
                case 0:
                case 2:
                case 4:
                case 5:
                case 7:
                case 8:
                    assertEquals(expDataset(id), actBalancesPresenter.getDataset());
                    break;
                default:
                    Assert.assertEquals(expExMsg(id), actBalancesPresenter.getExMsg());
                    break;
            }
        }
    }

    @Test
    public void onClicExchengTest() throws Exception {

        setActContext(appContext);

        for (int id = 0; id < 10; id++) {
            actBalancesPresenter.setUsersId(id);
            actBalancesPresenter.onClicExcheng();
            switch (id) {
                case 0:
                case 2:
                case 4:
                case 5:
                case 7:
                case 8:
                    assertEquals(expDataset(id), actBalancesPresenter.getDataset());
                    break;
                default:
                    Assert.assertEquals(expExMsg(id), actBalancesPresenter.getExMsg());
                    break;
            }
        }
    }

    private Map<Integer, Response<JsonQiwisUsersBalances>> getResponsesJsonQiwisUsersBalances() throws IOException {

        setActContext(appContext);
        List<QiwiUsersBalances> expDataset = null;

        for (int id = 0; id < 10; id++) {

            actBalancesPresenter.setUsersId(id);
            expDataset = expDataset(id);

            try {
                assertEquals(expDataset, actBalancesPresenter.getDataset());
            } catch (AssertionError e) {
                try {
                    responses.put(Integer.valueOf(id), ControllerAPI.getAPI().getBalancesById(id).execute());
                } catch (IOException e1) {
                    e1.printStackTrace();
                    throw e1;
                }
            }
        }
        return responses;
    }

    private void setActContext(Context appContext) {

        try {
            Assert.assertEquals(appContext, actBalancesPresenter.getCxt());
        } catch (Throwable t) {
            t.printStackTrace();
            actBalancesPresenter.setCxt(appContext);
        }
    }

    private String expExMsg(final int id) {

        setActContext(appContext);

        String expExMsg = "";
        switch (id) {
            case 1:
            case 3:
            case 6:
            case 9:
                expExMsg = "result code: 300";
                break;
            default:
                break;
        }
        return expExMsg;
    }

    private List<QiwiUsersBalances> expDataset(final int id) {

        List<QiwiUsersBalances> expDataset = null;
        switch (id) {
            case 0:
                expDataset = new ArrayList<>();
                expDataset.add(new QiwiUsersBalances("KZT", 3760.43f));
                expDataset.add(new QiwiUsersBalances("RUB", 1428.48f));
                expDataset.add(new QiwiUsersBalances("USD", 1267.87f));
                expDataset.add(new QiwiUsersBalances("KZT", 3936.91f));
                break;
            case 2:
                expDataset = new ArrayList<>();
                expDataset.add(new QiwiUsersBalances("EUR", 3647.86f));
                expDataset.add(new QiwiUsersBalances("EUR", 2347.36f));
                expDataset.add(new QiwiUsersBalances("EUR", 1979.86f));
                expDataset.add(new QiwiUsersBalances("USD", 1450.88f));
                break;
            case 4:
                expDataset = new ArrayList<>();
                expDataset.add(new QiwiUsersBalances("USD", 2229.95f));
                expDataset.add(new QiwiUsersBalances("KZT", 1337.18f));
                expDataset.add(new QiwiUsersBalances("RUB", 2033.76f));
                break;
            case 5:
                expDataset = new ArrayList<>();
                expDataset.add(new QiwiUsersBalances("KZT", 2416.32f));
                expDataset.add(new QiwiUsersBalances("USD", 3351.83f));
                break;
            case 7:
                expDataset = new ArrayList<>();
                expDataset.add(new QiwiUsersBalances("USD", 3750.88f));
                break;
            case 8:
                expDataset = new ArrayList<>();
                expDataset.add(new QiwiUsersBalances("KZT", 3453.49f));
                expDataset.add(new QiwiUsersBalances("EUR", 3883.45f));
                expDataset.add(new QiwiUsersBalances("RUB", 3726.43f));
                break;
            default:
                break;
        }
        return expDataset;
    }
}
