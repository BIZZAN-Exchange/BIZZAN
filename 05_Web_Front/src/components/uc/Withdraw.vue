<template>
  <div class="nav-rights withdraw">
    <div class="nav-right">
      <div class="rightarea">
        <section class="trade-groups merchant-tops" style="margin-top: 10px;">
          <!-- <i class="merchant-icon tips"></i>
          <span class="tips-word">{{$t('uc.finance.withdraw.pickup')}}</span> -->
          <!--<router-link to="/uc/withdraw/address">{{$t('uc.finance.withdraw.addressmanager')}}</router-link>-->
          <router-link to="/uc/withdraw/code">
            {{ $t('uc.finance.withdraw.withdrawbycode') }}
          </router-link>
        </section>
        <section>
          <div class="table-inner action-box">
            <!-- <i class="angle" style="right: 27px;"></i> -->
            <div class="action-inner">
              <div class="inner-box">
                <div class="form-group inner-left">
                  <p class="describe input-title">{{ $t('uc.finance.withdraw.symbol') }}</p>
                  <Select v-model="coinType" style="margin-top: 14px;" @on-change="changeCoin">
                    <Option v-for="item in coinList" :value="item.name" :key="item.name">{{ item.name }}</Option>
                  </Select>
                </div>
                <div class="form-group form-network">
                  <label class="input-title">{{ $t('uc.finance.withdraw.network') }}</label>
                  <div class="control-input-group">
                    <Select ref="network" v-model="withdrawFrom.protocol" :placeholder="$t('common.pleaseselect')" @on-change="changeCoinext">
                      <Option v-for="item in comCoinextList()" :value="item.protocol" :key="item.protocol">{{ item.protocolname }}</Option>
                    </Select>
                  </div>

                  <div class="balance-fee">
                    <p class="balance-tips bf-tips">
                      <span class="title">{{ $t('uc.finance.withdraw.avabalance') }}：</span>
                      {{ balance|toFloor }}
                    </p>
                  </div>
                </div>
                <div class="form-group form-address">
                  <label for="controlAddress"
                         class="controlAddress describe input-title">{{ $t('uc.finance.withdraw.address') }}</label>
                  <div class="control-input-group">
                    <input class="input-address" v-model="withdrawFrom.address" :placeholder="$t('common.inputpleaseselect')"/>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-group-container">
              <div class="form-group form-amount">
                <label class="label-amount input-title"> {{ $t('uc.finance.withdraw.num') }}</label>
                <div class="input-group" style="position: relative;">
                  <Input v-model="withdrawFrom.money" @on-keyup="moneyChange" :placeholder="$t('uc.finance.withdraw.numtip1')" size="large"></Input>
                  <span class="input-group-addon addon-tag uppercase firstt">{{ coinType }}</span>
                </div>
                <div class="balance-fee">
                  <p class="balance-tips bf-tips">
                    <span class="title">{{ $t('uc.finance.withdraw.arriamount') }}：</span>
                    {{ comMoney < 0 ? '--' : comMoney + ' ' + coinType }}
                  </p>
                  <p class="fee-tips bf-tips">
                    <span class="title">{{ $t('uc.finance.withdraw.fee') }}：</span>
                    {{comFee < 0 ? '--' : comFee + ' ' + coinType}}
                  </p>
                </div>
              </div>
            </div>
            <div class="action-foot">
              <Button id="withdrawSubmit" v-if="coinextItem.iswithdraw !== 0" long size="large" type="primary" style="height:40px;" :loading="withdrawLoading" @click="applyShow">
                {{ $t('uc.finance.withdraw.pickup') }}
              </Button>

              <Button v-else class="withdraw-disable" long size="large" style="height:40px;" :loading="withdrawLoading" disabled>
                {{ $t('uc.finance.withdraw.withdrawDisable') }}
              </Button>

            </div>
            <div class="action-content pt10">
              <div class="action-body" style="text-align: left;">
                <p class="acb-p1">{{ $t('common.tip') }}</p>
                <p class="acb-p2">• {{ $t('uc.finance.withdraw.msg3') }}：{{coinextItem.minwithdraw ? coinextItem.minwithdraw : '--' + ' ' + coinType}}。<br>•
                  {{ $t('uc.finance.withdraw.msg5') }}<br>• {{ $t('uc.finance.withdraw.msg6') }} </p>
              </div>
            </div>
            <div class="action-content">
              <div class="action-body">
                <div style="text-align: left" class="acb-p1">{{ $t('uc.finance.withdraw.record') }}</div>
                <div class="order-table">
                  <Table :no-data-text="$t('common.nodata')" :columns="tableColumnsWithdraw" :data="tableWithdraw"
                         :loading="loading"></Table>
                  <div id="pages">
                    <div style="float: right;">
                      <Page class="pages_a" :total="tableWithdrawTotal" :current="pageNo" @on-change="changePage"></Page>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

