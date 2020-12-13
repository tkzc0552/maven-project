package com.zhm.controller;

import com.zhm.dto.LoginDto;
import com.zhm.service.UserService;
import com.zhm.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response){
            return  userService.login(loginDto,request,response);
    }

}
