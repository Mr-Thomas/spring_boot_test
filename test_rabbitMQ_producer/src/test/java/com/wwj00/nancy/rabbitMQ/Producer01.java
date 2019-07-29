package com.wwj00.nancy.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Nancy on 2019/7/29 18:21
 *
 * rabbitMQ入门程序
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Producer01 {

    @Test
    public void StartRabbitMQ(){
        //通过连接工厂创建连接 和 mq 建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672); //通信端口   15672外部管理端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机
        //一个mq服务可以设置多个虚拟机,每个虚拟机相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        try {
            //建立新连接
            connection=connectionFactory.newConnection();
            //创建会话通道 生产者和mq服务所有的通信都在channel通道中完成
            Channel channel = connection.createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
