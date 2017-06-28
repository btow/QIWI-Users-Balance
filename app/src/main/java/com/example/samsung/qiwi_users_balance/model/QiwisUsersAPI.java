package com.example.samsung.qiwi_users_balance.model;

import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QiwisUsersAPI {

    @GET("/mobile/testtask/index.json")
    Call<List<JsonQiwisUsers>> getUsers();

    @GET("/mobile/testtask/users/{id}/index.json")
    Call<List<JsonQiwisUsersBalances>> getBalancesById(@Path("id") int id);

}
