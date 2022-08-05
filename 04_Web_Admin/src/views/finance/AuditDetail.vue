<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('auditdetails.auditdetails') }} <Button type="primary" size="small" @click="reset">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <div class="stepWrapper">
        <Steps :current="status">
          <Step :title="$t('auditdetails.reviewed')" v-if="!!status"></Step>
          <Step :title="$t('currencywithdrawalapproval.pendingreview')" v-if="!status"></Step>
          <Step :title="$t('auditdetails.transferring')"></Step>
          <Step :title="$t('auditdetails.transactionfailed')" v-if="status===2"></Step>
          <Step :title="$t('auditdetails.successfultrade')" v-if="status!==2"></Step>
        </Steps>
      </div>
      <Row class="userDetails">

        <span>{{ $t('auditdetails.username') }}
          <i>{{ userInfo.memberUsername }}</i>
        </span>
        <span>{{ $t('auditdetails.realname') }}
          <i>{{ userInfo.memberRealName }}</i>
        </span>
        <span>{{ $t('currencywithdrawalauditmanagement.quantityreceived') }}
          <i>{{ userInfo.arrivedAmount }}</i>
        </span>
        <span>{{ $t('currencywithdrawalauditmanagement.withdrawaladdress') }}
          <i>{{ userInfo.address }}</i>
        </span>

        <br>
        <span>{{ $t('auditdetails.withdrawalcurrency') }}
          <i>{{ userInfo.unit }}</i>
        </span>

        <span>{{ $t('transactiondetailsinlegalcurrency.servicecharge') }}
          <i>{{ userInfo.fee }}</i>
        </span>
        <span>{{ $t('auditdetails.withdrawalquantity') }}
          <i>{{ userInfo.totalAmount }}</i>
        </span>
        <span>{{ $t('auditdetails.applicationtime') }}
          <i>{{ userInfo.createTime }}</i>
        </span>

        <div class="passCoinWrapper" v-if="status===1&&isAuto===0">
          <Button type="error" size="large" @click=" ifPassCoin = true ">{{ $t('auditdetails.starttoputmoney') }}</Button>
        </div>

      </Row>

      <Modal class="passCoinModal" width="400" v-model="ifPassCoin" @on-ok="confrimPass">
        <div>{{ $t('auditdetails.pleaseentertheserialnumber') }}
          <p>
            <Input type="textarea" v-model="transactionNum"></Input>
          </p>
        </div>
      </Modal>

      <Row class="tradeRecord">
        <h3>{{ $t('detailsofcurrencyentrustment.transactions') }}</h3>
        <div class="tableWrapper">
          <div class="poptip" style="display:flex;flex-direction:row;margin-bottom: 10px;">
            <div style="width:100px;">{{ $t('servicechargewithdrawaldetails.transactiontype') }}</div>
            <Select v-model="tradeType" style="width:100px;margin-right:20px;">
              <Option v-for="(item, index) in tradeTypeArr"
                    :value="index"
                    :key="item">{{ item }}</Option>
            </Select>
            <Button type="info" size="small" @click="searchByFilter" style="padding: 5px 20px;">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
          </div>
          <Table :columns="columns_first" :data="trade_data"></Table>
          <Page :total="totalNum" :page-size=10  @on-change="changePage"></Page>
        </div>
        <p class="line"></p>
        <div class="btnContainer" v-if="!status">
          <Button type="error" size="large" @click="ifPass(false)">{{ $t('auditdetails.fail') }}</Button>
          <Button type="success" size="large" @click="ifPass(true)">{{ $t('auditdetails.adopt') }}</Button>
        </div>
      </Row>

    </Card>

  </div>
</template>

<script>
import Cookies from "js-cookie";
import { setStore, getStore, removeStore } from "@/config/storage";
import {
  auditPass,
  auditNoPass,
  withdrawManage,
  perTradeAll,
  passCoinByOne
} from "@/service/getData";

