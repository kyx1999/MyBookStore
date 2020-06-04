package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.Bulletin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BulletinMapper {
    int insert(Bulletin record);

    int insertSelective(Bulletin record);
}