<!--        <Modal-->
<!--            :title="$t('uc.safe.fundpwd')"-->
<!--            v-model="withdrawFromVisible"-->
<!--            :mask-closable="false"-->
<!--        >-->

<!--          <Form ref="formInline" :model="withdrawFrom" inline>-->
<!--            <FormItem prop="user"></FormItem>-->
<!--            <FormItem :label="$t('uc.safe.fundpwd')" prop="payPwd">-->
<!--              <Input name="a" v-model="withdrawFrom.payPwd" :placeholder="$t('common.inputpleaseselect')"/>-->
<!--            </FormItem>-->
<!--            <FormItem :label="$t('uc.safe.fundpwd')" prop="payPwd">-->
<!--              <Input name="a" v-model="withdrawFrom.payPwd" type="password" :placeholder="$t('common.inputpleaseselect')"/>-->
<!--            </FormItem>-->
<!--          </Form>-->
<!--          <div slot="footer">-->
<!--            <Button @click="withdrawFromVisible = false">{{$t('common.close')}}</Button>-->
<!--            <Button type="primary" @click="apply" :loading="withdrawLoading">{{$t('common.ok')}}</Button>-->
<!--          </div>-->
<!--        </Modal>-->


        <Modal
            :title="$t('uc.finance.withdraw.safevalidate')"
            v-model="withdrawFromVisible"
            :mask-closable="false"
        >
          <Form ref="withdrawFrom" :label-width="95">
            <!-- payPwd -->
            <FormItem :label="$t('uc.safe.fundpwd')" prop="payPwd">
              <Input v-model="withdrawFrom.payPwd" size="large" type="password" :placeholder="$t('common.inputpleaseselect')" ></Input>
            </FormItem>
            <FormItem v-if="withdrawFrom.codeType==3" :label="$t('uc.forget.googleAuth')" prop="payPwd">
              <Input v-model="withdrawFrom.code" size="large" :placeholder="$t('common.inputpleaseselect')" ></Input>
            </FormItem>
            <!-- 邮箱验证码 -->
            <FormItem v-if="withdrawFrom.codeType==1" :label="$t('uc.safe.emailcode')" prop="vailCode3">
              <Input v-model="withdrawFrom.code" size="large">
                <!-- <Button slot="append">点击获取</Button> -->
                <div class="timebox" slot="append">
                  <Button @click="send(3)" style="padding: 8px 15px 8px;border-bottom-left-radius: unset;border-top-left-radius: unset;" :disabled="sendMsgDisabled3">
                    <span v-if="sendMsgDisabled3">{{time3+$t('uc.safe.second')}}</span>
                    <span v-if="!sendMsgDisabled3">{{$t('uc.safe.clickget')}}</span>
                  </Button>
                </div>
              </Input>
            </FormItem>

            <!-- 手机验证码 -->
            <FormItem v-if="withdrawFrom.codeType==2" :label="$t('uc.safe.phonecode')" prop="vailCode2">
              <Input v-model="withdrawFrom.code" size="large">
                <!-- <Button slot="append">点击获取</Button> -->
                <div class="timebox" slot="append">
                  <Button @click="send(2)" :disabled="sendMsgDisabled2">
                    <span v-if="sendMsgDisabled2">{{time2+$t('uc.safe.second')}}</span>
                    <span v-if="!sendMsgDisabled2">{{$t('uc.safe.clickget')}}</span>
                  </Button>
                </div>
              </Input>
            </FormItem>

          </Form>
          <div slot="footer">
            <Button @click="withdrawFromVisible = false">{{$t('common.close')}}</Button>
            <Button type="primary" @click="apply" :loading="withdrawLoading">{{$t('common.ok')}}</Button>
          </div>
        </Modal>


      </div>
    </div>
  </div>
