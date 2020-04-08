package com.atguigu.staservice.service.impl;

import com.atguigu.commonutils.R;
import com.atguigu.staservice.client.UcenterClient;
import com.atguigu.staservice.entity.StatisticsDaily;
import com.atguigu.staservice.mapper.StatisticsDailyMapper;
import com.atguigu.staservice.service.StatisticsDailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-04-01
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UcenterClient ucenterClient;

    //生成统计数据
    @Override
    public void createData(String day) {
        //1远程调用获得注册人数
        R r = ucenterClient.countRegister(day);
        Integer registerCount = (Integer)r.getData().get("registerCount");
        //2添加之前，根据统计日期删除表内同日期数据
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.eq("date_calculated",day);
        baseMapper.delete(wrapper);
        //3添加新统计的数据
        Integer loginNum = RandomUtils.nextInt(100, 200);//TODO
        Integer videoViewNum = RandomUtils.nextInt(100, 200);//TODO
        Integer courseNum = RandomUtils.nextInt(100, 200);//TODO

        StatisticsDaily daily = new StatisticsDaily();
        daily.setRegisterNum(registerCount);
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);
        daily.setDateCalculated(day);
        baseMapper.insert(daily);
    }

    //返回图表显示数据
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        //1 根据时间范围查询数据（制定查询字段type）
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        //1.1查询指定列的方法
        wrapper.select("date_calculated",type);
        List<StatisticsDaily> staList = baseMapper.selectList(wrapper);

        //2 封装数据X轴数据（数据日期），Y轴（指标数值）
        List<String> dateCalculatedList = new ArrayList<>();
        List<Integer> dataList = new ArrayList<>();
        //2.1遍历staList
        for (int i = 0; i <staList.size() ; i++) {
            StatisticsDaily sta = staList.get(i);
            //2.2取出日期数据
            String dateCalculated = sta.getDateCalculated();
            dateCalculatedList.add(dateCalculated);
            //2.3取具体指标值，放入dataList
            switch(type) {
                case "login_num":
                    Integer loginNum = sta.getLoginNum();
                    dataList.add(loginNum);
                    break;
                case "register_num":
                    Integer registerNum = sta.getRegisterNum();
                    dataList.add(registerNum);
                    break;
                case "video_view_num":
                    Integer videoViewNum = sta.getVideoViewNum();
                    dataList.add(videoViewNum);
                    break;
                case "course_num":
                    Integer courseNum = sta.getCourseNum();
                    dataList.add(courseNum);
                    break;
                default:
                    break;

            }

        }
        Map<String,Object> map = new HashMap<>();
        map.put("dateCalculatedList",dateCalculatedList);
        map.put("dataList",dataList);
        return map;
    }
}
