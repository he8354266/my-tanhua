package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.server.mapper.AnnouncementMapper;
import com.tanhua.server.pojo.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Title: project
 * @Package * @Description:     * @author CodingSir
 * @date 2021/1/1410:08
 */
@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementMapper announcementMapper;

    public IPage<Announcement> queryList(Integer page, Integer pageSize) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("created");
        return announcementMapper.selectPage(new Page<Announcement>(page, pageSize), queryWrapper);
    }
}
