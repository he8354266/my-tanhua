package com.tanhua.sso.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/410:31
 */
public interface UserInfoService {
    /**
     * 完善个人信息
     *
     * @return
     */
    public Boolean saveUserInfo(Map<String, String> param, String token);

    /**
     * 保存logo
     *
     * @return
     */
    public Boolean saveLogo(MultipartFile file, String token);
}
