package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.dubbo.server.pojo.FollowUser;
import com.tanhua.dubbo.server.pojo.Video;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1118:25
 */
@Service(version = "1.0.0")
public class VideoApiImpl implements VideoApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean saveVideo(Video video) {
        if (video.getUserId() == null) {
            return false;
        }
        video.setId(ObjectId.get());
        video.setCreated(System.currentTimeMillis());
        mongoTemplate.save(video);
        return true;
    }

    @Override
    public PageInfo<Video> queryVideoList(Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Query query = new Query().with(pageRequest);
        List<Video> videos = mongoTemplate.find(query, Video.class);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page);
        pageInfo.setTotal(0);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(videos);
        return pageInfo;
    }

    @Override
    public Boolean followUser(Long userId, Long followUserId) {
        try {
            FollowUser followUser = new FollowUser();
            followUser.setFollowUserId(followUserId);
            followUser.setId(ObjectId.get());
            followUser.setUserId(userId);
            followUser.setCreated(System.currentTimeMillis());
            mongoTemplate.save(followUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean disFollowUser(Long userId, Long followUserId) {

        Criteria criteria = Criteria.where("userId").is(userId)
                .and("followUserId").is(followUserId);
        Query query = Query.query(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query, FollowUser.class);
        return deleteResult.getDeletedCount() > 0;
    }
}
