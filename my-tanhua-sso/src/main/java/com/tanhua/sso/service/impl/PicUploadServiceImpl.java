package com.tanhua.sso.service.impl;

import com.aliyun.oss.OSSClient;
import com.tanhua.sso.config.AliyunConfig;
import com.tanhua.sso.service.PicUploadService;
import com.tanhua.sso.vo.PicUploadResult;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/411:51
 */
@Service
public class PicUploadServiceImpl implements PicUploadService {
    //允许上传的格式
    private static final String[] IMAGE_TYPE = new String[]{
            ".bmp", ".jpg",
            ".jpeg", ".gif", ".png"
    };
    @Autowired
    private OSSClient ossClient;
    @Autowired
    private AliyunConfig aliyunConfig;


    @Override
    public PicUploadResult upload(MultipartFile uploadFile) {
        PicUploadResult picUploadResult = new PicUploadResult();
        //图片做校验，对后缀名
        boolean isLegal = false;
        //图片做校验
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {
            picUploadResult.setStatus("error");
            return picUploadResult;
        }
        String fileName = uploadFile.getOriginalFilename();
        String filePath = getFilePath(fileName);
        //上传到阿里云
        try {
            ossClient.putObject(aliyunConfig.getBucketName(), filePath, new ByteArrayInputStream(uploadFile.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
            picUploadResult.setStatus("error");
            return picUploadResult;
        }
        //上传成功
        picUploadResult.setStatus("done");
        picUploadResult.setName(this.aliyunConfig.getUrlPrefix() + filePath);
        picUploadResult.setUid(String.valueOf(System.currentTimeMillis()));
        return picUploadResult;
    }

    private String getFilePath(String fileName) {
        DateTime dateTime = new DateTime();
        return "images/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(100, 9999) + "." +
                StringUtils.substringAfterLast(fileName, ".");
    }
}
