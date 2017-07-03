package com.example.samsung.qiwi_users_balance.model;

import com.example.samsung.qiwi_users_balance.model.JsonQiwisUsers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QiwisUsersAPI {

    @GET("index.json")
    Call<JsonQiwisUsers> getUsers();

    @GET("users/{id}/index.json")
    Call<JsonQiwisUsersBalances> getBalancesById(@Path("id") int id);

}
