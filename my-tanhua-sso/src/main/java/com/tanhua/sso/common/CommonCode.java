package com.tanhua.sso.common;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3115:44
 */
public  class CommonCode {
    public static final Integer InvalidCode = 1; //失效
    public static final Integer FailCode = 2; //失败
    public static final Integer SuccessCode = 3; //成功
    public static final Integer ErrorCode = 4; //成功


    public static final String InvalidMsg ="上一次发送的验证码还未失效";
    public static final String FailMsg ="发送短信验证码失败";
    public static final String SuccessMsg ="发送短信验证码成功";
    public static final String ErrorMsg ="发送验证码出现异常";
}
