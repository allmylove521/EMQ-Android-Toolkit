package io.emqtt.emqandroidtoolkit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import io.emqtt.emqandroidtoolkit.net.MQTTManager;
import io.emqtt.emqandroidtoolkit.util.LogUtil;
import io.realm.Realm;

/**
 * ClassName: MyApplication
 * Desc:
 * Created by zhiw on 2017/3/22.
 */

public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        Realm.init(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtil.d(activity.getClass().getSimpleName(),"onActivityCreated");

            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtil.d(activity.getClass().getSimpleName(),"onActivityStarted");


            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtil.d(activity.getClass().getSimpleName(),"onActivityResumed");


            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtil.d(activity.getClass().getSimpleName(),"onActivityPaused");


            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtil.d(activity.getClass().getSimpleName(),"onActivityStopped");


            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtil.d(activity.getClass().getSimpleName(),"onActivitySaveInstanceState");

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtil.d(activity.getClass().getSimpleName(),"onActivityDestroyed");


            }


        });
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onLowMemory() {
        MQTTManager.release();
        super.onLowMemory();
    }


}
