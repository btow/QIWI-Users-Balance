package com.example.samsung.qiwi_users_balance.presentation.presenter.users;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.ControllerDB;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.example.samsung.qiwi_users_balance.component.ComponentInstrumentedTest.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UsersPresenterInstrumentedTest {

    // Context of the app under test.
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private UsersPresenter actUsersPresenter = new UsersPresenter();
    private ControllerDB expControllerDB, actControllerDB;
    private SQLiteDatabase expDB, actDB;
    private List<QiwiUsers> expDataset, actDataset;

    @Before
    public void setUp() throws Exception {
        expControllerDB = new ControllerDB(appContext, "exp_db");
        expDB = createDatabase(expControllerDB.getDbName());
        expDataset = expDataset();
        actControllerDB = new ControllerDB(appContext, "act_db");
        actControllerDB.openWritableDatabase();
        actControllerDB.downloadData(ControllerAPI.getAPI().getUsers().execute());
        actDB = actControllerDB.getDb();
    }

    @Test
    public void createListQiwiUsersWithoutBDTest() throws Exception {

        ControllerDB newControllerDB = new ControllerDB(appContext, "new_db");
        newControllerDB.openWritableDatabase();
        actUsersPresenter.setDb(newControllerDB.getDb());
        actUsersPresenter.createListQiwiUsers();
        assertEquals(expDataset, actUsersPresenter.getDataset());
        actUsersPresenter.setDb(actDB);
    }

    @Test
    public void createListQiwiUsersWithDBTest() throws Exception {

        actUsersPresenter.createListQiwiUsers();
        assertEquals(expDataset, actUsersPresenter.getDataset());
    }

    @Test
    public void onClicExchengTest() throws Exception {

        actUsersPresenter.onClicExcheng();
        assertEquals(expDataset, actUsersPresenter.getDataset());
    }
}
