package com.boc.rpc;

import com.boc.utils.RabbitMqUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * rpc服务提供方
 * @author boc
 */
public class RpcServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtil.createChannel();
        channel.basicQos(1);
        System.out.println("正在等待 RPC 请求");
        channel.basicConsume(RPC_QUEUE_NAME, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(properties.getCorrelationId()).build();
                int n = Integer.parseInt(new String(body));
                channel.basicPublish("", properties.getReplyTo(), replyProps, String.valueOf(fib(n)).getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }

    private static int fib(int n) {
        if (n == 0 || n == 1) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }

}
