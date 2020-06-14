package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommentMapper {
    int insert(Comment record);

    int insertSelective(Comment record);

    Comment[] selectByBookIdFromX(@Param("bid") Integer bid, @Param("index") Integer index);

    Integer getCommentCountByBid(Integer bid);
}
