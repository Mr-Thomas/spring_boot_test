package com.wwj00.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Nancy on 2019/7/29 14:14
 *
 * @ConfigurationProperties使用:
 *      1).需要get set方法
 *      2).读取配置文件 prefix = "person" 表示SpringBoot配置文件中的前缀，SpringBoot会将配置文件中以server开始的属性映射到该类的字段中
 */
@Data
@Slf4j
@Controller
@ResponseBody
@ConfigurationProperties(prefix="person")
public class QuickController {

    private String name;
    private Integer age;

    @RequestMapping("/quick")
    public String quick(){
        log.info("person: {}",name+" : "+age);
        return "name: "+name+" -- "+"age: "+age;
    }
}
