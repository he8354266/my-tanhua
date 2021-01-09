package com.tanhua.sso.service;

import com.tanhua.sso.vo.PicUploadResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/411:50
 */
public interface PicUploadService {
    public PicUploadResult upload(MultipartFile uploadFile);



}
