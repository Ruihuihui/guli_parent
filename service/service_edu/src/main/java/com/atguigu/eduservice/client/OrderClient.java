package com.atguigu.eduservice.client;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="service-order",fallback = OrderFileDegradeFeignClient.class)//服务名就是配置文件里配置的
@Component
public interface OrderClient {

    //"查询课程是否被用户购买,cid 课程id，mid用户id"
    @GetMapping("/orderservice/order/isBuyCourse/{cid}/{mid}")
    public boolean isBuyCourse(@PathVariable("cid") String cid,
                               @PathVariable("mid") String mid);
}
