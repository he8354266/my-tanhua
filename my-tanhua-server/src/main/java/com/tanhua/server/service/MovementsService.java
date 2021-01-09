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
        Long userId = null;
        if (user != null) {
            userId = user.getId();
        }
        PageInfo<Publish> pageInfo = quanZiApi.queryPublishList(userId, page, pageSize);
        System.out.println(pageInfo);
        pageResult.setCounts(0);
        pageResult.setPages(0);
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);

        List<Publish> records = pageInfo.getRecords();
        if (records.size() <= 0) {
            return pageResult;
        }

        List<Movements> movementsList = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (Publish record : records) {
            Movements movements = new Movements();
            movements.setId(record.getId().toHexString());
            System.out.println(movements);
            if (!userIds.contains(record.getUserId())) {
                userIds.add(record.getUserId());
            }
            movements.setLoveCount(100); //喜欢数
            movements.setLikeCount(80);//点赞数
            movements.setDistance("1.2公里"); //TODO 距离
            movements.setHasLoved(1); //TODO 是否喜欢
            movements.setHasLiked(0); //TODO 是否点赞
            movements.setCommentCount(30); //TODO 评论数
            movements.setCreateDate(RelativeDateFormat.format(new Date(record.getCreated()))); //发布时间，10分钟前
            movements.setTextContent(record.getText());
            movements.setImageContent(record.getMedias().toArray(new String[]{}));
            movementsList.add(movements);
        }
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = userInfoService.queryUserInfoList(userInfoQueryWrapper);

        for(Movements movements:movementsList){
            for()
        }
        return null;
    }

}
