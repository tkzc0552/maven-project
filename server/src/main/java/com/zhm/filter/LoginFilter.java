package com.zhm.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhm.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Created by 赵红明 on 2019/11/1.
 * filterName的首字母一定要小写！！！小写！！！小写！！！
 * 如果不小写，会导致配置的多个过滤器拦截url都失效了
 * urlPatterns的意思是过滤一些参数
 */
@WebFilter(urlPatterns = {"/*"}, filterName = "loginFilter")
@Component
public class LoginFilter implements Filter {

    private static Logger logger= LoggerFactory.getLogger(LoginFilter.class);

    private final Environment environment;

    private final StringRedisTemplate redisTemplate;

    public static final String KEY_USER = "user";


    @Autowired
    private StringRedisTemplate rt=new StringRedisTemplate();
    /**
     * 记录那些方法需要token
     */
    private Map<RequestMappingInfo, HandlerMethod> ignoredRequestMappingInfoMap;
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public LoginFilter(Environment environment, StringRedisTemplate redisTemplate){
        this.environment=environment;
        this.redisTemplate=redisTemplate;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        logger.info("进入LoginFilter拦截器初始化方法内");
        logger.info("getFilterName:"+filterConfig.getFilterName());//返回<filter-name>元素的设置值。
        logger.info("getServletContext:"+filterConfig.getServletContext());//返回FilterConfig对象中所包装的ServletContext对象的引用。
        logger.info("getInitParameter:"+filterConfig.getInitParameter("cacheTimeout"));//用于返回在web.xml文件中为Filter所设置的某个名称的初始化的参数值
        logger.info("getInitParameterNames:"+filterConfig.getInitParameterNames());//返回一个Enum

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("进入LoginFilter拦截器");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String servletPath = httpServletRequest.getServletPath();
        logger.info("servletPath : " + servletPath);

        //如果是登录页面则不需要拦截
        if(servletPath.contains("/login")){
            chain.doFilter(request, response);
            return;
        }
        //验证码不需要拦截
        if(servletPath.contains("/api/defaultKaptcha")){
            chain.doFilter(request, response);
            return;
        }


        //判断方法体(注解TokenFilter)是否需要过滤掉不执行拦截
        HandlerExecutionChain handlerExecutionChain = null;
        try {
            handlerExecutionChain = requestMappingHandlerMapping.getHandler(httpServletRequest);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
        boolean isIgnore = ignore(handlerExecutionChain);
        if (isIgnore) {
            logger.info(servletPath+"不需要拦截");
            chain.doFilter(request, response);
            return;
        }else{
            logger.info(servletPath+"需要拦截");
        }

        /**
         * 配置一些页面参数不需要过滤器处理
         */
        String excludedPatterns = environment.getProperty("mptest.authority.default.excluded_patterns");
        String[] excludedPatternsArray = StringUtils.split(excludedPatterns, ",");

        PathMatcher matcher = new AntPathMatcher();
        for (String excludedPattern : excludedPatternsArray) {
            logger.debug("servletPath : " + servletPath + ", excludedPattern : " + excludedPattern);
            if (matcher.match(excludedPattern, servletPath)) {
                chain.doFilter(request, response);
                return;
            }
        }


        String cookieName=environment.getProperty("mptst.authority.default.cookieName");

        //登录页面
        //判断cookieName是否配置
        if(cookieName!=null){
            Cookie cookie= WebUtils.getCookie(httpServletRequest,cookieName);
            if(cookie!=null){
                String redisName=environment.getProperty("mptst.redis.user.info")+cookie.getValue();
                logger.info("redis中key值是："+redisName);
                //获取Redis中用户信息
                String userInfo=rt.opsForValue().get(redisName);
                //如果Redis中用户信息是空的
                if(userInfo!=null){
                    JSONObject userObj=JSONObject.parseObject(userInfo);
                   /* LoginUser loginUser=new LoginUser();
                    loginUser.setUserId(Integer.parseInt(userObj.get("userId").toString()));
                    loginUser.setUserName(userObj.get("userName").toString());
                    if(userObj.get("avataUrl")!=null){
                        loginUser.setAvataUrl(userObj.get("avataUrl").toString());
                    }else{
                        loginUser.setAvataUrl("");
                    }
                    // 把用户信息放到log4j
                    MDC.put(KEY_USER, loginUser.getUserName());
                    loginUser.setExpires(userObj.get("expires").toString());
                    AuthorityUtils.setCurrentUser(loginUser);*/
                }else{
                    logger.info("redis中用户信息不存在！");
                     writerFailure(httpServletResponse);
                    return;
                }
            }else{
                logger.info("cookie不存在！");
                writerFailure(httpServletResponse);
                return;
            }
        }else{
            logger.info("cookieName不存在！");
            writerFailure(httpServletResponse);
            return;
        }
        chain.doFilter(request, response);
    }

    private void writerFailure(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");

        //把返回值输出到客户端
        String json= JSON.toJSONString(Result.sendFailure("请重新登陆"));
        OutputStream out=response.getOutputStream();
        out.write(json.getBytes("UTF-8"));
        out.flush();
    }

    @Override
    public void destroy() {
        logger.info("销毁LoginFilter拦截器");
    }


    private boolean ignore(HandlerExecutionChain handlerExecutionChain) {
        HandlerMethod handler;
        try {
            if(handlerExecutionChain == null) {
                return false;
            }
            handler = (HandlerMethod) handlerExecutionChain.getHandler();
            if (handler == null) {
                return false;
            }
        } catch (Exception ignore) {
            return false;
        }
        if(ignoredRequestMappingInfoMap != null && !ignoredRequestMappingInfoMap.isEmpty()) {
            Collection<HandlerMethod> handlerMethods = ignoredRequestMappingInfoMap.values();
            for (HandlerMethod handlerMethod : handlerMethods) {
                if (handler.getBeanType().equals(handlerMethod.getBeanType()) && handler.getMethod().equals(handlerMethod.getMethod())) {
                    return true;
                }
            }
        }
        return false;
    }
}
