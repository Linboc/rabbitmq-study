package com.boc.rpc;

import com.boc.utils.RabbitMqUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.RpcServer;

import java.util.UUID;

/**
 * rpc客户端
 * @author boc
 */
public class RpcClient {

    private String requestQueueNmae = "rpc_queue";

    private String replyQueueName;

    private DefaultConsumer consumer;

    private Channel channel;

    public RpcClient () throws Exception{
        channel = RabbitMqUtil.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new DefaultConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    public String call(String message) throws Exception {
        String response = null;
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();
        channel.basicPublish("", requestQueueNmae, props, message.getBytes());

        while (true) {
        }
    }

    public static void main(String[] args) {
    }

}
