package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.CourseFrontInfo;
import com.atguigu.eduservice.entity.vo.CourseInfoForm;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-18
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    EduCourseDescriptionService courseDescriptionService;

    @Autowired
    EduVideoService videoService;
    @Autowired
    EduChapterService chapterService;

    @Autowired
    VodClient vodClient;

    //添加课程信息
    @Override
    public String addCourseInfo(CourseInfoForm courseInfoForm) {
        //1添加课程信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm,eduCourse);
        int insert = baseMapper.insert(eduCourse);
        if(insert==0){//添加课程信息失败
            throw  new GuliException(20001,"添加课程信息失败");
        }
        //1.1获取添加课程后的id
        String courseId = eduCourse.getId();
        //2添加课程描述信息
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseId);
        eduCourseDescription.setDescription(courseInfoForm.getDescription());
        courseDescriptionService.save(eduCourseDescription);
        return courseId;
    }

    //根据id课程信息
    @Override
    public CourseInfoForm getCourseInfoId(String courseId) {
        //1查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        //1.1 eduCourse复制到CourseInfoForm
        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(eduCourse,courseInfoForm);
        //2查询描述表
        EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
        courseInfoForm.setDescription(courseDescription.getDescription());

        return courseInfoForm;
    }

    //修改课程信息
    @Override
    public void updateCourse(CourseInfoForm courseInfoForm) {
        //1修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm ,eduCourse);
        int update = baseMapper.updateById(eduCourse);
        if(update==0){
            throw  new GuliException(20001,"修改课程信息失败");
        }
        //2修改描述表
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseInfoForm.getId());
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescriptionService.updateById(courseDescription);
    }

    //根据课程id查询课程发布信息
    @Override
    public CoursePublishVo getCoursePublishVo(String id) {
        CoursePublishVo coursePublishVo = baseMapper.getCoursePublishVo(id);
        return coursePublishVo;
    }

    //删除课程（以及相关信息）
    @Override
    public void deleteCourse(String courseId) {
        //1根据课程id删除小节
        //删除小节的同时删除视频
        //1.1根据课程id查询该课程所有视频
        QueryWrapper<EduVideo> wrapperVideoId = new QueryWrapper<>();
        wrapperVideoId.eq("course_id",courseId);
        List<EduVideo> list = videoService.list(wrapperVideoId);

        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < list.size() ; i++) {
            EduVideo eduVideo = list.get(i);
            //1.2从小节信息获取视频id
            String videoSourceId = eduVideo.getVideoSourceId();
            //1.3 判断后，存入集合
            if(!StringUtils.isEmpty(videoSourceId)){
                videoIds.add(videoSourceId);
            }
        }
        //1.4 判断后，调接口删除多个视频
        if(videoIds.size()>0){
            vodClient.deleteMoreVideo(videoIds);
        }


        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id",courseId);
        videoService.remove(wrapperVideo);
        //2根据课程id删除章节
        QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id",courseId);
        chapterService.remove(wrapperChapter);
        //3根据课程id删除描述
        courseDescriptionService.removeById(courseId);
        //4根据课程id删除课程
        int delete = baseMapper.deleteById(courseId);
        if(delete==0){
            throw new GuliException(20001,"删除课程失败");
        }
    }

    //前台课程的条件分页查询功能
    @Override
    public Map<String, Object> getCoursePageList(Page<EduCourse> pageParam, CourseQueryVo courseQueryVo) {
        //1取出查询条件
        String subjectParentId = courseQueryVo.getSubjectParentId();//一级分类id
        String subjectId = courseQueryVo.getSubjectId();//二级分类id

        String buyCountSort = courseQueryVo.getBuyCountSort();//关注度
        String priceSort = courseQueryVo.getPriceSort();//价格
        String gmtCreateSort = courseQueryVo.getGmtCreateSort();//时间
        //2条件判断
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(subjectParentId)){
            wrapper.eq("subject_parent_id",subjectParentId);
        }
        if(!StringUtils.isEmpty(subjectId)){
            wrapper.eq("subject_id",subjectId);
        }
        if(!StringUtils.isEmpty(buyCountSort)){
            wrapper.orderByDesc("buy_count");
        }
        if(!StringUtils.isEmpty(priceSort)){
            wrapper.orderByDesc("price");
        }
        if(!StringUtils.isEmpty(gmtCreateSort)){
            wrapper.orderByDesc("gmt_create");
        }

        //3查询数据
        baseMapper.selectPage(pageParam,wrapper);

        //4封装数据，并返回
        List<EduCourse> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);


        return map;
    }

    //根据id查询课程详情(前台)
    @Override
    public CourseFrontInfo getFrontCourseInfo(String id) {
        CourseFrontInfo courseFrontInfo = baseMapper.getFrontCourseInfo(id);
        return courseFrontInfo;
    }
}