export default {
  data() {
    return {
      isAuto: null,
      transactionNum: null,
      ifPassCoin: false,
      userInfo: {},
      totalNum: null,
      status: null,
      tradeType: null,
      tradeTypeArr: [ this.$t('handlingchargemanagement.recharge'), this.$t('handlingchargemanagement.withdrawal'), this.$t('handlingchargemanagement.transfer'), this.$t('servicechargewithdrawaldetails.currencytransaction'), this.$t('handlingchargemanagement.purchaseinlegalcurrency'), this.$t('handlingchargemanagement.sellinginlegalcurrency'), this.$t('handlingchargemanagement.activityrewards'), this.$t('handlingchargemanagement.promotionreward'), this.$t('handlingchargemanagement.dividends'), this.$t('handlingchargemanagement.vote'), this.$t('handlingchargemanagement.manualrecharge'), this.$t('handlingchargemanagement.pairing'), this.$t('handlingchargemanagement.eventredemption') ],
      columns_first: [
        {
          title: this.$t('realnamemanagement.memberid'),
          key:"memberId"
        },
        {
          title: this.$t('auditdetails.transactionmethod'),
          key: "type",
          render: (h, obj) => {
            let type = obj.row.type;
            let arr = [
              this.$t('handlingchargemanagement.recharge'),
              this.$t('handlingchargemanagement.withdrawal'),
              this.$t('handlingchargemanagement.transfer'),
              this.$t('servicechargewithdrawaldetails.currencytransaction'),
              this.$t('handlingchargemanagement.purchaseinlegalcurrency'),
              this.$t('handlingchargemanagement.sellinginlegalcurrency'),
              this.$t('handlingchargemanagement.activityrewards'),
              this.$t('handlingchargemanagement.promotionreward'),
              this.$t('handlingchargemanagement.dividends'),
              this.$t('handlingchargemanagement.vote'),
              this.$t('handlingchargemanagement.manualrecharge'),
              this.$t('handlingchargemanagement.pairing'),
              this.$t('handlingchargemanagement.eventredemption')
            ];
            return h("span", {}, arr[type]);
          }
        },
        {
          title: this.$t('transactiondetails.transactionamount'),
          render: (h, obj) => {
            let amount = obj.row.amount;
            let symbol = obj.row.symbol;
            return h("span", {}, amount + " " + symbol);
          }
        },
        {
          title: this.$t('handlingchargemanagement.transactionhandlingfee'),
          key:"fee"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.transactiontime'),
          key:"createTime"
        }
      ],
      trade_data: []
    };
  },
  methods: {
    confrimPass() {
      if (!this.transactionNum) {
        this.$Message.warning(this.$t('auditdetails.pleasefillintheserialnumber'));
      } else {
        let obj = {
          id: this.userInfo.id,
          transactionNumber: this.transactionNum
        };

        passCoinByOne(obj).then(res => {
          if (!res.code) {
            this.$Message.success(this.$t('perpetualcontractcurrencystandardmanagement.operationsucceeded'));
            this.$router.push("/finance/userwithdrawals");
          } else this.$Message.error(res.message);
        });
      }
    },
    reset() {},
    searchByFilter(){
      perTradeAll({pageNo: 1, pageSize: 10, memberId: this.userInfo.memberId, type: this.tradeType}).then(res => {
        this.totalNum = (res.data && res.data.totalElements) || 1;
        this.trade_data = (res.data && res.data.content)|| [];
      });
    },
    changePage(pageIndex) {
      perTradeAll({pageNo: pageIndex, pageSize: 10, memberId: this.userInfo.memberId, type: this.tradeType}).then(res => {
        this.totalNum = (res.data && res.data.totalElements) || 1;
        this.trade_data = (res.data && res.data.content)|| [];
      });
    },
    ifPass(bol) {
      let id = this.userInfo.id;
      console.log(this.userInfo);
      let fn = null;

      if (bol) fn = auditPass;
      else fn = auditNoPass;

      fn({ ids: [id] }).then(res => {
        if (!res.code) {
          this.$Message.success(res.message);
        } else {
          this.$Message.error(this.$t('auditdetails.requestexception'));
        }

        this.$router.push({ path: "/finance/userwithdrawals" });
      });
    },
    refreshPage() {
      this.userInfo = JSON.parse(getStore("userDetails"));
      this.status = this.userInfo.status;
      this.isAuto = this.userInfo.isAuto;
      perTradeAll({pageNo: 1,pageSize: 6,memberId: this.userInfo.memberId}).then(res => {
          if (!res.code)
          this.totalNum = (res.data && res.data.totalElements) || 1;
          this.trade_data = (res.data && res.data.content) || [];
        },
        err => {
          console.log(err);
        }
      );
    }
  },
  created() {
    this.refreshPage();
  }
};
</script>

<style scoped>
.ivu-modal-body div {
  font-size: 14px;
  font-weight: 700;
  line-height: 40px;
}
.stepWrapper {
  padding: 10px 0;
  padding-left: 10%;
}
.userDetails {
  position: relative;
  font-size: 13px;
}
.passCoinWrapper {
  position: absolute;
  top: 10px;
  right: 0;
}
.userDetails span {
  padding-left: 10px;
  display: inline-block;
  min-width: 150px;
  height: 25px;
  font-weight: 700;
}
.userDetails i {
  font-style: normal;
  font-weight: normal;
}
.tradeRecord {
  padding-left: 10px;
  margin-top: 10px;
}
.tradeRecord h3 {
  color: #2b85e4;
  margin-bottom: 10px;
}
.btnContainer {
  margin-top: 15px;
  text-align: center;
}
.btnContainer button {
  margin: 10px;
}
.tableWrapper {
  text-align: right;
}
.tableWrapper .ivu-page {
  margin-top: 20px;
  margin-right: 80px;
}
.line {
  margin-top: 10px;
  border-top: 1px solid #dddee1;
}
</style>
