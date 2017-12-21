package com.stonymoon.bubble.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

//java版计算signature签名
public class SNGenerator {
    public static void main(String[] args) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {

    }

    public static String generateSN(Map paramsMap, String url) {
        SNGenerator snCal = new SNGenerator();
        try {
            String paramsStr = snCal.toQueryString(paramsMap);

            String wholeStr = new String("/" + url + "?" + paramsStr + "DD705fd2a8a1aad05502ee330640231a");

            // 对上面wholeStr再作utf8编码
            String tempStr = URLEncoder.encode(wholeStr, "UTF-8");

            return snCal.MD5(tempStr);
        } catch (Exception e) {
            LogUtil.e("SN", e.toString());
        }
        // 调用下面的MD5方法得到最后的sn签名7de5a22212ffaa9e326444c75a58f9a0
        return null;
    }


    // 对Map内所有value作utf8编码，拼接返回结果
    public String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            String ss[] = pair.getValue().toString().split(",");
            if (ss.length > 1) {
                for (String s : ss) {
                    queryString.append(URLEncoder.encode(s, "UTF-8") + ",");
                }
                queryString.deleteCharAt(queryString.length() - 1);
                queryString.append("&");
            } else {
                queryString.append(URLEncoder.encode((String) pair.getValue(),
                        "UTF-8") + "&");
            }
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
