package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.CourseInfoForm;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.service.EduCourseService;
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
 * @since 2020-03-18
 */
@Api(description="课程管理")
@RestController
@RequestMapping("/eduservice/educourse")
@CrossOrigin
public class EduCourseController {

    @Autowired
    EduCourseService courseService;

    @ApiOperation(value = "课程列表查询")
    @GetMapping("getList")
    //TODO 完善条件查询、分页
    public R getList(){
        List<EduCourse> list = courseService.list(null);
        return R.ok().data("list",list);
    }


    @ApiOperation(value = "删除课程（以及相关信息）")
    @DeleteMapping("{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        courseService.deleteCourse(courseId);
        return R.ok();
    }


    @ApiOperation(value = "添加课程信息")
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoForm courseInfoForm){
        String courseId  = courseService.addCourseInfo(courseInfoForm);
        return R.ok().data("courseId",courseId);
    }

    @ApiOperation(value = "根据id课程信息")
    @GetMapping("{courseId}")
    public R getCourseInfoId(@PathVariable String courseId){
        CourseInfoForm courseInfoForm = courseService.getCourseInfoId(courseId);
        return R.ok().data("courseInfo",courseInfoForm);
    }

    @ApiOperation(value = "修改课程信息")
    @PostMapping("updateCourse")
    public R updateCourse(@RequestBody CourseInfoForm courseInfoForm){
        courseService.updateCourse(courseInfoForm);
        return R.ok();
    }

    @ApiOperation(value = "根据课程id查询课程发布信息")
    @GetMapping("getCoursePublishVoById/{courseId}")
    public R getCoursePublishVoById(@PathVariable String courseId){
        CoursePublishVo coursePublishVo = courseService.getCoursePublishVo(courseId);
        return R.ok().data("coursePublishVo",coursePublishVo);
    }

    @ApiOperation(value = "发布课程信息")
    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        //根据课程id查询课程信息
        //把status字段改为Normal
        EduCourse eduCourse = courseService.getById(id);
        eduCourse.setStatus("Normal");
        courseService.updateById(eduCourse);
        return R.ok();
    }

}

