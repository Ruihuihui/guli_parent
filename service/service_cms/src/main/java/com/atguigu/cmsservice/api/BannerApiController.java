package com.atguigu.cmsservice.api;

import com.atguigu.cmsservice.entity.Banner;
import com.atguigu.cmsservice.service.BannerService;
import com.atguigu.commonutils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description="banner前台展示")
@RestController
@RequestMapping("/cmsservice/bannerapi")
@CrossOrigin
public class BannerApiController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation(value = "前台查询banner方法")
    @GetMapping("getAllBanner")
    public R getAllBanner(){
        List<Banner> list = bannerService.getAllBanner();
        return R.ok().data("list",list);
    }

}
