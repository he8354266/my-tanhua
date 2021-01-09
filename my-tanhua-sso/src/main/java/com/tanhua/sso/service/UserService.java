package com.tanhua.sso.service;

import com.tanhua.sso.pojo.User;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3117:06
 */
public interface UserService {
    /**
     * 登录逻辑
     *
     * @param mobile
     * @param code
     * @return 如果校验成功返回token，失败返回null
     */
    public String login(String mobile, String code);

    //查询token
    User queryUserByToken(String token);

}
