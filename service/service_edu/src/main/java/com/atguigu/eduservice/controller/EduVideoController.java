package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.service.EduVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-03-20
 */
@Api(description="小节管理")
@RestController
@RequestMapping("/eduservice/eduvideo")
@CrossOrigin
public class EduVideoController {
    @Autowired
    EduVideoService videoService;

    @Autowired
    VodClient vodClient;

    @ApiOperation(value = "新增小节信息")
    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo){
        videoService.save(eduVideo);
        return R.ok();
    }
    @ApiOperation(value = "删除小节信息")
    @DeleteMapping("{id}")
    //删除小节，需要删除阿里云对应视频
    public R deleteVideo(@PathVariable String id){
        //1根据小节id查询视频id
        EduVideo eduVideo = videoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();
        //2判断视频id不为空
        if(!StringUtils.isEmpty(videoSourceId)){
            vodClient.deleteVideoAliyun(videoSourceId);
        }

        videoService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "根据id查询小节信息")
    @GetMapping("{id}")
    public R getVideoInfo(@PathVariable String id){
        EduVideo eduVideo = videoService.getById(id);
        return R.ok().data("video",eduVideo);
    }
    @ApiOperation(value = "修改小节信息")
    @PostMapping("updateVideo")
    public R updateVideo(@RequestBody EduVideo eduVideo){
        videoService.updateById(eduVideo);
        return R.ok();
    }



}

