package com.atguigu.vodservice.controller;


import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;

import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.commonutils.R;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.vodservice.utils.AliyunVodSDKUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(description="视频管理")
@RestController
@RequestMapping("/eduvod/video")
@CrossOrigin
public class VodController {

    @ApiOperation(value = "上传视频")
    @PostMapping("uploadVideo")
    public R uploadVideo(MultipartFile file){
        try {
            //1上传文件名
            String fileName = file.getOriginalFilename();
            //2阿里云显示名称
            String title  = fileName.substring(0,fileName.lastIndexOf("."));
            //3获取文件流
            InputStream inputStream = file.getInputStream();
            //4创建request
            UploadStreamRequest request = new UploadStreamRequest("LTAI3buexRAagkdy","A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz",title,fileName,inputStream);
            //5创建对象，提交请求，获得响应
            UploadVideoImpl uploadVideo = new UploadVideoImpl();
            UploadStreamResponse response = uploadVideo.uploadStream(request);
            //6 从响应中获取视频id
            String videoId="";
            if(response.isSuccess()){
                videoId = response.getVideoId();
            }else {
                videoId = response.getVideoId();
            }

            return  R.ok().data("videoId",videoId);
        } catch (IOException e) {
            throw new GuliException(20001,"上传失败");
        }
    }

    @ApiOperation(value = "删除视频")
    @DeleteMapping("{videoId}")
    public R deleteVideoAliyun(@PathVariable String videoId){
        try {
            //1创建初始化对象
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient("LTAI3buexRAagkdy", "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz");
            //2创建删除视频的request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //3向request里设置视频id,支持传入多个视频ID，多个用逗号分隔
            request.setVideoIds(videoId);
            //4调用初始化对象方法
            client.getAcsResponse(request);
            return R.ok();
        } catch (Exception e) {
            return R.error();
        }
    }

    @ApiOperation(value = "删除多个视频")
    @DeleteMapping("deleteMoreVideo")
    public R deleteMoreVideo(@RequestParam("videoIdList")List videoIdList){
        try {
            //1创建初始化对象
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient("LTAI3buexRAagkdy", "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz");
            //2创建删除视频的request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //3向request里设置视频id,支持传入多个视频ID，多个用逗号分隔
            //videoIdList修改成11,12,13
            String videoIds = StringUtils.join(videoIdList.toArray(), ",");
            request.setVideoIds(videoIds);
            //4调用初始化对象方法
            client.getAcsResponse(request);
            return R.ok();

        } catch (Exception e) {
           return R.error();
        }

    }

    @ApiOperation(value = "根据视频id获取视频播放凭证")
    @GetMapping("getPlayAuth/{vid}")
    public R getPlayAuth(@PathVariable String vid){
        try {
            //1 创建初始化对象
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient("LTAI3buexRAagkdy", "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz");
            //2 创建获取播放凭证的请求request和响应response
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
            //3 向request里放入视频id
            request.setVideoId(vid);
            //设定凭证有效时长
            //request.setAuthInfoTimeout(200L);

            //4 调用初始化对象方法
            response = client.getAcsResponse(request);
            //5 通过响应response获取播放凭证
            String playAuth = response.getPlayAuth();
            return R.ok().data("playAuth",playAuth);
        } catch (Exception e) {
            throw new GuliException(20001,"获取视频凭证失败");
        }
    }


}
