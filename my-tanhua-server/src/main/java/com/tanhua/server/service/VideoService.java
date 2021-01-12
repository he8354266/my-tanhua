package com.tanhua.server.service;


import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.upload.FastFile;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.api.VideoApi;
import com.tanhua.dubbo.server.pojo.Video;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.PicUploadResult;
import com.tanhua.server.vo.VideoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1211:18
 */
@Service
public class VideoService {
    @Reference(version = "1.0.0")
    private VideoApi videoApi;
    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;
    @Autowired
    private PicUploadService picUploadService;
    @Autowired
    private FdfsWebServer fdfsWebServer;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private UserInfoService userInfoService;

    public Boolean saveVideo(MultipartFile picFile, MultipartFile videoFile) {
        User user = UserThreadLocal.get();
        Video video = new Video();
        video.setUserId(user.getId());
        video.setSeeType(1);
        try {
            //上传图片
            PicUploadResult picUploadResult = picUploadService.upload(picFile);
            video.setPicUrl(picUploadResult.getName());
            //上传视频
            StorePath storePath = storageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), StringUtils.substringAfter(videoFile.getOriginalFilename(), "."), null);
            video.setVideoUrl(fdfsWebServer.getWebServerUrl() + storePath.getFullPath());
            return videoApi.saveVideo(video);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public PageResult queryVideoList(Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);
        pageResult.setCounts(0);
        pageResult.setPages(0);
        PageInfo<Video> pageInfo = videoApi.queryVideoList(page, pageSize);
        List<Video> records = pageInfo.getRecords();
        List<VideoVo> videoVoList = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (Video record : records) {
            VideoVo videoVo = new VideoVo();
            videoVo.setUserId(record.getUserId());
            videoVo.setCover(record.getPicUrl());
            videoVo.setVideoUrl(record.getVideoUrl());
            videoVo.setId(record.getId());
            videoVo.setSignature("我就是我-");
            Long commentCount = quanZiApi.queryCommentCount(videoVo.getId(), 2);
            videoVo.setCommentCount(commentCount == null ? 0 : commentCount.intValue());
            videoVo.setHasFocus(0); //TODO 是否关注

            String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + videoVo.getId();
            videoVo.setHasLiked(redisTemplate.hasKey(likeUserCommentKey) ? 1 : 0); //是否点赞
            String value = redisTemplate.opsForValue().get(likeUserCommentKey);
            if (StringUtils.isNotEmpty(value)) {
                videoVo.setLikeCount(Integer.valueOf(value));
            } else {
                videoVo.setLikeCount(0); //点赞数
            }
            if (!userIds.contains(record.getUserId())) {
                userIds.add(record.getUserId());
            }
            videoVoList.add(videoVo);

        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        List<UserInfo> userInfos = userInfoService.queryUserInfoList(queryWrapper);
        for (VideoVo videoVo : videoVoList) {
            for (UserInfo userInfo : userInfos) {
                if (videoVo.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    videoVo.setNickname(userInfo.getNickName());
                    videoVo.setAvatar(userInfo.getLogo());
                    break;
                }
            }
        }
        pageResult.setItems(videoVoList);
        return pageResult;
    }

    /**
     * 取消关注
     *
     * @param userId
     * @return
     */
    public Boolean disFollowUser(Long userId) {
        try {
            User user = UserThreadLocal.get();
            videoApi.disFollowUser(user.getId(), userId);

            String followUserKey = "VIDEO_FOLLOW_USER_" + user.getId() + "_" + userId;
            if (redisTemplate.hasKey(followUserKey)) {
                redisTemplate.delete(followUserKey);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关注用户
     *
     * @param userId
     * @return
     */
    public Boolean followUser(Long userId) {
        User user = UserThreadLocal.get();
        try {
            videoApi.followUser(user.getId(), userId);
            //记录已关注
            String followUserKey = "VIDEO_FOLLOW_USER_" + user.getId() + "_" + userId;
            redisTemplate.opsForValue().set(followUserKey, "1");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
