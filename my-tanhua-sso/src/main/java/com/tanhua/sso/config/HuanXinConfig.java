package com.tanhua.sso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1315:36
 */
@Data
@Configuration
@PropertySource("classpath:huanxin.properties")
@ConfigurationProperties(prefix = "tanhua.huanxin")
public class HuanXinConfig {
    private String url;
    private String orgName;
    private String appName;
    private String clientId;
    private String clientSecret;
}
