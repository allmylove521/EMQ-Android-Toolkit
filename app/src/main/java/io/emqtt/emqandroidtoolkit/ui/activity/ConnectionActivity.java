package io.emqtt.emqandroidtoolkit.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import butterknife.BindView;
import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.model.Connection;
import io.emqtt.emqandroidtoolkit.ui.base.ToolBarActivity;
import io.emqtt.emqandroidtoolkit.ui.widget.QoSChooseLayout;
import io.emqtt.emqandroidtoolkit.util.RealmHelper;
import io.emqtt.emqandroidtoolkit.util.TipUtil;
import io.realm.Realm;


public class ConnectionActivity extends ToolBarActivity {

    public static final String EXTRA_CONNECTION = "connection";


    @BindView(R.id.linear_layout) LinearLayout mLinearLayout;
    @BindView(R.id.host) EditText mHost;
    @BindView(R.id.port) EditText mPort;
    @BindView(R.id.client_id) EditText mClientId;
    @BindView(R.id.clean_session) Switch mCleanSession;
    @BindView(R.id.username) EditText mUsername;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.timeout) EditText mTimeout;
    @BindView(R.id.keepalive) EditText mKeepAlive;
    @BindView(R.id.lw_topic) EditText mTopic;
    @BindView(R.id.lw_payload) EditText mPayload;
    @BindView(R.id.lw_qos) QoSChooseLayout mQos;
    @BindView(R.id.lw_retained) Switch mRetained;


    private Connection mConnection;

    private boolean mIsNew = true;


    public static void openActivityForResult(Context context, int requestCode) {
        openActivityForResult(context, requestCode, null);
    }

    public static void openActivityForResult(Context context, int requestCode, Connection connection) {
        Intent intent = new Intent(context, ConnectionActivity.class);
        if (null != connection) {
            intent.putExtra(EXTRA_CONNECTION, connection);
        }
        ((Activity) context).startActivityForResult(intent, requestCode);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_connection;
    }

    @Override
    protected void setUpView() {

        if (getIntent().getParcelableExtra(EXTRA_CONNECTION) != null) {
            Connection connection = getIntent().getParcelableExtra(EXTRA_CONNECTION);
            String id = connection.getId();
            Realm realm = RealmHelper.getInstance().getRealm();
            mConnection = realm.where(Connection.class).equalTo("id", id).findFirst();
            setConnection(mConnection);
            mIsNew = false;
        } else {
            mClientId.setText(getRandomClientId());
        }

    }

    private String getRandomClientId() {
        int randNum = (int) (Math.random() * 99999);
        return "EMQ-" + randNum;
    }


    @Override
    protected void setUpData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_connection,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            completeOperation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void completeOperation(){
        if (getString(mHost).isEmpty()) {
            TipUtil.showSnackbar(mLinearLayout, "Host cannot be empty");
            return;
        }

        if (getString(mClientId).isEmpty()) {
            TipUtil.showSnackbar(mLinearLayout, "Client Id cannot be empty");
            return;
        }

        if (getString(mPort).isEmpty()) {
            TipUtil.showSnackbar(mLinearLayout, "Port cannot be empty");
            return;
        }

        String srvURI = "tcp://" + getString(mPort) + ":" + getString(mPort);
        try {
            validateURI(srvURI);
        } catch (IllegalArgumentException e) {
            TipUtil.showSnackbar(mLinearLayout, "IllegalArgumentException:" + srvURI);
            return;
        }


        if (mIsNew) {
            mConnection = new Connection();
            updateConnection(mConnection);
            RealmHelper.getInstance().addData(mConnection);
        } else {
            Realm realm = RealmHelper.getInstance().getRealm();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    updateConnection(mConnection);
                }
            });
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONNECTION, mConnection);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void setConnection(Connection connection) {
        mHost.setText(connection.getHost());
        mPort.setText(connection.getPort());
        mClientId.setText(connection.getClientId());
        mCleanSession.setChecked(connection.isCleanSession());
        mUsername.setText(connection.getUsername());
        mPassword.setText(connection.getPassword());
        mTimeout.setText(String.format(Locale.getDefault(),"%d",connection.getTimeout()));
        mKeepAlive.setText(String.format(Locale.getDefault(),"%d",connection.getKeepAlive()));
        mTopic.setText(connection.getLwtTopic());
        mPayload.setText(connection.getLwtPayload());
        mQos.setQoS(connection.getLwtQos());

    }


    private void updateConnection(Connection connection) {
        String host = getString(mHost);
        String port = getString(mPort);
        String clientId = getString(mClientId);

        boolean cleanSession = mCleanSession.isChecked();
        String username = getString(mUsername);
        String password = getString(mPassword);

        String topic = getString(mTopic);
        String payload = getString(mPayload);
        int qos = mQos.getQoS();
        boolean retained = mRetained.isChecked();

        connection.setClientInfo(host,port,clientId);

        connection.setCleanSession(cleanSession);

        connection.setUsername(username);
        connection.setPassword(password);

        if (getString(mTimeout).length() > 0) {
            connection.setTimeout(Integer.parseInt(getString(mTimeout)));
        }

        if (getString(mKeepAlive).length() > 0) {
            connection.setKeepAlive(Integer.parseInt(getString(mKeepAlive)));
        }

        connection.setWill(topic, payload, qos, retained);

    }


    private String getString(EditText editText){
        return editText.getText().toString().trim();
    }

    /**
     * Validate a URI
     */

    private boolean validateURI(String srvURI) {
        try {
            URI vURI = new URI(srvURI);
            if (vURI.getScheme().equals("ws")) {
                return true;
            } else if (vURI.getScheme().equals("wss")) {
                return true;
            }

            if (!vURI.getPath().equals("")) {
                throw new IllegalArgumentException(srvURI);
            }
            if (vURI.getScheme().equals("tcp")) {
                return true;
            } else if (vURI.getScheme().equals("ssl")) {
                return true;
            } else if (vURI.getScheme().equals("local")) {
                return true;
            } else {
                throw new IllegalArgumentException(srvURI);
            }
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(srvURI);
        }
    }

}
