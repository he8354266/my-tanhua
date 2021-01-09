package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/516:09
 */
@Service(version = "1.0.0")
public class RecommendUserApiImpl implements RecommendUserApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryWithMaxScore(Long userId) {
        //条件

        Criteria criteria = Criteria.where("toUserId").is(userId);
        //按照的得分倒序排序，获取第一条教程
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize) {
        //条件
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("score")));
        Query query = Query.query(criteria).with(pageable);
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        return new PageInfo<>(0, pageNum, pageSize, recommendUsers);
    }
}
