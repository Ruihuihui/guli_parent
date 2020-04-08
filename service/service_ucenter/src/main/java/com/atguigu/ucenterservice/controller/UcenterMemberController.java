package com.atguigu.ucenterservice.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.UcenterMemberPay;
import com.atguigu.ucenterservice.entity.UcenterMember;
import com.atguigu.ucenterservice.entity.vo.RegisterVo;
import com.atguigu.ucenterservice.service.UcenterMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-03-25
 */
@Api(description="注册登录")
@RestController
@RequestMapping("/ucenterservice/ucenter")
@CrossOrigin
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService ucenterService;

    @ApiOperation(value = "注册方法")
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){
        ucenterService.register(registerVo);
        return R.ok();
    }

    @ApiOperation(value = "登录方法")
    @PostMapping("login")
    public R loginUser(@RequestBody UcenterMember member){
        //返回token字符串，包含用户信息
        String token = ucenterService.login(member);
        return R.ok().data("token",token);
    }

    @ApiOperation(value = "根据token字符串获取用户信息")
    @PostMapping("getInfoToken")
    public R getInfoToken(HttpServletRequest request){
        //根据JWT字符串获取用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //根据用户id获取用户信息
        UcenterMember ucenterMember = ucenterService.getById(memberId);
        return  R.ok().data("member",ucenterMember);
    }

    @ApiOperation(value = "根据用户id获取用户信息,订单支付远程调用")
    @GetMapping("getUcenterPay/{memberId}")
    public UcenterMemberPay getUcenterPay(@PathVariable String memberId){
        //根据用户id获取用户信息
        UcenterMember ucenterMember = ucenterService.getById(memberId);
        UcenterMemberPay ucenterMemberPay = new UcenterMemberPay();
        BeanUtils.copyProperties(ucenterMember,ucenterMemberPay);
        return ucenterMemberPay;
    }

    @ApiOperation(value = "统计某一天注册人数,统计分析远程调用")
    @GetMapping("countRegister/{day}")
    public R countRegister(@PathVariable String day){
        Integer count = ucenterService.countRegister(day);
        return R.ok().data("registerCount",count);
    }



}

