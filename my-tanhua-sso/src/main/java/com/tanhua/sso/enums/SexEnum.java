package com.tanhua.sso.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import org.omg.CORBA.UNKNOWN;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3114:21
 */
public enum  SexEnum implements IEnum<Integer> {
    MAN(1,"男"),
    WOMAN(2,"女"),
    UNKNOWN(3,"未知");

    private int value;
    private String desc;

    SexEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
