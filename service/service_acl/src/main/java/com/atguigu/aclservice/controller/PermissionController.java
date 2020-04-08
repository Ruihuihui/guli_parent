package com.atguigu.aclservice.controller;


import com.atguigu.aclservice.entity.Permission;
import com.atguigu.aclservice.service.PermissionService;
import com.atguigu.commonutils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 权限 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-04-05
 */
@Api(description="菜单管理")
@RestController
@RequestMapping("/admin/acl/permission")
@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "1、获取所有菜单信息")
    @GetMapping
    public R queryAllPermission(){
        List<Permission> list = permissionService.queryAllMenu();
        return R.ok().data("children",list);
    }

    @ApiOperation(value = "递归删除菜单")
    @DeleteMapping("remove/{id}")
    public R remove(@PathVariable String id){
        permissionService.removePermissionChild(id);
        return R.ok();
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public R save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return R.ok();
    }


    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public R updateById(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return R.ok();
    }


    @ApiOperation(value = "根据角色id获取菜单")
    @GetMapping("toAssign/{roleId}")
    public R toAssign(@PathVariable String roleId){
        List<Permission> list =permissionService.selectMenuByRoleId(roleId);
        return R.ok().data("children",list);
    }

    @ApiOperation(value = "给角色分配权限")
    @PostMapping("doAssign")
    public R doAssign(String roleId,String[] permissionId){
        permissionService.doRoleAssignPermission(roleId,permissionId);
        return R.ok();
    }

}

