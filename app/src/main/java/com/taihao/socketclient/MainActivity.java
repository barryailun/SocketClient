package com.taihao.socketclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.taihao.socketclient.callback.MqttCallbackBus;
import com.taihao.socketclient.utils.MqttManager;
import com.taihao.socketclient.utils.NettyUtil;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private String brokerUrl = "tcp://192.168.3.238:8000";
    private String clientId = "88888888";
    private String topic = "yb/notice/";
    private String mqttUsername = "admin";
    private String mqttPwd = "123456";


    private MqttCallbackBus mqttCallbackBus = new MqttCallbackBus(){
        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText msg = (EditText) findViewById(R.id.msg);
        Button sendSocketMsg = (Button) findViewById(R.id.send_socket_msg);
        sendSocketMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NettyUtil.start("192.168.3.238", 9999, msg.getText().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Button sendMqttMsg = (Button) findViewById(R.id.send_mqtt_msg);
        sendMqttMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MqttManager.getInstance()
                        .createConnect(
                                brokerUrl,
                                mqttUsername,
                                mqttPwd,
                                clientId,
                                topic);
                for (int i = 0; i < 3; i++) {
                    try {
                        String content = "hello world!" + i;
                        Log.i("mqtt server", "public message " + content);
                        /**
                         *消息发送到某个主题 Topic，所有订阅这个 Topic 的设备都能收到这个消息。
                         * 遵循 MQTT 的发布订阅规范，Topic 也可以是多级 Topic。此处设置了发送到二级 Topic
                         */
                        MqttManager.getInstance().publish(topic, 0, content);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
