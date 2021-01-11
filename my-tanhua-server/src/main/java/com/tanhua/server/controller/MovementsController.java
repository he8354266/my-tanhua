package com.tanhua.server.controller;

import com.tanhua.server.service.MovementsService;
import com.tanhua.server.vo.Movements;
import com.tanhua.server.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("movements")
@RestController
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    /**
     * 发布动态
     *
     * @param textContent
     * @param location
     * @param longitude
     * @param latitude
     * @param multipartFile
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveMovements(@RequestParam("textContent") String textContent,
                                              @RequestParam("location") String location,
                                              @RequestParam("longitude") String longitude,
                                              @RequestParam("latitude") String latitude,
                                              @RequestParam("imageContent") MultipartFile[] multipartFile) {
        try {
            Boolean bool = this.movementsService.saveMovements(textContent, location, longitude, latitude, multipartFile);
            if (bool) {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 查询好友的动态信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResult> queryPublishList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                       @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        try {
            PageResult pageResult = this.movementsService.queryUserPublishList(page, pageSize);
            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询推荐的动态信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("recommend")
    public ResponseEntity<PageResult> queryRecommendPublishList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        try {
            PageResult pageResult = this.movementsService.queryRecommendPublishList(page, pageSize);
            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
