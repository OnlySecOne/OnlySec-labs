package com.only.www.onlyseclabs.mapper;
import com.only.www.onlyseclabs.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAllUsers();

    User findByUsername(String username);

    int insertUser(User user);

    int updateUser(User user);

    int deleteUser(int id);

    User findById(String id);

    List<User> searchUsers(String keyword);
}