<template>
  <div class="nav-rights">
    <div class="nav-right">
      <div class="bill_box">
        <!-- <section class="trade-group merchant-top">
                    <i class="merchant-icon tips"></i>
                    <span class="tips-word">{{$t('uc.finance.recharge.recharge')}}</span>
                </section> -->
        <section>
          <div class="table-inner action-box open">
            <!-- <i class="angle" style="right: 71px;"></i> -->
            <div class="" style="margin-top: 10px;text-align: right;">
              <a class="withdrawcoderecharge" @click="openCodeModal">{{$t('uc.finance.recharge.withdrawrecharge')}}</a>
            </div>
            <div class="action-inner" style="display: block;margin-top: 20px">
              <div class="coin-network">
                <div style="display: flex;align-items: center;">
                  <div class="inner-left">
                    <p class="describe">{{ $t('uc.finance.recharge.symbol') }}</p>
                    <Select v-model="coinType" style="width:300px;margin-top: 10px;"  @on-change="changeCoin">
                      <Option v-for="item in coinList" :value="item.name" :key="item.unit">{{ item.name }}</Option>
                    </Select>
                  </div>
                  <div class="inner-left">
                    <p class="describe">{{ $t('uc.finance.recharge.network') }}</p>
                    <Select v-model="protocol" style="width:300px;margin-top: 10px;"  @on-change="changeCoinext">
                      <Option v-for="item in comCoinextList()" :value="item.protocol" :key="item.protocol">
                        {{ item.protocolname }}
                      </Option>
                    </Select>
                  </div>
                </div>
              </div>
              <div class="inner-left">
                <p class="describe">{{ $t('uc.finance.recharge.address') }}</p>
                <div class="title">
                  <!-- <div v-if="qrcode.value">
                    <qriously :value="qrcode.value" :size="qrcode.size"/>
                    <div>
                      <span>{{qrcode.value}}</span>
                      <a v-clipboard:copy="qrcode.value" v-clipboard:success="onCopy" v-clipboard:error="onError" href="javascript:;" id="copyBtn" class="link-copy">{{$t('uc.finance.recharge.copy')}}</a>
                    </div>
                  </div> -->
                  <template v-if="coinextItem.isrecharge !== 0">
                    <div class="address-data-cont" v-if="qrcode.value">
                      <span>{{ qrcode.value }}</span>
                      <a v-clipboard:copy="qrcode.value" v-clipboard:success="onCopy" v-clipboard:error="onError"
                         href="javascript:;" id="copyBtn" class="link-copy">{{ $t('uc.finance.recharge.copy') }}</a>
                      <div class="code-cont">
                        <img class="code-icon" src="../../assets/img/619f4cbcb561f7267614e1c3a252e28.png">
                        <div class="code">
                          <qriously :value="qrcode.value" :size="qrcode.size"/>
                        </div>
                      </div>
                    </div>
                    <div v-else
                         style="text-align: center;width: 300px;display: flex;flex-direction: column;align-items: center">
                      <div style="font-size: 12px;">{{ $t('uc.finance.recharge.notaddress') }}</div>
                      <div
                          style="cursor: pointer;padding: 2px;font-size: 13px;margin-top: 5px;background-color: #f0ac19;width: 100px;border-radius: 4px;"
                          @click="resetAddress">{{ $t('uc.finance.recharge.getaddress') }}
                      </div>

                    </div>
                  </template>
                  <div v-else
                       style="text-align: center;width: 300px;display: flex;flex-direction: column;align-items: center">
                    <div style="font-size: 12px;">{{ $t('uc.finance.recharge.rechargeDisable') }}</div>
                  </div>
                </div>
              </div>

            </div>


            <div class="action-content">
              <div class="action-body">
                <p class="acb-p1">{{ $t('common.tip') }}</p>
                <p class="acb-p2">•
                  {{ $t('uc.finance.recharge.msg3') }}{{ comCoinextItem().minrecharge }}{{ coinType }}{{ $t('uc.finance.recharge.msg3_1') }}<br>•
                  {{ $t('uc.finance.recharge.msg1') }}<br>• {{ $t('uc.finance.recharge.msg2') }}<br>•
                  {{ $t('uc.finance.recharge.msg4') }}<br>• {{ $t('uc.finance.recharge.msg5') }}</p>
              </div>
            </div>
            <div class="action-content">
              <div class="action-body">
                <p class="acb-p1">{{ $t('uc.finance.recharge.record') }}</p>
                <div class="order-table">
                  <Table :columns="tableColumnsRecharge" :data="tableRecharge" :loading="loading"
                         :no-data-text="$t('common.nodata')"></Table>
                  <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                      <Page :total="tableRechargeTotal" :current="1" @on-change="changePage" class="recharge_btn"></Page>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- model1 -->
    <Modal v-model="modal1" width="360">
      <p slot="header" style="color:#f60;text-align:center">
        <Icon type="ios-mail" size="20" color="#00b5f6;"/>
        <span>{{ $t('uc.finance.recharge.coderechargetip') }}</span>
      </p>
      <div style="text-align:center">
        <Form ref="formValidate" :label-width="0">
          <FormItem>
            <Input v-model="withdrawCode" :placeholder="$t('uc.finance.recharge.coderechargetip')"></Input>
          </FormItem>
        </Form>
      </div>
      <div slot="footer">
        <Button type="primary" size="large" long @click="getCodeInfo">{{ $t('uc.finance.withdraw.submit') }}</Button>
      </div>
    </Modal>

    <!-- model2 -->
    <Modal v-model="modal2" width="360">
      <p slot="header" style="color:#f60;text-align:center">
        <Icon type="ios-mail" size="20" color="#00b5f6;"/>
        <span>{{ $t('uc.finance.recharge.rechargeconfirm') }}</span>
      </p>
      <div style="text-align:center">
        <p><span>{{ $t('uc.finance.recharge.symbol') }}: </span><span>{{ withdrawCodeInfo.coin.unit }}</span></p>

        <p><span>{{ $t('uc.finance.recharge.amount') }}: </span><span>{{ withdrawCodeInfo.withdrawAmount }}</span></p>

      </div>
      <div slot="footer">
        <Button type="primary" size="large" long @click="submitCode">{{ $t('uc.finance.withdraw.submit') }}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
