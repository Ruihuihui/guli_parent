package com.atguigu.msmservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.msmservice.service.MsmService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {
    @Override
    public boolean sendMsm(String phone, Map<String, String> map) {
        //1创建初始化对象
        DefaultProfile profile =
                DefaultProfile.getProfile("default", "LTAI3buexRAagkdy", "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz");
        IAcsClient client = new DefaultAcsClient(profile);

        //2创建request对象
        CommonRequest request = new CommonRequest();

        //3向request里设置参数
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "我的谷粒在线教育网站");
        request.putQueryParameter("TemplateCode", "SMS_183195440");
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(map));

        try {
            //4调用初始化对象方法实现发送
            CommonResponse response = client.getCommonResponse(request);
            //5通过response获取是否发送成功
            boolean success = response.getHttpResponse().isSuccess();
            return success;
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
    }
}
