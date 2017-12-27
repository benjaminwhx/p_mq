package com.github.mq.example.simple;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-12-05
 * Time: 0:43 pm
 *
 * 同步发送
 */
public class SyncProducer {

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int i = 0; i < 10; ++i) {
            Message message = new Message("topic2", "tag1",
                    "Hello World".getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送消息到其中一个broker中
            SendResult sendResult = producer.send(message);
            System.out.println("sendResult: " + sendResult);
        }

        // producer一旦不再需要就把它关闭
        producer.shutdown();
    }
}
