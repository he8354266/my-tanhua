package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Users;
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

    @Test
    public void saveUsers(){
        this.mongoTemplate.save(new Users(ObjectId.get(),1L, 2L, new Date()));
        this.mongoTemplate.save(new Users(ObjectId.get(),1L, 3L, new Date()));
        this.mongoTemplate.save(new Users(ObjectId.get(),1L, 4L, new Date()));
        this.mongoTemplate.save(new Users(ObjectId.get(),1L, 5L, new Date()));
        this.mongoTemplate.save(new Users(ObjectId.get(),1L, 6L, new Date()));
    }

    @Test
    public void testQueryList(){
        Criteria criteria = Criteria.where("userId").is(1L);
        List<Users> users = this.mongoTemplate.find(Query.query(criteria), Users.class);
        for (Users user : users) {
            System.out.println(user);
        }
    }
}
                       