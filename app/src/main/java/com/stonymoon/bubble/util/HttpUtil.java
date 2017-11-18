package com.stonymoon.bubble.util;

import android.content.Context;
import android.widget.Toast;

import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class HttpUtil {
    private static final String tableId = "1000002164";
    private static final String ak = "A61df0d768beeecce052cc58283d84c2";
    private static final int pageSize = 40;
    private static Map<String, Object> createParameters = new HashMap<>();
    private static Map<String, Object> updateLocateParameters = new HashMap<>();
    private static Map<String, Object> updateMapParameters = new HashMap<>();
    private static Map<String, Object> userParameters = new HashMap<>();

    private HttpUtil() {
    }

    public static Novate sendHttpRequest(Context context) {
        //String baseUrl = "http://120.24.238.200:7888/";
        String baseUrl = "http://123.207.26.208:8080/Bubble/api/v1/";
        Novate novate = new Novate.Builder(context).baseUrl(baseUrl).build();
        return novate;
    }

    //连入百度的sdk
    //创建用户
    public static void createUser(final Context context, String username, String userId) {
        createParameters.clear();
        createParameters.put("username", username);
        createParameters.put("imageUrl", "http://oupl6wdxc.bkt.clouddn.com/nene.png");
        createParameters.put("userId", userId);
        createParameters.put("geotable_id", tableId);
        createParameters.put("ak", ak);
        createParameters.put("latitude", 1.0);
        createParameters.put("longitude", 1.0);

        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/create", createParameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(context, "加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });
    }


    public static void updateLocate(final Context context, String locationId, double latitude, double longitude) {
        updateLocateParameters.clear();
        updateLocateParameters.put("id", locationId);
        updateLocateParameters.put("geotable_id", tableId);
        updateLocateParameters.put("ak", ak);
        updateLocateParameters.put("latitude", latitude);
        updateLocateParameters.put("longitude", longitude);
        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/update", updateLocateParameters, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                //Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(context, "加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });
    }

    public static void updateMap(final Context context, RxStringCallback rxStringCallback, double latitude, double longitude) {
        updateMapParameters.clear();
        DecimalFormat df = new DecimalFormat("######0.00");
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

    public static void getUser(final Context context, String userId, RxStringCallback rxStringCallback) {
        userParameters.clear();
        userParameters.put("geotable_id", tableId);
        userParameters.put("ak", ak);
        userParameters.put("uid", userId + "," + userId);
        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/list", userParameters, rxStringCallback);

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
                Toast.makeText(context, "加载失败，请检查网络", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });
    }


}
