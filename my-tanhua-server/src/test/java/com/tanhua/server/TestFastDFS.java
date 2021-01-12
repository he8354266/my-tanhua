package com.tanhua.server;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/129:23
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Test
    public void testUpload(){
        String path = "D:\\timg.jpg";
        File file = new File(path);
        if(file.exists()){
            System.out.println("file");
        }
        try {
            StorePath storePath = this.fastFileStorageClient.uploadFile(FileUtils.openInputStream(file), file.length(), "jpg", null);

            System.out.println(storePath); //StorePath [group=group1, path=M00/00/00/wKgfUV2GJSuAOUd_AAHnjh7KpOc1.1.jpg]
            System.out.println(storePath.getFullPath());//group1/M00/00/00/wKgfUV2GJSuAOUd_AAHnjh7KpOc1.1.jpg
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
