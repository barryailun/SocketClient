package com.taihao.socketclient.callback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author chenwei
 * @date 2019/6/21
 * description：
 */
public class MqttCallbackBus implements MqttCallbackExtended {
    /**
     * 连接中断
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause) {

    }

    /**
     * 消息送达
     * @param topic
     * @param message
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    /**
     * 交互完成
     * @param token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    /**
     * 连接成功，需要上传客户端所有的订阅关系
     * @param reconnect
     * @param serverURI
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

    }
}
