package com.atguigu.orderservice.service.impl;

import com.atguigu.commonutils.vo.CourseFrontInfoPay;
import com.atguigu.commonutils.vo.UcenterMemberPay;
import com.atguigu.orderservice.client.EduClient;
import com.atguigu.orderservice.client.UcenterClient;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.mapper.TOrderMapper;
import com.atguigu.orderservice.service.TOrderService;
import com.atguigu.orderservice.utils.OrderNoUtil;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-31
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

    @Autowired
    private EduClient eduClient;

    @Autowired
    private UcenterClient ucenterClient;

    //生成课程支付订单
    @Override
    public String createOrder(String courseId, String memberId) {
        //1 生成订单号
        String orderNo = OrderNoUtil.getOrderNo();
        //2根据课程id查询课程信息，远程调用
        CourseFrontInfoPay courseInfoPay = eduClient.getCourseInfoPay(courseId);
        if(courseInfoPay==null){
            throw  new GuliException(20001,"课程获取失败");
        }

        //3根据用户id查询用户信息，远程调用
        UcenterMemberPay ucenterPay = ucenterClient.getUcenterPay(memberId);
        if(ucenterPay==null){
            throw  new GuliException(20001,"用户获取失败");
        }
        //4 把订单数据插入数据库
        TOrder order = new TOrder();
        order.setOrderNo(orderNo);
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfoPay.getTitle());
        order.setCourseCover(courseInfoPay.getCover());
        order.setTeacherName(courseInfoPay.getTeacherName());
        order.setTotalFee(courseInfoPay.getPrice());
        order.setMemberId(memberId);
        order.setMobile(ucenterPay.getMobile());
        order.setNickname(ucenterPay.getNickname());
        order.setStatus(0);//0未支付,1已支付
        order.setPayType(1);//1微信
        baseMapper.insert(order);

        return orderNo;
    }
}
