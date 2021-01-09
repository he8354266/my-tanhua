package com.tanhua.sso.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.enums.SexEnum;
import com.tanhua.sso.mapper.UserInfoMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.pojo.UserInfo;
import com.tanhua.sso.service.PicUploadService;
import com.tanhua.sso.service.UserInfoService;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.vo.PicUploadResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/410:34
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserService userService;
//    @Autowired
//    private FaceEngineService faceEngineService;
    @Autowired
    private PicUploadService picUploadService;

    /**
     * 完善个人信息
     *
     * @return
     */
    @Override
    public Boolean saveUserInfo(Map<String, String> param, String token) {
        try {
            User user = userService.queryUserByToken(token);

            if (user == null) {
                return false;
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(user.getId());
            userInfo.setSex(StringUtils.equals(param.get("gender"), "man") ? SexEnum.MAN : SexEnum.WOMAN);
            userInfo.setNickName(param.get("nickname"));
            userInfo.setBirthday(param.get("birthday"));
            userInfo.setCity(param.get("city"));
            userInfoMapper.insert(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Boolean saveLogo(MultipartFile file, String token) {
        User user = userService.queryUserByToken(token);
        if (user == null) {
            return false;
        }
//        try {
//            boolean isPortrait = faceEngineService.checkIsPortrait(file.getBytes());
//            if (!isPortrait) {
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            LOGGER.error("检测人像图片出错", e);
//            return false;
//        }
        //图片上传到oss
        PicUploadResult uploadResult = picUploadService.upload(file);
        UserInfo userInfo = new UserInfo();
        userInfo.setLogo(uploadResult.getName());

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", user.getId());
        userInfoMapper.update(userInfo, queryWrapper);
        return true;
    }
}
