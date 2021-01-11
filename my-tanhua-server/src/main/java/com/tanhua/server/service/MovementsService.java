package com.tanhua.server.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.RelativeDateFormat;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.Movements;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.PicUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MovementsService {

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private PicUploadService picUploadService;

    @Autowired
    private UserInfoService userInfoService;


    /**
     * 发布动态
     *
     * @param textContent
     * @param location
     * @param longitude
     * @param latitude
     * @param multipartFile
     * @return
     */
    public Boolean saveMovements(String textContent,
                                 String location,
                                 String longitude,
                                 String latitude,
                                 MultipartFile[] multipartFile) {

        User user = UserThreadLocal.get();

        Publish publish = new Publish();
        publish.setUserId(user.getId());
        publish.setText(textContent);
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);


        //图片上传
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            PicUploadResult uploadResult = this.picUploadService.upload(file);
            imageUrls.add(uploadResult.getName());
        }

        publish.setMedias(imageUrls);

        return this.quanZiApi.savePublish(publish);
    }

    private PageResult queryPublishList(User user, Integer page, Integer pageSize) {
        PageResult pageResult = new PageResult();
        Long userId = null; //默认查询推荐动态
        if (user != null) {
            // 查询好友动态
            userId = user.getId();
        }

        PageInfo<Publish> pageInfo = this.quanZiApi.queryPublishList(userId, page, pageSize);

        user = UserThreadLocal.get(); //查询完成后，依然还是需要获取到当前的登录用户

        pageResult.setCounts(0);
        pageResult.setPages(0);
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);

        List<Publish> records = pageInfo.getRecords();

        if (CollectionUtils.isEmpty(records)) {
            // 没有查询到动态数据
            return pageResult;
        }

//        pageResult.setItems(this.fillValueToMovements(records));

        return pageResult;
    }

    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {
        return this.queryPublishList(null, page, pageSize);
    }

    public PageResult queryUserPublishList(Integer page, Integer pageSize) {
        return this.queryPublishList(UserThreadLocal.get(), page, pageSize);
    }
}
