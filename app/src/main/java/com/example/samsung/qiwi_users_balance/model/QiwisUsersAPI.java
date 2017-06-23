package com.example.samsung.qiwi_users_balance.model;

import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QiwisUsersAPI {

    @GET("/index.json")
    Call<List<JsonQiwisUsers>> getUsers();

}
