package com.atguigu.cmsservice.controller;


import com.atguigu.cmsservice.entity.Banner;
import com.atguigu.cmsservice.service.BannerService;
import com.atguigu.commonutils.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 首页banner表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-03-24
 */
@Api(description="banner后台管理")
@RestController
@RequestMapping("/cmsservice/banner")
@CrossOrigin
public class BannerController {

    @Autowired
    private BannerService bannerService;
    //1
    @ApiOperation(value = "分页查询")
    @GetMapping("getBannerList/{current}/{limit}")
    public R getBannerList(@PathVariable long current,@PathVariable long limit){
        Page<Banner> page = new Page<>(current,limit);
        QueryWrapper<Banner> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        bannerService.page(page,wrapper);
        return R.ok().data("total",page.getTotal()).data("rows",page.getRecords());
    }

    //2
    @ApiOperation(value = "添加")
    @PostMapping("addBanner")
    public R addBanner (@RequestBody Banner banner ){
        bannerService.saveBanner(banner);
        return R.ok();
    }

    //3
    @ApiOperation(value = "根据id查询")
    @GetMapping("{id}")
    public R getBanner(@PathVariable  String id){
        Banner banner = bannerService.getById(id);
        return R.ok().data("banner",banner);
    }
    //4
    @ApiOperation(value = "修改")
    @PostMapping("updateBanner")
    public R updateBanner (@RequestBody Banner banner ){
        bannerService.updateById(banner);
        return R.ok();
    }
    //5
    @ApiOperation(value = "删除")
    @DeleteMapping("{id}")
    public R deleteBanner(@PathVariable  String id){
         bannerService.removeById(id);
        return R.ok();
    }



}

