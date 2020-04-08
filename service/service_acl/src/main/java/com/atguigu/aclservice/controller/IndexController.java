package com.atguigu.aclservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.aclservice.service.IndexService;
import com.atguigu.commonutils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description="登录后管理")
@RestController
@RequestMapping("/admin/acl/index")
@CrossOrigin
public class IndexController {

    @Autowired
    private IndexService indexService;

    @ApiOperation(value = "根据用户名获取用户信息")
    @GetMapping("info")
    public R info(){
        //获取当前登录用户的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //根据用户名获取用户信息
        Map<String,Object> resultMap = indexService.getUserInfo(username);
        return R.ok().data(resultMap);
    }


    @ApiOperation(value = "根据用户名获取菜单信息")
    @GetMapping("menu")
    public R getMenu(){
        //获取当前登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<JSONObject> permissionList = indexService.getMenu(username);
        return R.ok().data("permissionList",permissionList);
    }


    @PostMapping("logout")
    public R logout(){
        return R.ok();
    }



}
