package com.wwj00.nancy.rabbitMQ;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Nancy on 2019/7/30 14:19
 * 路由模式[生产者]
 */
@Slf4j
public class Producer_routing {
    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    //routingKey
    private static final String ROUTINGKEY_EMAIL = "inform_email";
    private static final String ROUTINGKEY_SMS = "inform_sms";

    public static void main(String[] args) {
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
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //声明交换机
            /**
             * 参数:String exchange, String type
             *  1).exchange 交换机名称
             *  2).type 交换机类型
             *      类型:
             *          fanout :对应工作模式是 publish/subscribe
             *          direct :对应Routing 工作模式
             *          topic : 对用的Topics 工作模式
             *          headers:对应headers 工作模式
             */
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            //交换机 队列 绑定
            /**
             * 参数:String queue, String exchange, String routingKey
             *  1).queue 对列名称
             *  2).exchange 交换机名称
             *  3).routingKey 路由key 作用:交换机根据路由key的值将消息转发到指定的队列中; 在发布订阅模式中设置为空串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
            //绑定相同的routingKey
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, "aaa");
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, "aaa");
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            //发送消息
            /**
             * 参数: String exchange, String routingKey, BasicProperties props, byte[] body
             *  1).exchange 交换机 如果不指定 则使用mq的默认交换机(设置为"")
             *  2).routingKey 路由key,交换机根据路由key来将消息转发到指定的队列, 如果使用默认交换机,routingKey设置为队列名称
             *  3).props 消息属性
             *  4).消息内容
             */
            /*for (int i = 0; i < 5; i++) {
                //发送消息要指定routingKey
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL, null, "send inform message".getBytes());
                log.info("send to mq: {}", "send email inform message");
            }
            for (int i = 0; i < 3; i++) {
                //发送消息要指定routingKey
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS, null, "send inform message".getBytes());
                log.info("send to mq: {}", "send sms inform message");
            }*/
            for (int i = 0; i < 3; i++) {
                //发送消息要指定routingKey
                //实现发布订阅模式功能 交换机将消息转发到绑定此交换机的每个队列，每个绑定交换机的队列都将接收到消息
                //将消息发送到routingKey为aaa的队列 即 QUEUE_INFORM_SMS 和 QUEUE_INFORM_EMAIL 对列
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, "aaa", null, "send inform message".getBytes());
                log.info("send to mq: {}", "send inform message");
            }
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
