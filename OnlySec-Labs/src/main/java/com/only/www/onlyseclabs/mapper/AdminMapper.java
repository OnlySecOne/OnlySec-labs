package com.only.www.onlyseclabs.mapper;

import com.only.www.onlyseclabs.entity.Admin;
import com.only.www.onlyseclabs.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where username=#{username} and password=#{password}")
    Admin findByLogin(@Param("username") String username, @Param("password") String password);
}
