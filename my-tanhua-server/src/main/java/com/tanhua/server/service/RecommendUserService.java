package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.RecommendUserApi;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.vo.TodayBest;
import org.springframework.stereotype.Service;

@Service
public class RecommendUserService {

    @Reference(version = "1.0.0")
    private RecommendUserApi recommendUserApi;

    public TodayBest queryTodayBest(Long userId) {
        RecommendUser recommendUser = this.recommendUserApi.queryWithMaxScore(userId);
        if(null == recommendUser){
            return null;
        }

        TodayBest todayBest = new TodayBest();
        // 如果得分为98.2时，需要显示的得分是98
        double score = Math.floor(recommendUser.getScore());
        todayBest.setFateValue(Double.valueOf(score).longValue()); //缘分值

        todayBest.setId(recommendUser.getUserId()); //用户id

        return todayBest;
    }

    public PageInfo<RecommendUser> queryRecommendUserList(Long id, Integer page, Integer pagesize) {
        return this.recommendUserApi.queryPageInfo(id,page,pagesize);
    }
}