</template>
<script>
import { parseTime } from "../../assets/js/util"
import {accSub} from "../../assets/js/decimal";
export default {
  data() {
    return {
      user: {},
      modal: false,
      currentCoin: {},
      transaction: {
        page: 0,
        pageSize: 10,
        total: 0
      },
      balance: 0,
      coinType: "",
      coinList: [],
      coinextList: [],
      coinextItem: {},
      oldMoney: 0,
      withdrawLoading: false,
      withdrawFrom: {
        protocol: "",
        money: 0,
        address: "",
        payPwd: "",
        code: "",
        codeType: 1,
      },
      withdrawFromRules: {

      },
      withdrawFromVisible: false,
      loading: true,
      pageNo: 1,
      pageSize: 10,
      tableWithdraw: [],
      tableWithdrawTotal: 0,

      time2: 60, // 发送验证码倒计时
      time3: 60, // 发送验证码倒计时
      sendMsgDisabled2: false,
      sendMsgDisabled3: false,
    };
  },
  methods: {
    moneyChange() {
      var re1 = new RegExp(
          "([0-9]+.[0-9]{6})[0-9]*",
          ""
      );
      this.withdrawFrom.money = this.withdrawFrom.money.replace(re1, "$1")
    },
    changeCoin() {
      this.withdrawFrom.protocol = ""
      this.balance = 0
      this.coinextItem = {}
      this.getBalance()
    },
    changeCoinext() {
      this.coinextItem = {}
      for (let item of this.coinextList) {
        if (item.coinname === this.coinType && item.protocol === this.withdrawFrom.protocol) {
          this.coinextItem = item
          this.withdrawFrom.money = item.minwithdraw
          this.oldMoney = item.minwithdraw
        }
      }
    },
    getCoinList() {
      //获取
      this.$http.get(this.host + "/uc/coin/list").then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.coinList = resp.data.coinList
          // 如果没有值，则默认第一个
          if (!this.coinType && this.coinList.length > 0) {
            this.coinType = this.coinList[0].name
            this.getBalance()
          }
          this.coinextList = resp.data.coinextList
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    getBalance() {
      //获取
      this.$http.get(this.host + "/uc/coin/balance?coinName=" + this.coinType).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.balance = resp.data
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    changePage(index) {
      this.pageNo = index;
      this.getList();
    },
    getList() {
      // 获取列表
      let params = {};
      params["page"] = this.pageNo - 1;
      params["pageSize"] = this.pageSize;
      this.$http
          .post(this.host + "/uc/withdraw/list", params)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.tableWithdraw = resp.data.content || [];
              this.tableWithdrawTotal = resp.data.totalElements;
            } else {
              this.$Message.error(resp.message);
            }
            this.loading = false
          });
    },
    applyShow() {
      if (!this.coinType || !this.withdrawFrom.protocol) {
        this.$Message.error(this.$t('uc.finance.withdraw.symboltip'));
        return false
      }
      if (!this.withdrawFrom.address) {
        this.$Message.error(this.$t('uc.finance.withdraw.addresstip'));
        return false
      }
      if (this.withdrawFrom.money <= 0) {
        this.$Message.error(this.$t('uc.finance.withdraw.amounttip'));
        return false
      }
      if (this.withdrawFrom.money > this.coinextItem.maxwithdraw) {
        this.$Message.error(this.$t('uc.finance.withdraw.moneymaxttip') + this.coinextItem.maxwithdraw);
        return false
      }
      if (this.withdrawFrom.money < this.coinextItem.minwithdraw) {
        this.$Message.error(this.$t('uc.finance.withdraw.moneyminttip') + this.coinextItem.minwithdraw);
        return false
      }
      if (this.comMoney <= 0) {
        this.$Message.error(this.$t('uc.finance.withdraw.moneyttip'));
        return false
      }
      this.withdrawFromVisible = true
    },

    send(index) {
      let me = this;
      if (index == 2) {
        //获取手机code
        this.$http
            .post(this.host + "/uc/mobile/withdraw/code")
            .then(response => {
              var resp = response.body;
              if (resp.code == 0) {
                me.sendMsgDisabled2 = true;
                let interval = window.setInterval(function() {
                  if (me.time2-- <= 0) {
                    me.time2 = 60;
                    me.sendMsgDisabled2 = false;
                    window.clearInterval(interval);
                  }
                }, 1000);
              } else {
                this.$Message.error(resp.message);
              }
            });
      } else if (index == 3) {
        //获取邮件验证
        this.$http
            .post(this.host + "/uc/email/withdraw/code")
            .then(response => {
              var resp = response.body;
              if (resp.code == 0) {
                me.sendMsgDisabled3 = true;
                let interval = window.setInterval(function() {
                  if (me.time3-- <= 0) {
                    me.time3 = 60;
                    me.sendMsgDisabled3 = false;
                    window.clearInterval(interval);
                  }
                }, 1000);
              } else {
                this.$Message.error(resp.message);
              }
            });
      }
    },

    apply() {
      if (this.withdrawLoading) {
        return false
      }
      if (!this.coinType || !this.withdrawFrom.protocol) {
        this.$Message.error(this.$t('uc.finance.withdraw.symboltip'));
        return false
      }
      if (!this.withdrawFrom.address) {
        this.$Message.error(this.$t('uc.finance.withdraw.addresstip'));
        return false
      }
      if (this.withdrawFrom.money <= 0) {
        this.$Message.error(this.$t('uc.finance.withdraw.amounttip'));
        return false
      }
      if (!this.withdrawFrom.code) {
        this.$Message.error(this.$t("uc.safe.codetip"));
        return false
      }

      //获取
      let data = {
        coinName: this.coinType,
        coinprotocol: this.withdrawFrom.protocol,
        address: this.withdrawFrom.address,
        money: this.withdrawFrom.money,
        payPwd: this.withdrawFrom.payPwd,
        code: this.withdrawFrom.code,
        codeType: this.withdrawFrom.codeType
      }
      this.withdrawLoading = true
      this.$http.post(this.host + "/uc/withdraw/create", data).then(response => {
        var resp = response.body;
        this.withdrawLoading = false
        if (resp.code == 0) {
          this.$Message.success(this.$t('uc.finance.withdraw.shenqing'));
          this.withdrawFrom.money = this.oldMoney
          this.withdrawFrom.address = ""
          this.withdrawFrom.payPwd = ""
          this.withdrawFromVisible = false
          this.getBalance()
          this.getList()
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    getMember() {
      //获取个人安全信息
      let self = this;
      this.$http.post(this.host + "/uc/approve/security/setting").then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.user = resp.data;
          if (resp.data.realName == null || resp.data.realName == "") {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc: this.$t("otc.publishad.submittip1")
            });
            // 判断是否实名认证，未认证跳转到实名认证页面；
            //this.$Message.success(this.$t("otc.publishad.submittip1"));
            self.$router.push("/uc/safe");
          }
          // else if (resp.data.phoneVerified == 0) {
          //   this.$Notice.error({
          //     title: this.$t("common.tip"),
          //     desc: this.$t("otc.publishad.submittip2")
          //   });
          //   // 判断是否是手机号0，1，未认证跳转到实名认证页面；
          //   //this.$Message.success(this.$t("otc.publishad.submittip2"));
          //   self.$router.push("/uc/safe");
          // }
          else if (resp.data.fundsVerified == 0) {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc: this.$t("otc.publishad.submittip3")
            });
            // 判断是否设置交易密码，未认证跳转到实名认证页面；
            //this.$Message.success(this.$t("otc.publishad.submittip3"));
            self.$router.push("/uc/safe");
          }
          if(resp.data.emailVerified==1){
            this.withdrawFrom.codeType=1;
          }
          if(resp.data.phoneVerified==1){
            this.withdrawFrom.codeType=2;
          }
          if(resp.data.googleStatus==1){
            this.withdrawFrom.codeType=3;
          }

        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    _accMul(arg1, arg2) { //乘法
      if(arg1 == 0 || !arg1) return "0";
      if(arg2 == 0 || !arg2) return "0";
      var m = 0,
          s1 = arg1.toString(),
          s2 = arg2.toString();
      try {
        m += s1.split(".")[1].length
      } catch(e) {
        // console.log(e);
      }
      try {
        m += s2.split(".")[1].length
      } catch(e) {
        // console.log(e);
      }
      return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
    },
  },
  created() {
    this.getMember();
    this.$http.options.emulateJSON = false;
    this.coinType = this.$route.query.name || "";
    if (this.coinType) {
      this.getBalance()
    }
    this.getCoinList()
    this.getList()
  },
  computed: {
    member: function () {
      console.log(this.$store.getters.member);
      return this.$store.getters.member;
    },
    tableColumnsWithdraw() {
      let columns = [],
          filters = [];
      if (this.coinList.length > 0) {
        this.coinList.forEach(v => {
          filters.push({
            label: v.unit,
            value: v.unit
          });
        });
      }
      columns.push({
        title: this.$t("uc.finance.withdraw.time"),
        width: 180,
        key: "addtime",
        render: function (h, params) {
          return h("span", parseTime(params.row.addtime));
        }
      });
      columns.push({
        title: this.$t("uc.finance.withdraw.symbol"),
        key: "coinname"
      });
      columns.push({
        title: this.$t("uc.finance.withdraw.protocol"),
        key: "protocolname"
      });
      columns.push({
        title: this.$t("uc.finance.withdraw.address"),
        key: "address",
        render: (h, params) => {
          return h('div', [
            h('Tooltip', {
              props: { placement: 'top' }
            }, [
              h('span', {
                style: {
                  display: 'inline-block',
                  width: params.column._width*0.9+'px',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                },
              }, params.row.address),
              h('span', {
                slot: 'content',
                style: { whiteSpace: 'normal', wordBreak: 'break-all' }
              },params.row.address)
            ])
          ])
        }
      });
      columns.push({
        title: this.$t("uc.finance.withdraw.num"),
        key: "money"
      });
      columns.push({
        title: this.$t("uc.finance.withdraw.fee"),
        key: "fee"
      });
      columns.push({
        title: this.$t("uc.finance.withdraw.txid"),
        key: "hash"
      });
      columns.push({
        title: this.$t("uc.finance.withdraw.status"),
        key: "status",
        render: (h, params) => {
          let text = "";
          if (params.row.status == -1) {
            text = this.$t("uc.finance.withdraw.status_3");
          } else if (params.row.status == 0) {
            text = this.$t("uc.finance.withdraw.status_1");
          } else if (params.row.status == 1) {
            text = this.$t("uc.finance.withdraw.status_2");
          } else if (params.row.status == 2) {
            text = this.$t("uc.finance.withdraw.status_4");
          } else if (params.row.status == 3) {
            text = this.$t("uc.finance.withdraw.status_3");
          }
          return h("div", [h("p", text)]);
        }
      });
      return columns;
    },
    comCoinextList() {
      return () => {
        let list = []
        for (let item of this.coinextList) {
          if (item.coinname == this.coinType) {
            list.push(item)
          }
        }
        return list
      }
    },
    comFee() {
      let item = this.coinextItem
      let withdrawfee = item.withdrawfee || 0
      let minwithdrawfee = item.minwithdrawfee || 0
      let fee = this._accMul(withdrawfee, this.withdrawFrom.money)
      if (fee < minwithdrawfee) {
        fee = minwithdrawfee
      }
      return fee
    },
    comMoney() {
      let fee = this.comFee
      let money = accSub(this.withdrawFrom.money, fee)
      return money
    }
  }
};
</script>
<style lang="scss">
.withdraw-form-inline {
  padding: 20px 40px 0 40px;

  .ivu-input {
    height: 40px;
    line-height: 40px;
  }
}
</style>

