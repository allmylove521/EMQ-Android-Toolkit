package io.emqtt.emqandroidtoolkit.ui.activity;

import android.content.Intent;
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
import io.emqtt.emqandroidtoolkit.ui.OnItemClickListener;
import io.emqtt.emqandroidtoolkit.ui.adapter.ConnectionAdapter;
import io.emqtt.emqandroidtoolkit.ui.base.BaseActivity;
import io.emqtt.emqandroidtoolkit.ui.widget.RecyclerViewDivider;
import io.emqtt.emqandroidtoolkit.util.RealmHelper;
import io.realm.RealmResults;


public class MainActivity extends BaseActivity {

    private static final int REQUEST_ADD = 123;

    private static final int REQUEST_EDIT = 456;


    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.connection) RecyclerView mConnectionRecyclerView;
    @BindView(R.id.fab) FloatingActionButton mFab;

    private ConnectionAdapter mConnectionAdapter;

    private int mPosition;
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

        mConnectionAdapter = new ConnectionAdapter(mConnectionResults);
        mConnectionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemEdit(int position, Object item) {
                ConnectionActivity.openActivityForResult(MainActivity.this, REQUEST_EDIT, (Connection) item);
                mPosition = position;

            }

            @Override
            public void onItemDelete(int position, Object item) {
//                RealmHelper.getInstance().delete(mConnectionResults.get(position));

            }
        });

        mConnectionRecyclerView.setAdapter(mConnectionAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Connection connection = data.getParcelableExtra(ConnectionActivity.EXTRA_CONNECTION);
            if (null != connection) {
                switch (requestCode) {
                    case REQUEST_ADD:
                        mConnectionAdapter.notifyItemInserted(mConnectionResults.size() - 1);
                        break;
                    case REQUEST_EDIT:
                        mConnectionAdapter.notifyItemChanged(mPosition);
                        break;
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        ConnectionActivity.openActivityForResult(this, REQUEST_ADD);
    }

    @Override
    protected void onDestroy() {
        RealmHelper.getInstance().deleteAll(EmqMessage.class);
//        MQTTManager.release();
        super.onDestroy();
    }

}
