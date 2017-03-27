package io.emqtt.emqandroidtoolkit;

/**
 * ClassName: Constant
 * Desc:
 * Created by zhiw on 2017/3/23.
 */

public class Constant {

    public class ExtraConstant {

        public static final String EXTRA_CONNECTION = "connection";

        public static final String EXTRA_SUBSCRIPTION = "subscription";

        public static final String EXTRA_PUBLICATION = "publication";

        public static final String EXTRA_TOPIC = "topic";



    }

    public class MQTTStatusConstant {

        public static final int CONNECT_SUCCESS = 100;

        public static final int CONNECT_FAIL = 101;

        public static final int SUBSCRIBE_SUCCESS = 102;

        public static final int SUBSCRIBE_FAIL = 103;

        public static final int PUBLISH_SUCCESS = 104;

        public static final int PUBLISH_FAIL = 105;

        public static final int MESSAGE_ARRIVED = 106;

        public static final int CONNECTION_LOST = 107;

        public static final int DELIVERY_COMPLETE = 108;
    }

}
