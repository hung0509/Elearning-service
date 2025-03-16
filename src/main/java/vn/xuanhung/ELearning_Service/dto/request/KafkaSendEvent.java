package vn.xuanhung.ELearning_Service.dto.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class KafkaSendEvent implements Serializable {
    private final String topic;
    private final Object data;

    public KafkaSendEvent(String topic, Object data) {
        this.topic = topic;
        this.data = data;
    }
}
