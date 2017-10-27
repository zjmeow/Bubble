package com.stonymoon.bubble.util;

import android.content.Context;
import android.widget.Toast;

import com.stonymoon.bubble.ui.RegisterActivity;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by A on 2017/10/18.
 */

public class HttpUtil {
    private static final String tableId = "1000002164";
    private static final String ak = "A61df0d768beeecce052cc58283d84c2";
    private static final int pageSize = 40;
    private static Map<String, Object> parameters = new HashMap<>();

    private HttpUtil() {
    }

    public static Novate sendHttpRequest(Context context) {
        Novate novate = new Novate.Builder(context).baseUrl("http://123.207.26.208:9700/api/v1/").build();
        return novate;
    }

    //连入百度的sdk
    //创建用户
    public static void createUser(final Context context, String username, String userId) {
        parameters.clear();
        parameters.put("username", username);
        parameters.put("imageUrl", "http://oupl6wdxc.bkt.clouddn.com/nene.png");
        parameters.put("userId", userId);
        parameters.put("geotable_id", tableId);
        parameters.put("ak", ak);
        parameters.put("latitude", 1.0);
        parameters.put("longitude", 1.0);

        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/create", parameters, new RxStringCallback() {
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
        parameters.clear();
        parameters.put("id", locationId);
        parameters.put("geotable_id", tableId);
        parameters.put("ak", ak);
        parameters.put("latitude", latitude);
        parameters.put("longitude", longitude);

        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/update", parameters, new RxStringCallback() {
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

    public static void updateMap(final Context context, RxStringCallback rxStringCallback, double latitude, double longitude) {
        parameters.clear();
        DecimalFormat df = new DecimalFormat("######0.00");
        String bounds = String.format("%.2f", (longitude - 0.2)) + ","
                + String.format("%.2f", (latitude - 0.2)) + ";" +
                String.format("%.2f", (longitude + 0.2)) + "," + String.format("%.2f", (latitude + 0.2));
        parameters.put("bounds", bounds);
        parameters.put("page_size", pageSize);
        parameters.put("geotable_id", tableId);
        parameters.put("ak", ak);
        Novate novate = new Novate.Builder(context).baseUrl("http://api.map.baidu.com/").build();
        novate.rxPost("geodata/v4/poi/list", parameters, rxStringCallback);

    }

}
