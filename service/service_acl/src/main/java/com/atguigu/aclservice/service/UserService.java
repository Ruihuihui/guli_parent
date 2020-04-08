package com.atguigu.aclservice.service;

import com.atguigu.aclservice.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-04-05
 */
public interface UserService extends IService<User> {

    Map<String, Object> getRoleAssignUserId(String userId);
}
