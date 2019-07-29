package com.wwj00.nancy.rabbitMQ;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Nancy on 2019/7/29 22:03
 *
 * rabbitMQ入门程序 消费者
 *
 * Work queues [工作队列模式]  即入门程序 开启多个消费者 ，多个消费端共同消费同一个队列中的消息
 * 应用场景：对于 任务过重或任务较多情况使用工作队列可以提高任务处理的速度。
 *      1、一条消息只会被一个消费者接收；
 *      2、rabbit采用轮询的方式将消息是平均发送给消费者的；
 *      3、消费者在处理完某条消息后，才会收到下一条消息。
 */
@Slf4j
public class Consumer_start {
    //队列
    private static final String QUEUE = "helloword";

    public static void main(String[] args)throws IOException, TimeoutException {

        //通过连接工厂创建连接 和 mq 建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672); //通信端口   15672外部管理端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机
        //一个mq服务可以设置多个虚拟机,每个虚拟机相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        //建立新连接
        Connection connection = connectionFactory.newConnection();
        //创建会话通道 生产者和mq服务所有的通信都在channel通道中完成
        Channel channel = connection.createChannel();

        // [首先声明队列]
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

        //实现消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            /**
             * 当收到消息后 此方法被调用
             *
             * @param consumerTag  消费者标签 用来标识消费者的,在监听队列时设置[channel.basicConsume]
             * @param envelope  信封
             * @param properties  消息属性
             * @param body   消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //消息id ,mq在channel中用来标识消息的id ,可用于确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                //消息
                String message = new String(body,"utf-8");
                log.info("receive message: {}",message);
            }
        };

        //监听队列
        /**
         * 参数: String queue, boolean autoAck, Consumer callback
         *  1).queue 对列名称
         *  2).autoAck 自动恢复当消费者收到消息后 告诉mq消息已接受;如果为true 表示自动回复mq,如果为false 通过编码手动回复
         *  3).callback 消费方法 当消费者收到消息要执行的方法
         */
        channel.basicConsume(QUEUE,true,defaultConsumer);
    }
}
