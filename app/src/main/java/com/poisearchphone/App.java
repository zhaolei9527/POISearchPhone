package com.poisearchphone;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.poisearchphone.Utils.PausableThreadPoolExecutor;
import com.tencent.bugly.Bugly;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by 赵磊 on 2017/7/12.
 */

public class App extends MultiDexApplication {
    /**
     * 先创建一个请求队列，因为这个队列是全局的，所以在Application中声明这个队列
     */
    public static RequestQueue queues;
    public static PausableThreadPoolExecutor pausableThreadPoolExecutor;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        MultiDex.install(this);
        Bugly.init(getApplicationContext(), "f905a9d3d6", false);
        queues = Volley.newRequestQueue(getApplicationContext());
        Fresco.initialize(this);
        pausableThreadPoolExecutor = new PausableThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
    }

    public static RequestQueue getQueues() {
        return queues;
    }
}
