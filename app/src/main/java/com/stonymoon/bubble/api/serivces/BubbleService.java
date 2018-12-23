package com.stonymoon.bubble.api.serivces;

import com.stonymoon.bubble.bean.BubbleBean;
import com.stonymoon.bubble.bean.BubbleDetailBean;
import com.stonymoon.bubble.bean.BubbleListBean;
import com.stonymoon.bubble.bean.UpdateBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Stony on 2018/12/22.
 */

public interface BubbleService {
    @GET("bubbles/detail")
    Observable<BubbleDetailBean> getBubbleDetail(@Query("id") int id);

    @FormUrlEncoded
    @POST("bubbles/upload")
    Observable<UpdateBean> uploadBubble(@Field("lng") double lng
            , @Field("lat") double lat
            , @Field("title") String title
            , @Field("content") String content
            , @Field("pic") String pic);

    @GET("bubbles/around")
    Observable<BubbleBean> getAroundBubble(@Query("lng") double lng, @Query("lat") double lat);

    @GET("bubbles/user")
    Observable<BubbleListBean> getBubbleByUserId(@Query("userId") int id);
}
