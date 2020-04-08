package com.atguigu.aclservice.service;

import com.atguigu.aclservice.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author testjava
 * @since 2020-04-05
 */
public interface RoleService extends IService<Role> {

    void saveUserRoleRealtionShip(String userId, String[] roleId);

    List<Role> selectRoleByUserId(String id);
}
