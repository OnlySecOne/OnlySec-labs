package com.only.www.onlyseclabs.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.only.www.onlyseclabs.entity.Admin;

@Mapper
public interface CaptchaMapper {
    
    @Select("SELECT * FROM admin WHERE username = #{username} AND password = #{password}")
    Admin findByUsernameAndPassword(String username, String password);
} 