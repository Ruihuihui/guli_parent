package com.atguigu.orderservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.orderservice.service.TPayLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-03-31
 */
@Api(description="支付管理")
@RestController
@RequestMapping("/orderservice/paylog")
@CrossOrigin
public class TPayLogController {

    @Autowired
    private TPayLogService payLogService;

    @ApiOperation(value = "根据订单号生成支付二维码")
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo){
        Map map = payLogService.createNative(orderNo);
        return R.ok().data(map);
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("queryOrderState/{orderNo}")
    public R queryOrderState(@PathVariable String orderNo){
        //调用微信接口，根据订单号，查询订单状态
        Map<String,String> map = payLogService.queryPayStatus(orderNo);
        //判断支付返回结果是否成功
        if(map.get("trade_state").equals("SUCCESS")){//支付成功
            //支付成功，修改订单状态，插入支付日志
            payLogService.updateOrderState(map);
            return R.ok().message("支付成功");
        }
        return R.ok().code(25000).message("支付中");
    }


}