<style scoped lang="scss">
#sendCode {
  position: absolute;
  border: none;
  background: none;
  top: 10px;
  outline: none;
  right: 0;
  width: 30%;
  color: #f0ac19;
  cursor: pointer;
  height: 20px;
  line-height: 20px;
  border-left: 1px solid #dddee1;
}

.nav-rights {
  .nav-right {
    height: auto;
    overflow: hidden;
    padding: 0 15px;

    .rightarea {
      //padding-left: 15px;

      .trade-groups.merchant-tops {
        font-size: 14px;
        height: 50px;
        //padding: 0 15px;
        color: #fff;
        overflow: hidden;
        display: block;
        margin-right: 0;

        a {
          display: inline-block;
          color: #f0a70a;
          width: 160px;
          height: 40px;
          border: 1px solid #f0a70a;
          line-height: 40px;
          text-align: center;
          float: right;

          &:hover {
            background: #f0a70a;
            color: #000;
          }
        }
      }

      .action-box {
        //padding: 10px 20px 20px;

        .form-group-container {
          .form-group.form-amount {
            .input-group .ivu-poptip {
              .ivu-poptip-rel {
                display: block;

                .ivu-input-number {
                  width: 100%;
                }
              }
            }
          }
        }
      }
    }
  }
}

.ivu-slider-button-wrap {
  top: -6px;
}

