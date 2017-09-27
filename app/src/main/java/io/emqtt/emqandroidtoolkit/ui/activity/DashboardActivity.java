package io.emqtt.emqandroidtoolkit.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.emqtt.emqandroidtoolkit.Constant;
import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.event.MQTTActionEvent;
import io.emqtt.emqandroidtoolkit.event.MessageEvent;
import io.emqtt.emqandroidtoolkit.model.Connection;
import io.emqtt.emqandroidtoolkit.model.Publication;
import io.emqtt.emqandroidtoolkit.model.Subscription;
import io.emqtt.emqandroidtoolkit.net.MQTTManager;
import io.emqtt.emqandroidtoolkit.ui.adapter.ConnectionViewPagerAdapter;
import io.emqtt.emqandroidtoolkit.ui.base.BaseActivity;
import io.emqtt.emqandroidtoolkit.ui.fragment.PublicationListFragment;
import io.emqtt.emqandroidtoolkit.ui.fragment.SubscriptionFragment;
import io.emqtt.emqandroidtoolkit.ui.fragment.SubscriptionListFragment;
import io.emqtt.emqandroidtoolkit.util.RealmHelper;
import io.emqtt.emqandroidtoolkit.util.TipUtil;
import io.realm.Realm;
import io.realm.RealmResults;

public class DashboardActivity extends BaseActivity implements SubscriptionListFragment.OnListFragmentInteractionListener ,SubscriptionFragment.OnAddSubscriptionListener{

    private static final int SUBSCRIPTION = 63;

