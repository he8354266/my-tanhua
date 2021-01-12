package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.TimeLine;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestQuanzi {

    @Autowired
    private QuanZiApi quanZiApi;

//    @Autowired
//    private MongoTemplate mongoTemplate;

    @Test
    public void testSaveLikeComment() {
        quanZiApi.saveLikeComment(6l,"1");
    }

@Test
    public void testSaveLoveComment(){
        quanZiApi.saveLoveComment(7l,"4");
}
@Test
    public void testRemoveComment(){
        quanZiApi.removeComment(7l,"4",3);
}
@Test
    public void testQueryCommentList(){
    PageInfo<Comment> commentPageInfo =   quanZiApi.queryCommentList("4",1,10);
    System.out.println(commentPageInfo);
    }
}
