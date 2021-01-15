package com.tanhua.sso.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.sso.config.HuanXinConfig;
import com.tanhua.sso.vo.HuanXinUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1315:39
 */
@Service
public class HuanXinService {
    @Autowired
    private HuanXinConfig huanXinConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HuanXinTokenService huanXinTokenService;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 注册环信用户
     *
     * @param userId 自己的id
     * @return
     */
    public boolean register(Long userId) {
        String url = this.huanXinConfig.getUrl()
                + this.huanXinConfig.getOrgName() + "/"
                + this.huanXinConfig.getAppName() + "/users";

        String token = this.huanXinTokenService.getToken();

        //请求头信息
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", "Bearer " + token);

        List<HuanXinUser> huanXinUsers = new ArrayList<>();
        huanXinUsers.add(new HuanXinUser(userId.toString(), DigestUtils.md5Hex(userId + "_itcast_tanhua")));

        try {
            HttpEntity<String> httpEntity = new HttpEntity(MAPPER.writeValueAsString(huanXinUsers), httpHeaders);

            //发起请求
            ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(url, httpEntity, String.class);

            return responseEntity.getStatusCodeValue() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 添加好友
     *
     * @param userId
     * @param friendId
     * @return
     */
    public boolean contactUsers(Long userId, Long friendId) {
        String targetUrl = this.huanXinConfig.getUrl()
                + this.huanXinConfig.getOrgName() + "/"
                + this.huanXinConfig.getAppName() + "/users/" +
                userId + "/contacts/users/" + friendId;
        String token = huanXinTokenService.getToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json ");
        httpHeaders.add("Authorization", "Bearer " + token);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(targetUrl, httpEntity, String.class);
        System.out.println(responseEntity);
        return responseEntity.getStatusCodeValue() == 200;
    }
}
