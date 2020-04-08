package com.atguigu.aclservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.aclservice.entity.Role;
import com.atguigu.aclservice.entity.User;
import com.atguigu.aclservice.service.IndexService;
import com.atguigu.aclservice.service.PermissionService;
import com.atguigu.aclservice.service.RoleService;
import com.atguigu.aclservice.service.UserService;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RedisTemplate redisTemplate;

    //根据用户名获取用户信息
    @Override
    public Map<String, Object> getUserInfo(String username) {
        //1根据用户名查询用户信息
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if(user==null){
            throw new GuliException(20001,"用户不存在");
        }
        //2根据用户id获取角色信息
        List<Role> roleList = roleService.selectRoleByUserId(user.getId());
        List<String> roleNameList = roleList.stream().map(f->f.getRoleName()).collect(Collectors.toList());
        if(roleNameList.size()==0){
            //前端必先返回一个角色，没有会报错，如果没有返回一个空角色
            roleNameList.add("");
        }
        //3根据用户id获取权限信息
        List<String> permissionValueList = permissionService.selectPermissionValueByUserId(user.getId());

        //4权限信息存入redis
        redisTemplate.opsForValue().set(username,permissionValueList);
        //5返回封装好的数据
        Map<String, Object> result = new HashMap<>();
        result.put("name", user.getUsername());
        result.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        result.put("roles", roleNameList);
        result.put("permissionValueList", permissionValueList);
        return result;

    }

    //根据用户名获取菜单信息
    @Override
    public List<JSONObject> getMenu(String username) {
        //1 根据用户名获取用户信息
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if(user==null){
            throw new GuliException(20001,"用户不存在");
        }
        //2 根据用户id获取菜单权限
        List<JSONObject> permissionList = permissionService.selectPermissionByUserId(user.getId());

        return permissionList;
    }
}
