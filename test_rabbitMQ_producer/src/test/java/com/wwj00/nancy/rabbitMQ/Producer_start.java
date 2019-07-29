package com.wwj00.nancy.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * Created by Nancy on 2019/7/29 18:21
 * <p>
 * rabbitMQ入门程序 生产者
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Producer_start {

    //队列
    private static final String QUEUE = "helloword";

    @Test
    public void StartRabbitMQ() {
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
        Channel channel = null;
        try {
            //建立新连接
            connection = connectionFactory.newConnection();
            //创建会话通道 生产者和mq服务所有的通信都在channel通道中完成
            channel = connection.createChannel();

            //声明队列:如果队列在mq中没有 则要创建
            /**
             * 参数: String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             *  1).queue 队列名称
             *  2).durable 是否持久化 如果持久化 mq重启后 队列还在
             *  3).exclusive 是否独占连接,对列只允许在该连接中访问,如果connection连接关闭 则队列自动删除,如果为true 可用于创建临时队列
             *  4).autoDelete 自动删除 队列不再使用时是否自动删除队列,如果此参数和exclusive都设置为true就可以实现临时队列(队列不用就自动删除)
             *  5).arguments 参数 可以设置一个队列的扩展参数.eg:可设置存活时间
             */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //发送消息
            /**
             * 参数: String exchange, String routingKey, BasicProperties props, byte[] body
             *  1).exchange 交换机 如果不指定 则使用mq的默认交换机(设置为"")
             *  2).routingKey 路由key,交换机根据路由key来将消息转发到指定的队列, 如果使用默认交换机,routingKey设置为队列名称
             *  3).props 消息属性
             *  4).消息内容
             */
            channel.basicPublish("",QUEUE,null,"hello world".getBytes());
            log.info("send to mq: {}","hello world");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //先关通道
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            //关闭连接
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
