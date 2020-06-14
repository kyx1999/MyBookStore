package com.kyx1999.mybookstore.service;

import com.kyx1999.mybookstore.dao.CommentMapper;
import com.kyx1999.mybookstore.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public Comment[] selectByBookIdAndPage(Integer bid, Integer page) {
        return commentMapper.selectByBookIdFromX(bid, (page - 1) * 5);
    }

    public Integer getCommentCountByBid(Integer bid) {
        return commentMapper.getCommentCountByBid(bid);
    }

    public void submitComment(Integer uid, Integer bid, String content) {
        Comment comment = new Comment();
        comment.setUid(uid);
        comment.setBid(bid);
        comment.setContent(content);
        comment.setTime(new Date());
        commentMapper.insertSelective(comment);
    }
}
