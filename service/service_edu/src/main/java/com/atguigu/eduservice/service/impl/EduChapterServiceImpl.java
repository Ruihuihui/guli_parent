package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.entity.vo.VideoVo;
import com.atguigu.eduservice.mapper.EduChapterMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-20
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    EduVideoService videoService;

    //根据课程id查询章节小节信息
    @Override
    public List<ChapterVo> getChapterVideoById(String courseId) {
        //1根据课程id查询课程章节信息
        QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id",courseId);
        List<EduChapter> chapterList = baseMapper.selectList(wrapperChapter);

        //2根据课程id查询课程小节信息
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id",courseId);
        List<EduVideo> videoList = videoService.list(wrapperVideo);

        //2.1创建list集合，封装最终结果
        List<ChapterVo> finalList = new ArrayList<>();

        //3遍历章节list进行封装
        for (int i = 0; i <chapterList.size() ; i++) {
            //3.1获取每个章节信息
            EduChapter eduChapter = chapterList.get(i);
            //3.2eduChapter转化成ChapterVo
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(eduChapter,chapterVo);
            //3.3chapterVo存入list
            finalList.add(chapterVo);

            //创建list，封装小节信息
            List<VideoVo> videoVoList = new ArrayList<>();
            //4遍历小节list进行封装
            for (int m = 0; m <videoList.size() ; m++) {
                //4.1得到每个小节
                EduVideo eduVideo = videoList.get(m);
                //4.2判断小节chapterId和章节id是否相同
                if(eduVideo.getChapterId().equals(eduChapter.getId())){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo,videoVo);
                    videoVoList.add(videoVo);
                }

            }
            //5把封装好的小节存入章节
            chapterVo.setChildren(videoVoList);


        }

        return finalList;
    }
}
