package com.atguigu.msmservice.service;

import java.util.Map;

public interface MsmService {
    boolean sendMsm(String phone, Map<String, String> map);
}
