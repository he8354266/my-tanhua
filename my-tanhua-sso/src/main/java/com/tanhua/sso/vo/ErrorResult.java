package com.tanhua.sso.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3114:27
 */
@Data
@Builder
public class ErrorResult {
    private String errCode;
    private String errMessage;
}
