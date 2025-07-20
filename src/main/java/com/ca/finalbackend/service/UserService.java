package com.ca.finalbackend.service;

import com.ca.finalbackend.model.User;
import com.ca.finalbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void register(User user) {
        userMapper.insertUser(user);
    }

    public User findByAccountName(String accountName) {
        return userMapper.findByAccountName(accountName);
    }

    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }
}
