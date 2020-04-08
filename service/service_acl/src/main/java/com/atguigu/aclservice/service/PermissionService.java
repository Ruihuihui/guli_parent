package com.atguigu.aclservice.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.aclservice.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-04-05
 */
public interface PermissionService extends IService<Permission> {

    List<Permission> queryAllMenu();

    void removePermissionChild(String id);

    List<Permission> selectMenuByRoleId(String roleId);

    void doRoleAssignPermission(String roleId, String[] permissionId);

    List<String> selectPermissionValueByUserId(String id);

    List<JSONObject> selectPermissionByUserId(String id);
}
