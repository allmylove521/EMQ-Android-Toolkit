package io.emqtt.emqandroidtoolkit.ui.activity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import io.emqtt.emqandroidtoolkit.Constant;
import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.event.DeleteTopicMessageEvent;
import io.emqtt.emqandroidtoolkit.event.MessageEvent;
import io.emqtt.emqandroidtoolkit.model.EmqMessage;
import io.emqtt.emqandroidtoolkit.model.Subscription;
import io.emqtt.emqandroidtoolkit.ui.adapter.MessageAdapter;
import io.emqtt.emqandroidtoolkit.ui.base.ToolBarActivity;
import io.emqtt.emqandroidtoolkit.ui.widget.RecyclerViewDivider;
import io.emqtt.emqandroidtoolkit.util.RealmHelper;
import io.emqtt.emqandroidtoolkit.util.StringUtil;
import io.realm.RealmResults;

public class MessageListActivity extends ToolBarActivity {

    @BindView(R.id.message_list) RecyclerView mMessageRecyclerView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;

    private MessageAdapter mAdapter;

    private Subscription mSubscription;

    private boolean mIsDelete;
    private String mDeleteTime;


    public static void openActivity(Context context, Subscription subscription){
        Intent intent = new Intent(context, MessageListActivity.class);
        intent.putExtra(Constant.ExtraConstant.EXTRA_SUBSCRIPTION, subscription);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void setUpView() {
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecyclerView.addItemDecoration(new RecyclerViewDivider(this));
        mSubscription = getIntent().getParcelableExtra(Constant.ExtraConstant.EXTRA_SUBSCRIPTION);
        setTitle(mSubscription.getTopic());


    }

    @Override
    protected void setUpData() {
        RealmResults<EmqMessage> list = RealmHelper.getInstance().queryTopicMessage(EmqMessage.class, mSubscription.getTopic());
        mAdapter = new MessageAdapter(list);
        mMessageRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsDelete) {
            EventBus.getDefault().postSticky(new DeleteTopicMessageEvent(new EmqMessage(mSubscription.getTopic(), null), mDeleteTime));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_list,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            RealmHelper.getInstance().deleteTopicMessage(EmqMessage.class, mSubscription.getTopic());
            mAdapter.deleteAll();
            mIsDelete = true;
            mDeleteTime = StringUtil.formatNow();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
        mAdapter.insertData(event.getMessage());
        RealmHelper.getInstance().addData(event.getMessage());
        mMessageRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }
}
