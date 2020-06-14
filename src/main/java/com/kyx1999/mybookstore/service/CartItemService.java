package com.kyx1999.mybookstore.service;

import com.kyx1999.mybookstore.dao.CartItemMapper;
import com.kyx1999.mybookstore.model.CartItem;
import com.kyx1999.mybookstore.model.CartItemKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    @Autowired
    private CartItemMapper cartItemMapper;

    public int deleteByPrimaryKey(CartItemKey key) {
        return cartItemMapper.deleteByPrimaryKey(key);
    }

    public void changeCart(Integer uid, Integer bid, Integer qty, Boolean isAdd) {
        CartItemKey cartItemKey = new CartItemKey();
        cartItemKey.setUid(uid);
        cartItemKey.setBid(bid);
        CartItem cartItem = cartItemMapper.selectByPrimaryKey(cartItemKey);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUid(uid);
            cartItem.setBid(bid);
            cartItem.setQty(qty);
            cartItemMapper.insertSelective(cartItem);
        } else {
            if (isAdd) {
                cartItem.setQty(cartItem.getQty() + qty);
            } else {
                cartItem.setQty(qty);
            }
            cartItemMapper.updateByPrimaryKeySelective(cartItem);
        }
    }

    public CartItem[] getCartItemsByUserId(Integer uid) {
        return cartItemMapper.getCartItemByUserId(uid);
    }
}