.withdraw-disable {
  color: #c0c4cc;
}
.withdraw-disable:hover{
  color: #c0c4cc;
  background-color: #27313e !important;
}

#withdrawAddressList {
  position: absolute;
  height: 0;
  transition: height 0.3s;
  top: 100%;
  left: 0;
  width: 100%;
  z-index: 1;
  max-height: 245px;
  overflow: auto;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
  height: auto;
  background: #fff;
}

#withdrawAddressList .address-item {
  padding: 0 20px;
  display: flex;
  line-height: 48px;
  border-bottom: 1px solid transparent;
  position: relative;
  white-space: nowrap;
  overflow: hidden;
  z-index: 99;
}

#withdrawAddressList .address-item:hover {
  background: #f5f5f5;
  cursor: pointer;
}

#withdrawAddressList .notes {
  position: absolute;
  bottom: 0;
  right: 20px;
  height: 48px;
  line-height: 48px;
  max-width: 300px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

p.describe {
  font-size: 16px;
  font-weight: 600;
}

.acb-p1 {
  font-size: 18px;
  font-weight: 600;
  line-height: 50px;
}

.acb-p2 {
  font-size: 13px;
  line-height: 24px;
  color: #8c979f;
}

.action-content.pt10 {
  padding-top: 0px;
  padding-bottom: 50px;
}

