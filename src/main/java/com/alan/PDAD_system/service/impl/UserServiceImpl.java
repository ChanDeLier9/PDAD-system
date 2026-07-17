package com.alan.PDAD_system.service.impl;

import com.alan.PDAD_system.entity.User;
import com.alan.PDAD_system.mapper.UserMapper;
import com.alan.PDAD_system.service.UserService;
import org.springframework.stereotype.Service;

@Service   //把当前的类注册到容器里
public class UserServiceImpl implements UserService{
    //因为在server层会用到Mapper层的方法操作数据库，因此这里
    // 定义一个UserMapper类的对象userMapper，后续可以通过
    // 该对象实现对数据库的操作。
    private final UserMapper userMapper;


    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;

    }
    @Override
    public User findById(String userId) {
        return userMapper.findById(userId);  // 根据用户ID查询用户
    }

    @Override
    public void registerUser(User user) {
        // 如果用户已存在，抛出异常
        if (userMapper.findById(user.getUserId()) != null) {
            throw new RuntimeException("用户账号已存在！");
        }

        // 如果 role 为 null 或为空，默认设置为 "doctor"
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("doctor");  // 默认角色
        }

        // 调用 userMapper 插入用户信息
        userMapper.addUser(user);
    }
    //管理员相关的设计如下





}
