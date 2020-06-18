package com.kyx1999.mybookstore.service;

import com.kyx1999.mybookstore.dao.BulletinMapper;
import com.kyx1999.mybookstore.model.Bulletin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BulletinService {

    @Autowired
    private BulletinMapper bulletinMapper;

    public int deleteByPrimaryKey(Integer bltid) {
        return bulletinMapper.deleteByPrimaryKey(bltid);
    }

    public int insertSelective(Bulletin record) {
        return bulletinMapper.insertSelective(record);
    }

    public Bulletin selectByPrimaryKey(Integer bltid) {
        return bulletinMapper.selectByPrimaryKey(bltid);
    }

    public Bulletin[] getTop3Bulletins() {
        return bulletinMapper.getTop3Bulletins();
    }

    public Bulletin[] getAllBulletins() {
        return bulletinMapper.getAllBulletins();
    }

    public void changeBulletin(Integer bltid, String content, Boolean valid) {
        Bulletin bulletin = bulletinMapper.selectByPrimaryKey(bltid);
        bulletin.setContent(content);
        bulletin.setValid(valid);
        bulletinMapper.updateByPrimaryKey(bulletin);
    }
}
