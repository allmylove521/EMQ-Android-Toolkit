package io.emqtt.emqandroidtoolkit.ui.widget;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.model.QoSConstant;

/**
 * ClassName: QoSChooseLayout
 * Desc:
 * Created by zhiw on 2017/3/27.
 */

public class QoSChooseLayout extends LinearLayout {

    RadioGroup mRadioGroup;


    private int QoS;


    public QoSChooseLayout(Context context) {
        this(context, null);
    }

    public QoSChooseLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QoSChooseLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.qos_chosse_layout, this);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group_qos);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.qos0) {
                    QoS = QoSConstant.AT_MOST_ONCE;
                } else if (checkedId == R.id.qos1) {
                    QoS = QoSConstant.AT_LEAST_ONCE;
                } else if (checkedId == R.id.qos2) {
                    QoS = QoSConstant.EXACTLY_ONCE;
                }

            }
        });

        ((RadioButton) findViewById(R.id.qos1)).setChecked(true);
    }

    public int getQoS() {
        return QoS;
    }

    public void setQoS(int qos) {
        if (qos == QoSConstant.AT_MOST_ONCE) {
            mRadioGroup.check(R.id.qos0);
        } else if (qos == QoSConstant.AT_LEAST_ONCE) {
            mRadioGroup.check(R.id.qos1);
        } else if (qos == QoSConstant.EXACTLY_ONCE) {
            mRadioGroup.check(R.id.qos2);
        }
    }
}
