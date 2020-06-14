package com.kyx1999.mybookstore.service;

import com.kyx1999.mybookstore.dao.BookMapper;
import com.kyx1999.mybookstore.dao.CartItemMapper;
import com.kyx1999.mybookstore.dao.OrderInfoMapper;
import com.kyx1999.mybookstore.dao.OrderItemMapper;
import com.kyx1999.mybookstore.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    BookMapper bookMapper;

    @Autowired
    CartItemMapper cartItemMapper;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    public OrderInfo[] getOrderInfosByUserId(Integer uid) {
        return orderInfoMapper.selectByUserId(uid);
    }

    public Map<OrderInfo, OrderItem[]> getOrdersByOrderInfos(OrderInfo[] orderInfos) {
        Map<OrderInfo, OrderItem[]> orders = new HashMap<>();
        for (OrderInfo orderInfo : orderInfos) {
            orders.put(orderInfo, orderItemMapper.selectByOrderId(orderInfo.getOid()));
        }
        return orders;
    }

    @Transactional
    public Boolean submitOrder(Integer uid) {
        CartItem[] cartItems = cartItemMapper.getCartItemByUserId(uid);
        if (cartItems == null || cartItems.length == 0) {
            return false;
        }
        Book[] books = new Book[cartItems.length];
        for (int i = 0; i < books.length; i++) {
            books[i] = bookMapper.selectByPrimaryKey(cartItems[i].getBid());
            if (books[i].getAmount() < cartItems[i].getQty()) {
                return false;
            }
            books[i].setAmount(books[i].getAmount() - cartItems[i].getQty());
        }

        for (Book book : books) {
            bookMapper.updateByPrimaryKeySelective(book);
            if (bookMapper.selectByPrimaryKey(book.getBid()).getAmount() < 0) {
                throw new IllegalArgumentException("库存不足。");
            }
        }

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOid(null);
        orderInfo.setUid(uid);
        orderInfo.setStatus("交易中");
        orderInfo.setTime(new Date());
        orderInfoMapper.insertSelective(orderInfo);

        OrderItem[] orderItems = new OrderItem[cartItems.length];
        for (int i = 0; i < orderItems.length; i++) {
            orderItems[i] = new OrderItem();
            orderItems[i].setOid(orderInfo.getOid());
            orderItems[i].setBid(cartItems[i].getBid());
            orderItems[i].setQty(cartItems[i].getQty());
            orderItemMapper.insertSelective(orderItems[i]);

            CartItemKey cartItemKey = new CartItemKey();
            cartItemKey.setUid(uid);
            cartItemKey.setBid(cartItems[i].getBid());
            cartItemMapper.deleteByPrimaryKey(cartItemKey);
        }

        return true;
    }
}
