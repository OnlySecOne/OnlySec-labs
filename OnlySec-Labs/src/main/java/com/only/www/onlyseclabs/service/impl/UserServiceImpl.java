package com.only.www.onlyseclabs.service.impl;

import com.only.www.onlyseclabs.entity.User;
import com.only.www.onlyseclabs.mapper.UserMapper;
import com.only.www.onlyseclabs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 查询所有用户

    public List<User> findAllUsers() {
        return userMapper.findAllUsers();
    }

    // 根据用户名查询用户
    public User findByUsername(String username) {
        return null;
    }

    // 新增用户
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    // 更新用户信息
    public int updateUser(User user) {
        return userMapper.updateUser(user);
    }

    // 删除用户
    public int deleteUser(int id) {
        return userMapper.deleteUser(id);
    }

    // 根据ID查询用户
    public User findById(String id) {
        return userMapper.findById(id);
    }

    // 模糊搜索用户
    @Override
    public List<User> searchUsers(String keyword) {
        // 直接构造SQL语句
        return userMapper.searchUsers(keyword);
    }
}
