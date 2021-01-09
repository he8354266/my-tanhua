package com.tanhua.sso.controller;

import com.tanhua.sso.service.PicUploadService;
import com.tanhua.sso.vo.PicUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/515:32
 */
@RequestMapping("/pic/upload")
@RestController
public class PicUploadController {
    @Autowired
    private PicUploadService picUploadService;

    @PostMapping
    public PicUploadResult upload(@RequestParam("file") MultipartFile multipartFile) {
        return picUploadService.upload(multipartFile);
    }
}
