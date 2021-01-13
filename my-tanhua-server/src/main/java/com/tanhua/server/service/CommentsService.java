package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.Comments;
import com.tanhua.server.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1314:07
 */
@Service
public class CommentsService {
    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public PageResult queryCommentsList(String publishId, Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);
        pageResult.setCounts(0);
        pageResult.setPages(0);

        PageInfo<Comment> pageInfo = quanZiApi.queryCommentList(publishId, page, pageSize);

        List<Comment> records = pageInfo.getRecords();
        if (records.size() <= 0) {
            return pageResult;
        }
        List<Long> userIds = new ArrayList<>();
        for (Comment record : records) {
            if (!userIds.contains(record.getUserId())) {
                userIds.add(record.getUserId());
            }
        }
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = userInfoService.queryUserInfoList(queryWrapper);
        List<Comments> commentsList = new ArrayList<>();
        for (Comment record : records) {
            Comments comments = new Comments();
            comments.setId(record.getId());
            comments.setCreateDate(new DateTime(record.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
            comments.setContent(record.getContent());
            for (UserInfo userInfo : userInfoList) {
                if (record.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    comments.setNickname(userInfo.getNickName());
                    comments.setAvatar(userInfo.getLogo());
                    break;
                }
            }
            String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + comments.getId();
            comments.setHasLiked(redisTemplate.hasKey(likeUserCommentKey) ? 1 : 0);//是否点赞

            String likeCommentKey = "QUANZI_COMMENT_LIKE_" + comments.getId();
            String value = redisTemplate.opsForValue().get(likeCommentKey);
            if (StringUtils.isNotEmpty(value)) {
                comments.setLikeCount(Integer.valueOf(value)); //点赞数
            } else {
                comments.setLikeCount(0);
            }
            commentsList.add(comments);
        }

        pageResult.setItems(commentsList);
        return pageResult;
    }

    public Boolean saveComments(String publishId, String content) {
        User user = UserThreadLocal.get();
        return quanZiApi.saveComment(user.getId(), publishId, 2, content);
    }
}
