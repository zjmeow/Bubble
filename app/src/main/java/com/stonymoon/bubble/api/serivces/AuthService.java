package com.stonymoon.bubble.api.serivces;

import com.stonymoon.bubble.bean.LoginBean;
import com.stonymoon.bubble.bean.RegisterBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Stony on 2018/12/16.
 */

public interface AuthService {
    @FormUrlEncoded
    @POST("auth/login")
    Observable<LoginBean> login(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/register")
    Observable<RegisterBean> register(@Field("phone") String phone
            , @Field("psw") String password, @Field("username") String username
            , @Field("token") String token);
}
