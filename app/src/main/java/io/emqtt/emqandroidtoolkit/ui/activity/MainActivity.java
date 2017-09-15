package io.emqtt.emqandroidtoolkit.ui.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.OnClick;
import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.model.Connection;
import io.emqtt.emqandroidtoolkit.model.EmqMessage;
import io.emqtt.emqandroidtoolkit.ui.adapter.ConnectionAdapter;
import io.emqtt.emqandroidtoolkit.ui.base.BaseActivity;
import io.emqtt.emqandroidtoolkit.ui.widget.RecyclerViewDivider;
import io.emqtt.emqandroidtoolkit.util.RealmHelper;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;


public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.connection) RecyclerView mConnectionRecyclerView;
    @BindView(R.id.fab) FloatingActionButton mFab;

    private ConnectionAdapter mConnectionAdapter;

    private RealmResults<Connection> mConnectionResults;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setUpView() {
        setSupportActionBar(mToolbar);
        mConnectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mConnectionRecyclerView.addItemDecoration(new RecyclerViewDivider(this));
        mConnectionRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    protected void setUpData() {

        mConnectionResults = RealmHelper.getInstance().queryAll(Connection.class);

        mConnectionResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Connection>>() {
            @Override
            public void onChange(RealmResults<Connection> collection, OrderedCollectionChangeSet changeSet) {
                if (changeSet == null) {
                    mConnectionAdapter.notifyDataSetChanged();
                    return;
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    mConnectionAdapter.notifyItemInserted(range.startIndex);
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    mConnectionAdapter.notifyItemChanged(range.startIndex);
                }


            }
        });

        mConnectionAdapter = new ConnectionAdapter(mConnectionResults);

        mConnectionRecyclerView.setAdapter(mConnectionAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.fab)
    public void onClick() {
        ConnectionActivity.openActivity(this);
    }

    @Override
    protected void onDestroy() {
        RealmHelper.getInstance().deleteAll(EmqMessage.class);
        super.onDestroy();
    }

}
