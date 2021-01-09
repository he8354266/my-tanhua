package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/515:52
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Document(collection = "recommend_user")
public class RecommendUser implements Serializable {
    private static final long serialVersionUID = 5874126532504390567L;

    @Id
    private ObjectId id; //主键id
    @Indexed
    private Long userId; //推荐的用户id
    private Long toUserId; //用户id
    @Indexed
    private Double score; //推荐得分
    private String date; //日期
}
