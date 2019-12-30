package com.boc.utils;

import com.boc.constants.CommonConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * 跟rabbitmq相关的工具类
 * @author boc
 */
public class RabbitMqUtil {

    private static Connection connection;
    private static ConnectionFactory connectionFactory;

    /**
     * 创建一个新的通道
     * @return 通道
     */
    public static Channel createChannel() {
        try {
            return getConnection().createChannel();
        } catch (IOException e) {
            throw new RuntimeException("通道创建失败!");
        }
    }

    /**
     * 获取一个连接
     * @return 一个连接
     */
    public static Connection getConnection() {
        return getConnection(false);
    }

    /**
     * 获取一个连接
     * @param refresh 是否要用新的连接
     * @return 一个连接
     */
    public static Connection getConnection(boolean refresh) {
        if (RabbitMqUtil.connection == null || refresh) {
            try {
                // 如果有旧的连接就先关闭
                if (RabbitMqUtil.connection != null) {
                    RabbitMqUtil.connection.close();
                }
                // 创建一个连接
                RabbitMqUtil.connection = getConnectionFactory().newConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return RabbitMqUtil.connection;
    }

    /**
     * 获取一个连接工厂
     * @return 连接工厂
     */
    private static ConnectionFactory getConnectionFactory() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        if (RabbitMqUtil.connectionFactory == null) {
            // 创建一个连接工厂
            RabbitMqUtil.connectionFactory = new ConnectionFactory();
            RabbitMqUtil.connectionFactory.setHost(CommonConstant.IP_ADDRESS);
            RabbitMqUtil.connectionFactory.setPort(CommonConstant.PORT);
            RabbitMqUtil.connectionFactory.setUsername(CommonConstant.USERNAME);
            RabbitMqUtil.connectionFactory.setPassword(CommonConstant.PASSWORD);
            // 也能用uri的方式来创建连接工厂：
            // RabbitMqUtil.connectionFactory.setUri("amqp://username:password@ipAddress:port/virtualHost");
        }
        return RabbitMqUtil.connectionFactory;
    }

    /**
     * 关闭所有资源
     */
    public static void closeAll() {
        try {
            if (RabbitMqUtil.connection != null) {
                RabbitMqUtil.connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
