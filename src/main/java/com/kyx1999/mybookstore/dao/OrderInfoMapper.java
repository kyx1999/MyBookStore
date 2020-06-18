package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderInfoMapper {
    int deleteByPrimaryKey(Integer oid);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Integer oid);

    OrderInfo[] selectByUserId(Integer uid);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);

    Integer getOrdersCount();

    OrderInfo[] getOrderInfosFromX(Integer index);
}
