package com.wwj00.rabbitMQ;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nancy on 2019/7/31 14:50
 */
@Component
@Slf4j
public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send() {
        String msg = "你好现在是 " + new Date();
        rabbitTemplate.convertAndSend("hello", msg);
    }

    public void sendObj() {
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("ACK", false);
                put("msg", "ack");
            }
        };
        String jsonString = JSON.toJSONString(map);
        this.rabbitTemplate.convertAndSend("helloObj", jsonString);
    }
}
