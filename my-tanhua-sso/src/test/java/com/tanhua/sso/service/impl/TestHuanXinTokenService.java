package com.tanhua.sso.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestHuanXinTokenService {

    @Autowired
    private HuanXinTokenService huanXinTokenService;

    @Test
    public void testGetToken(){
        String token = this.huanXinTokenService.getToken();
        System.out.println(token);
    }

}
