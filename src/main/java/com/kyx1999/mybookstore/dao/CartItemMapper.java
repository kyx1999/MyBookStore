package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.CartItem;
import com.kyx1999.mybookstore.model.CartItemKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CartItemMapper {
    int deleteByPrimaryKey(CartItemKey key);

    int insert(CartItem record);

    int insertSelective(CartItem record);

    CartItem selectByPrimaryKey(CartItemKey key);

    int updateByPrimaryKeySelective(CartItem record);

    int updateByPrimaryKey(CartItem record);

    CartItem[] getCartItemByUserId(Integer uid);
}
