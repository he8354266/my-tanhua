package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.Users;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1317:33
 */
@Service(version = "1.0.0")
public class UsersApiImpl implements UsersApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String saveUsers(Users users) {
        if (users.getUserId() == null || users.getFriendId() == null) {
            return null;
        }

        Query query = Query.query(Criteria.where("userId").is(users.getUserId())
                .and("friendId").is(users.getFriendId()));
        Users oldUsers = mongoTemplate.findOne(query, Users.class);
        if (oldUsers != null) {
            return null;
        }
        users.setId(ObjectId.get());
        users.setDate(System.currentTimeMillis());
        mongoTemplate.save(users);
        return users.getId().toHexString();
    }

    @Override
    public List<Users> queryAllUsersList(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Users.class);
    }

    @Override
    public PageInfo<Users> queryUsersList(Long userId, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageRequest);

        List<Users> usersList = mongoTemplate.find(query, Users.class);
        PageInfo<Users> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setRecords(usersList);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(0);
        return pageInfo;
    }
}
