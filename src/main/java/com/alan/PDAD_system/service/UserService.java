package com.alan.PDAD_system.service;

import com.alan.PDAD_system.entity.User;

public interface UserService {
    User findById(String userId);

    //注册用户
   void registerUser(User user);



}
