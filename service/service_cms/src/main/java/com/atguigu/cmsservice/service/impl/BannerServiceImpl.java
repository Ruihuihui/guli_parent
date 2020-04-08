package com.atguigu.cmsservice.service.impl;

import com.atguigu.cmsservice.entity.Banner;
import com.atguigu.cmsservice.mapper.BannerMapper;
import com.atguigu.cmsservice.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-24
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    //前台查询banner的方法 value :: selectIndexList
    @Cacheable(value = "banner",key = "'selectIndexList'")
    @Override
    public List<Banner> getAllBanner() {
        List<Banner> bannerList = baseMapper.selectList(null);
        return bannerList;
    }

    @CacheEvict(value = "banner",allEntries=true)
    @Override
    public void saveBanner(Banner banner) {
        baseMapper.insert(banner);
    }


}
