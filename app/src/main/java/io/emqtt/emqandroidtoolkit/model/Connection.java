package io.emqtt.emqandroidtoolkit.model;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * ClassName: Connection
 * Desc:
 * Created by zhiw on 2017/3/22.
 */

public class Connection extends RealmObject implements Parcelable {

    private String host;

    private String port;

    private String clientId;

    private boolean cleanSession;

    private String username;

    private String password;

    private String id;

    private int timeout = MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT;

    private int keepAlive = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;

    private String lwtTopic;

    private String lwtPayload;

    @QoSConstant.QoS
    private int lwtQos;

    private boolean lwtRetained;


    public Connection() {
    }

    public Connection(String host, String port, String clientId, boolean cleanSession) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.cleanSession = cleanSession;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientInfo(String host, String port, String clientId) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.id = getServerURI() + clientId;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void generateId() {
        this.id = getServerURI() + clientId;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getLwtTopic() {
        return lwtTopic;
    }

    public String getLwtPayload() {
        return lwtPayload;
    }

    public int getLwtQos() {
        return lwtQos;
    }


    public boolean isLwtRetained() {
        return lwtRetained;
    }


    public void setWill(String lwtTopic, String lwtPayload, int lwtQos, boolean lwtRetained) {
        this.lwtTopic = lwtTopic;
        this.lwtPayload = lwtPayload;
        this.lwtQos = lwtQos;
        this.lwtRetained = lwtRetained;
    }


    public String getServerURI() {
        return "tcp://" + host + ":" + port;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(isCleanSession());
        options.setConnectionTimeout(getTimeout());
        options.setKeepAliveInterval(getKeepAlive());

        if (!getUsername().isEmpty()) {
            options.setUserName(getUsername());
        }

        if (!getPassword().isEmpty()) {
            options.setPassword(getPassword().toCharArray());
        }

        if (!getLwtTopic().isEmpty() && !getLwtPayload().isEmpty()) {
            options.setWill(getLwtTopic(), getLwtPayload().getBytes(), getLwtQos(), isLwtRetained());
        }

        return options;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.host);
        dest.writeString(this.port);
        dest.writeString(this.clientId);
        dest.writeByte(this.cleanSession ? (byte) 1 : (byte) 0);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.id);
        dest.writeInt(this.timeout);
        dest.writeInt(this.keepAlive);
        dest.writeString(this.lwtTopic);
        dest.writeString(this.lwtPayload);
        dest.writeInt(this.lwtQos);
        dest.writeByte(this.lwtRetained ? (byte) 1 : (byte) 0);
    }

    protected Connection(Parcel in) {
        this.host = in.readString();
        this.port = in.readString();
        this.clientId = in.readString();
        this.cleanSession = in.readByte() != 0;
        this.username = in.readString();
        this.password = in.readString();
        this.id = in.readString();
        this.timeout = in.readInt();
        this.keepAlive = in.readInt();
        this.lwtTopic = in.readString();
        this.lwtPayload = in.readString();
        this.lwtQos = in.readInt();
        this.lwtRetained = in.readByte() != 0;
    }

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {
        @Override
        public Connection createFromParcel(Parcel source) {
            return new Connection(source);
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };
}
