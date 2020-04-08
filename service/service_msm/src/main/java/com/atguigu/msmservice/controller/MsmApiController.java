package com.atguigu.msmservice.controller;

import com.atguigu.commonutils.R;
import com.atguigu.msmservice.service.MsmService;
import com.atguigu.msmservice.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(description="短信发送管理")
@RestController
@RequestMapping("/edumsm/msm")
@CrossOrigin
public class MsmApiController {


    @Autowired
    private MsmService msmService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @ApiOperation(value = "根据手机号发送验证码")
    @GetMapping("send/{phone}")
    public R sendMsmPhone(@PathVariable String phone){
        //1 从redis根据手机号获取数据
        String rphone = redisTemplate.opsForValue().get(phone);
        //2 如果能取出数据直接返回
        if(!StringUtils.isEmpty(rphone)){
            return R.ok();
        }
        //3 如果去不出来，调用接口发送短信
        //3.1生成校验码，随机四位数
        String code = RandomUtil.getFourBitRandom();
        //3.2 把生成的校验码封装到map
        Map<String,String> map =new HashMap<>();
        map.put("code",code);
        //3.3 调用service方法
        boolean isSuccess = msmService.sendMsm(phone,map);

        //4 如果发送成功，把校验码往redis里存一份
        if(isSuccess){
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        }else{
            return R.error().message("发送短信失败");
        }

    }

}
