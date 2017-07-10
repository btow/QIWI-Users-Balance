package com.example.samsung.qiwi_users_balance.presentation.presenter;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.samsung.qiwi_users_balance.R;
import com.example.samsung.qiwi_users_balance.model.App;
import com.example.samsung.qiwi_users_balance.model.ControllerAPI;
import com.example.samsung.qiwi_users_balance.model.ControllerDB;
import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;
import com.example.samsung.qiwi_users_balance.model.QiwiUsers;
import com.example.samsung.qiwi_users_balance.model.exceptions.CreateListQiwiUsersException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBCursorIsNullException;
import com.example.samsung.qiwi_users_balance.model.exceptions.DBIsNotDeletedException;
import com.example.samsung.qiwi_users_balance.presentation.view.ServiceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

import static com.example.samsung.qiwi_users_balance.model.ManagerControllerDB.getControllerDB;

@InjectViewState
public class ServicePresenter extends MvpPresenter<ServiceView> {

    private LoadingUsersTask mLoadingUsersTask;
    private ExchangeUsersTask mExchangeUsersTask;
    private LoadingBalancesTask mLoadingBalancesTask;
    private ExchangeBalancesTask mExchangeBalancesTask;
    private MessageTask mMessageTask;
    private Bundle mArgs;

    public ServicePresenter() {

        mArgs= App.getArguments();

        switch (mArgs.getInt(App.SERV_VERSION)) {

            case App.LOAD_FRAG:

                switch (mArgs.getInt(App.CALL_FROM)) {

                    case App.CALL_FROM_MAIN_ACTIVITY:
                        mLoadingUsersTask = new LoadingUsersTask();
                        mLoadingUsersTask.execute();
                        break;
                    case App.CALL_FROM_PRIM_FRAGMENT:
                        mExchangeUsersTask = new ExchangeUsersTask();
                        mExchangeUsersTask.execute();
                        break;
                    case App.CALL_FROM_BALANCES_ACTIVITY:
                        mLoadingBalancesTask = new LoadingBalancesTask();
                        mLoadingBalancesTask.execute();
                        break;
                    case App.CALL_FROM_SECOND_FRAGMENT:
                        mExchangeBalancesTask = new ExchangeBalancesTask();
                        mExchangeBalancesTask.execute();
                        break;
                    default:
                        break;
                }
                break;
            case App.MESS_FRAG:
                mMessageTask = new MessageTask();
                mMessageTask.execute();
                break;
            default:
                break;
        }

    }

