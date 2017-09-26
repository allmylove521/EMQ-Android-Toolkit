package io.emqtt.emqandroidtoolkit.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.model.Subscription;
import io.emqtt.emqandroidtoolkit.ui.widget.QoSChooseLayout;

/**
 * ClassName: SubscriptionFragment
 * Desc:
 * Created by zhiw on 2017/3/24.
 */

public class SubscriptionFragment extends DialogFragment {


    @BindView(R.id.topic) EditText mTopic;
    @BindView(R.id.qos) QoSChooseLayout mQosLayout;

    private OnAddSubscriptionListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_suscription, null);
        ButterKnife.bind(this, view);

        builder.setView(view)
                .setPositiveButton(getString(R.string.subscribe),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String topic = mTopic.getText().toString().trim();
                                int qos = mQosLayout.getQoS();

                                Subscription subscription = new Subscription(topic, qos);
                                mListener.onAddSubscription(subscription);

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnAddSubscriptionListener) {
            mListener = (OnAddSubscriptionListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnAddSubscriptionListener");
        }
    }

    public interface OnAddSubscriptionListener {

        void onAddSubscription(Subscription subscription);
    }


}
