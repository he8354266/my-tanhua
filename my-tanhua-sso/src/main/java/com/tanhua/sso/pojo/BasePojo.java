package com.tanhua.sso.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3111:53
 */
public abstract class BasePojo {
    @TableField(fill = FieldFill.INSERT) //自动填充
    private Date created;
    @TableField(fill = FieldFill.INSERT_UPDATE) //更新新增插入
    private Date updated;
}
