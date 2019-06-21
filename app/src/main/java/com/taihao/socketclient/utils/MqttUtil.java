package com.taihao.socketclient.utils;

import android.util.Log;

import com.taihao.socketclient.callback.MqttCallbackBus;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

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
    private MqttCallback callback;
    /**
     * mqtt客户端
     */
    private static MqttClient client;
    /**
     * mqtt连接选项
     */
    private MqttConnectOptions conOpt;

    private MqttUtil() {
        callback = new MqttCallbackBus();
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
        String tmpDir = System.getProperty("java.io.tmpDir");
        // mqtt默认文件持久化
        MqttDefaultFilePersistence datasource = new MqttDefaultFilePersistence(tmpDir);
        try {
            conOpt = new MqttConnectOptions();
            conOpt.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            // 设置清空session，false表示服务器会保留客户端记录
            conOpt.setCleanSession(false);
            if(userName != null) {
                conOpt.setUserName(userName);
            }
            if (password != null) {
                conOpt.setPassword(password.toCharArray());
            }
            // 客户端是否尝试重连到数据库
            conOpt.setAutomaticReconnect(true);
            client = new MqttClient(brokerUrl, clientId, datasource);
            client.setCallback(callback);
            doConnect();
        } catch (MqttException e) {
            Log.e("MqttUtil", "createConnect: " + e.toString());
        }
    }

    /**
     * 建立连接
     */
    private void doConnect() {
        if (client != null) {
            try {
                client.connect(conOpt);
            } catch (Exception e) {
                Log.e("MqttManager", "doConnect : " + e.toString());
            }
        }
    }

    private static void disConnect() throws MqttException{
        if(client != null &&client.isConnected())
            client.disconnect();
    }

    public static boolean isConnected() {
        return client != null &&client.isConnected();
    }
}
