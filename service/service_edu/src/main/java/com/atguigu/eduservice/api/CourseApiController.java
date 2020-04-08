package com.atguigu.eduservice.api;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.CourseFrontInfoPay;
import com.atguigu.eduservice.client.OrderClient;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.entity.vo.CourseFrontInfo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(description="前台课程管理")
@RestController
@RequestMapping("/eduservice/courseapi")
@CrossOrigin
public class CourseApiController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private OrderClient orderClient;


    @ApiOperation(value = "前台课程的条件分页查询功能")
    @PostMapping("getFrontCourseList/{current}/{limit}")
    public R getFrontCourseList(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody(required = false) CourseQueryVo courseQueryVo){

        Page<EduCourse> page = new Page<>(current,limit);
        Map<String,Object> map = courseService.getCoursePageList(page,courseQueryVo);
        return R.ok().data(map);
    }

    @ApiOperation(value = "根据id查询课程详情")
    @GetMapping("getCourseInfo/{id}")
    public R getCourseInfo(@PathVariable String id, HttpServletRequest request){
        //1查询课程基本信息
        CourseFrontInfo courseFrontInfo = courseService.getFrontCourseInfo(id);
        //2展示课程相关大纲数据
        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoById(id);

        //3查询课程是否被购买
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        System.out.println("----------memberId = "+memberId);

        boolean isBuyCourse = orderClient.isBuyCourse(id, memberId);

        return R.ok().data("courseFrontInfo",courseFrontInfo)
                .data("chapterVideoList",chapterVideoList)
                .data("isBuyCourse",isBuyCourse);
    }

    @ApiOperation(value = "根据课程id查询课程信息，支付订单远程调用")
    @GetMapping("getCourseInfoPay/{id}")
    public CourseFrontInfoPay getCourseInfoPay(@PathVariable String id){
        //查询课程基本信息
        CourseFrontInfo courseFrontInfo = courseService.getFrontCourseInfo(id);
        CourseFrontInfoPay courseFrontInfoPay = new CourseFrontInfoPay();
        BeanUtils.copyProperties(courseFrontInfo,courseFrontInfoPay);
        return courseFrontInfoPay;
    }


}
