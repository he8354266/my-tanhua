package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/810:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "quanzi_album")
public class Album implements Serializable {
    private static final long serialVersionUID = 432183095092216817L;

    private ObjectId id;//主键id

    private ObjectId publishId; //发布id
    private Long created; //发布时间

}
