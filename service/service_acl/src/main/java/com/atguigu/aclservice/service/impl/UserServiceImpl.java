package com.atguigu.aclservice.service.impl;

import com.atguigu.aclservice.entity.Role;
import com.atguigu.aclservice.entity.User;
import com.atguigu.aclservice.entity.UserRole;
import com.atguigu.aclservice.mapper.UserMapper;
import com.atguigu.aclservice.service.RoleService;
import com.atguigu.aclservice.service.UserRoleService;
import com.atguigu.aclservice.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-04-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;

    //根据用户id获取角色数据
    @Override
    public Map<String, Object> getRoleAssignUserId(String userId) {
        //1查询所有角色
        List<Role> roleList = roleService.list(new QueryWrapper<Role>().orderByDesc("id"));

        //2根据用户id查询用户关联角色id
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.select("role_id");
        List<UserRole> userRoleList = userRoleService.list(wrapper);

        //3从用户关联角色对象里获取角色id封装到另一个list
        List<String> existRoleList = userRoleList.stream().map(c->c.getRoleId()).collect(Collectors.toList());

        //4遍历所有角色进行判断
        List<Role> userFinalRole = new ArrayList<>();
        for (Role role : roleList) {
            if(existRoleList.contains(role.getId())){
                userFinalRole.add(role);
            }
        }
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoles", userFinalRole);
        roleMap.put("allRolesList", roleList);

        return roleMap;
    }
}
