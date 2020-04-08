package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.service.EduChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-03-20
 */
@Api(description="章节管理")
@RestController
@RequestMapping("/eduservice/educhapter")
@CrossOrigin
public class EduChapterController {

    @Autowired
    EduChapterService chapterService;

    @ApiOperation(value = "根据课程id查询章节小节信息")
    @GetMapping("getChapterVideoById/{courseId}")
    public R getChapterVideoById(@PathVariable String courseId){
        List<ChapterVo> allChapterVideo = chapterService.getChapterVideoById(courseId);
        return R.ok().data("allChapterVideo",allChapterVideo);
    }

    @ApiOperation(value = "新增章节信息")
    @PostMapping("addChapter")
    public R addChapter(@RequestBody EduChapter eduChapter){
        chapterService.save(eduChapter);
        return R.ok();
    }

    @ApiOperation(value = "删除章节信息")
    @DeleteMapping("{id}")
    public R deleteChapter(@PathVariable String id){
        chapterService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "根据id查询章节信息")
    @GetMapping("{id}")
    public R getChapterInfo(@PathVariable String id){
        EduChapter eduChapter = chapterService.getById(id);
        return R.ok().data("chapter",eduChapter);
    }

    @ApiOperation(value = "修改章节信息")
    @PostMapping("updateChapter")
    public R updateChapter(@RequestBody EduChapter eduChapter){
        chapterService.updateById(eduChapter);
        return R.ok();
    }



}

