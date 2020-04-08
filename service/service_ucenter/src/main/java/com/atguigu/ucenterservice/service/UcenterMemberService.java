package com.atguigu.ucenterservice.service;

import com.atguigu.ucenterservice.entity.UcenterMember;
import com.atguigu.ucenterservice.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-03-25
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    void register(RegisterVo registerVo);

    String login(UcenterMember member);

    Integer countRegister(String day);
}
