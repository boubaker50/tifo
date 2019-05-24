package com.thephoenixit.walidchaieb;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<String> getsequence(@Url String chairID);
}
