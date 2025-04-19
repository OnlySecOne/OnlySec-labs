package com.only.www.onlyseclabs.service;

import com.only.www.onlyseclabs.entity.User;
import com.only.www.onlyseclabs.mapper.UserMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


public interface UserService {

    // 查询所有用户

    public List<User> findAllUsers();

    // 根据用户名查询用户
    public User findByUsername(String username);

    // 新增用户
    public int insertUser(User user);

    // 更新用户信息
    public int updateUser(User user);

    // 删除用户
    public int deleteUser(int id);

    // 根据ID查询用户
    public User findById(String id);

    // 模糊搜索用户
    public List<User> searchUsers(String keyword);
}
