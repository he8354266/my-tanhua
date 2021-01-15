package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Users;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestUsers {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UsersApi usersApi;

    @Test
    public void saveUsers() {
        Users users = new Users();
        users.setUserId(1l);
        users.setFriendId(11l);
        String result = usersApi.saveUsers(users);
        System.out.println(result);
    }

    @Test
    public void queryAllUsersList() {

        PageInfo<Users> pageInfo = usersApi.queryUsersList(1l, 1, 10);
        System.out.println(pageInfo);
    }

}
                       