package com.atguigu.ucenterservice.service.impl;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.MD5;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.ucenterservice.entity.UcenterMember;
import com.atguigu.ucenterservice.entity.vo.RegisterVo;
import com.atguigu.ucenterservice.mapper.UcenterMemberMapper;
import com.atguigu.ucenterservice.service.UcenterMemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-03-25
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //注册方法
    @Override
    public void register(RegisterVo registerVo) {
        //获得注册信息，进行校验
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();
        //1校验参数是否为空
        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(nickname)
                ||StringUtils.isEmpty(password)||StringUtils.isEmpty(code)){
            throw new GuliException(20001,"参数为空");
        }
        //2根据手机号查询是否有相同手机
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if(count>0){
            throw new GuliException(20001,"手机号已存在");
        }
        //3判断校验码
        String codeRedis = redisTemplate.opsForValue().get(mobile);
        if(!code.equals(codeRedis)){
            throw new GuliException(20001,"验证码错误");
        }

        //4 存入数据库
        //4.1复制数据
        UcenterMember ucenterMember = new UcenterMember();
        BeanUtils.copyProperties(registerVo,ucenterMember);
        //4.2密码加密
        String passwordBefore = ucenterMember.getPassword();
        String passwordAfter = MD5.encrypt(passwordBefore);
        ucenterMember.setPassword(passwordAfter);
        //4.3手动设置一些值
        ucenterMember.setIsDisabled(false);
        ucenterMember.setAvatar("https://guli-file191101test.oss-cn-beijing.aliyuncs.com/2020/03/17/6b610edd-66e0-4adf-8cd4-49c6c9debb50file.png");

        int insert = baseMapper.insert(ucenterMember);
        if(insert==0){
            throw new GuliException(20001,"注册失败");
        }
    }

    //登录方法
    @Override
    public String login(UcenterMember member) {
        //获得参数
        String mobile = member.getMobile();
        String password = member.getPassword();
        //1判断参数是否为空
        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            throw new GuliException(20001,"手机号或密码有误");
        }
        //2根据手机号查询
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember ucenterMember = baseMapper.selectOne(wrapper);
        if(ucenterMember==null){
            throw new GuliException(20001,"手机号或密码有误");
        }
        //3校验密码
        String passwordDatabase = ucenterMember.getPassword();
        //3.1对输入密码进行MD5加密
        String encrypt = MD5.encrypt(password);
        //3.2校验加密后的密码是否一致
        if(!encrypt.equals(passwordDatabase)){
            throw new GuliException(20001,"手机号或密码有误");
        }
        //4判断用户是否禁用
        if(ucenterMember.getIsDisabled()){
            throw new GuliException(20001,"手机号或密码有误");
        }
        //5生成token
        String jwtToken = JwtUtils.getJwtToken(ucenterMember.getId(), ucenterMember.getNickname());
        return jwtToken;
    }

    //统计某一天注册人数
    @Override
    public Integer countRegister(String day) {
        Integer count = baseMapper.countRegister(day);
        return count;
    }
}
