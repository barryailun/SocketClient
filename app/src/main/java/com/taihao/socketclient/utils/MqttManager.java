package com.taihao.socketclient.utils;

import android.util.Log;

import com.taihao.socketclient.callback.MqttCallbackBus;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * @author chenwei
 * @date 2019/6/21
 * description：使用Eclipse Paho编写mqtt客户端
 * https://www.jianshu.com/p/70b7391a3ca1 Eclipse Paho 实现Android推送
 * https://java.ctolib.com/daoshenzzg-socket-mqtt.html
 */
public class MqttManager {

    private static MqttManager mInstance = null;

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

    private MqttManager() {
        callback = new MqttCallbackBus();
    }

    public static MqttManager getInstance() {
        if(null == mInstance) {
            synchronized (MqttManager.class) {
                if(mInstance == null) {
                    mInstance = new MqttManager();
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
            Log.e("MqttManager", "release: " + e.toString());
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
                              final String topic) {
//        String tmpDir = System.getProperty("java.io.tmpDir");
        // mqtt默认文件持久化
        MemoryPersistence datasource = new MemoryPersistence();
//        MqttDefaultFilePersistence datasource = new MqttDefaultFilePersistence(tmpDir);
        try {
            conOpt = new MqttConnectOptions();
            conOpt.setServerURIs(new String[]{brokerUrl});
            conOpt.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            // 设置清空session，false表示服务器会保留客户端记录
            conOpt.setCleanSession(false);
            // 设置会话心跳时间，单位为秒
            // 客户端每隔10秒向服务器发送心跳包判断客户端是否在线
            conOpt.setKeepAliveInterval(10);
            if(userName != null) {
                conOpt.setUserName(userName);
            }
            if (password != null) {
                conOpt.setPassword(password.toCharArray());
            }
            // 最后的遗言(最后断开时，发送"close"给订阅了该主题的用户告知连接中断)
            conOpt.setWill(topic, "close".getBytes(), 2, true);
            // 客户端是否尝试重连到数据库
            conOpt.setAutomaticReconnect(true);
            // 创建Mqtt客户端
            client = new MqttClient(brokerUrl, clientId, datasource);
            // 设置回调
            client.setCallback(new MqttCallbackBus() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i("mqtt server", String.format("message arrived. topic=%s, message=%s.", topic, new String(message.getPayload())));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.i("mqtt server", "连接成功");
                    try {
                        client.subscribe(topic,0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            doConnect();
        } catch (MqttException e) {
            Log.e("MqttManager", "createConnect: " + e.toString());
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

    /**
     * 发布消息
     * @param topicName 主题名称
     * @param qos       消息质量(0, 1, 2)
     * @param payload   发送的内容
     */
    public void publish(String topicName, int qos, byte[] payload) {
        if(client != null &&client.isConnected()) {
            MqttMessage message = new MqttMessage(payload);
            message.setPayload(payload);
            message.setQos(qos);
            try {
                client.publish(topicName, message);
            } catch (MqttException e) {
                Log.e("MqttManager", "publish: " + e.toString());
            }
        }
    }

    public void publish(String topicName, int qos, String payload) {
        if(client != null &&client.isConnected()) {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setPayload(payload.getBytes());
            message.setQos(qos);
            try {
                client.publish(topicName, message);
            } catch (MqttException e) {
                Log.e("MqttManager", "publish: " + e.toString());
            }
        }
    }

    /**
     * 订阅主题
     * @param topicName 主题名称
     * @param qos       消息质量
     */
    public void subscribe(String topicName, int qos) {
        if(client != null &&client.isConnected()) {
            try {
                client.subscribe(topicName, qos);
            } catch (MqttException e) {
                Log.e("MqttManager", "subscribe: " + e.toString());
            }
        }
    }

    private static void disConnect() throws MqttException{
        if(client != null &&client.isConnected()) {
            client.disconnect();
        }
    }

    public static boolean isConnected() {
        return client != null &&client.isConnected();
    }
}
