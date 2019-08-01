package com.wwj00.nancy.rabbitMQ;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Nancy on 2019/7/31 18:26
 */
@Slf4j
@Component
@RabbitListener(queues = "hello")
public class Consumer {
    @RabbitHandler
    public void process(String hello, Channel channel, Message message) throws IOException {
        System.out.println("HelloReceiver收到  : " + hello + "收到时间" + new Date());
        //手动ACK
        //默认情况下如果一个消息被消费者所正确接收则会被从队列中移除
        //如果一个队列没被任何消费者订阅，那么这个队列中的消息会被 Cache（缓存），
        //当有消费者订阅时则会立即发送，当消息被消费者正确接收时，就会被从队列中移除
        try {
            //手动ack应答   true确认所有消费者获得的消息
            //消息的标识，false只确认当前一个消息收到，true确认所有consumer获得的消息
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.error("消息消费成功：id：{}", message.getMessageProperties().getDeliveryTag());
        } catch (Exception e) {
            e.printStackTrace();
            //最后一个参数是：是否重回队列
            //丢弃这条消息
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            //ack返回false，并重新回到队列，api里面解释得很清楚
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            //多条消息被重新发送
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, true);
            //拒绝消息
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            //消息被丢失
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            log.error("receiver fail");
        }
    }

}
