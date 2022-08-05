<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('detailsofcurrencyentrustment.detailsofcurrencyentrustment') }} <!-- <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button> -->
      </p>

      <div class="baseInfo">
        <Row>
          <Col span="6">
          <p>{{ $t('detailsofcurrencyentrustment.commissionno') }}<span>{{detail[0].orderId}}</span></p>
          </Col>
          <Col span="6">
          <p>{{ $t('currencywithdrawalauditmanagement.status') }}<span>{{getStatus(detail[0].status)}}</span></p>
          </Col>
          <Col span="6">
          <p>{{ $t('detailsofcurrencyentrustment.entrustedtime') }}<span>{{getTime(detail[0].time)}}</span></p>
          </Col>
          <Col span="6">
          <p>{{ $t('detailsofcurrencyentrustment.typeofdelegation') }}<span>{{getStyle(detail[0].type)}}</span></p>
          </Col>
        </Row>

        <Row>
          <Col span="6">
          <p>{{ $t('detailsofcurrencyentrustment.entrustedprice') }}<span>{{detail[0].price}}</span></p>
          </Col>
          <Col span="6">
          <p>{{ $t('detailsofcurrencyentrustment.entrustedquantity') }}<span>{{detail[0].amount}}</span></p>
          </Col>
          <Col span="6">
          <p>{{ $t('detailsofcurrencyentrustment.numberoftransactions') }}<span>{{detail[0].tradedAmount}}</span></p>
          </Col>
        </Row>
      </div>

    </Card>
    <br><br>
    <Card>
      <p slot="title">
        {{ $t('detailsofcurrencyentrustment.transactions') }} </p>
      <Row>
        <Table :columns="columnsList" :data="userpage" border :loading="ifLoading" class='user_center'>
        </Table>
      </Row>

      <Row class="pageWrapper">
        <Page :total="totalNum" style='margin-top:8px' :current="current" @on-change="changePage" show-elevator></Page>
      </Row>
    </Card>
  </div>
</template>

<script>
import dtime from "time-formater";
import { exgOrderDetail, queryBBOrder } from "@/service/getData";
import { setStore, getStore, removeStore } from "@/config/storage";
export default {
  data() {
    return {
      currentPageIdx: null,
      totalNum: null,
      ifLoading: true,
      current: 1,
      pageIndex: 1,
      columnsList: [
        {
          title: this.$t('detailsofcurrencyentrustment.orderno'),
          key: "orderId"
        },
        {
          title: this.$t('detailsofcurrencyentrustment.pendingorderprice'),
          key: "price"
        },
        {
          title: this.$t('detailsofcurrencyentrustment.pendingorderquantity'),
          key: "amount"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.handlingcharges'),
          key: "fee"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.transactiontime'),
          key: "time",
          render: (h, obj) => {
            let formatTime = dtime(obj.row.time).format("YYYY-MM-DD HH:mm:ss");
            return h("span", {}, formatTime);
          }
        }
      ],
      userpage: [],
      detail: [
        {
          amount: null,
          baseSymbol: null,
          canceledTime: null,
          coinSymbol: null,
          completed: null,
          completedTime: null,
          detail: null,
          direction: null,
          memberId: null,
          orderId: null,
          price: null,
          status: null,
          symbol: null,
          time: null,
          tradedAmount: null,
          turnover: null,
          type: null,
          useDiscount: null
        }
      ],
      status: [this.$t('detailsofcurrencyentrustment.trading'), this.$t('transactiondetailsinlegalcurrency.completed'), this.$t('transactiondetailsinlegalcurrency.cancelled')]
    };
  },
  methods: {
    refreshPageManual() {
      this.ifLoading = true;
      this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
    },
    getTime(time){
      return dtime(time).format('YYYY-MM-DD HH:mm:ss')
    },
    getStyle(str){
      return str=="MARKET_PRICE" ? this.$t('detailsofcurrencyentrustment.marketprice') : this.$t('detailsofcurrencyentrustment.pricelimit');
    },
    getStatus(str) {
      var direction = ""
      if (str == "TRADING") {
        direction = this.$t('detailsofcurrencyentrustment.trading');
      } else if (str == "COMPLETED") {
        direction = this.$t('transactiondetailsinlegalcurrency.completed');
      } else if (direction == "CANCELED") {
        direction = this.$t('transactiondetailsinlegalcurrency.cancelled');
      }
      return direction;
    },
    changePage(pageIndex) {
      this.ifLoading = true;
      this.currentPageIdx = pageIndex;
      this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    refreshPage() {
      let orderID = getStore("exchangeOrderId");
      let completed = JSON.parse(getStore("completed"));
      completed = completed ? 1 : 0;
      let obj = { orderId: orderID, completed: completed };
      queryBBOrder(obj).then(res => {
        if (!res.code) {
          this.detail = res.data && res.data.content;
        } else {
          this.$Message.error(this.$t('detailsofcurrencyentrustment.queryerror'));
        }
      });
      exgOrderDetail({ id: orderID }).then(res => {
        this.userpage = res.data;
        this.totalNum = res.data.length;
        this.ifLoading = false;
      });
    }
  },
  created() {
    this.refreshPage();
  }
};
</script>

<style lang="less" scoped>
</style>