package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer uid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer uid);

    User selectByUserName(String uname);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    Boolean isUserNameExist(String uname);
}
