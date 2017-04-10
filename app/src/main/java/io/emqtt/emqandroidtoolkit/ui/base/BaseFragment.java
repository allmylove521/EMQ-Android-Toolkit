package io.emqtt.emqandroidtoolkit.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * ClassName: BaseFragment
 * Desc:
 * Created by zhiw on 2017/3/22.
 */

public abstract class BaseFragment extends Fragment{

    public FragmentActivity fragmentActivity;

    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutResId(), container, false);

        ButterKnife.bind(this, view);

        init();

        setUpView();

        setUpData();

        return view;
    }

    protected abstract int getLayoutResId();

    protected abstract void setUpView();

    protected abstract void setUpData();

    protected void init() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentActivity = getActivity();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void startActivity(Class<?> aClass) {
        fragmentActivity.startActivity(new Intent(fragmentActivity, aClass));
    }

    public void startActivity(Intent intent) {
        fragmentActivity.startActivity(intent);
    }
}
