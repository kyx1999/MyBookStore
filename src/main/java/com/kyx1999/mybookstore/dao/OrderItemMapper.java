package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.OrderItem;
import com.kyx1999.mybookstore.model.OrderItemKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderItemMapper {
    int deleteByPrimaryKey(OrderItemKey key);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(OrderItemKey key);

    OrderItem[] selectByOrderId(Integer oid);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    void deleteByOrderId(Integer oid);
}
