package com.tanhua.sso.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.sso.mapper.UserMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3117:07
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Value("${jwt.secret}")
    private String secret;

    private static final ObjectMapper MAPPER = new ObjectMapper();
//    @Autowired
//    private RocketMQTemplate rocketMQTemplate;


    @Override
    public String login(String mobile, String code) {
        //校验验证码是否正确
        String redisKey = "CHECK_CODE_" + mobile;
        String value = this.redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (!value.equals(code)) {
            return null;
        }
        Boolean isNew = false;//默认是已经注册
        //校验该手机号是否已经注册，如果没有注册，需要注册一个账号，如果已经注册，直接登录
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            user = new User();
            user.setMobile(mobile);
            user.setPassword(DigestUtils.md5Hex("123456"));
            userMapper.insert(user);
            isNew = true;
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("mobile", mobile);
        claims.put("id", user.getId());

        //生成token
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secret).compact();
        try {
            //将token存储到redis中
            String redisTokenKey = "TOKEN_" + token;
            redisTemplate.opsForValue().set(redisTokenKey, JSON.toJSONString(user), Duration.ofHours(1));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("存储token出错");
        }


        //发送消息
        Map<String, Object> msg = new HashMap<>();
        msg.put("id", user.getId());
        msg.put("mobile", mobile);
        msg.put("date", new Date());
//        rocketMQTemplate.convertAndSend("tanhua-sso-login", msg);
        System.out.println(msg);
        return isNew + "|" + token;
    }

    @Override
    public User queryUserByToken(String token) {
        try {
            String redisTokenKey = "TOKEN_" + token;
            System.out.println("token_key========" + redisTokenKey);
            String cacheData = redisTemplate.opsForValue().get(redisTokenKey);
            if (StringUtils.isEmpty(cacheData)) {
                return null;
            }
            //刷新时间
            redisTemplate.expire(redisTokenKey, 1, TimeUnit.HOURS);
            return JSON.parseObject(cacheData, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
