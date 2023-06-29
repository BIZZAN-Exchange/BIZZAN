<template>
  <div>
    <Card>
      <div slot="title" style="min-height:30px;width: 100%;">
        <div style="display:inline-block;float:left;" class="clearfix">
          <Button :type="btnType===0 ? 'primary' : 'ghost'" @click="localEnsure">{{ $t('currentdelegation.currentdelegation') }}</Button>
          <Button :type="btnType===1 ? 'primary' : 'ghost'" @click="hisEnsure" style="margin-left: 20px;">{{ $t('currentdelegation.historicalentrustment') }}</Button>
        </div>
        <Button type="primary" @click="refreshPageManual" style="float:right;">
          <Icon type="refresh"></Icon>{{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
        </Button>
      </div>
      <Row class="priceSectionWrapper clearfix" >

        <div class="priceSection">{{ $t('currentdelegation.pricerange') }}<Input v-model="filterSearch.minPrice"></Input> ~
          <Input v-model="filterSearch.maxPrice"></Input>
        </div>

        <div class="orderStatus">
          <span>{{ $t('transactiondetailsinlegalcurrency.orderstatus') }}</span>
          <Select v-model="filterSearch.status">
            <Option v-for="item in orderStatus"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>{{ $t('currentdelegation.orderdirection') }}</span>
          <Select v-model="filterSearch.orderDirection">
            <Option v-for="item in orderDirArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>{{ $t('currentdelegation.pendingordertype') }}</span>
          <Select v-model="filterSearch.type" style="width:80px">
            <Option v-for="item in typeArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>

        <div class="orderStatus">
          <span>{{ $t('currentdelegation.robot') }}</span>
          <Select v-model="filterSearch.robotOrder" >
            <Option v-for="item in robotOrderArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>
      </Row>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix" style="width:100%;">
          <Poptip trigger="hover" :content="$t('currentdelegation.orderdirection1')" placement="bottom-start">
            <Input :placeholder="$t('currentdelegation.orderdirection')"
                  v-model="filterSearch.coinSymbol"
                  />
            </Input>
          </Poptip>

          <Poptip trigger="hover" :content="$t('currentdelegation.pleaseentertheordernumbertosearch')" placement="bottom-start">
            <Input :placeholder="$t('currentdelegation.pleaseentertheordernumbertosearch')"
                  v-model="filterSearch.orderId"
                  />
            </Input>
          </Poptip>
          <Poptip trigger="hover" :content="$t('currentdelegation.pleaseenteruseridtosearch')" placement="bottom-start">
            <Input :placeholder="$t('currentdelegation.pleaseenteruseridtosearch')"
                  v-model="filterSearch.memberId"
                  />
            </Input>
          </Poptip>

          <Poptip trigger="hover" :content="$t('currentdelegation.pleaseentersettlementunitsearch')" placement="bottom-start">
            <Input :placeholder="$t('currentdelegation.pleaseentersettlementunitsearch')"
                  v-model="filterSearch.baseSymbol"
                  />
            </Input>
          </Poptip>

          <div class="btns" style="float:right;">
            <Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
            <Button type="success" size="small" @click="exportExcel">{{ $t('positionmanagementcontractassetmanagement.export') }}</Button>
          </div>
        </div>
      </Row>



			<Row>
        <Table
          :columns="columns_first"
          :data="userpage"
          :loading="ifLoading">
				</Table>
			</Row>

			<Row class="pageWrapper" >
				<Page :total="totalNum"
					style='margin-top:8px'
					:current="current"
					@on-change="changePage"
					show-elevator></Page>
			</Row>
    </Card>
  </div>

</template>

<script>

import dtime from 'time-formater'
import { queryBBOrder,cancelOrder } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';
import {queryBBOrderOut} from "../../service/getData";

export default {
  data() {
    return {
			btnType: 0,
      filterSearch: {
        robotOrder: 1,//是否包含机器人
				coinSymbol: '',
        orderDirection: '',
        type: '',
        orderId: '',
        memberId: '',
        baseSymbol: '',
        minPrice: '',
        maxPrice: '',
        status: '',
        pageNo: 1,
				pageSize: 10,
				completed: 0, //0是委托订单1是历史订单,
        isOut: 0
      },
      robotOrderArr:[
        { status: 0, text: this.$t('currentdelegation.viewrobot') },
        { status: 1, text: this.$t('currentdelegation.dontlookatrobots') },
        { status: '', text: this.$t('transactiondetailsinlegalcurrency.all') }
      ],
      typeArr: [
        { status: 0, text: this.$t('detailsofcurrencyentrustment.marketprice') },
        { status: 1, text: this.$t('detailsofcurrencyentrustment.pricelimit') },
        { status: '', text: this.$t('transactiondetailsinlegalcurrency.all') }
      ],
      orderDirArr: [
        { status: 0, text: this.$t('transactiondetailsinlegalcurrency.buy') },
        { status: 1, text: this.$t('transactiondetailsinlegalcurrency.sell') },
        { status: '', text: this.$t('transactiondetailsinlegalcurrency.all') }
      ],
      priceRange: '',
      orderStatusModel: null,
      orderStatus: [
        { status: 0, text: this.$t('detailsofcurrencyentrustment.trading') },
        { status: 1, text: this.$t('transactiondetailsinlegalcurrency.completed') },
        { status: 2, text: this.$t('transactiondetailsinlegalcurrency.cancelled') },
        { status: 3, text: this.$t('currentdelegation.timeout') },
        { status: '', text: this.$t('transactiondetailsinlegalcurrency.all') },
      ],
      totalNum: null,
      current:　1,
      priceSecPass: false,
      priceLow: null,
      priceHeight: null,
      coinSymbol: null,
      orderDirection: null,
      orderType: null,
      orderId: null,
      memberId: null,
      baseSymbol: null,
      currentPageIdx: 1,
      ifLoading: true,
      columns_first: [
        {
          title: this.$t('detailsofcurrencyentrustment.orderno'),
          key: "orderId",
          width: 180
				},
        {
          title: this.$t('positionmanagement.userid'),
          key: "memberId",
				},
        {
          title: this.$t('currencywithdrawalauditmanagement.emailmobilenumber'),
          render: (h, params) => {
            const row = params.row;
            let list = [];
            if (row.email) {
              list.push(h("div", {}, row.email))
            }
            if (row.mobilePhone) {
              list.push(h("div", {}, row.mobilePhone))
            }
            return list;
          }
        },
        {
          title: this.$t('businessinformation.realname'),
          key: "realName"
				},
        {
          title: this.$t('managementofoptioncontractsineachperiod.transactionpair'),
          key: "symbol"
        },
        {
          title: this.$t('currentdelegation.entrustedquantity'),
          key: "amount"
        },
        {
          title: this.$t('currentdelegation.tradingvolume'),
          key: "tradedAmount"
        },
        {
          title: this.$t('currentdelegation.pendingordertype1'),
          key: "type",
          render: (h, params) => {
            const row = params.row;

            const type = row.type == "MARKET_PRICE" ? this.$t('detailsofcurrencyentrustment.marketprice') : this.$t('detailsofcurrencyentrustment.pricelimit');
            return h("span", {}, type);
          }
        },
        {
          title: this.$t('currentdelegation.orderdirection1'),
          key: "direction",
          render: (h, params) => {
            const row = params.row;
            const direction = row.direction == "BUY" ? this.$t('transactiondetailsinlegalcurrency.buy') : this.$t('transactiondetailsinlegalcurrency.sell');
            return h("span", {}, direction);
          }
        },
        {
          title: this.$t('detailsofcurrencyentrustment.pendingorderprice'),
          key: "price"
        },
        {
          title: this.$t('entrustedmanagement.billingtime'),
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.time).format('YYYY-MM-DD HH:mm:ss')
            return h('span',{},formatTime)
          }
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          key: "status",
          render: (h, params) => {
            const row = params.row;
            var direction = '';
            if(row.status == 'TRADING'){
                direction = this.$t('detailsofcurrencyentrustment.trading');
            }
            else if(row.status == 'COMPLETED'){
                direction = this.$t('transactiondetailsinlegalcurrency.completed')
            }
            else if(row.status == 'CANCELED'){
                direction = this.$t('transactiondetailsinlegalcurrency.cancelled')
            }
            return h("span", {}, direction);
          }
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          key: "age",
          width: 150,
          render: (h, obj) => {
            var actions = [];
            actions.push(h(
                "Button",
                {
                  props: {
                    type: "info",
                    size: "small"
                  },
                  style: {
                    marginRight: '10px'
                  },
                  on: {
                    click: () => {
                      this.$router.push('/exchange/bborder/detail');
                      removeStore('exchangeOrderId');
                      removeStore("completed")
                      setStore('exchangeOrderId',obj.row.orderId);
                      setStore('completed',this.filterSearch.completed);
                    }
                  }
                },
                this.$t('currentdelegation.details')));
            if(obj.row.status == 'TRADING'){
             actions.push( h(
                "Button",
                {
                  props: {
                    type: "error",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.cancelOrder(obj.row.orderId)
                    }
                  }
                },
                this.$t('secondcontractordermanagement.undo')));
            }
            return h("div", actions);
          }
        }
      ],
      userpage: []
    };
  },
  methods: {
		// switchEnsure() {

		// },
    exportExcel() {
      this.filterSearch.isOut = 1
      queryBBOrderOut(this.filterSearch)
          .then(res => {
            // 文件下载
            console.log(res)
            let blob = new Blob([res], {type: 'application/vnd.ms-excel'})
            let objectUrl = URL.createObjectURL(blob)
            // window.location.href = objectUrl
            const fileName = this.$t('currentdelegation.currencyorder')// 导出文件名
            const elink = document.createElement('a') // 创建a标签
            elink.download = fileName // a标签添加属性
            elink.style.display = 'none'
            elink.href = objectUrl
            document.body.appendChild(elink)
            elink.click() // 执行下载
            URL.revokeObjectURL(elink.href) // 释放URL 对象
            document.body.removeChild(elink) // 释放标签
            this.$Message.success(this.$t('positionmanagementcontractassetmanagement.exportsuccessful'))
          })
    },
		localEnsure() {
			this.filterSearch.pageNo = 1;
			this.current = 1;
			this.btnType = 0;
			this.filterSearch.completed = 0;
			this.refreshPage(this.filterSearch);
		},
		hisEnsure () {
			this.filterSearch.pageNo = 1;
			this.current = 1;
			this.btnType = 1;
			this.filterSearch.completed = 1;
			this.refreshPage(this.filterSearch);
		},
    searchByFilter() {
			let reg = /[^0-9]/;
      // alert(this.memberId)
      this.current = 1
			let bol = reg.test(this.memberId);
			if(bol&&(!!this.memberId)) {
				this.$Message.warning(this.$t('currentdelegation.pleaseenterthecorrectid'))
				return;
			}

      if(isNaN(this.priceLow*1) ||　isNaN(this.priceHeight*1)) {
        this.$Message.warning(this.$t('currentdelegation.pleaseenterthecorrectprice'))
      }else if(this.priceLow*1<0 || this.priceHeight*1<0) {
        this.$Message.warning(this.$t('currentdelegation.thepriceshouldbegreaterthanorequaltozero'))
      } else {
        if(this.priceLow*1>this.priceHeight*1) {
          this.$Message.warning(this.$t('currentdelegation.note2'))
        }else{
					this.$store.commit('switchLoading', true);
          this.refreshPage(this.filterSearch);
        }
      }
    },
    refreshPageManual() {
			for(let key in this.filterSearch)  {
				this.filterSearch[key] = '';
			}
      this.currentPageIdx = 1;
      this.current = 1;
			this.btnType = 0;
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.filterSearch.completed = 0;
      this.refreshPage(this.filterSearch);
    },
   changePage(pageIndex) {
			this.current= pageIndex;
      this.filterSearch.isOut = 0
			this.filterSearch.pageNo = pageIndex;
      this.refreshPage( this.filterSearch);
    },
    refreshPage(obj) {
      obj.isOut = 0
      this.ifLoading = true;
      queryBBOrder(obj).then(res => {
        this.userpage = res.data.content;
        this.totalNum = res.data.totalElements;
				this.ifLoading = false;
				this.$store.commit('switchLoading', false);
      });
    },
    cancelOrder(orderId){
			cancelOrder({orderId:orderId}).then(res=>{
				if(res.code == 0){
					this.$Message.success(this.$t('entrustedmanagement.revocationsucceeded'));
					this.refreshPage(this.filterSearch);
				}else{
					this.$Message.error(this.$t('entrustedmanagement.undofailed'));
				}
			})
			.catch(err => console.log(err))
    }
  },
  created() {
    this.refreshPage(this.filterSearch);
  }
}
</script>

<style lang="less" scoped>
.switchTab {
	margin: 20px 0;
}
Input,.ivu-select.ivu-select-single{
  width: 150px;
}
.priceSectionWrapper{
  margin-bottom: 10px;
  .ivu-input-wrapper.ivu-input-type {
    width: 60px ;
  }
  .priceSection {
    float: left;
  }
  .orderStatus {
    margin-left: 20px;
    float: left;
  }
  .btns{
    margin-left: 10px;
    float: left;
    height: 30px;
    line-height: 30px;
  }
}
.tips{
  color: red;
}
</style>
