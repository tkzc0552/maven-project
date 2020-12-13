package com.zhm.service;

import com.zhm.dto.LoginDto;
import com.zhm.util.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {

    Result login(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response);
}
