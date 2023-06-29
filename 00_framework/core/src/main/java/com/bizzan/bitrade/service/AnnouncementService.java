package com.bizzan.bitrade.service;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.dao.AnnouncementDao;
import com.bizzan.bitrade.entity.Announcement;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @description
 * @date 2019/3/5 15:24
 */
@Service
public class AnnouncementService extends BaseService<Announcement> {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AnnouncementDao announcementDao;

    public Announcement save(Announcement announcement) {
        return announcementDao.save(announcement);
    }

    @Override
    public List<Announcement> findAll() {
        return announcementDao.findAll();
    }

    public Announcement findById(Long id) {
        return announcementDao.findOne(id);
    }

    public void deleteById(Long id) {
        announcementDao.delete(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) {
        for (Long id : ids) {
            Announcement announcement = findById(id);
            Assert.notNull(announcement, "validate id!");
            deleteById(id);
        }
    }

    public int getMaxSort(){
        return announcementDao.findMaxSort();
    }

    public Page<Announcement> findAll(Predicate predicate, Pageable pageable) {
        return announcementDao.findAll(predicate, pageable);
    }


    /**
     * 获取公告上一条
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Announcement getBack(long id, String lang){
        if(lang.indexOf("#")>0){
            Query query = em.createNativeQuery(lang.split("#")[1]);
            query.executeUpdate();
            lang = lang.split("#")[0];
        }
        String sql = "select * from announcement where id < "+id+" AND is_show=1 AND lang='"+lang+"' ORDER by id desc limit 0,1";
        Query nativeQuery = em.createNativeQuery(sql);
        nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List list = nativeQuery.getResultList();
        Announcement back = null;
        // 无需内容传输
    	if(list != null && list.size()>0) {
            back = JSON.parseObject(JSON.toJSONString(list.get(0)),Announcement.class);
    		back.setContent(null);
    	}
        return back;
    }

    /**
     * 获取公告下一条
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Announcement getNext(long id, String lang){
    	Announcement next = announcementDao.getNext(id, lang);
    	if(next != null) {
    		next.setContent(null);
    	}
        return next;
    }
}
