package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.vo.OneSubjectVo;
import com.atguigu.eduservice.entity.vo.TwoSubjectVo;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-17
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //添加课程分类
    @Override
    public List<String> importSubjectData(MultipartFile file) {
        try {
            //1获取文件输入流
            InputStream inputStream = file.getInputStream();
            //2创建workbook
            //2.1获取文件名的后缀，xls 03版 HSSFWorkbook，xlsx 07版 XSSFWorkbook
            //2.2 Workbook workbook = WorkbookFactory.create(inputStream);
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            //3获取sheet
            HSSFSheet sheet = workbook.getSheetAt(0);
            //4获取row
            //Row row = sheet.getRow(0);
            int lastRowNum = sheet.getLastRowNum();
            //创建list封装错误数据
            List<String> msg = new ArrayList<>();
            for (int i = 1; i <=lastRowNum ; i++) {
                Row row = sheet.getRow(i);
                //5获取cell
                //获取第一列
                Cell cellOne = row.getCell(0);
                if(cellOne==null){
                    //错误提示
                    String error = "第"+(i+1)+"行，第1列数据为空";
                    msg.add(error);
                    //跳过去继续执行
                    continue;
                }
                //6获取数据
                //获取值
                String oneCellValue = cellOne.getStringCellValue();
                if(oneCellValue==null){
                    //错误提示
                    String error = "第"+(i+1)+"行，第1列数据为空";
                    msg.add(error);
                    //跳过去继续执行
                    continue;
                }


                //7插入数据库
                //判断
                EduSubject existOneSubject = this.existOneSubject(oneCellValue);
                if(existOneSubject==null){
                    existOneSubject = new EduSubject();
                    existOneSubject.setTitle(oneCellValue);
                    existOneSubject.setParentId("0");
                    baseMapper.insert(existOneSubject);
                }
                //获取一级id
                String pid = existOneSubject.getId();
                //获取第二列
                Cell cellTwo = row.getCell(1);
                if(cellTwo==null){
                    //错误提示
                    String error = "第"+(i+1)+"行，第2列数据为空";
                    msg.add(error);
                    //跳过去继续执行
                    continue;
                }
                //获取值
                String twoCellValue = cellTwo.getStringCellValue();
                if(twoCellValue==null){
                    //错误提示
                    String error = "第"+(i+1)+"行，第2列数据为空";
                    msg.add(error);
                    //跳过去继续执行
                    continue;
                }

                //判断
                EduSubject existTwoSubject = this.existTwoSubject(twoCellValue, pid);
                if(existTwoSubject==null){
                    existTwoSubject = new EduSubject();
                    existTwoSubject.setTitle(twoCellValue);
                    existTwoSubject.setParentId(pid);
                    baseMapper.insert(existTwoSubject);
                }
            }
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            throw  new GuliException(20002,"添加课程分类失败");
        }


    }


    //查询课程分类
    @Override
    public List<OneSubjectVo> getAllSubject() {
        //1获取一级分类数据
        //parent_id=0
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id","0");
        List<EduSubject> oneSubjectList = baseMapper.selectList(wrapperOne);
        //2获取二级分类数据
        //parent_id!=0
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.ne("parent_id","0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);

        //2.2创建最终数据的list集合
        List<OneSubjectVo> finalSubjectList = new ArrayList<>();

        //3封装一级分类
        //3.1遍历一级分类list集合
        for (int i = 0; i <oneSubjectList.size() ; i++) {
            //得到每一个一级分类
            EduSubject oneSubject = oneSubjectList.get(i);
            //oneSubject转化oneSubjectVo
            OneSubjectVo oneSubjectVo = new OneSubjectVo();
//            oneSubjectVo.setId(oneSubject.getId());
//            oneSubjectVo.setTitle(oneSubject.getTitle());
            //把oneSubject里面的值复制给oneSubjectVo
            BeanUtils.copyProperties(oneSubject,oneSubjectVo);
            //把oneSubjectVo存入finalSubjectList
            finalSubjectList.add(oneSubjectVo);

            //创建集合，封装二级分类
            List<TwoSubjectVo> twoVoList = new ArrayList<>();
            //遍历所有二级分类
            for (int m = 0; m <twoSubjectList.size() ; m++) {
                //获得每一个二级分类
                EduSubject twoSubject = twoSubjectList.get(m);
                //判断：判断一级分类id和二级分类的parentid比较
                if(oneSubject.getId().equals(twoSubject.getParentId())){
                    //twoSubject转化成TwoSubjectVo
                    TwoSubjectVo twoSubjectVo = new TwoSubjectVo();
                    BeanUtils.copyProperties(twoSubject,twoSubjectVo);
                    //把数据存入二级分类集合
                    twoVoList.add(twoSubjectVo);
                }
            }
            //把二级分类vo集合放到一级分类的vo里面
            oneSubjectVo.setChildren(twoVoList);

        }

        return finalSubjectList;
    }

    //判断一级分类是否重复
    private  EduSubject existOneSubject(String name){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        EduSubject eduSubject = baseMapper.selectOne(wrapper);
        return eduSubject;
    }

    //判断二级分类是否重复
    private  EduSubject existTwoSubject(String name,String pid){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id",pid);
        EduSubject eduSubject = baseMapper.selectOne(wrapper);
        return eduSubject;
    }


}
