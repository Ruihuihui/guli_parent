package com.atguigu.orderservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.entity.TPayLog;
import com.atguigu.orderservice.mapper.TPayLogMapper;
import com.atguigu.orderservice.service.TOrderService;
import com.atguigu.orderservice.service.TPayLogService;
import com.atguigu.orderservice.utils.HttpClient;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-31
 */
@Service
public class TPayLogServiceImpl extends ServiceImpl<TPayLogMapper, TPayLog> implements TPayLogService {

    @Autowired
    private TOrderService orderService;

    //根据订单号生成支付二维码
    @Override
    public Map createNative(String orderNo) {
        try {
            //1根据订单号获取订单信息
            QueryWrapper<TOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            TOrder order = orderService.getOne(wrapper);
            if(order==null){
                throw  new GuliException(20001,"订单失效");
            }

            //2封装参数值，用map集合
            Map m = new HashMap();
            //1、设置支付参数
            m.put("appid", "wx74862e0dfcf69954");//微信支付id
            m.put("mch_id", "1558950191");//商户号
            m.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            m.put("body", order.getCourseTitle());//商品描述
            m.put("out_trade_no", orderNo);//订单号
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");//支付金额
            m.put("spbill_create_ip", "127.0.0.1");//终端ip地址
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");//支付回调地址
            m.put("trade_type", "NATIVE");//交易类型

            //3 创建httpClient对象，进行请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

            //4 向httpClient设置xml格式参数
            //generateSignedXml方法的参数，第一个map集合,第二个参数微信的商户key
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();

            //5 发送请求得到返回结果
            String content = client.getContent();//xml格式
            System.out.println("content="+content);
            //xml转换成map集合
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            //6 从map获取需要的值map.get("");
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("out_trade_no", orderNo);
            resultMap.put("course_id", order.getCourseId());
            resultMap.put("total_fee", order.getTotalFee());
            resultMap.put("result_code", map.get("result_code"));
            resultMap.put("code_url", map.get("code_url"));

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            throw  new GuliException(20001,"生成支付二维码失败");
        }



    }

    //调用微信接口，查询支付状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1封装参数
            Map m = new HashMap();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());

            //2设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();
            //3返回第三方数据
            String content = client.getContent();//xml格式
            System.out.println("支付结果返回="+content);
            //xml转换成map集合
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            throw  new GuliException(20001,"支付失败");
        }

    }

    //支付成功，修改订单状态，插入支付日志
    @Override
    public void updateOrderState(Map<String, String> map) {
        //1 修改订单状态state改为1
        //1.1根据订单号查询订单信息
        QueryWrapper<TOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",map.get("out_trade_no"));
        TOrder order = orderService.getOne(wrapper);
        //1.2判断订单
        if(order==null){
            throw new GuliException(20001,"订单失效");
        }
        //1.3更改状态
        order.setStatus(1);//1表示已支付
        orderService.updateById(order);

        //2 向订单支付日志表添加日志
        TPayLog payLog = new TPayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));
        payLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(payLog);//插入到支付日志表

    }
}
