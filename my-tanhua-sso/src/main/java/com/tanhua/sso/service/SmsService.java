package com.tanhua.sso.service;

import java.util.Map;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3115:27
 */
public interface SmsService {
    /**
     * 发送验证码
     *
     * @param mobile
     * @return
     */
    public Map<String, Object> sendCheckCode(String mobile);

    /**
     * 发送验证码短信
     *
     * @param mobile
     */
    public String sendSms(String mobile);
}
