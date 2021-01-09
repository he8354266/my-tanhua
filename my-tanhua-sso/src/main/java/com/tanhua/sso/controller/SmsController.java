package com.tanhua.sso.controller;

import com.tanhua.sso.service.SmsService;
import com.tanhua.sso.vo.ErrorResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2020/12/3115:16
 */
@RequestMapping("/sms")
@RestController
@Api(value = "短信验证码", tags = "短信验证码", description = "短信验证码")
public class SmsController {
    @Autowired
    private SmsService smsService;

    @ApiOperation("发送验证码接口")
    @ApiImplicitParam(name = "sms", value = "短信验证码", required = true, dataType = "String", paramType = "query")
    @GetMapping("/login")
    public ResponseEntity<Object> sendCheckCode(@RequestParam String mobile) {
        ErrorResult.ErrorResultBuilder builder = ErrorResult.builder().errCode("000000").errMessage("短信发送失败");


        Map<String, Object> sendCheckCode = this.smsService.sendCheckCode(mobile);
        int code = (int) sendCheckCode.get("code");
        if (code == 3) {
            //发送成功
            return ResponseEntity.ok(null);
        } else if (code == 1) {
            //未失效
            String msg = String.valueOf(sendCheckCode.get("msg"));
            builder.errCode("000001").errMessage(msg);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(builder.build());
    }
}
