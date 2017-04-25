package io.emqtt.emqandroidtoolkit.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * ClassName: BaseActivity
 * Desc:
 * Created by zhiw on 2017/3/22.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(savedInstanceState);

        setContentView(getLayoutResId());

        ButterKnife.bind(this);

        setUpView();

        setUpData();
    }

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract void setUpView();

    protected abstract void setUpData();

    protected void init(Bundle savedInstanceState) {

    }

    protected void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

}
