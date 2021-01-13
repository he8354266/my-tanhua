package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.bulk.DeleteRequest;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.dubbo.server.pojo.*;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/810:32
 */
@Service(version = "1.0.0")
public class QuanziApiImpl implements QuanZiApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean savePublish(Publish publish) {
        //校验publish
        if (publish.getUserId() == null) {
            return false;
        }

        try {
           publish.setId(String.valueOf(ObjectId.get()));
            publish.setCreated(System.currentTimeMillis()); //发布时间
            publish.setSeeType(1); //查看权限

            //保存动态信息
            mongoTemplate.save(publish);

            Album album = new Album();
            album.setId(ObjectId.get());
            album.setPublishId(publish.getId()); //动态id
            album.setCreated(System.currentTimeMillis());


            //将相册对象写入到mongodb中
            mongoTemplate.save(album, "quanzi_album_" + publish.getUserId());
            //查询当前用户的好友数据，将动态数据写入到好友的时间线表中
            Query query = Query.query(Criteria.where("userId").is(publish.getUserId()));
            List<Users> users = mongoTemplate.find(query, Users.class);
            for (Users user : users) {
                TimeLine timeLine = new TimeLine();
                timeLine.setId(ObjectId.get());
                timeLine.setUserId(publish.getUserId());
//                timeLine.setPublishId(publish.getId());
                timeLine.setDate(System.currentTimeMillis());
                mongoTemplate.save(timeLine, "quanzi_time_line_" + user.getFriendId());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize) {

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("date")));
        Query query = new Query().with(pageable);
        String tableName = "quanzi_time_line_";
        if (userId == null) {
            tableName += "recommend";
        } else {
            tableName += userId;
        }
        //查询自己的时间线表
        List<TimeLine> timeLines = mongoTemplate.find(query, TimeLine.class, tableName);
        System.out.println(timeLines);
        List<ObjectId> ids = new ArrayList<>();
        for (TimeLine timeLine : timeLines) {
            ids.add(timeLine.getPublishId());
        }
        Query queryPublish = Query.query(Criteria.where("id").in(ids)).with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = mongoTemplate.find(queryPublish, Publish.class);
        //封装分页对象
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(0);
        pageInfo.setRecords(publishList);

        return pageInfo;
    }

    @Override
    public boolean saveLikeComment(Long userId, String publishId) {
        //判断是否已经点赞，如果已经点赞就返回
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(publishId)
                .and("commentType").is(1);
        Query query = Query.query(criteria);
        long count = this.mongoTemplate.count(query, Comment.class);
        if (count > 0) {
            return false;
        }
        return this.saveComment(userId, publishId, 1, null);
    }

    @Override
    public boolean removeComment(Long userId, String publishId, Integer commentType) {

        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(publishId)
                .and("commentType").is(commentType);
        Query query = Query.query(criteria);
        DeleteResult deleteRequest = mongoTemplate.remove(query, Comment.class);
        return deleteRequest.getDeletedCount() > 0;
    }

    @Override
    public boolean saveLoveComment(Long userId, String publishId) {
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(publishId)
                .and("commentType").is(3);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Comment.class);
        if (count > 0) {
            return false;
        }
        return saveComment(userId, publishId, 3, null);
    }

    @Override
    public boolean saveComment(Long userId, String publishId, Integer type, String content) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setIsParent(true);
            comment.setCommentType(type);
            comment.setPublishId(publishId);
            comment.setUserId(userId);
            comment.setId(ObjectId.get());
            comment.setCreated(System.currentTimeMillis());
            this.mongoTemplate.save(comment);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Long queryCommentCount(String publishId, Integer type) {

        Criteria criteria = Criteria.where("publishId").is(publishId)
                .and("commentType").is(type);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Comment.class);
        return count;
    }

    @Override
    public Publish queryPublishById(String publishId) {
        return mongoTemplate.findById(publishId, Publish.class);
    }

    @Override
    public PageInfo<Comment> queryCommentList(String publishId, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.asc("created")));
        Criteria criteria = Criteria.where("publishId").is(publishId)
                .and("commentType").is(2);
        Query query = Query.query(criteria).with(pageRequest);
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(0);
        pageInfo.setPageNum(page);
        pageInfo.setRecords(commentList);
        return pageInfo;
    }
}
