package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.Album;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.TimeLine;
import com.tanhua.dubbo.server.pojo.Users;
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
            publish.setId(ObjectId.get());
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
                timeLine.setPublishId(publish.getId());
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
}
