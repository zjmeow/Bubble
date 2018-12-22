package com.stonymoon.bubble.util;

import android.content.Context;
import android.widget.Toast;

import com.stonymoon.bubble.api.BaseDataManager;
import com.stonymoon.bubble.api.serivces.UserService;
import com.stonymoon.bubble.bean.MapUserBean;
import com.stonymoon.bubble.bean.UpdateBean;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HttpUtil {
    private static final String tableId = "1000002164";
    private static final String ak = "A61df0d768beeecce052cc58283d84c2";
    private static final int pageSize = 40;
    private static final String TAG = "HttpUtil";
    private static Map<String, Object> createParameters = new HashMap<>();
    private static Map<String, Object> updateLocateParameters = new HashMap<>();
    private static Map<String, Object> updateMapParameters = new HashMap<>();
    private static Map<String, Object> userParameters = new HashMap<>();

    private HttpUtil() {
    }

    public static Novate sendHttpRequest(Context context) {
        String baseUrl = "http://123.207.26.208:8080/Bubble/api/v1/";
        Novate novate = new Novate.Builder(context).baseUrl(baseUrl).addCache(false).build();
        return novate;
    }


    public static void updateLocate(double latitude, double longitude) {

        BaseDataManager.getHttpManager()
                .create(UserService.class)
                .updateLocation(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UpdateBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {

                    }

                    @Override
                    public void onNext(UpdateBean updateBean) {

                    }
                });

    }

    public static void updateMap(final Context context, RxStringCallback rxStringCallback, double latitude, double longitude) {
        BaseDataManager.getHttpManager()
                .create(UserService.class)
                .getAroundUsers(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<MapUserBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(java.lang.Throwable e) {

                    }

                    @Override
                    public void onNext(MapUserBean updateBean) {

                    }
                });


        updateMapParameters.clear();
        String bounds = String.format("%.2f", (longitude - 0.2)) + ","
                + String.format("%.2f", (latitude - 0.2)) + ";" +
                String.format("%.2f", (longitude + 0.2)) + "," + String.format("%.2f", (latitude + 0.2));
        updateMapParameters.put("bounds", bounds);
        updateMapParameters.put("page_size", pageSize);
        updateMapParameters.put("geotable_id", tableId);
        updateMapParameters.put("ak", ak);
        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/list", updateMapParameters, rxStringCallback);

    }



    public static void updateHead(final Context context, String locationId, String url) {
        updateLocateParameters.clear();
        updateLocateParameters.put("id", locationId);
        updateLocateParameters.put("geotable_id", tableId);
        updateLocateParameters.put("ak", ak);
        updateLocateParameters.put("url", url);
        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/update", updateLocateParameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                //Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(context, "上传失败，请检查网络", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });
    }


}
