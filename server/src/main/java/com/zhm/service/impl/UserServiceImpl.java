package com.zhm.service.impl;

import com.zhm.dto.LoginDto;
import com.zhm.service.UserService;
import com.zhm.util.Result;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService{

    @Override
    public Result login(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        return Result.sendSuccess("登陆成功");
    }
}
