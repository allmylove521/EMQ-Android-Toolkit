package io.emqtt.emqandroidtoolkit.event;

import io.emqtt.emqandroidtoolkit.model.EmqMessage;

/**
 * Created by zhiw on 2017/4/6.
 */
public class DeleteTopicMessageEvent {

    private EmqMessage message;
    private String deleteTime;

    public DeleteTopicMessageEvent(EmqMessage message, String deleteTime) {
        this.message = message;
        this.deleteTime = deleteTime;
    }

    public EmqMessage getMessage() {
        return message;
    }

    public String getDeleteTime() {
        return deleteTime;
    }
}
