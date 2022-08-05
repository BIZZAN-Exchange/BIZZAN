package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.RechargeDao;
import com.bizzan.bitrade.dao.RedEnvelopeDetailDao;
import com.bizzan.bitrade.entity.Recharge;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class RechargeService extends BaseService<Recharge> {

    @Autowired
    private RechargeDao rechargeDao;

    public Page<Recharge> findAllByMemberId(Integer memberId, int page, int pageSize) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(page, pageSize, orders);
        Criteria<Recharge> specification = new Criteria<>();
        specification.add(Restrictions.eq("memberid", memberId, false));
        return rechargeDao.findAll(specification, pageRequest);
    }


    public Page<Recharge> findAll(Predicate predicate, Pageable pageable) {
        return rechargeDao.findAll(predicate, pageable);
    }

    public Recharge save(Recharge recharge){
        return rechargeDao.save(recharge);
    }


    public Iterable<Recharge> findAllOut(Predicate predicate) {
        return rechargeDao.findAll(predicate, new Sort(Sort.Direction.DESC, "id"));
    }

}
