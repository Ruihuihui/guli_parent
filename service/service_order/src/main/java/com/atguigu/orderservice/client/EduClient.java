package com.atguigu.orderservice.client;

import com.atguigu.commonutils.vo.CourseFrontInfoPay;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-edu")
public interface EduClient {
    //根据课程id查询课程信息，支付订单远程调用
    @GetMapping("/eduservice/courseapi/getCourseInfoPay/{id}")
    public CourseFrontInfoPay getCourseInfoPay(@PathVariable("id") String id);
}