.action-content {
  width: 100%;
  margin-top: 20px;
  // overflow: hidden;
  display: table;
}

.action-content .action-body {
  display: table-cell;
  vertical-align: top;
  line-height: 20px;
  font-size: 12px;
  color: #ccc;
}

.action-foot {
  display: flex;
  padding-top: 25px;
}

// 提币按钮
.action-foot button {
  width: 160px !important;
}

.hb-night .btn.btn-primary,
.hb-night .btn.btn_submit {
  background-color: #7a98f7;
  color: white;
}

.action-inner {
  width: 100%;
}

// 选择币种
.action-inner .inner-left {
  width: 447px;
  text-align: left;
}

// 提币地址
.action-inner .inner-box {
  width: 447px;
  text-align: left;
}

.form-group {
  position: relative;
  margin-bottom: 17px;
  font-size: 16px;
}

.controlAddress {
  line-height: 50px;
}

.form-group label {
  max-width: 100%;
  font-weight: 600;
}

.control-input-group {
  position: relative;
}

.control-input-group.open .select-list {
  height: auto;
}

.form-group-container {
  display: table;
  width: 447px;
  text-align: left;
}

.form-group-container .form-amount {
  width: 100%;
}

.form-group-container .form-amount .label-amount {
  margin-bottom: 13px;
  display: inline-block;
}

.form-group-container .form-group {
  display: table-cell;
}

.form-group-container .form-group span.addon-tag:last-child {
  padding: 0;
  border: none;
  background: none;
  cursor: default;
  position: absolute;
  right: 26px;
  top: 6px;
}

.form-group-container .form-group span.addon-tag:last-child.firstt {
  top: 8px;
}

.form-group-container2 {
  padding-top: 20px;
}

.form-group-container .form-fee {
  width: 50%;
  padding: 0 20px 0 0;
}

.label-amount .label-fr {
  float: right;
  color: #aaa;
  font-size: 14px;
}