import Vue from "vue";
import VueQriously from "vue-qriously";
import {parseTime} from "../../assets/js/util";

Vue.use(VueQriously);

export default {
  components: {
    VueQriously
  },
  inject: ['reload'],
  data() {
    return {
      modal1: false,
      modal2: false,
      withdrawCode: "",
      fundpwd: "",
      accountType: 0,
      memoCode: "",
      minRechargeAmount: "0.001",
      isShowGetAddress: false,
      isShowEwm: false,
      qrcode: {
        value: "",
        size: 220,
        coinName: "",
        unit: ""
      },
      coinType: "",
      protocol: "",
      coinList: [],
      coinextList: [],
      coinextItem: {},
      createAddressLoading: false,
      loading: true,
      pageNo: 1,
      pageSize: 10,
      tableRecharge: [],
      tableRechargeTotal: 0,
      allTableRecharge: [],
      withdrawCodeInfo: {
        coin: {
          unit: ""
        }
      },
    };
  },
  methods: {
    changePage(index) {
      this.pageNo = index
      this.getList();
    },
    showEwm() {
      this.isShowEwm = !this.isShowEwm;
    },
    onCopy(e) {
      this.$Message.success(
          this.$t("uc.finance.recharge.copysuccess") + e.text
      );
    },
    onError(e) {
      this.$Message.error(this.$t("uc.finance.recharge.copysuccess"));
    },
    changeCoin() {
      this.protocol = ""
      this.qrcode.value = ""
      this.coinextItem = {}
    },
    changeCoinext() {
      if (this.createAddressLoading) {
        return false
      }
      if (!this.protocol) {
        return false
      }
      for (let item of this.coinextList) {
        if (item.coinname === this.coinType && item.protocol === this.protocol) {
          this.coinextItem = item
        }
      }
      if (this.coinextItem.isrecharge === 0) {
        return false
      }
      this.qrcode.value = ""
      this.createAddressLoading = true
      this.$http
          .get(this.host + "/uc/address/read?coinprotocol=" + this.protocol)
          .then(response => {
            var resp = response.body;
            this.createAddressLoading = false
            if (resp.code == 0) {
              this.qrcode.value = resp.data.address
            } else {
              this.$Message.error(resp.message);
            }
          });
    },
    openCodeModal() {
      this.modal1 = true;
      this.withdrawCode = "";
    },
    getCodeInfo() {
      if (this.withdrawCode == "") {
        this.$Message.warning(this.$t("uc.finance.recharge.coderechargetip"));
        return;
      }
      let param = {};
      param["withdrawCode"] = this.withdrawCode;

      this.$http
          .post(this.host + "/uc/withdrawcode/apply/info", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.withdrawCodeInfo = resp.data;
              this.modal1 = false;
            } else {
              this.$Message.error(resp.message);
            }
          });
      this.modal1 = false;
      this.modal2 = true;
    },
    submitCode() {
      if (this.withdrawCode == "") {
        this.$Message.warning(this.$t("uc.finance.recharge.coderechargetip"));
        return;
      }
      let param = {};
      param["withdrawCode"] = this.withdrawCode;

      this.$http
          .post(this.host + "/uc/withdrawcode/apply/recharge", param)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.finance.recharge.rechargesuccess"));
              this.modal1 = false;
            } else {
              this.$Message.error(resp.message);
            }
          });
      this.modal2 = false;
    },
    resetAddress() {
      if (!this.protocol) {
        this.$Message.error(this.$t("uc.finance.recharge.protocolerr"));
        return false
      }
      this.$http.post(this.host + "/uc/address/create", {coinprotocol: this.protocol}).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.qrcode.value = resp.data.address
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    getCoinList() {
      //获取
      this.$http.get(this.host + "/uc/coin/list").then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.coinList = resp.data.coinList
          this.coinextList = resp.data.coinextList
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    getList() {
      //获取tableRecharge
      let params = {};
      params["page"] = this.pageNo - 1;
      params["pageSize"] = this.pageSize;
      this.$http
          .post(this.host + "/uc/recharge/list", params)
          .then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.tableRecharge = resp.data.content || [];
              this.tableRechargeTotal = resp.data.totalElements;
            } else {
              this.$Message.error(resp.message);
            }
            this.loading = false
          });
    },
    getMember() {
      let self = this;
      this.$http.post(this.host + "/uc/approve/security/setting").then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          if (resp.data.realName == null || resp.data.realName == "") {
            // 判断是否实名认证，未认证跳转到实名认证页面；
            this.$Message.success(this.$t("otc.publishad.submittip1"));
            self.$router.push("/uc/safe");
          } else if (resp.data.phoneVerified == 0) {
            // 判断是否是手机号0，1，未认证跳转到实名认证页面；
            this.$Message.success(this.$t("otc.publishad.submittip2"));
            self.$router.push("/uc/safe");
          } else if (resp.data.fundsVerified == 0) {
            // 判断是否设置交易密码，未认证跳转到实名认证页面；
            this.$Message.success(this.$t("otc.publishad.submittip3"));
            self.$router.push("/uc/safe");
          }
        } else {
          this.$Message.error(resp.message);
        }
      });
    }
  },
  created() {
    this.coinType = this.$route.query.name || "";
    //this.getMember();
    this.getCoinList()
    this.getList();
  },
  computed: {
    tableColumnsRecharge() {
      let columns = [];
      columns.push({
        title: this.$t("uc.finance.recharge.time"),
        width: 180,
        key: "addtime",
        render: function (h, params) {
          return h("span", parseTime(params.row.addtime));
        }
      });
      columns.push({
        title: this.$t("uc.finance.recharge.symbol"),
        key: "coinname"
      });
      columns.push({
        title: this.$t("uc.finance.recharge.protocol"),
        key: "protocolname"
      });
      columns.push({
        title: this.$t("uc.finance.recharge.address"),
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
        title: this.$t("uc.finance.recharge.amount"),
        key: "money"
      });
      columns.push({
        title: this.$t("uc.finance.recharge.status"),
        key: "status",
        render: (h, params) => {
          let text = "";
          if (params.row.status == -1) {
            text = this.$t("uc.finance.recharge.status_0");
          } else if (params.row.status == 0) {
            text = this.$t("uc.finance.recharge.status_1");
          } else if (params.row.status == 1) {
            text = this.$t("uc.finance.recharge.status_2");
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
        // if(list.length==0){
        //   let s = {}
        //   list.push(s)
        // }
        return list
      }
    },
    comCoinextItem() {
      return () => {
        for (let item of this.coinextList) {
          if (item.coinname === this.coinType && item.protocol == this.protocol) {
            return item
          }
        }
        return {}
      }
    }
  }
};
</script>
<style scoped>
.table-inner {
  position: relative;
  text-align: left;
  border-radius: 3px;
}

.acb-p1 {
  font-size: 16px;
  font-weight: 400;
  line-height: 50px;
}

.acb-p2 {
  font-size: 13px;
  line-height: 24px;
  color: #8c979f;
}

.action-inner {
  width: 100%;
  display: table;
}

.action-inner .inner-box {
  display: table-cell;
  width: 100%;
}

.action-box .title .copy {
  user-select: text;
}

.action-box .title a.link-copy {
  font-size: 14px;
  margin-left: 20px;
  color: #f0a70a;
}

.hb-night a {
  text-decoration: none;
  color: #7a98f7;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}

.action-box .title a.link-qrcode {
  margin-left: 20px;
  font-size: 14px;
  position: relative;
  color: #f0a70a;
}

.hb-night a {
  text-decoration: none;
  color: #7a98f7;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}

.action-box .subtitle {
  font-size: 12px;
  margin-top: 30px;
}

.action-content {
  width: 100%;
  margin-top: 30px;
  /* overflow: hidden; */
  display: table;
  color: #ccc;
}

.action-box .title {
  margin-top: 20px;
  font-size: 20px;
  user-select: none;
}

.action-box .title .show-qrcode {
  position: absolute;
  top: -100px;
  left: 40px;
  padding: 10px;
  background: #FFF;
}

.action-inner .inner-box.deposit-address {
  width: 80%;
}

p.describe {
  font-size: 16px;
  font-weight: 600;
}

.merchant-icon {
  display: inline-block;
  margin-left: 4px;
  background-size: 100% 100%;
}

.merchant-icon.tips {
  width: 4px;
  height: 22px;
  margin-right: 10px;
  background: #f0a70a;
}

.bill_box {
  width: 100%;
  height: auto;
  /* overflow: hidden; */
}

.nav-right {
  height: auto;
  overflow: hidden;
  padding: 0 15px;
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

.order_box .search {
  position: absolute;
  width: 300px;
  height: 32px;
  top: 12px;
  right: 0;
  display: flex;
  /* border: #c5cdd7 solid 1px; */
}

a.withdrawcoderecharge {
  font-weight: normal;
  line-height: 40px;
  color: #f0a70a;
  width: 160px;
  height: 40px;
  border: 1px solid #f0a70a;
  display: inline-block;
  text-align: center;
}

.coin-network {
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 40px;
}

.coin-network .inner-left:last-child {
  margin-left: 30px;
}

.inner-left .describe {
  font-size: 14px;
  color: #999;
  font-weight: 500;
}

.address-data-cont {
  width: auto;
  display: inline-flex;
  align-items: center;
  position: relative;
  font-size: 18px;
}

.address-data-cont .code-cont {
  margin-left: 15px;
  display: inline-flex;
}

.address-data-cont .code-icon {
  cursor: pointer;
  width: 25px;
  height: 25px;
  margin-right: 5px;
  margin-bottom: 0px;
}

.address-data-cont .code-cont .code {
  position: absolute;
  top: -50px;
  left: 100%;
  width: 220px;
  height: 220px;
  background-color: #fff;
  display: none;
}

.address-data-cont .code-cont:hover .code {
  display: block;
}
</style>
