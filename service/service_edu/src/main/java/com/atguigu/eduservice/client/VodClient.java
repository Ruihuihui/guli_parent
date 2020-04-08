package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value="service-vod",fallback = VodFileDegradeFeignClient.class)//服务名就是配置文件里配置的
@Component
public interface VodClient {

    //定义操作方法
    //url必须完整
    //@PathVariable后面必须添加参数名称
    @DeleteMapping("/eduvod/video/{videoId}")
    public R deleteVideoAliyun(@PathVariable("videoId") String videoId);

    @DeleteMapping("/eduvod/video/deleteMoreVideo")
    public R deleteMoreVideo(@RequestParam("videoIdList") List videoIdList);

}