    public List<QiwiUsers> createListQiwiUsers() throws DBCursorIsNullException {

        boolean isRun = false;
        App.setQiwiUsersListCreated(isRun);

        List<QiwiUsers> qiwiUsersList = new ArrayList<>();
        ControllerDB controllerDB = getControllerDB(App.getPrimDbName()
        );
        //Создаём списк из БД
        int couner = 0, maxTrys = 2;
        do {
            if (!controllerDB.DBisOpen()) {
                controllerDB.openWritableDatabase();
            }
            Cursor cursor = controllerDB.getCursor();

            if (cursor != null) {

                if (cursor.moveToFirst()) {
                    do {
                        qiwiUsersList.add(new QiwiUsers(cursor.getInt(0), cursor.getString(1)));
                    } while (cursor.moveToNext());
                    isRun = true;
                } else {
                    if (cursor.getCount() == 0) {
                        try {
                            controllerDB.downloadData(ControllerAPI.getAPI().getUsers().execute());
                        } catch (Exception e) {
                            e.printStackTrace();
                            cursor.close();
                            String msg = App.getApp().getString(R.string.error_loading_response_in_db)
                                    + e.getMessage() + ": more 100 iterations";
                            throw new CreateListQiwiUsersException(msg);
                        }
                    }
                }
            } else {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                throw new DBCursorIsNullException(
                        App.getApp().getString(R.string.error_when_writing_data_from_the_response_db)
                                + " " + App.getApp().getString(R.string.db_cursor_is_null)
                );
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (couner > maxTrys) {
                String msg = App.getApp().getString(R.string.error_loading_response_in_db)
                        + ": run more " + maxTrys + " trys";
                App.getDequeMsg().pushMsg(msg);
                throw new CreateListQiwiUsersException(msg);
            }
            couner++;

        } while (!isRun);

        controllerDB.close();
        App.setQiwiUsersListCreated(isRun);

        return qiwiUsersList;
    }

    public View.OnClickListener onClickRepeat(View view) {

        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.btnRepeat:

                        if (App.getDequeMsg().isEmpty()) {
                            App.setUsedPrimFragmentsVersion(App.LOADING_FRAGMENT);
                            getViewState().showCallingScreen();
                        } else {
                            getViewState().showNewMsg();
                        }
                        break;
                    case R.id.btnContinue:
                        App.setUsedPrimFragmentsVersion(App.USERS_FRAMENT);
                        getViewState().showCallingScreen();
                    default:
                        break;
                }
            }
        };
        return onClickListener;
    }

    private class LoadingUsersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Map<String, ControllerDB> allConrollersBDs = App.getManagerControllerDB().getAllControllerDBs();
            ControllerDB controllerDB = allConrollersBDs.get(App.getPrimDbName());
            Response<JsonQiwisUsers> response = null;
            try {
                response = ControllerAPI.getAPI().getUsers().execute();
                controllerDB.downloadData(response);
            } catch (Exception e) {
                e.printStackTrace();
                App.getDequeMsg().pushMsg(e.getMessage());
            }

            App.setQiwiUsersList(createListQiwiUsers());
            try {
                if (App.getQiwiUsersList() == null) {
                    Toast.makeText(App.getApp().getBaseContext(),
                            App.getApp().getString(R.string.the_dataset_is_null),
                            Toast.LENGTH_SHORT
                    );
                }
            } catch (DBCursorIsNullException e) {
                e.printStackTrace();
                Toast.makeText(App.getApp().getBaseContext(),
                        e.getMessage(),
                        Toast.LENGTH_SHORT
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            App.setUsedPrimFragmentsVersion(App.MESSAGE_FRAGMENT);
            getViewState().showCallingScreen();
        }
    }

    private class ExchangeUsersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //onClickExcheng()
            ControllerDB controllerDB = getControllerDB(
                    App.getPrimDbName()
            );
            //Создаём резервную копию БД
            try {
                String dbName = "copy_db";
                App.createControllerDB(dbName);
                ControllerDB copyControllerDB = getControllerDB(dbName);
                copyControllerDB.openWritableDatabase();
                try {
                    controllerDB.copyDB(dbName);
                } catch (DBIsNotDeletedException e) {
                    e.printStackTrace();
                    Toast.makeText(App.getApp().getBaseContext(),
                            App.getApp().getString(R.string.error_while_backing_up_database)
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(App.getApp().getBaseContext(),
                            App.getApp().getString(R.string.error_while_backing_up_database)
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    );
                }
                //Обновляем список
                try {
                    App.setQiwiUsersList(createListQiwiUsers());
                    if (App.getQiwiUsersListCreated()) {
                        Toast.makeText(App.getApp().getBaseContext(),
                                App.getApp().getString(R.string.error_when_updating_database) + " " +
                                        App.getApp().getString(R.string.create_list_of_qiwis_users_is_not_performed),
                                Toast.LENGTH_SHORT
                        );
                    }
                } catch (DBCursorIsNullException e) {
                    e.printStackTrace();
                    //Восттанавливаем в случае неудачного обновления
                    try {
                        copyControllerDB.copyDB(App.getPrimDbName());
                    } catch (DBIsNotDeletedException e1) {
                        e1.printStackTrace();
                        Toast.makeText(App.getApp().getBaseContext(),
                                App.getApp().getString(R.string.error_restoring_db_from_backup)
                                        + e1.getMessage(),
                                Toast.LENGTH_SHORT
                        );
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Toast.makeText(App.getApp().getBaseContext(),
                                App.getApp().getString(R.string.error_restoring_db_from_backup)
                                        + e1.getMessage(),
                                Toast.LENGTH_SHORT
                        );
                    }
                    App.getDequeMsg().pushMsg(
                            App.getApp().getString(R.string.error_when_updating_database)
                                    + e.getMessage()
                    );
                }
            } catch (DBCursorIsNullException e) {
                e.printStackTrace();
                Toast.makeText(App.getApp().getBaseContext(),
                        App.getApp().getString(R.string.error_while_backing_up_database)
                                + e.getMessage(),
                        Toast.LENGTH_SHORT
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            App.setUsedPrimFragmentsVersion(App.MESSAGE_FRAGMENT);
            getViewState().showCallingScreen();
        }
    }

    private class LoadingBalancesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //onLoadBalancesDataset()

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            App.setUsedPrimFragmentsVersion(App.MESSAGE_FRAGMENT);
            getViewState().showCallingScreen();
        }
    }

    private class ExchangeBalancesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //onExchengBalancesDataset()

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            App.setUsedPrimFragmentsVersion(App.MESSAGE_FRAGMENT);
            getViewState().showCallingScreen();
        }
    }

    private class MessageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //onShowMessage()

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            App.setUsedPrimFragmentsVersion(App.USERS_FRAMENT);
            getViewState().showCallingScreen();
        }
    }
}
