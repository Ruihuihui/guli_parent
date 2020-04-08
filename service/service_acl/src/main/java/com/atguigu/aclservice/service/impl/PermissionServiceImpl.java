package com.atguigu.aclservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.aclservice.entity.Permission;
import com.atguigu.aclservice.entity.RolePermission;
import com.atguigu.aclservice.entity.User;
import com.atguigu.aclservice.mapper.PermissionMapper;
import com.atguigu.aclservice.service.PermissionService;
import com.atguigu.aclservice.service.RolePermissionService;
import com.atguigu.aclservice.service.UserService;
import com.atguigu.aclservice.utils.MenuHelper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-04-05
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {


    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserService userService;

    //获取所有菜单信息
    @Override
    public List<Permission> queryAllMenu() {
        //1把菜单所有数据查询出来
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<Permission> permissionList = baseMapper.selectList(wrapper);

        //2封装成需要的结构
        List<Permission> result = bulid(permissionList);

        return result;
    }

    //递归删除菜单
    @Override
    public void removePermissionChild(String id) {
        //当前菜单id
        //当前菜单子菜单id
        //把要删除的菜单id获取到，放到list集合
        List<String> idList = new ArrayList<>();
        this.selectIdList(id,idList);
        //当前菜单id,放入list集合
        idList.add(id);
        baseMapper.deleteBatchIds(idList);
    }

    //根据角色id获取菜单
    @Override
    public List<Permission> selectMenuByRoleId(String roleId) {
        //1根据角色id查询角色菜单关系表，查询角色关联所有菜单id
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id",roleId);
        List<RolePermission> rolePermissionList = rolePermissionService.list(wrapper);

        //2查询所有菜单
        List<Permission> allPermissionList = baseMapper.selectList(new QueryWrapper<Permission>().orderByDesc("id"));

        //3遍历所有菜单，判断菜单是否选中
        for (int i = 0; i < allPermissionList.size(); i++) {
            //3.1得到每个菜单
            Permission permission = allPermissionList.get(i);
            //3.2遍历rolePermissionList
            for (RolePermission rp : rolePermissionList) {
                //3.3比较id
                if(rp.getPermissionId().equals(permission.getId())){
                    //3.4让菜单select是true
                    permission.setSelect(true);
                }
            }

        }

        //4封装成树形结构
        List<Permission> result = bulid(allPermissionList);
        return result;
    }

    //给角色分配权限
    @Override
    public void doRoleAssignPermission(String roleId, String[] permissionId) {
        //1根据角色id清除所有的关联关系
        rolePermissionService.remove(new
                QueryWrapper<RolePermission>().eq("role_id",roleId));
        //2保存角色与菜单关系
        //2.1创建批量插入数据的集合
        List<RolePermission> rpList = new ArrayList<>();
        //2.2遍历permissionId数组
        for (String perId : permissionId) {
           //2.3创建对象，存入值
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(perId);
            rpList.add(rolePermission);
        }
        //批量插入数据
        rolePermissionService.saveBatch(rpList);
    }

    //根据用户id获取关联权限值
    @Override
    public List<String> selectPermissionValueByUserId(String userid) {
        List<String> selectPermissionValueList = null;
        //1判断一下当前用户是否是管理员，用户名是否是admin
        //1.1如果是管理员，查询所有菜单
        if(this.isAdmin(userid)){//管理员
            selectPermissionValueList = baseMapper.selectAllPermissionValue();
        }else{
            //2如果不是管理员，编写sql语句，根据用户id查询用户菜单值
            selectPermissionValueList = baseMapper.selectPermissionValueByUserId(userid);

        }

        return selectPermissionValueList;
    }

    //根据用户id获取菜单权限
    @Override
    public List<JSONObject> selectPermissionByUserId(String userId) {
        List<Permission> permissionList = null;
        //1如果是管理员查询所有菜单,如果不是条件查询
        if(this.isAdmin(userId)){
            permissionList = baseMapper.selectList(null);
        }else{
            permissionList = baseMapper.selectPermissionByUserId(userId);
        }
        //2转化成树形对象
        List<Permission> bulidList = bulid(permissionList);
        //3 转化List<JSONObject>
        List<JSONObject> resultList = MenuHelper.bulid(bulidList);

        return resultList;
    }

    //判断用户是否是管理员
    private  boolean isAdmin(String userid){
        User user = userService.getById(userid);
        if(user!=null&&"admin".equals(user.getUsername())){
            //管理员
            return true;
        }
        return false;
    }

    private void selectIdList(String id,List<String> idList){
        //根据当前id查询菜单下面的子菜单
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("pid",id);
        List<Permission> childList = baseMapper.selectList(wrapper);
        //获取到的菜单id放到List里面，再进行下一级别的查询
        childList.stream().forEach(item->{
            idList.add(item.getId());
            //递归
            this.selectIdList(item.getId(),idList);
        });
    }


    //封装成需要的结构
    private  List<Permission> bulid(List<Permission> permissionList){
        //1创建list集合，存放返回数据
        List<Permission> trees = new ArrayList<>();
        //2 遍历所有菜单集合
        for (Permission permission : permissionList) {
            //2.1得到每个菜单，判断是不是第一层目录
            if("0".equals(permission.getPid())){
                //2.2设置permission的level为1
                permission.setLevel(1);
                //2.3把值放到最终list集合
                trees.add(findChild(permission,permissionList));
            }
        }
        return trees;
    }

    private Permission findChild(Permission permission,List<Permission> permissionList){
        //1 初始化值
        permission.setChildren(new ArrayList<>());
        //2 permissionList遍历
        for (Permission it : permissionList) {
            //2.1判断上层id和下层pid是否相同
            if(permission.getId().equals(it.getPid())){
                //2.1把层级加1
                int newLevel = permission.getLevel() + 1;
                it.setLevel(newLevel);
                if(permission.getChildren()==null){
                    permission.setChildren(new ArrayList<>());
                }
                //2.2递归调用存入数据
                permission.getChildren().add(findChild(it,permissionList));
            }


        }
        return permission;
    }


}
