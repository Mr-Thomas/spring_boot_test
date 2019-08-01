package com.wwj00;

import com.wwj00.rabbitMQ.Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Nancy on 2019/7/29 17:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestJunit {

    @Autowired
    private Producer producer;

    @Test
    public void test(){
        System.out.println("测试...");
    }

    @Test
    public void TestRabbitACK(){
        producer.send();
    }

    @Test
    public void TestRabbit(){
        producer.sendObj();
    }
}
