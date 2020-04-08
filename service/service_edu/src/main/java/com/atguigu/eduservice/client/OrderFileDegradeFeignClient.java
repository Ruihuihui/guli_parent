package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import org.springframework.stereotype.Component;

@Component
public class OrderFileDegradeFeignClient implements OrderClient {
    @Override
    public boolean isBuyCourse(String cid, String mid) {
        return false;
    }
}
