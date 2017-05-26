package io.emqtt.emqandroidtoolkit.ui.fragment;

import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.event.DeleteTopicMessageEvent;
import io.emqtt.emqandroidtoolkit.event.MessageEvent;
import io.emqtt.emqandroidtoolkit.model.EmqMessage;
import io.emqtt.emqandroidtoolkit.model.Subscription;
import io.emqtt.emqandroidtoolkit.ui.activity.DashboardActivity;
import io.emqtt.emqandroidtoolkit.ui.adapter.SubscriptionRecyclerViewAdapter;
import io.emqtt.emqandroidtoolkit.ui.base.BaseFragment;
import io.emqtt.emqandroidtoolkit.util.RealmHelper;
import io.emqtt.emqandroidtoolkit.util.StringUtil;

/**
 *
 */
public class SubscriptionListFragment extends BaseFragment {

    public static final String TAG = "Subscription";

    @BindView(R.id.subscription_list) RecyclerView mSubscriptionRecyclerView;

    private OnListFragmentInteractionListener mListener;
    private SubscriptionRecyclerViewAdapter mAdapter;

    private List<String> mTopicList = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubscriptionListFragment() {
    }

    public static SubscriptionListFragment newInstance() {
        return new SubscriptionListFragment();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_subscription_list;
    }

    @Override
    protected void setUpView() {
        mSubscriptionRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentActivity));
    }

    @Override
    protected void setUpData() {
        List<Subscription> list = ((DashboardActivity) fragmentActivity).getSubscriptionList();
        for (Subscription subscription : list) {
            EmqMessage emqMessage = RealmHelper.getInstance().queryFirstTopicMessage(EmqMessage.class, subscription.getTopic());
            if (emqMessage != null) {
                subscription.setMessage(emqMessage);
            }
            mTopicList.add(subscription.getTopic());
        }
        mAdapter = new SubscriptionRecyclerViewAdapter(list, mListener);
        mSubscriptionRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTopicMessage();
    }

    /**
     * Deal with the {@link DeleteTopicMessageEvent} and {@link MessageEvent},
     * if both are not null,choose the latest to update;
     * otherwise update the message which is not null.
     */
    private void updateTopicMessage() {
        DeleteTopicMessageEvent deleteTopicMessageEvent = EventBus.getDefault().getStickyEvent(DeleteTopicMessageEvent.class);
        MessageEvent messageEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if (deleteTopicMessageEvent != null & messageEvent != null) {
            long deleteTime = StringUtil.getTimeStamp(deleteTopicMessageEvent.getDeleteTime());
            long messageTime = StringUtil.getTimeStamp(messageEvent.getMessage().getUpdateTime());
            if (deleteTime > messageTime) {
                updateMessage(deleteTopicMessageEvent.getMessage());
            } else {
                updateMessage(messageEvent.getMessage());
            }

        } else if (deleteTopicMessageEvent != null) {
            updateMessage(deleteTopicMessageEvent.getMessage());
        } else if (messageEvent != null) {
            updateMessage(messageEvent.getMessage());
        }
    }


    public void updateSubscription(Subscription subscription){
        if (subscription != null) {
            if (!mTopicList.contains(subscription.getTopic())) {
                mAdapter.addData(subscription);
                mTopicList.add(subscription.getTopic());
            }else {
                mAdapter.updateSubscription(subscription);

            }

        }
    }


    public void updateMessage(EmqMessage emqMessage){
        if (mTopicList.contains(emqMessage.getTopic())) {
            mAdapter.updateData(emqMessage);
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        void onItemClick(int position,Subscription item);

        void onItemDelete(int position,Subscription item);

        void onItemSubscribe(int position,Subscription item);

        void onItemUnsubscribe(int position, Subscription item);
    }
}
