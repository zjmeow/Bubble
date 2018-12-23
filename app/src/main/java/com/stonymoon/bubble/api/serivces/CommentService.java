package com.stonymoon.bubble.api.serivces;

import com.stonymoon.bubble.bean.BubbleCommentBean;
import com.stonymoon.bubble.bean.UpdateBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Stony on 2018/12/23.
 */

public interface CommentService {
    @FormUrlEncoded
    @POST("comments/comment")
    Observable<UpdateBean> comment(@Field("content") String content
            , @Field("bubbleId") int bubbleId);

    @GET("comments/comment")
    Observable<BubbleCommentBean> getComment(@Query("id") int id);
}
