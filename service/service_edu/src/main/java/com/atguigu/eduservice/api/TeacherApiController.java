package com.atguigu.eduservice.api;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description="前台讲师管理")
@RestController
@RequestMapping("/eduservice/teacherapi")
@CrossOrigin
public class TeacherApiController {

    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private EduCourseService courseService;

    @ApiOperation(value = "前台讲师分页查询功能")
    @GetMapping("getFrontTeacherList/{current}/{limit}")
    public R getFrontTeacherList(@PathVariable long current,@PathVariable long limit){
        Page<EduTeacher> pageParam = new Page<>(current,limit);
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("gmt_create");
        teacherService.page(pageParam,wrapper);

        List<EduTeacher> records = pageParam.getRecords();
        long currentPage = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();
        //把分页数据放到map
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", currentPage);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return  R.ok().data(map);
    }

    @ApiOperation(value = "根据id查询讲师详情")
    @GetMapping("getTeacherInfo/{id}")
    public R getTeacherInfo(@PathVariable String id){
        //1查询讲师基本信息
        EduTeacher eduTeacher = teacherService.getById(id);

        //2讲师所讲课程信息
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id",id);
        List<EduCourse> courseList = courseService.list(wrapper);

        return R.ok().data("eduTeacher",eduTeacher).data("courseList",courseList);
    }


}
