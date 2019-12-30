package com.boc.chapter1;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * 简单实现了DefaultConsumer的消费功能
 * @author boc
 */
public class SimpleConsumer extends DefaultConsumer {

    public SimpleConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("recv message:" + new String(body));
        // 确认消费完成
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }
}
