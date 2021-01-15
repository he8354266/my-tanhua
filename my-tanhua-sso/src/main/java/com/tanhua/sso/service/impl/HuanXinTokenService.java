package com.tanhua.sso.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.sso.config.HuanXinConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1315:52
 */
@Service
public class HuanXinTokenService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String tokenRedisKey = "HUANXIN_TOEKN";
    @Autowired
    private HuanXinConfig huanXinConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getToken() {
        String cacheData = redisTemplate.opsForValue().get(tokenRedisKey);
        if (StringUtils.isNotEmpty(cacheData)) {
            return cacheData;
        }
        String url = this.huanXinConfig.getUrl()
                + this.huanXinConfig.getOrgName() + "/"
                + this.huanXinConfig.getAppName() + "/token";

        Map<String, Object> param = new HashMap<>();
        param.put("grant_type", "client_credentials");
        param.put("client_id", huanXinConfig.getClientId());
        param.put("client_secret", huanXinConfig.getClientSecret());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, param, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            return null;
        }
        String body = responseEntity.getBody();
        try {
            JsonNode jsonNode = MAPPER.readTree(body);
            System.out.println(jsonNode);
            String accessToken = jsonNode.get("access_token").asText();
            System.out.println(accessToken);
            //过期时间，提前一天失效
            Long expiresIn = jsonNode.get("access_token").asLong() - 86400;
            redisTemplate.opsForValue().set(tokenRedisKey, accessToken,expiresIn);
            return accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
