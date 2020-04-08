package com.atguigu.orderservice.client;

import com.atguigu.commonutils.vo.UcenterMemberPay;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-ucenter")
public interface UcenterClient {

    //根据用户id获取用户信息,订单支付远程调用
    @GetMapping("/ucenterservice/ucenter/getUcenterPay/{memberId}")
    public UcenterMemberPay getUcenterPay(@PathVariable("memberId") String memberId);
}
