package com.atguigu.staservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.staservice.service.StatisticsDailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-04-01
 */
@Api(description="统计管理")
@RestController
@RequestMapping("/staservice/stadaily")
@CrossOrigin
public class StatisticsDailyController {

    @Autowired
    private StatisticsDailyService staService;

    @ApiOperation(value = "生成统计数据")
    @PostMapping("createData/{day}")
    public R createData(@PathVariable String day){
        staService.createData(day);
        return R.ok();
    }

    @ApiOperation(value = "返回图表显示数据")
    @GetMapping("showData/{type}/{begin}/{end}")
    public R showData(@PathVariable String type,
                      @PathVariable String begin,
                      @PathVariable String end){
        Map<String,Object> map  = staService.getShowData(type,begin,end);
        return R.ok().data(map);
    }


}

