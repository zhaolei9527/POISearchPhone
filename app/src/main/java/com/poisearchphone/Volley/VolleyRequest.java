package com.poisearchphone.Volley;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.poisearchphone.App;
import com.poisearchphone.R;
import com.poisearchphone.Utils.UrlUtils;
import com.poisearchphone.Utils.Utils;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Created by 赵磊 on 2017/7/12.
 */

public class VolleyRequest {
    public static StringRequest request;
    private Context context;

    public static void RequestGet(Context context, String url, String tag, VolleyInterface vif) {
        App.getQueues().cancelAll(tag);
        request = new StringRequest(Request.Method.GET, url, vif.loadingListener(), vif.errorListener());
        //tag设置
        request.setTag(tag);
        //网络状态检测
        if (Utils.isConnected(context)) {
            App.getQueues().add(request);
        } else {
            Toast.makeText(context, context.getString(R.string.Networkexception), Toast.LENGTH_SHORT).show();
        }
    }

    public static void RequestPost(Context context, String url, String tag, final Map<String, String> params, VolleyInterface vif) {

        request = new StringRequest(Request.Method.POST, url, vif.loadingListener(), vif.errorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String s = formatUrlMap(params, false, false);
                String s1 = urlmd5(s, UrlUtils.KEY);
                params.put("pwd", s1);
                Log.e("VolleyRequest", "params:" + s);
                Log.e("VolleyRequest", "params:" + s1);
                Log.e("VolleyRequest", "params:" + params);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //tag设置
        request.setTag(tag);
        //网络状态检测
        if (Utils.isConnected(context)) {
            App.getQueues().add(request);
        } else {
            Toast.makeText(context, context.getString(R.string.Networkexception), Toast.LENGTH_SHORT).show();
        }
    }

    public static void RequestPost(Context context, String url, final Map<String, String> params, VolleyInterface vif) {
        request = new StringRequest(Request.Method.POST, url, vif.loadingListener(), vif.errorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String s = formatUrlMap(params, false, false);
                String s1 = urlmd5(s, UrlUtils.KEY);
                params.put("pwd", s1);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //网络状态检测
        if (Utils.isConnected(context)) {
            App.getQueues().add(request);
        } else {
            Toast.makeText(context, context.getString(R.string.Networkexception), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 方法用途: 对所有传入参数按照字段名的Unicode码从小到大排序（字典序），并且生成url参数串
     *
     * @param paraMap    要排序的Map对象
     * @param urlEncode  是否需要URLENCODE
     * @param keyToLower 是否需要将Key转换为全小写
     *                   true:key转化成小写，false:不转化
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (!TextUtils.isEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buff;
    }

    public static String urlmd5(String url, String appid) {
        String s = url + "&appkey=" + appid;
        Log.e("VolleyRequest", "params:" + s);
        String s1 = Utils.md5(s);
        String substring = s1.substring(0, 18);
        return substring.toUpperCase();
    }

}