package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.GoogleAuthenticatorUtil;
import com.bizzan.bitrade.util.Md5;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.BigDecimalUtils.compare;
import static com.bizzan.bitrade.util.BigDecimalUtils.sub;
import static com.bizzan.bitrade.util.MessageResult.error;
import static org.springframework.util.Assert.*;

/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @date 2021年01月26日
 */
@RestController
@Slf4j
@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
public class WithdrawController {
    @Autowired
    private MemberAddressService memberAddressService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private WithdrawRecordService withdrawApplyService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Autowired
    private MemberTransactionService memberTransactionService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private CoinextService coinextService;

    /**
     * 增加提现地址
     *
     * @param address
     * @param unit
     * @param remark
     * @param user
     * @return
     */
    @RequestMapping("address/add")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult addAddress(String address, String unit, String remark, String fundpwd, @SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        hasText(address, sourceService.getMessage("MISSING_COIN_ADDRESS"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        hasText(fundpwd, sourceService.getMessage("MISSING_JYPASSWORD"));
        Member member = memberService.findOne(user.getId());
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(fundpwd + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));

        MessageResult result = memberAddressService.addMemberAddress(user.getId(), address, unit, remark);
        if (result.getCode() == 0) {
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_SUCCESS"));
        } else if (result.getCode() == 500) {
            result.setMessage(sourceService.getMessage("ADD_ADDRESS_FAILED"));
        } else if (result.getCode() == 600) {
            result.setMessage(sourceService.getMessage("COIN_NOT_SUPPORT"));
        }
        return result;
    }

    /**
     * 删除提现地址
     *
     * @param id
     * @param user
     * @return
     */
    @RequestMapping("address/delete")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult deleteAddress(long id, @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        MessageResult result = memberAddressService.deleteMemberAddress(user.getId(), id);
        if (result.getCode() == 0) {
            result.setMessage(sourceService.getMessage("DELETE_ADDRESS_SUCCESS"));
        } else {
            result.setMessage(sourceService.getMessage("DELETE_ADDRESS_FAILED"));
        }
        return result;
    }

    /**
     * 提现地址分页信息
     *
     * @param user
     * @param pageNo
     * @param pageSize
     * @param unit
     * @return
     */
    @RequestMapping("address/page")
    public MessageResult addressPage(@SessionAttribute(SESSION_MEMBER) AuthMember user, int pageNo, int pageSize, String unit) {
        Page<MemberAddress> page = memberAddressService.pageQuery(pageNo, pageSize, user.getId(), unit);
        Page<ScanMemberAddress> scanMemberAddresses = page.map(x -> ScanMemberAddress.toScanMemberAddress(x));
        MessageResult result = MessageResult.success();
        result.setData(scanMemberAddresses);
        return result;
    }

    /**
     * 支持提现的地址
     *
     * @return
     */
    @RequestMapping("support/coin")
    public MessageResult queryWithdraw() {
        List<Coin> list = coinService.findAllCanWithDraw();
        List<String> list1 = new ArrayList<>();
        list.stream().forEach(x -> list1.add(x.getUnit()));
        MessageResult result = MessageResult.success();
        result.setData(list1);
        return result;
    }

    /**
     * 提现币种详细信息
     *
     * @param user
     * @return
     */
    @RequestMapping("support/coin/info")
    public MessageResult queryWithdrawCoin(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        List<Coin> list = coinService.findAllCanWithDraw();
        List<MemberWallet> list1 = memberWalletService.findAllByMemberId(user.getId());
        long id = user.getId();
        List<WithdrawWalletInfo> list2 = list1.stream().filter(x -> list.contains(x.getCoin())).map(x ->
                WithdrawWalletInfo.builder()
                        .balance(x.getBalance())
                        .withdrawScale(x.getCoin().getWithdrawScale())
                        .maxTxFee(x.getCoin().getMaxTxFee())
                        .minTxFee(x.getCoin().getMinTxFee())
                        .minAmount(x.getCoin().getMinWithdrawAmount())
                        .maxAmount(x.getCoin().getMaxWithdrawAmount())
                        .name(x.getCoin().getName())
                        .nameCn(x.getCoin().getNameCn())
                        .threshold(x.getCoin().getWithdrawThreshold())
                        .unit(x.getCoin().getUnit())
                        .accountType(x.getCoin().getAccountType())
                        .canAutoWithdraw(x.getCoin().getCanAutoWithdraw())
                        .addresses(memberAddressService.queryAddress(id, x.getCoin().getName())).build()
        ).collect(Collectors.toList());
        MessageResult result = MessageResult.success();
        result.setData(list2);
        return result;
    }


    /**
     * 申请提币(请到PC端提币或升级APP)
     * 没有验证码校验
     *
     * @param user
     * @param unit
     * @param address
     * @param amount
     * @param fee
     * @param remark
     * @param jyPassword
     * @return
     * @throws Exception
     */
    @RequestMapping("apply")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdraw(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit, String address,
                                  BigDecimal amount, BigDecimal fee, String remark, String jyPassword) throws Exception {
        return MessageResult.success(sourceService.getMessage("WITHDRAW_TO_PC"));
    }

    /**
     * 申请提币（添加验证码校验）
     *
     * @param user
     * @param unit
     * @param address
     * @param amount
     * @param fee
     * @param remark
     * @param jyPassword
     * @return
     * @throws Exception
     */
    @RequestMapping("apply/code")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit, String address,
                                      BigDecimal amount, BigDecimal fee, String remark, String jyPassword) throws Exception {
        hasText(jyPassword, sourceService.getMessage("MISSING_JYPASSWORD"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        Coin coin = coinService.findByUnit(unit);
        amount.setScale(coin.getWithdrawScale(), BigDecimal.ROUND_DOWN);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));

        isTrue(coin.getStatus().equals(CommonStatus.NORMAL) && coin.getCanWithdraw().equals(BooleanEnum.IS_TRUE), sourceService.getMessage("COIN_NOT_SUPPORT"));
        isTrue(compare(fee, new BigDecimal(String.valueOf(coin.getMinTxFee()))), sourceService.getMessage("CHARGE_MIN") + coin.getMinTxFee());
        isTrue(compare(new BigDecimal(String.valueOf(coin.getMaxTxFee())), fee), sourceService.getMessage("CHARGE_MAX") + coin.getMaxTxFee());
        isTrue(compare(coin.getMaxWithdrawAmount(), amount), sourceService.getMessage("WITHDRAW_MAX") + coin.getMaxWithdrawAmount());
        isTrue(compare(amount, coin.getMinWithdrawAmount()), sourceService.getMessage("WITHDRAW_MIN") + coin.getMinWithdrawAmount());
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, user.getId());
        isTrue(compare(memberWallet.getBalance(), amount), sourceService.getMessage("INSUFFICIENT_BALANCE"));
        isTrue(memberWallet.getIsLock() == BooleanEnum.IS_FALSE, "The wallet is locked.");
        Member member = memberService.findOne(user.getId());
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));

        MessageResult result = memberWalletService.freezeBalance(memberWallet, amount);
        if (result.getCode() != 0) {
            throw new InformationExpiredException("Information Expired");
        }
        WithdrawRecord withdrawApply = new WithdrawRecord();
        withdrawApply.setCoin(coin);
        withdrawApply.setFee(fee);
        withdrawApply.setArrivedAmount(sub(amount, fee));
        withdrawApply.setMemberId(user.getId());
        withdrawApply.setTotalAmount(amount);
        withdrawApply.setAddress(address);
        withdrawApply.setRemark(remark);
        withdrawApply.setCanAutoWithdraw(coin.getCanAutoWithdraw());

        //提币数量低于或等于阈值并且该币种支持自动提币
        if (amount.compareTo(coin.getWithdrawThreshold()) <= 0 && coin.getCanAutoWithdraw().equals(BooleanEnum.IS_TRUE)) {
            withdrawApply.setStatus(WithdrawStatus.WAITING);
            withdrawApply.setIsAuto(BooleanEnum.IS_TRUE);
            withdrawApply.setDealTime(withdrawApply.getCreateTime());
            WithdrawRecord withdrawRecord = withdrawApplyService.save(withdrawApply);
            JSONObject json = new JSONObject();
            json.put("uid", user.getId());
            //提币总数量
            json.put("totalAmount", amount);
            //手续费
            json.put("fee", fee);
            //预计到账数量
            json.put("arriveAmount", sub(amount, fee));
            //币种
            json.put("coin", coin);
            //提币地址
            json.put("address", address);
            //提币记录id
            json.put("withdrawId", withdrawRecord.getId());
            kafkaTemplate.send("withdraw", coin.getUnit(), json.toJSONString());
            return MessageResult.success(sourceService.getMessage("APPLY_SUCCESS"));
        } else {
            withdrawApply.setStatus(WithdrawStatus.PROCESSING);
            withdrawApply.setIsAuto(BooleanEnum.IS_FALSE);
            if (withdrawApplyService.save(withdrawApply) != null) {
                return MessageResult.success(sourceService.getMessage("APPLY_AUDIT"));
            } else {
                throw new InformationExpiredException("Information Expired");
            }
        }
    }


    /**
     * 提币记录
     *
     * @param user
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("record")
    public MessageResult pageWithdraw(@SessionAttribute(SESSION_MEMBER) AuthMember user, int page, int pageSize) {
        MessageResult mr = new MessageResult(0, "success");
        Page<WithdrawRecord> records = withdrawApplyService.findAllByMemberId(user.getId(), page, pageSize);
        records.map(x -> ScanWithdrawRecord.toScanWithdrawRecord(x));
        mr.setData(records);
        return mr;
    }


    /**
     * 提币记录
     */
    @GetMapping("list")
    public MessageResult list(@SessionAttribute(SESSION_MEMBER) AuthMember user, int page, int pageSize) {
        MessageResult mr = new MessageResult(0, "success");
        Page<Withdraw> records = withdrawService.findAllByMemberId((int) user.getId(), page, pageSize);
        mr.setData(records);
        return mr;
    }


    /**
     * 提币
     *
     * @return
     */
    @PostMapping("create")
    public MessageResult create(@SessionAttribute(SESSION_MEMBER) AuthMember user,
                                @RequestParam(value = "coinName") String coinName,
                                @RequestParam(value = "coinprotocol") Integer coinprotocol,
                                @RequestParam(value = "address") String address,
                                @RequestParam(value = "money") double money,
                                @RequestParam(value = "code") String code,
                                @RequestParam(value = "codeType") Integer codeType,
                                @RequestParam(value = "payPwd") String payPwd) throws Exception {


        Long memberid = user.getId();
        // 查询资金密码
        Member member = memberService.findOne(user.getId());
        isTrue(Md5.md5Digest(payPwd + member.getSalt()).toLowerCase().equals(member.getJyPassword()), sourceService.getMessage("ERROR_JYPASSWORD"));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //验证验证码
        if(codeType==1){
            //邮箱
            String email = member.getEmail();
            Object redisCode =valueOperations.get(SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email);
            notNull(redisCode, sourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
            if (!redisCode.toString().equals(code)) {
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email);
            }
        }else if(codeType==2){
            //手机
            String key = SysConstant.PHONE_WITHDRAW_MONEY_CODE_PREFIX + member.getMobilePhone();
            Object redisCode =valueOperations.get(key);
            notNull(redisCode, sourceService.getMessage("VERIFICATION_CODE_NOT_EXISTS"));
            if (!redisCode.toString().equals(code)) {
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            } else {
                valueOperations.getOperations().delete(key);
            }
        }else{
            //Google
            long t = System.currentTimeMillis();
            GoogleAuthenticatorUtil ga = new GoogleAuthenticatorUtil();
            //  ga.setWindowSize(0); // should give 5 * 30 seconds of grace...
            boolean r = ga.check_code(member.getGoogleKey(), Long.valueOf(code), t);
            if(!r){
                return error(sourceService.getMessage("VERIFICATION_CODE_INCORRECT"));
            }
        }

        // 查询币种配置
        Coinext firstByCoinnameAndProtocol = coinextService.findFirstByCoinnameAndProtocol(coinName, coinprotocol);

        if (firstByCoinnameAndProtocol == null) {
            return MessageResult.error(sourceService.getMessage("COIN_ILLEGAL"));
        }

        Integer iswithdraw = firstByCoinnameAndProtocol.getIswithdraw();
        if (iswithdraw != 1) {
            return MessageResult.error(sourceService.getMessage("COIN_NOT_SUPPORT"));
        }
        Integer decimals = firstByCoinnameAndProtocol.getDecimals();
        Integer isautowithdraw = firstByCoinnameAndProtocol.getIsautowithdraw();

        // 保留两位小数
        BigDecimal bigDecimalMoney = new BigDecimal(money).setScale(decimals, BigDecimal.ROUND_DOWN);

        BigDecimal bigDecimalMinwithdraw = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMinwithdraw());
        BigDecimal bigDecimalMaxwithdraw = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMaxwithdraw());
        BigDecimal bigDecimalWithdrawfee = BigDecimal.valueOf(firstByCoinnameAndProtocol.getWithdrawfee());
        BigDecimal bigDecimalMinwithdrawfee = BigDecimal.valueOf(firstByCoinnameAndProtocol.getMinwithdrawfee());

        // 如果提现金额为0或者负数
        if (bigDecimalMoney.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN")+"0");
        }

        // 如果提现金额小于最低提现数量
        if (bigDecimalMoney.compareTo(bigDecimalMinwithdraw) < 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN")+bigDecimalMinwithdraw.toPlainString());
        }

        // 如果提现金额大于最大金额
        if (bigDecimalMoney.compareTo(bigDecimalMaxwithdraw) > 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MAX")+bigDecimalMaxwithdraw.toPlainString());
        }

        BigDecimal fee = bigDecimalMoney.multiply(bigDecimalWithdrawfee).setScale(decimals, BigDecimal.ROUND_DOWN);
        // 如果手续费小于最低手续费则使用最低手续费
        if (fee.compareTo(bigDecimalMinwithdrawfee) < 0) {
            fee = bigDecimalMinwithdrawfee;
        }

        BigDecimal Real_Money = bigDecimalMoney.subtract(fee).setScale(decimals, BigDecimal.ROUND_DOWN);

        // 如果实际到账为0或者负数
        if (Real_Money.compareTo(new BigDecimal(0)) <= 0) {
            return MessageResult.error(sourceService.getMessage("WITHDRAW_MIN"));
        }
        Coin coin = coinService.findOne(coinName);
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, memberid);
        isTrue(compare(memberWallet.getBalance(), bigDecimalMoney), sourceService.getMessage("INSUFFICIENT_BALANCE"));

        isTrue(memberWallet.getIsLock() == BooleanEnum.IS_FALSE, "钱包已锁定");


        Withdraw withdraw = new Withdraw();
        withdraw.setMemberid(memberid);
        withdraw.setAddtime(new Date().getTime());
        withdraw.setCoinid(0);
        withdraw.setCoinname(coinName);
        withdraw.setAddress(address);
        withdraw.setMoney(bigDecimalMoney.doubleValue());
        withdraw.setFee(fee.doubleValue());
        withdraw.setReal_money(Real_Money.doubleValue());
        withdraw.setProcessmold(0);
        withdraw.setHash("");
        withdraw.setStatus(isautowithdraw == 1 ? 1 : 0);
        withdraw.setProcesstime(0L);
        withdraw.setWithdrawinfo("");
        withdraw.setRemark("");
        withdraw.setProtocol(coinprotocol);
        withdraw.setProtocolname(firstByCoinnameAndProtocol.getProtocolname());

        withdrawService.create(withdraw);

        return MessageResult.success();
    }

}
