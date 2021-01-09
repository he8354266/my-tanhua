package com.tanhua.sso.mapper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3111:49
 */
@SpringBootApplication
@MapperScan("com.tanhua.sso.mapper")
@ComponentScan(basePackages = "com.tanhua.*")
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
