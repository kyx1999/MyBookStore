package com.kyx1999.mybookstore.service;

import com.kyx1999.mybookstore.dao.UserMapper;
import com.kyx1999.mybookstore.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }

    public User selectByPrimaryKey(Integer uid) {
        return userMapper.selectByPrimaryKey(uid);
    }

    public User selectByUserName(String uname) {
        return userMapper.selectByUserName(uname);
    }

    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    public Boolean isUserNameExist(String uname) {
        return userMapper.isUserNameExist(uname);
    }
}
