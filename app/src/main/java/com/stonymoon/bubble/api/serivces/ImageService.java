package com.stonymoon.bubble.api.serivces;

import com.stonymoon.bubble.bean.ImageTokenBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Stony on 2018/12/24.
 */

public interface ImageService {
    @GET("images/upload")
    Observable<ImageTokenBean> getImageToken();
}
