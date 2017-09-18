# EMQ-Android-Toolkit
A Toolkit to show how to use MQTT with Android
## Getting started

The first step is to include the library into your project via Gradle

```
compile 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
compile 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
```

The second step is to register `MqttService` in the `AndroidManifest.xml`

```xml
<application>

	<service android:name="org.eclipse.paho.android.service.MqttService">
    </service>

</application>
```

Create your client
```java
MqttAndroid client = MQTTManager.getInstance().createClient(id, serverURI, ClientId);
```
Connect
```java
MQTTManager.getInstance().connect(client, options);
```
Your can config your `MqttConnectOptions`:

- `setPassword`
- `setUserName`
- `setWill`
- `setKeepAliveInterval`
- `setMaxInflight`
- `setConnectionTimeout`
- `setMqttVersion`
- `setAutomaticReconnect`

For more information,you can see them in the `MqttConnectOptions`

Now you can subscribe/unsubscirbe/publish
```java
MQTTManager.getInstance().subscribe(client, topic, qos);

MQTTManager.getInstance().unsubscribe(client, topic);

MQTTManager.getInstance().publish(client, mqttMessage);

```

**Notic**

In this project,we use [EventBus](https://github.com/greenrobot/EventBus) to pass the Message

Process MQTTAction
```java
	@Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MQTTActionEvent event){

		IMqttToken token = event.getAsyncActionToken();
		// Only when the onFailure method invoke
		Thrwable exception = event.getException();
        switch (event.getStatus()) {
            case Constant.MQTTStatusConstant.CONNECT_SUCCESS:
                break;

            case Constant.MQTTStatusConstant.CONNECT_FAIL:
                break;

            case Constant.MQTTStatusConstant.SUBSCRIBE_SUCCESS:
                break;

            case Constant.MQTTStatusConstant.SUBSCRIBE_FAIL:
                break;

            case Constant.MQTTStatusConstant.UNSUBSCRIBE_SUCCESS:
                break;

            case Constant.MQTTStatusConstant.UNSUBSCRIBE_FAIL:
                break;

            case Constant.MQTTStatusConstant.PUBLISH_SUCCESS:
                break;

            case Constant.MQTTStatusConstant.PUBLISH_FAIL:
                break;

            case Constant.MQTTStatusConstant.CONNECTION_LOST:
                break;
            default:
                break;

        }

    }
```

Process Messgae
```java
	@Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
    	EmqMessage message = event.getMessage();
    }
```
You can have your own implementation if you don't want to use `EventBus`.

Disconnect the client

```java
MQTTManager.getInstance().disconnect(client);
```

For more infomation,you can see [MQTTManager]("https://github.com/emqtt/EMQ-Android-Toolkit/blob/master/app/src/main/java/io/emqtt/emqandroidtoolkit/net/MQTTManager.java")
