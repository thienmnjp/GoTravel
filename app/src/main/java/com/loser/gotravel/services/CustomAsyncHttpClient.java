package com.loser.gotravel.services;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by DucAnhZ on 16/10/2015.
 */
public class CustomAsyncHttpClient extends AsyncHttpClient {
    private final String USERNAME = "admin";
    private final String PASSWORD = "admin";

    public CustomAsyncHttpClient(Context context, String tokenHeader) {
        this.addHeader("username", USERNAME);
        this.addHeader("password", PASSWORD);
        this.addHeader("token", tokenHeader);
    }
}
