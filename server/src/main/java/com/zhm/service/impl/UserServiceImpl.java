package com.zhm.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhm.dto.LoginCache;
import com.zhm.dto.LoginDto;
import com.zhm.extension.mapper.SysUserMapperExt;
import com.zhm.service.UserService;
import com.zhm.template.entity.SysUser;
import com.zhm.template.entity.SysUserExample;
import com.zhm.util.MD5Util;
import com.zhm.util.Result;
import com.zhm.util.StringUtils;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService{

    private static  final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private SysUserMapperExt sysUserMapperExt;
    @Autowired
    private Environment environment;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final MD5Util md5Util=new MD5Util();

    @Override
    public Result login(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        SysUserExample example=new SysUserExample();
        SysUserExample.Criteria crt=example.createCriteria();
        crt.andDeleteFlagEqualTo(0);
        if(StringUtil.isNullOrEmpty(loginDto.getAccount())){
           return Result.sendFailure("登陆名不能为空！");
        }else{
            crt.andAccountEqualTo(loginDto.getAccount());
        }
        if(StringUtil.isNullOrEmpty(loginDto.getPassword())){
            return Result.sendFailure("密码不能为空");
        }
        List<SysUser> list=sysUserMapperExt.selectByExample(example);
        if(list!=null){
            SysUser user=list.get(0);
            String cookieName=environment.getProperty("mptest.authority.default.cookieName");
            if(cookieName!=null){
               Cookie cookie= WebUtils.getCookie(request,cookieName);
               if(cookie!=null){
                   String redisName=environment.getProperty("mptest.redis.user.info")+cookie.getValue();

                   String userInfo=redisTemplate.opsForValue().get(redisName);
                   if(userInfo==null){
                        this.saveCookieAndRedis(request,response,user,cookieName);
                   }
               }else{
                   this.saveCookieAndRedis(request,response,user,cookieName);
               }
            }
        }else{
            return Result.sendFailure("账号："+loginDto.getAccount()+"还没注册");
        }
        return Result.sendSuccess("登陆成功");
    }

    private void saveCookieAndRedis(HttpServletRequest request, HttpServletResponse response, SysUser user, String cookieName) {

        String host = request.getHeader("host");// 域名+端口，例如www.zhm.com:8080
        String domain = StringUtils.getSecondLevelDomain(host);

        long timestamp=System.currentTimeMillis();
        String userInfo=user.getAccount()+timestamp;
        String cookieInfo=md5Util.StringInMd5(userInfo);
        String cookieExpireTimeStr=(String)request.getAttribute("loginCookieExpireTime");

        Integer cookieExpireTime=0;
        if(cookieExpireTimeStr!=null){
            cookieExpireTime=Integer.parseInt(cookieExpireTimeStr);
        }else{
            cookieExpireTime=2*60*60;
        }

        String redisName=environment.getProperty("mptest.redis.user.info")+cookieInfo;

        LoginCache loginCache=new LoginCache();
        loginCache.setAccount(user.getAccount());
        loginCache.setTelephone(user.getTelephone());
        loginCache.setUserName(user.getUserName());
        String redisStr= JSON.toJSONString(loginCache);
        redisTemplate.opsForValue().set(redisName,redisStr);

        redisTemplate.expire(redisName,cookieExpireTime, TimeUnit.SECONDS);


        Cookie loginCokie=new Cookie(cookieName,cookieInfo);
        //1:跨域共享cookie的方法：设置cookie.setDomain(".jszx.com")
        logger.info("cookie共享的域名："+domain);
        loginCokie.setDomain(domain);
        loginCokie.setPath("/");
        loginCokie.setMaxAge(cookieExpireTime);
        response.addCookie(loginCokie);
        logger.info("账号"+user.getAccount()+"在"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))+"登陆");
    }


}