    private static final int PUBLICATION = 64;

    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.viewpager) ViewPager mViewpager;

    private Connection mConnection;
    private Subscription mSubscription;
    private Publication mPublication;

    private MQTTManager mMQTTManager;
    private MqttAndroidClient mClient;
    private ConnectionViewPagerAdapter mAdapter;

    private SubscriptionListFragment mSubscriptionListFragment;
    private PublicationListFragment mPublicationListFragment;

    private RealmResults<Subscription> mSubscriptionResults;
    private List<Subscription> mSubscriptionList;

    private int mCurrentMode = SUBSCRIPTION;

    private boolean mIsDelete;


    public static void openActivity(Context context, Connection connection) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(Constant.ExtraConstant.EXTRA_CONNECTION, connection);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_dashboard;
    }

    @Override
    protected void setUpView() {
        mConnection = getIntent().getParcelableExtra(Constant.ExtraConstant.EXTRA_CONNECTION);

        mToolbar.setTitle(mConnection.getServerURI());
        setSupportActionBar(mToolbar);
        setSubtitle(getString(R.string.connecting) );
    }


    @Override
    protected void setUpData() {

        EventBus.getDefault().register(this);

        getSubscription();

        setUpTabLayout();

        initClient();

    }




    @Override
    public void onItemClick(int position, Subscription item) {
        MessageListActivity.openActivity(this, item);

    }

    @Override
    public void onItemDelete(int position, Subscription item) {
        mIsDelete = true;
        unsubscribe(item);
        RealmHelper.getInstance().delete(mSubscriptionResults.get(position));
    }

    @Override
    public void onItemSubscribe(int position, Subscription item) {
        subscribe(item);
    }

    @Override
    public void onItemUnsubscribe(int position, Subscription item) {
        mIsDelete = false;
        unsubscribe(item);

    }

    @Override
    public void onAddSubscription(Subscription subscription) {
        try {
            MqttTopic.validate(subscription.getTopic(),true);
        }catch (IllegalArgumentException e){
            TipUtil.showSnackbar(mCoordinatorLayout,e.getMessage());
            return;
        }
        subscription.setConnectionId(mConnection.getId());
        mSubscription = subscription;
        RealmHelper.getInstance().addSubscription(mSubscription);
        subscribe(subscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        if (resultCode == RESULT_OK) {
//            if (requestCode == SUBSCRIPTION) {
//                Subscription subscription = data.getParcelableExtra(Constant.ExtraConstant.EXTRA_SUBSCRIPTION);
//                subscription.setConnectionId(mConnection.getId());
//                mSubscription = subscription;
//                RealmHelper.getInstance().addSubscription(mSubscription);
//                subscribe(subscription);
//
//            }
            if (requestCode == PUBLICATION) {
                Publication publication = data.getParcelableExtra(Constant.ExtraConstant.EXTRA_PUBLICATION);
                mPublication = publication;
                publish(publication);

            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connection_detail,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isConnected()){
            menu.findItem(R.id.action_disconnect).setVisible(true);
            menu.findItem(R.id.action_connect).setVisible(false);
        }else {
            menu.findItem(R.id.action_disconnect).setVisible(false);
            menu.findItem(R.id.action_connect).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_connect) {
            connect();
            return true;
        }
        if (id == R.id.action_disconnect) {
            disconnect();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.fab)
    public void onClick() {
        if (!isConnected()){
            TipUtil.showSnackbar(mCoordinatorLayout, getString(R.string.hint_disconnect), getString(R.string.connect), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connect();
                }
            });
            return;
        }

        if (mCurrentMode == SUBSCRIPTION) {
//            startActivityForResult(SubscriptionActivity.class, SUBSCRIPTION);
            SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
            subscriptionFragment.show(getSupportFragmentManager(), "Subscription");
        } else if (mCurrentMode == PUBLICATION) {
            startActivityForResult(PublicationActivity.class, PUBLICATION);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MQTTActionEvent event){

        switch (event.getStatus()) {
            case Constant.MQTTStatusConstant.CONNECT_SUCCESS:
                setSubtitle(getString(R.string.connected));
                invalidateOptionsMenu();

                for (Subscription subscription : mSubscriptionList) {
                    subscribe(subscription);
                }
                break;

            case Constant.MQTTStatusConstant.CONNECT_FAIL:
                TipUtil.showSnackbar(mCoordinatorLayout, event.getException().getMessage());
                setSubtitle(getString(R.string.connect_fail));
                break;

            case Constant.MQTTStatusConstant.SUBSCRIBE_SUCCESS:
                TipUtil.showSnackbar(mCoordinatorLayout, getString(R.string.subscribe_success));
                RealmHelper.getInstance().getRealm()
                        .executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                mSubscription.setSubscribed(true);
                            }
                        });
                mSubscriptionListFragment.updateSubscription(mSubscription);

                break;

            case Constant.MQTTStatusConstant.SUBSCRIBE_FAIL:
                TipUtil.showSnackbar(mCoordinatorLayout,getString(R.string.subscribe_fail));
                break;

            case Constant.MQTTStatusConstant.UNSUBSCRIBE_SUCCESS:
                if (!mIsDelete){
                    TipUtil.showSnackbar(mCoordinatorLayout, getString(R.string.unsubscurbe_success));
                    RealmHelper.getInstance().getRealm()
                            .executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    mSubscription.setSubscribed(false);
                                }
                            });
                    mSubscriptionListFragment.updateSubscription(mSubscription);
                }

                break;

            case Constant.MQTTStatusConstant.UNSUBSCRIBE_FAIL:
                TipUtil.showSnackbar(mCoordinatorLayout,getString(R.string.unsubscirbe_fail));
                break;

            case Constant.MQTTStatusConstant.PUBLISH_SUCCESS:
                mPublicationListFragment.insertData(mPublication);
                break;

            case Constant.MQTTStatusConstant.PUBLISH_FAIL:
                TipUtil.showSnackbar(mCoordinatorLayout,getString(R.string.publish_fail));
                break;

            case Constant.MQTTStatusConstant.CONNECTION_LOST:
                setSubtitle(getString(R.string.disconnect));
                invalidateOptionsMenu();
                break;
            default:
                break;

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
        mSubscriptionListFragment.updateMessage(event.getMessage());
        updateView(true);
        RealmHelper.getInstance().addData(event.getMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


    }



    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeAllStickyEvents();
    }

    private void setSubtitle(String subtitle) {
        if (mToolbar != null) {
            mToolbar.setSubtitle(subtitle);
        }
    }


    private void getSubscription() {
        mSubscriptionList = new ArrayList<>();
        Realm realm = RealmHelper.getInstance().getRealm();
        realm.beginTransaction();
        mSubscriptionResults = realm.where(Subscription.class).equalTo("connectionId", mConnection.getId()).findAll();
        realm.commitTransaction();

        mSubscriptionList.addAll(mSubscriptionResults);


    }


    private void setUpTabLayout() {
        mSubscriptionListFragment = SubscriptionListFragment.newInstance();
        mPublicationListFragment = PublicationListFragment.newInstance();

        mAdapter = new ConnectionViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(mSubscriptionListFragment, SubscriptionListFragment.TAG);
        mAdapter.addFragment(mPublicationListFragment, PublicationListFragment.TAG);

        mViewpager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewpager);

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mAdapter.getItem(position) instanceof SubscriptionListFragment) {
                    mCurrentMode = SUBSCRIPTION;
                    updateView(false);
                } else if (mAdapter.getItem(position) instanceof PublicationListFragment) {
                    mCurrentMode = PUBLICATION;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab != null) {
            tab.setCustomView(getView());
        }


    }

    /**
     * Get custom tab view
     * @return custom tab view
     */
    private View getView() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(SubscriptionListFragment.TAG);
        return view;
    }

    /**
     * update tab view with message status
     * @param isShow isShow
     */
    private void updateView(boolean isShow) {
        View view = mTabLayout.getTabAt(0).getCustomView();
        ImageView imageView = (ImageView) view.findViewById(R.id.status);
        if (mCurrentMode != SUBSCRIPTION && isShow) {
            imageView.setVisibility(View.VISIBLE);
        }else if (!isShow){
            imageView.setVisibility(View.INVISIBLE);
        }


    }

    private void initClient() {
        mMQTTManager = MQTTManager.getInstance();
        mClient = mMQTTManager.getClient(mConnection.getId());
        if (mClient == null) {
            mClient = mMQTTManager.createClient(mConnection.getId(), mConnection.getServerURI(), mConnection.getClientId());
        }
        if (mMQTTManager.isConnected(mClient)) {
            setSubtitle(getString(R.string.connected));
        } else {
            connect();
        }

    }


    private void connect() {
        setSubtitle(getString(R.string.connecting));
        mMQTTManager.connect(mClient, mConnection.getMqttConnectOptions());
    }

    private void subscribe(Subscription subscription) {
        mSubscription = subscription;
        mMQTTManager.subscribe(mClient,subscription.getTopic(), subscription.getQoS());
    }

    private void unsubscribe(Subscription subscription) {
        mSubscription = subscription;
        mMQTTManager.unsubscribe(mClient, subscription.getTopic());
    }

    private void publish(Publication publication){
        mMQTTManager.publish(mClient,publication.getTopic(),publication.getMessage());
    }

    private void disconnect(){
        if (mMQTTManager.disconnect(mClient)){
            finish();
        }
    }


    private boolean isConnected() {
        return mMQTTManager.isConnected(mClient);
    }

    public List<Subscription> getSubscriptionList() {
        return mSubscriptionList;
    }


}
