package com.ca.finalbackend.mapper;

import com.ca.finalbackend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User findByAccountName(@Param("accountName") String accountName);
    User findById(@Param("id") Integer id);
    List<User> findAll();
}
