package com.atguigu.aclservice.service.impl;

import com.atguigu.aclservice.entity.Role;
import com.atguigu.aclservice.entity.UserRole;
import com.atguigu.aclservice.mapper.RoleMapper;
import com.atguigu.aclservice.service.RoleService;
import com.atguigu.aclservice.service.UserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-04-05
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserRoleService userRoleService;

    //根据用户分配角色
    @Override
    public void saveUserRoleRealtionShip(String userId, String[] roleIds) {
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));

        List<UserRole> userRoleList = new ArrayList<>();
        for(String roleId : roleIds) {
            if(StringUtils.isEmpty(roleId)) continue;
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);

            userRoleList.add(userRole);
        }
        userRoleService.saveBatch(userRoleList);
    }

    //根据用户id获取角色信息
    @Override
    public List<Role> selectRoleByUserId(String userid) {
        //1根据用户id查询角色id
        List<UserRole> userRoleList = userRoleService.list(new
                QueryWrapper<UserRole>().eq("user_id", userid).select("role_id"));
        //2List<UserRole>改变List<String>
        List<String> roleIdList = userRoleList.stream().map(f->f.getRoleId()).collect(Collectors.toList());

        //3 根据角色id集合进行查询
        List<Role> roleList = new ArrayList<>();
        if(roleIdList.size()>0){
            roleList = baseMapper.selectBatchIds(roleIdList);
        }
        return roleList;
    }
}
