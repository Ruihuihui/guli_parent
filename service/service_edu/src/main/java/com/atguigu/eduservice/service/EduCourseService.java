package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.CourseFrontInfo;
import com.atguigu.eduservice.entity.vo.CourseInfoForm;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-03-18
 */
public interface EduCourseService extends IService<EduCourse> {

    String addCourseInfo(CourseInfoForm courseInfoForm);

    CourseInfoForm getCourseInfoId(String courseId);

    void updateCourse(CourseInfoForm courseInfoForm);

    CoursePublishVo getCoursePublishVo(String courseId);

    void deleteCourse(String courseId);

    Map<String, Object> getCoursePageList(Page<EduCourse> page, CourseQueryVo courseQueryVo);

    CourseFrontInfo getFrontCourseInfo(String id);
}
