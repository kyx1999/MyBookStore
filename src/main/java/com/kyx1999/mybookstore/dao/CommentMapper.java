package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
    int insert(Comment record);

    int insertSelective(Comment record);
}
