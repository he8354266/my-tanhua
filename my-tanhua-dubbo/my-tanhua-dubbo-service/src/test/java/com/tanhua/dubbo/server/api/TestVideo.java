package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Video;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1120:16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestVideo {
    @Autowired
    private VideoApi videoApi;

    @Test
    public void testSave() {
        Video video = new Video();
        video.setUserId(5l);
        videoApi.saveVideo(video);
    }
}
