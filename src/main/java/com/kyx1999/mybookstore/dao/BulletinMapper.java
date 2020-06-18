package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.Bulletin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BulletinMapper {
    int deleteByPrimaryKey(Integer bltid);

    int insert(Bulletin record);

    int insertSelective(Bulletin record);

    Bulletin selectByPrimaryKey(Integer bltid);

    int updateByPrimaryKeySelective(Bulletin record);

    int updateByPrimaryKey(Bulletin record);

    Bulletin[] getTop3Bulletins();

    Bulletin[] getAllBulletins();
}
