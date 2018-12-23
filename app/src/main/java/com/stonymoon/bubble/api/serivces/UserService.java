package com.stonymoon.bubble.api.serivces;

import com.stonymoon.bubble.bean.MapUserBean;
import com.stonymoon.bubble.bean.UpdateBean;
import com.stonymoon.bubble.bean.UserProfileBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Stony on 2018/12/22.
 */

public interface UserService {
    @FormUrlEncoded
    @POST("users/location")
    Observable<UpdateBean> updateLocation(@Field("lng") double lng
            , @Field("lat") double lat);

    @GET("users/around")
    Observable<MapUserBean> getAroundUsers(@Query("lng") double lng
            , @Query("lat") double lat);


    @GET("users/detail")
    Observable<UserProfileBean> getUserDetail(@Query("id") int id);

}
