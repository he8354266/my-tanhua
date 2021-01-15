package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.api.UsersApi;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Users;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.Announcement;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.Contacts;
import com.tanhua.server.vo.MessageAnnouncement;
import com.tanhua.server.vo.MessageLike;
import com.tanhua.server.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/149:57
 */
@Service
public class IMService {
    @Reference(version = "1.0.0")
    private UsersApi usersApi;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${tanhua.sso.url}")
    private String ssoUrl;
    @Autowired
    private UserInfoService userInfoService;
    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;
    @Autowired
    private AnnouncementService announcementService;

    public Boolean contactUser(Long userId) {
        //  1、保存数据到MongoDB，2、好友关系注册到环信
        Users users = new Users();
        users.setUserId(UserThreadLocal.get().getId());
        users.setFriendId(userId);
        usersApi.saveUsers(users);

        String url = ssoUrl + "user/huanxin/contacts/" + users.getUserId() + "/" + users.getFriendId();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);

        if (responseEntity.getStatusCodeValue() == 200) {
            return true;
        }
        return false;
    }

    public PageResult queryContactsList(Integer page, Integer pageSize, String keyword) {
        User user = UserThreadLocal.get();
        List<Users> usersList = null;
        if (StringUtils.isNotEmpty(keyword)) {
            usersList = usersApi.queryAllUsersList(user.getId());
        } else {
            PageInfo<Users> pageInfo = usersApi.queryUsersList(user.getId(), page, pageSize);
            usersList = pageInfo.getRecords();
        }
        List<Long> fUserIds = new ArrayList<>();
        for (Users users : usersList) {
            fUserIds.add(users.getFriendId());
        }
        //查询用户（好友）的信息
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", fUserIds);
        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like("nice_name", keyword);
        }
        List<UserInfo> userInfoList = userInfoService.queryUserInfoList(queryWrapper);

        List<Contacts> contactsList = new ArrayList<>();
        if (StringUtils.isEmpty(keyword)) {
            for (Users users : usersList) {
                for (UserInfo userInfo : userInfoList) {
                    if (users.getFriendId().longValue() == userInfo.getId().longValue()) {
                        Contacts contacts = new Contacts();
                        contacts.setCity(StringUtils.substringBefore(userInfo.getCity(), "-"));
                        contacts.setUserId(userInfo.getUserId().toString());
                        contacts.setNickname(userInfo.getNickName());
                        contacts.setGender(userInfo.getSex().name().toLowerCase());
                        contacts.setAvatar(userInfo.getLogo());
                        contacts.setAge(userInfo.getAge());
                        contactsList.add(contacts);
                        break;
                    }
                }
            }
        } else {
            for (UserInfo userInfo : userInfoList) {
                Contacts contacts = new Contacts();
                contacts.setCity(StringUtils.substringBefore(userInfo.getCity(), "-"));
                contacts.setUserId(userInfo.getUserId().toString());
                contacts.setNickname(userInfo.getNickName());
                contacts.setGender(userInfo.getSex().name().toLowerCase());
                contacts.setAvatar(userInfo.getLogo());
                contacts.setAge(userInfo.getAge());

                contactsList.add(contacts);
            }
        }
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);
        pageResult.setCounts(0);
        pageResult.setPages(0);
        pageResult.setItems(contactsList);
        return pageResult;
    }

    public PageResult queryMessageLikeList(Integer page, Integer pageSize) {
        return this.messageCommentList(1, page, pageSize);
    }

    public PageResult queryMessageCommentList(Integer page, Integer pageSize) {
        return this.messageCommentList(2, page, pageSize);
    }

    public PageResult queryMessageLoveList(Integer page, Integer pageSize) {
        return this.messageCommentList(3, page, pageSize);
    }

    private PageResult messageCommentList(Integer type, Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();
        PageInfo<Comment> pageInfo = quanZiApi.queryCommentListByUser(user.getId(), type, page, pageSize);
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setPagesize(pageSize);

        List<Comment> records = pageInfo.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return pageResult;
        }

        List<Long> userIds = new ArrayList<>();
        for (Comment comment : records) {
            userIds.add(comment.getUserId());
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = userInfoService.queryUserInfoList(queryWrapper);

        List<MessageLike> messageLikeList = new ArrayList<>();
        for (Comment record : records) {
            for (UserInfo userInfo : userInfoList) {
                if (record.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    MessageLike messageLike = new MessageLike();
                    messageLike.setId(record.getId());
                    messageLike.setAvatar(userInfo.getLogo());
                    messageLike.setCreateDate(new DateTime(record.getCreated()).toString("yyyy-MM-dd HH:mm"));
                    messageLikeList.add(messageLike);
                    break;
                }
            }
        }
        pageResult.setItems(messageLikeList);
        return pageResult;
    }

    public PageResult queryMessageAnnouncementList(Integer page, Integer pageSize) {
        IPage<Announcement> announcementPage = announcementService.queryList(page, pageSize);
        List<MessageAnnouncement> messageAnnouncementList = new ArrayList<>();
        for (Announcement record : announcementPage.getRecords()) {
            MessageAnnouncement messageAnnouncement = new MessageAnnouncement();
            messageAnnouncement.setId(record.getId().toString());
            messageAnnouncement.setTitle(record.getTitle());
            messageAnnouncement.setDescription(record.getDescription());
            messageAnnouncement.setCreateDate(new DateTime(record.getCreated()).toString("yyyy-MM-dd HH:mm"));
            messageAnnouncementList.add(messageAnnouncement);
        }
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setPagesize(pageSize);
        pageResult.setItems(messageAnnouncementList);
        return pageResult;
    }
}
