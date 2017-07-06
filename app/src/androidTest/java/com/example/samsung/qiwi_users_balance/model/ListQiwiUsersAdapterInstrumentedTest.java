package com.example.samsung.qiwi_users_balance.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.presentation.presenter.users.UsersPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

import static com.example.samsung.qiwi_users_balance.component.ComponentInstrumentedTest.assertEquals;
import static com.example.samsung.qiwi_users_balance.component.ComponentInstrumentedTest.createDatabase;
import static com.example.samsung.qiwi_users_balance.component.ComponentInstrumentedTest.expDataset;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ListQiwiUsersAdapterInstrumentedTest {

    // Context of the app under test.
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private View expView, actView;
    private RecyclerView expRecyclerView, actRecyclerView;
    private List<QiwiUsers> expDataset, actDataset;

    @Before
    public void setUp() throws IOException {
        expDataset = expDataset();
        expView = new View(appContext);
        expRecyclerView = (RecyclerView) expView.findViewById(R.id.rvUsers);
    }

    @Test
    public void constructorListQiwiUsersAdapterTest() throws Exception {

        ListQiwiUsersAdapter adapter = new ListQiwiUsersAdapter(expDataset);
        assertEquals(expDataset(), adapter.getDataset());
    }

}
