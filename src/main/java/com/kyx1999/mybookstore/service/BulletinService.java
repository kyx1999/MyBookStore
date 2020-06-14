package com.kyx1999.mybookstore.service;

import com.kyx1999.mybookstore.dao.BulletinMapper;
import com.kyx1999.mybookstore.model.Bulletin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BulletinService {

    @Autowired
    private BulletinMapper bulletinMapper;

    public Bulletin[] getTop3Bulletins() {
        return bulletinMapper.getTop3Bulletins();
    }
}
