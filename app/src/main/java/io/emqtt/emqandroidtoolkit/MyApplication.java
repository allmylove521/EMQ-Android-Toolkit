package io.emqtt.emqandroidtoolkit;

import android.app.Application;
import android.content.Context;

import io.emqtt.emqandroidtoolkit.net.MQTTManager;
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
