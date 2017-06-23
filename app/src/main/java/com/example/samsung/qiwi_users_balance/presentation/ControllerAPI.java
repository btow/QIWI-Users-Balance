package com.example.samsung.qiwi_users_balance.presentation;

import com.example.samsung.qiwi_users_balance.model.QiwisUsersAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ControllerAPI {

    static final String BASE_URL = "https://w.qiwi.com/mobile/testtask/";

    public static QiwisUsersAPI getAPI() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        QiwisUsersAPI qiwisUsersAPI = retrofit.create(QiwisUsersAPI.class);
        return qiwisUsersAPI;
    }

}
