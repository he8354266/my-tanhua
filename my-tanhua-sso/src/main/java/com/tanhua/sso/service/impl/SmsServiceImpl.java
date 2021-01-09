package com.tanhua.sso.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.sso.common.CommonCode;
import com.tanhua.sso.service.SmsService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3115:32
 */
@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private String SmsUrl = "https://open.ucpaas.com/ol/sms/sendsms";

    private String sid = "56f6523e8f50c85fe92d5d12a8dabd6f";

    private String token = "41fabadd9a221ab4a439548b4dc88433";

    private String appid = "dd7d74e604284a6b9cc668c6591c84c7";

    private String templateid = "487656";


    @Override
    public Map<String, Object> sendCheckCode(String mobile) {
        Map<String, Object> result = new HashMap<>();

        try {
            String redisKey = "CHECK_CODE_" + mobile;
            String value = this.redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNotEmpty(value)) {
                result.put("code", CommonCode.InvalidCode);
                result.put("msg", CommonCode.InvalidMsg);
                return result;
            }
            String code = this.sendSms(mobile);
            if (code == null) {
                result.put("code", CommonCode.FailCode);
                result.put("msg", CommonCode.FailMsg);
                //测试用 短信发送工具未认证
                this.redisTemplate.opsForValue().set(redisKey, "123456", Duration.ofMinutes(2));
                return result;
            }
            //发送验证码成功
            result.put("code", CommonCode.SuccessCode);
            result.put("msg", CommonCode.SuccessCode);
            this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(2));

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("发送验证码出错！" + mobile, e);
            result.put("code", CommonCode.ErrorCode);
            result.put("msg", CommonCode.ErrorMsg);
            return result;
        }

        return result;


    }

    @Override
    public String sendSms(String mobile) {
        Map<String, Object> params = new HashMap<>();
        params.put("sid", sid);
        params.put("token", token);
        params.put("appid", appid);
        params.put("templateid", templateid);
        params.put("mobile", mobile);
        //生成6位数字验证码
        params.put("param", RandomUtils.nextInt(100000, 999999));
        System.out.println(params.get("param"));
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(SmsUrl, params, String.class);
        System.out.println(responseEntity);
        String body = responseEntity.getBody();
        System.out.println(body);
        Map resuleMap = JSON.parseObject(body, Map.class);
        System.out.println(resuleMap.get("msg"));
        try {
            JsonNode jsonNode = MAPPER.readTree(body);
            if (StringUtils.equals(jsonNode.get("code").textValue(), "000000")) {
                return String.valueOf(params.get("param"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