.label-amount .label-fr span {
  margin-left: 2px;
}

.form-group-container .form-group {
  display: table-cell;
}

.hb-night table.table .table-inner {
  margin: -4px -20px;
  position: relative;
  background-color: #181b2a;
  border-radius: 3px;
}

.hb-night table.table .table-inner {
  margin: -4px -20px;
  position: relative;
  background-color: #181b2a;
  border-radius: 3px;
}

.hb-night table.table .table-inner {
  margin: -4px -20px;
  position: relative;
  background-color: #181b2a;
  border-radius: 3px;
}

table.table .table-inner.action-box {
  margin: -1px -10px;
}

.merchant-top .tips-word {
  -webkit-box-flex: 2;
  -ms-flex-positive: 2;
  flex-grow: 2;
  text-align: left;
}

.rightarea .rightarea-tabs {
  border: none;
}

.order_box {
  width: 100%;
  background: #fff;
  height: 56px;
  line-height: 56px;
  margin-bottom: 20px;
  border-bottom: 2px solid #ccf2ff;
  position: relative;
  text-align: left;
}

.order_box a {
  color: #8994a3;
  font-size: 16px;
  padding: 0 30px;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
  line-height: 54px;
  display: inline-block;
}

.order_box .active {
  border-bottom: 2px solid #f0a70a;
}

.order_box .search {
  position: absolute;
  width: 300px;
  height: 32px;
  top: 12px;
  right: 0;
  display: flex;
  /* border: #c5cdd7 solid 1px; */
}

.ivu-btn-primary {
  background-color: #f0a70a;
  border-color: #f0a70a;
}

#pages {
  margin: 10px;
  overflow: hidden;
}

// 顶部输入框标题
.input-title {
  color: #999999;
  font-size: 14px !important;
  font-weight: 500 !important;
}

// 转账网络
.form-network {
  width: 447px;
  text-align: left;
}

.form-network .input-title {
  display: inline-block;
  margin-bottom: 17px;
}

// 顶部输入输入框样式重新更改
/deep/ .ivu-select-single .ivu-select-selection {
  height: 40px;
}

/deep/ .ivu-select-single .ivu-select-selection .ivu-select-input,
/deep/ .ivu-select-single .ivu-select-selection .ivu-select-selected-value {
  height: 40px;
  line-height: 40px;
}

// 输入数量
/deep/ .ivu-input-number-input-wrap,
/deep/ .ivu-input-number {
  height: auto;
}

/deep/ .ivu-input-number-large input,
/deep/ .ivu-select-single .ivu-select-selection .ivu-select-placeholder {
  height: 40px;
  line-height: 40px;
}

.form-address .input-address {
  width: 447px;
  height: 40px;
  outline: none;
  border: 0px;
  background-color: transparent;
  border: 1px solid #27313e;
  border-radius: 4px;
  font-size: 13px;
  padding: 0px 10px;
  color: #C5C8CE;
}

.form-address .input-address::placeholder {
  color: #C5C8CE;
}

.balance-fee {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
}

.balance-fee .bf-tips {
  max-width: 50%;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  font-size: 13px;
}

.balance-fee .bf-tips .title {
  color: #999999;
  font-size: 13px;
}

</style>
<style lang="scss">
.nav-rights {
  .nav-right {
    .rightarea {
      .action-box {
        .action-inner {
          .inner-left,
          .inner-box {
            .ivu-select-dropdown .ivu-select-item {
              padding: 6px 16px;
            }
          }
        }

        .form-group-container {
          .form-group {
            .input-group {
              .ivu-poptip-rel {
                display: block;

                .ivu-input-number {
                  width: 100%;
                }
              }

              .ivu-input-number {
                width: 100%;
              }
            }
          }
        }
      }
    }

    .table-inner.action-box {
      .action-content .action-body {
        /*分页*/
        .order-table .ivu-table-wrapper .ivu-table-header {
          thead .ivu-table-cell {
            .ivu-poptip .ivu-poptip-rel .ivu-table-filter {
              i.ivu-icon.ivu-icon-funnel.on {
                color: #f0ac19;
              }
            }
          }
        }
      }
    }
  }
}
</style>

