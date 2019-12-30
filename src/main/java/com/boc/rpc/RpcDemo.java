package com.boc.rpc;

import com.boc.utils.RabbitMqUtil;
import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.RpcClientParams;

import java.io.IOException;

/**
 * mq版prc测试，由于某些类因为版本原因用不了，因此用rabbitmq提供的RpcClient和RpcServer
 * @author boc
 */
public class RpcDemo {

    public static void main(String[] args) throws Exception {
        RpcClientParams params = new RpcClientParams();
        params.channel(RabbitMqUtil.createChannel());
        RpcClient client = new RpcClient(params);
        String message = client.stringCall("hello");
    }

}
