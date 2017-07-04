package com.example.samsung.qiwi_users_balance.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Response;

import static com.example.samsung.qiwi_users_balance.component.ComponentInstrumentedTest.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ControllerDBInstrumentedTest {

    private final String DB_NAME = "qiwisUsers";

    private final String TABLE_QIWI_USERS = "qiwi_users",
            TABLE_QIWI_USERS_ID = "id",
            TABLE_QIWI_USERS_NAME = "name";

    // Context of the app under test.
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private UsersPresenter actUsersPresenter = new UsersPresenter();
    private ControllerDB actControllerDB;
    private SQLiteDatabase expDB;
    private Response<JsonQiwisUsers> expResponse;

    @Before
    public void setUp() throws IOException {
        expDB = createDatabase("exp_" + DB_NAME);
        expResponse = ControllerAPI.getAPI().getUsers().execute();
    }

    @Test
    public void constructorControllerDBTest() throws Exception {

        actControllerDB = new ControllerDB(appContext);
        Assert.assertEquals(DB_NAME, actControllerDB.getDbName());
    }

    @Test
    public void getNameDBTest() throws Exception {

        String actNameDB = actUsersPresenter.collGetNameDB(expDB);
        Assert.assertEquals("exp_" + DB_NAME, actNameDB);
    }

    @Test
    public void openWritableDatabaseTest() throws Exception {
        actControllerDB.openWritableDatabase();
        Assert.assertEquals(expDB.isOpen(), actControllerDB.getDb().isOpen());
    }

    @Test
    public void downloadDataTest() throws Exception {

        actControllerDB.downloadData(expResponse);
        assertEquals(expDB, actControllerDB.getDb());
    }

    @Test
    public void copyDBTest() throws Exception {

        ControllerDB copyControllerDB = new ControllerDB(appContext, "copy_db");
        actControllerDB.copyDB(copyControllerDB);
        assertEquals(copyControllerDB.getDb(), actControllerDB.getDb());
    }
}
