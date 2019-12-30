package com.boc.chapter1;

import com.boc.utils.RabbitMqUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 惯例起点
 * @author boc
 */
public class HelloWorld {

    public static void main(String[] args) throws Exception {
        delayQueueDeclare();
        delayQueueProducer();
        delayQueueConsumer();
    }

    /**
     * 生产者
     * @throws Exception 发布消息期间的异常
     */
    public static void producer() throws Exception {
        Channel channel = RabbitMqUtil.createChannel();
        // 创建一个type=direct、持久化、非自动删除的交换器
        channel.exchangeDeclare("exchange_demo", "direct", true, false, null);
        // 创建一个持久化、非排他的、非自动删除的队列
        channel.queueDeclare("queue_demo", true, false, false, null);
        // 将交换器与队列通过路由键绑定
        channel.queueBind("queue_demo", "exchange_demo", "routing_key_demo");
        channel.addReturnListener(listener -> {
            System.out.println("被返回的消息：" + new String(listener.getBody()));
        });
        // 发送一条持久化消息
        String message = "Hello World!";
        channel.basicPublish("exchange_demo", "routing_key_demo", true
                , MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        channel.addShutdownListener(cause -> {
            System.out.println(cause.getMessage());
            System.out.println(cause.getReason());
        });
        RabbitMqUtil.closeAll();
        System.out.println("发布结束!");

    }

    public static void consumer() throws Exception {
        Channel channel = RabbitMqUtil.createChannel();
        // 客户端最多保持未被ack(确认)的消息的个数
        channel.basicQos(64);
        Consumer consumer = new SimpleConsumer(channel);
        // 开始消费,上面的消费者只是定义了处理方式，这里才真正开始消费消息
        channel.basicConsume("queue_demo", consumer);

        TimeUnit.SECONDS.sleep(5);
        RabbitMqUtil.closeAll();
        System.out.println("消费结束!");
    }

    /**
     * 延迟队列声明
     */
    public static void delayQueueDeclare() throws IOException {
        Channel channel = RabbitMqUtil.createChannel();

        // 删除原来的定义
        channel.exchangeDelete("delayExchange");
        channel.exchangeDelete("delayDLX");
        channel.exchangeDelete("delayDLX");
        channel.queueDelete("delayQueue_5000");
        channel.queueDelete("delayQueue_10000");
        channel.queueDelete("delayDLQueue");
        // 延迟交换器
        channel.exchangeDeclare("delayExchange", BuiltinExchangeType.DIRECT, true, false, false, null);
        // 死信交换器
        channel.exchangeDeclare("delayDLX", BuiltinExchangeType.DIRECT, true, false, false, null);

        // 5000ms延迟队列
        Map<String, Object> queueArgs = new HashMap<>(2, 1F);
        // 指定死信交换器
        queueArgs.put("x-dead-letter-exchange", "delayDLX");
        // 消息过期时间
        queueArgs.put("x-message-ttl", 5000);
        // 死信交换器路由键
        queueArgs.put("x-dead-letter-routing-key", "delayMessage");
        channel.queueDeclare("delayQueue_5000", true, false, false, queueArgs);

        // 10000ms延迟队列
        queueArgs.put("x-message-ttl", 10000);
        channel.queueDeclare("delayQueue_10000", true, false, false, queueArgs);

        // 20000ms延迟队列
        queueArgs.put("x-message-ttl", 20000);
        channel.queueDeclare("delayQueue_20000", true, false, false, queueArgs);

        // 死信交换器接受消息的队列
        channel.queueDeclare("delayDLQueue", true, false, false, null);

        // 绑定交换器
        channel.queueBind("delayQueue_5000", "delayExchange", "delayMessage_5000");
        channel.queueBind("delayQueue_10000", "delayExchange", "delayMessage_10000");
        channel.queueBind("delayQueue_20000", "delayExchange", "delayMessage_20000");
        channel.queueBind("delayDLQueue", "delayDLX", "delayMessage");
    }

    /**
     * 延迟队列消费者
     */
    public static void delayQueueConsumer() throws Exception {
        System.out.println("consumer start：");
        Channel channel = RabbitMqUtil.createChannel();
        channel.basicQos(32);
        channel.basicConsume("delayDLQueue", new SimpleConsumer(channel));
    }

    /**
     * 延迟队列生产者
     */
    public static void delayQueueProducer() throws Exception {
        Channel channel = RabbitMqUtil.createChannel();
        channel.basicPublish("delayExchange", "delayMessage_5000", MessageProperties.PERSISTENT_TEXT_PLAIN, "5000ms消息1".getBytes());
        channel.basicPublish("delayExchange", "delayMessage_10000", MessageProperties.PERSISTENT_TEXT_PLAIN, "10000ms消息1".getBytes());
        channel.basicPublish("delayExchange", "delayMessage_20000", MessageProperties.PERSISTENT_TEXT_PLAIN, "20000ms消息1".getBytes());
        System.out.println("发布成功!");
    }

}





















