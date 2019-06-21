package com.taihao.socketclient.utils;

import android.util.Log;

import com.taihao.socketclient.callback.MqttCallbackBus;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * @author chenwei
 * @date 2019/6/21
 * description：使用Eclipse Paho编写mqtt客户端
 * https://www.jianshu.com/p/70b7391a3ca1 Eclipse Paho 实现Android推送
 * https://java.ctolib.com/daoshenzzg-socket-mqtt.html
 */
public class MqttUtil {

    private static MqttUtil mInstance = null;

    /**
     * mqtt回调
     */
    private MqttCallback mqttCallback;
    /**
     * mqtt客户端
     */
    private MqttClient mqttClient;
    /**
     * mqtt连接选项
     */
    private MqttConnectOptions conOpt;

    private MqttUtil() {
        mqttCallback = new MqttCallbackBus();
    }

    public static MqttUtil getInstance() {
        if(null == mInstance) {
            synchronized (MqttUtil.class) {
                if(mInstance == null) {
                    mInstance = new MqttUtil();
                }
            }
        }
        return mInstance;
    }

    public static void release() {
        try {
            if(mInstance != null) {
                disConnect();
                mInstance = null;
            }
        } catch (Exception e) {
            Log.e("MqttUtil", "release: " + e.toString());
        }
    }

    /**
     * 创建mqtt连接
     * @param brokerUrl Mqtt服务器地址(tcp://xxxx:1863)
     * @param userName
     * @param password
     * @param clientId 客户端id
     * @param topic
     */
    public void createConnect(String brokerUrl, String userName,
                              String password, String clientId,
                              String topic) {

    }

    private static void disConnect() {

    }
}
