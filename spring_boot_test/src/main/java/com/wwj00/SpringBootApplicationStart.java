package com.wwj00;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Nancy on 2019/7/29 14:12
 *
 * 1.@SpringBootApplication 注解相当于一下三个注解:
 *      @SpringBootConfiguration  等同与@Configuration，既标注该类是Spring的一个配置类
 *      @EnableAutoConfiguration  SpringBoot自动配置功能开启
 *      @ComponentScan            包扫描 [引导类所在的包及其子包都会被扫描]
 *
 * 2.@EnableAutoConfiguration
 *      1).@Import(AutoConfigurationImportSelector.class)  导入了AutoConfigurationImportSelector类
 *      2).按住Ctrl点击查看AutoConfigurationImportSelector源码
 *          进入 List<String> configurations = getCandidateConfigurations(annotationMetadata,attributes);
 *          其中，SpringFactoriesLoader.loadFactoryNames 方法的作用就是从META-INF/spring.factories文件中读取指定类对应的类名称列表 [看截图]
 *          列表存在大量的以Configuration为结尾的类名称，这些类就是存有自动配置信息的类，而SpringApplication在获取这些类名后再加载
 *
 *
 *  eg:以 ServletWebServerFactoryAutoConfiguration 为例,实现自定义配置覆盖默认配置
 *      1).@EnableConfigurationProperties(ServerProperties.class)  代表加载ServerProperties服务器配置属性类
 *      2).进入ServerProperties.class
 *          @ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
 *          prefix = "server" 表示SpringBoot配置文件中的前缀，SpringBoot会将配置文件中以server开始的属性映射到该类的字段中 [截图]
 *
 */
@SpringBootApplication
@RestController
public class SpringBootApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplicationStart.class,args);
    }
}
