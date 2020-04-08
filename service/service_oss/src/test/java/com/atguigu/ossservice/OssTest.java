package com.atguigu.ossservice;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.ossservice.utils.ConstantPropertiesUtil;
import com.atguigu.servicebase.handler.GuliException;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.InputStream;
import java.util.UUID;

public class OssTest {

    @Test
    public void test(){
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId ="LTAI3buexRAagkdy";
        String accessKeySecret = "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz";
        String bucketName = "guli-file191101test";
        String objectName = "2020/03/16/887c259f-8100-4f20-a321-170d982dbd79file.png";



            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
            ossClient.deleteObject(bucketName, objectName);

// 关闭OSSClient。
            ossClient.shutdown();



    }

}
