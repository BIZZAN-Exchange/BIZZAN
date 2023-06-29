package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.MemberTransactionDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.vo.MemberTransactionVO;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MemberTransactionService extends BaseService {
    @Autowired
    private MemberTransactionDao transactionDao;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberWeightUpperService memberWeightUpperService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private DataDictionaryService dataDictionaryService;


    /**
     * 条件查询对象 pageNo pageSize 同时传时分页
     *
     * @param booleanExpressionList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<MemberTransaction> queryWhereOrPage(List<BooleanExpression> booleanExpressionList, Integer pageNo, Integer pageSize) {
        List<MemberTransaction> list;
        JPAQuery<MemberTransaction> jpaQuery = queryFactory.selectFrom(QMemberTransaction.memberTransaction);
        if (booleanExpressionList != null) {
            jpaQuery.where(booleanExpressionList.toArray(new BooleanExpression[booleanExpressionList.size()]));
        }
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());
    }

    /**
     * 保存交易记录
     *
     * @param transaction
     * @return
     */
    public MemberTransaction save(MemberTransaction transaction) {
        return transactionDao.saveAndFlush(transaction);
    }

    @Override
    public List<MemberTransaction> findAll() {
        return transactionDao.findAll();
    }


    public MemberTransaction findOne(Long id) {
        return transactionDao.findOne(id);
    }


    public List findAllByWhere(Date startTime, Date endTime, TransactionType type, Long memberId) {
        QMemberTransaction qMemberTransaction = QMemberTransaction.memberTransaction;
        List<BooleanExpression> booleanExpressionList = new ArrayList();
        if (startTime != null) {
            booleanExpressionList.add(qMemberTransaction.createTime.gt(startTime));
        }
        if (endTime != null) {
            booleanExpressionList.add(qMemberTransaction.createTime.lt(endTime));
        }
        if (type != null) {
            booleanExpressionList.add(qMemberTransaction.type.eq(type));
        }
        if (memberId != null) {
            booleanExpressionList.add(qMemberTransaction.memberId.eq(memberId));
        }
        return queryFactory.selectFrom(qMemberTransaction).
                where(booleanExpressionList.toArray(booleanExpressionList.toArray(new BooleanExpression[booleanExpressionList.size()])))
                .fetch();
    }

    /**
     * 根据用户ID，Type获取所有记录
     * @param uid
     * @param type
     * @return
     */
    public List<MemberTransaction> queryByMember(Long uid, TransactionType type){
    	Criteria<MemberTransaction> specification = new Criteria<MemberTransaction>();
        specification.add(Restrictions.eq("memberId", uid, false));
        specification.add(Restrictions.eq("type", type, false));
        return transactionDao.findAll(specification);
    }
    public Page<MemberTransaction> queryByMember(Long uid, Integer pageNo, Integer pageSize, TransactionType type) {
        //排序方式 (需要倒序 这样    Criteria.sort("id","createTime.desc") ) //参数实体类为字段名
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        //查询条件
        Criteria<MemberTransaction> specification = new Criteria<MemberTransaction>();
        specification.add(Restrictions.eq("memberId", uid, false));
        specification.add(Restrictions.eq("type", type, false));
        return transactionDao.findAll(specification, pageRequest);
    }

    public Page<MemberTransaction> queryByMember(Long uid, Integer pageNo, Integer pageSize,TransactionType type,String startDate,String endDate,String symbol) throws ParseException {
        //排序方式 (需要倒序 这样    Criteria.sort("id","createTime.desc") ) //参数实体类为字段名
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo-1, pageSize, orders);
        //查询条件
        Criteria<MemberTransaction> specification = new Criteria<MemberTransaction>();
        specification.add(Restrictions.eq("memberId", uid, false));
        if(type != null){
            specification.add(Restrictions.eq("type",type,false));
        }
        if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
            specification.add(Restrictions.gte("createTime",DateUtil.YYYY_MM_DD_MM_HH_SS.parse(startDate+" 00:00:00"),false));
            specification.add(Restrictions.lte("createTime",DateUtil.YYYY_MM_DD_MM_HH_SS.parse(endDate+" 23:59:59"),false));
        }
        if(StringUtils.isNotEmpty(symbol)){
            specification.add(Restrictions.eq("symbol",symbol,false));
        }
        return transactionDao.findAll(specification, pageRequest);
    }

    public BigDecimal findMatchTransactionSum(Long uid,String symbol){
        List<TransactionType> types = new ArrayList<>();
        types.add(TransactionType.RECHARGE);
        types.add(TransactionType.EXCHANGE);
        types.add(TransactionType.ADMIN_RECHARGE);
        Map<String,Object> result = transactionDao.findMatchTransactionSum(uid,symbol,types);
        return new BigDecimal(result.get("amount").toString());
    }

    public List<MemberTransaction> findMatchTransaction(Long uid,String symbol){
        Sort orders = Criteria.sortStatic("createTime.asc");
        //查询条件
        Criteria<MemberTransaction> specification = new Criteria<MemberTransaction>();
        specification.add(Restrictions.eq("memberId", uid, false));
        specification.add(Restrictions.eq("flag",0,false));
        specification.add(Restrictions.eq("symbol",symbol,false));
        specification.add(Restrictions.gt("amount",0,false));
        List<TransactionType> types = new ArrayList<>();
        types.add(TransactionType.RECHARGE);
        types.add(TransactionType.EXCHANGE);
        types.add(TransactionType.ADMIN_RECHARGE);
        specification.add(Restrictions.in("type",types,false));
        List<MemberTransaction> transactions = transactionDao.findAll(specification,orders);
        return transactions;
    }

    @Transactional
    public void matchWallet(Long uid,String symbol,BigDecimal amount){
        List<MemberTransaction> transactions = findMatchTransaction(uid,symbol);
        BigDecimal deltaAmount = BigDecimal.ZERO;
        MemberWallet gccWallet = walletService.findByCoinUnitAndMemberId("GCC",uid);
        MemberWallet gcxWallet = walletService.findByCoinUnitAndMemberId("GCX",uid);

        for(MemberTransaction transaction:transactions){
            if(amount.compareTo(deltaAmount) > 0) {
                BigDecimal  amt = amount.subtract(deltaAmount).compareTo(transaction.getAmount()) > 0 ? transaction.getAmount() : amount.subtract(deltaAmount);
                deltaAmount = deltaAmount.add(amt);
                transaction.setFlag(1);
            }
            else {
                break;
            }
        }

        gccWallet.setBalance(gccWallet.getBalance().subtract(deltaAmount));
        gcxWallet.setBalance(gcxWallet.getBalance().add(deltaAmount));

        MemberTransaction transaction = new MemberTransaction();
        transaction.setAmount(deltaAmount);
        transaction.setSymbol(gcxWallet.getCoin().getUnit());
        transaction.setAddress(gcxWallet.getAddress());
        transaction.setMemberId(gcxWallet.getMemberId());
        transaction.setType(TransactionType.MATCH);
        transaction.setFee(BigDecimal.ZERO);
        //保存配对记录
        save(transaction);
        if(gccWallet.getBalance().compareTo(BigDecimal.ZERO) < 0){
            gccWallet.setBalance(BigDecimal.ZERO);
        }
    }

    public boolean isOverMatchLimit(String day,double limit) throws Exception {
        BigDecimal totalAmount;
        Date date1 = DateUtil.YYYY_MM_DD_MM_HH_SS.parse(day+" 00:00:00");
        Date date2 = DateUtil.YYYY_MM_DD_MM_HH_SS.parse(day+" 23:59:59");
        Map<String,Object> result = transactionDao.findMatchTransactionSum("GCX",TransactionType.MATCH,date1,date2);
        if(result !=null && result.containsKey("amount")) {
            totalAmount = new BigDecimal(result.get("amount").toString());
        }
        else{
            totalAmount = BigDecimal.ZERO;
        }
        System.out.println("totalAmount:"+totalAmount);
        return totalAmount.doubleValue() >= limit;
    }

    public BigDecimal findMemberDailyMatch(Long uid,String day) throws Exception {
        Date date1 = DateUtil.YYYY_MM_DD_MM_HH_SS.parse(day+" 00:00:00");
        Date date2 = DateUtil.YYYY_MM_DD_MM_HH_SS.parse(day+" 23:59:59");
        Map<String,Object> result = transactionDao.findMatchTransactionSum(uid,"GCX",TransactionType.MATCH,date1,date2);
        if(result !=null && result.containsKey("amount")) {
            return new BigDecimal(result.get("amount").toString());
        }
        else {
            return BigDecimal.ZERO;
        }
    }

    public Page<MemberTransactionVO> joinFind(List<Predicate> predicates, PageModel pageModel){
        List<OrderSpecifier> orderSpecifiers = pageModel.getOrderSpecifiers() ;
        JPAQuery<MemberTransactionVO> query = queryFactory.select(Projections.fields(MemberTransactionVO.class,
                QMemberTransaction.memberTransaction.address,
                QMemberTransaction.memberTransaction.amount,
                QMemberTransaction.memberTransaction.createTime.as("createTime"),
                QMemberTransaction.memberTransaction.fee,
                QMemberTransaction.memberTransaction.flag,
                QMemberTransaction.memberTransaction.id.as("id"),
                QMemberTransaction.memberTransaction.symbol,
                QMemberTransaction.memberTransaction.type,
                QMember.member.username.as("memberUsername"),
                QMember.member.mobilePhone.as("phone"),
                QMember.member.email,
                QMember.member.realName.as("memberRealName"),
                QMember.member.id.as("memberId")))
                .from(QMemberTransaction.memberTransaction, QMember.member);
        predicates.add(QMemberTransaction.memberTransaction.memberId.eq(QMember.member.id));
                query.where(predicates.toArray(new BooleanExpression[predicates.size()]));
        query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
        List<MemberTransactionVO> list = query.offset((pageModel.getPageNo()-1)*pageModel.getPageSize()).limit(pageModel.getPageSize()).fetch();
        long total = query.fetchCount();
        return new PageImpl<>(list, pageModel.getPageable(), total);
    }
    public List<MemberTransactionVO> joinFindAll(List<Predicate> predicates, PageModel pageModel){
        List<OrderSpecifier> orderSpecifiers = pageModel.getOrderSpecifiers() ;
        JPAQuery<MemberTransactionVO> query = queryFactory.select(Projections.fields(MemberTransactionVO.class,
                QMemberTransaction.memberTransaction.address,
                QMemberTransaction.memberTransaction.amount,
                QMemberTransaction.memberTransaction.createTime.as("createTime"),
                QMemberTransaction.memberTransaction.fee,
                QMemberTransaction.memberTransaction.flag,
                QMemberTransaction.memberTransaction.id.as("id"),
                QMemberTransaction.memberTransaction.symbol,
                QMemberTransaction.memberTransaction.type,
                QMember.member.username.as("memberUsername"),
                QMember.member.mobilePhone.as("phone"),
                QMember.member.email,
                QMember.member.realName.as("memberRealName"),
                QMember.member.id.as("memberId")))
                .from(QMemberTransaction.memberTransaction, QMember.member);
        predicates.add(QMemberTransaction.memberTransaction.memberId.eq(QMember.member.id));
                query.where(predicates.toArray(new BooleanExpression[predicates.size()]));
        query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
        List<MemberTransactionVO> list = query.fetch();
        return list;
    }

    /**
     * 清理机器人交易记录
     * @param beforeTime
     * @return
     */
    public int deleteHistory(Date beforeTime) {
    	return transactionDao.deleteHistory(beforeTime);
    }
    public int deleteWalletHistory(Date beforeTime) {
    	return transactionDao.deleteWalletHistory(beforeTime);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendExchangeReward() {
        List<MemberTransaction> list = this.findCanRewardMemberTransactions(TransactionType.EXCHANGE);
        if(list!=null && list.size()>0){
            for(MemberTransaction transaction:list){
            }
        }
    }

    private void doSendReward(MemberTransaction transaction, BigDecimal amount,int type) {
        if(transaction==null){
            return ;
        }
        BigDecimal fee = amount;

        //修改返佣状态
        this.updateReward(transaction.getId(),1);
        //获取上级关系
        MemberWeightUpper upper = memberWeightUpperService.findMemberWeightUpperByMemberId(transaction.getMemberId());
        if(upper==null || upper.getFirstMemberId()==null){
            //没有邀请人
            return ;
        }
        //源用户
        Member member = memberService.findOne(transaction.getMemberId());
        if(member==null){
            //源用户不存在
            return;
        }
        if(org.apache.commons.lang.StringUtils.isEmpty(upper.getUpper())){
            //推荐关系不存在
            return;
        }
        //获取所有上级比重
        List<MemberWeightUpper> uppers = memberWeightUpperService.findAllByUpperIds(upper.getUpper());
        if(uppers==null || uppers.size()==0){
            //没有上级
            return ;
        }

        DataDictionary commission = dataDictionaryService.findByBond("commission_rate");
        BigDecimal totalReward = BigDecimal.ZERO;
        if(commission==null){
            //未设置比例 默认100%
            totalReward = fee;
        }else {
            totalReward = BigDecimalUtils.mulRound(fee,BigDecimal.valueOf(Double.parseDouble(commission.getValue())), 8);
        }
        //当前已返比例
        int currentRate = 0;
        for(MemberWeightUpper weightUpper : uppers){
            //获取用户信息
            Member upMember = memberService.findOne(weightUpper.getMemberId());
            if(upMember==null){
                //不返佣
                continue;
            }
            int userRate = 0;
            if("1".equals(upMember.getSuperPartner())){
                userRate=weightUpper.getRate();
            }
            //应返比例
            int releaseRate = userRate-currentRate;
            if(releaseRate<=0){
                //不返佣
                continue;
            }
            currentRate=userRate;
            BigDecimal rate = BigDecimal.valueOf(releaseRate).divide(BigDecimal.valueOf(100),8,BigDecimal.ROUND_DOWN);
            //返佣

            if(currentRate>=100){
                //停止
                break;
            }
        }
    }

    private void updateReward(Long id, int isReward) {
        transactionDao.updateReward(id,isReward);
    }

    private List<MemberTransaction> findCanRewardMemberTransactions(TransactionType type) {
        Criteria<MemberTransaction> specification = new Criteria<MemberTransaction>();
        specification.add(Restrictions.eq("isReward", 0, false));
        specification.add(Restrictions.eq("type", type, false));
        specification.add(Restrictions.ne("memberId", 1, false));
        return transactionDao.findAll(specification);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendOptionReward() {
        List<MemberTransaction> list = this.findCanRewardMemberTransactions(TransactionType.OPTION_FAIL);
        List<MemberTransaction> list1 = this.findCanRewardMemberTransactions(TransactionType.OPTION_REWARD);
        List<MemberTransaction> list2 = this.findCanRewardMemberTransactions(TransactionType.OPTION_FEE);
        list.addAll(list1);
        list.addAll(list2);
        if(list!=null && list.size()>0){
            for(MemberTransaction transaction:list){
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendSecondReward() {
        List<MemberTransaction> list = this.findCanRewardMemberTransactions(TransactionType.SECOND_REWARD);
        List<MemberTransaction> list1 = this.findCanRewardMemberTransactions(TransactionType.SECOND_FAIL);
        list.addAll(list1);
        if(list!=null && list.size()>0){
            for(MemberTransaction transaction:list){
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateRewardRobot() {
        transactionDao.updateRewardRobot();
    }
}
