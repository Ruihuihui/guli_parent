package com.atguigu.eduservice.api;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description="首页展示")
@RestController
@RequestMapping("/eduservice/index")
@CrossOrigin
public class IndexController {

    @Autowired
    private EduCourseService courseService;
    @Autowired
    private EduTeacherService teacherService;

    @ApiOperation(value = "首页展示8条课程、4位讲师")
    @GetMapping
    public R getIndexData(){
        //1、查询8条课程记录
        //SELECT * FROM edu_course ec ORDER BY id DESC LIMIT 8;
        QueryWrapper<EduCourse> wrapperCourse = new QueryWrapper<>();
        //1.1根据id进行降序排
        wrapperCourse.orderByDesc("id");
        //1.2查询8条记录
        wrapperCourse.last("limit 8");
        List<EduCourse> eduCourseList = courseService.list(wrapperCourse);
        //2、查询4位讲师
        QueryWrapper<EduTeacher> wrapperTeacher = new QueryWrapper<>();
        wrapperTeacher.orderByDesc("id");
        wrapperTeacher.last("limit 4");
        List<EduTeacher> eduTeacherList = teacherService.list(wrapperTeacher);

        return  R.ok().data("hotCourse",eduCourseList).data("teacher",eduTeacherList);
    }


}
