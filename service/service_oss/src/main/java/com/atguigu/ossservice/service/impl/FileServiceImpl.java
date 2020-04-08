package com.atguigu.ossservice.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.ossservice.service.FileService;
import com.atguigu.ossservice.utils.ConstantPropertiesUtil;
import com.atguigu.servicebase.handler.GuliException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFileOss(MultipartFile file) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtil.END_POINT;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId =ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;

        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //获得上传文件流
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            //uuid
            String uuid = UUID.randomUUID().toString();
            //11112222001.jpg
            filename = uuid+filename;
            //定义文件目录
            //目录/2020/03/16/001.jpg
            String path = new DateTime().toString("yyyy/MM/dd"); //2020/03/16
            filename = path+"/"+filename;

            ossClient.putObject(bucketName, filename, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();

            //拼接url
            //https://guli-file191101test.oss-cn-beijing.aliyuncs.com/01.jpg
            String url = "https://"+bucketName+"."+endpoint+"/"+filename;

            return url;
        } catch (Exception e) {
            throw new GuliException(20001,"上传失败");
        }

    }
}
