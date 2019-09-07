package com.wwj00.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通用rabbit配置
 * Created by Nancy on 2019/9/7 9:50
 */
@Configuration
@Slf4j
public class publicRabbitConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者   在消费方指定
     * @RabbitListener(queues = "队列名称",containerFactory = "simpleListenerContainer")
     * @return
     */
    @Bean(name = "simpleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //发送消息格式
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        return factory;
    }

    /**
     * 多消费者
     * @return
     */
    @Bean(name = "multiSimpleListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
        //发送消息格式
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //消息确认方式
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(15);
        factory.setPrefetchCount(10);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {

            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {

            }
        });
        return rabbitTemplate;
    }

    @Bean
    public Queue successQueue(){
        return new Queue("");
    }

    @Bean
    public TopicExchange successExchange(){
        return new TopicExchange("",true,false); //名称 持久化 自动删除
    }

    @Bean
    public Binding successBinding(){
        return BindingBuilder.bind(successQueue()).to(successExchange()).with(""); //带路由的交换机绑定到队列
    }
}
