package com.atguigu.aclservice.service.impl;

import com.atguigu.aclservice.entity.User;
import com.atguigu.aclservice.service.PermissionService;
import com.atguigu.aclservice.service.UserService;
import com.atguigu.security.entity.SecurityUser;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1 根据用户名获取用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        User user = userService.getOne(wrapper);
        if(user==null){
            throw  new GuliException(20001,"用户不存在");
        }
        //2 获取的用户对象转换成权限框架的用户对象
        com.atguigu.security.entity.User suser = new com.atguigu.security.entity.User();
        BeanUtils.copyProperties(user,suser);

        //3 根据用户id获取用户关联菜单值
        List<String> authorities = permissionService.selectPermissionValueByUserId(user.getId());

        //4 封装用户信息、权限信息反馈给权限框架
        SecurityUser securityUser = new SecurityUser(suser);
        securityUser.setPermissionValueList(authorities);


        return securityUser;
    }
}
