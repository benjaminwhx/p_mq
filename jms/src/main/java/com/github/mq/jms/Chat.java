package com.github.mq.jms;

import javax.jms.*;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-12-01
 * Time: 8:50 pm
 */
public class Chat implements MessageListener {
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;

    public Chat(String topicFactory, String topicName, String username) throws Exception {
        InitialContext ctx = new InitialContext();

        // 查找一个JMS连接工厂并创建连接
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) ctx.lookup(topicFactory);
        this.connection = topicConnectionFactory.createTopicConnection();

        // 创建两个JMS会话对象
        this.pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        // 查找一个JMS主题
        Topic chatTopic = (Topic) ctx.lookup(topicName);
        this.publisher = pubSession.createPublisher(chatTopic);
        TopicSubscriber subscriber = subSession.createSubscriber(chatTopic, null, true);
        subscriber.setMessageListener(this);

        this.username = username;
        connection.start();
    }

    /**
     * 接收来自TopicSubscriber的消息
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            System.out.println(textMessage.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void writeMessage(String text) throws JMSException {
        TextMessage textMessage = pubSession.createTextMessage();
        textMessage.setText(username + ": " + text);
        publisher.publish(textMessage);
    }

    /**
     * 关闭connection
     */
    public void close() throws JMSException {
        connection.close();
    }

    public static void main(String[] args) {
        try {
            Chat chat = new Chat(args[0], args[1], args[2]);
            if (args.length != 3) {
                System.out.println("Factory,Topic,or username missing");
            }

            BufferedReader commandReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = commandReader.readLine();
                if ("exit".equalsIgnoreCase(line)) {
                    chat.close();
                    System.exit(0);
                } else {
                    chat.writeMessage(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
