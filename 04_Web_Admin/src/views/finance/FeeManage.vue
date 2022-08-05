<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('handlingchargemanagement.handlingchargemanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row class="functionWrapper">
        <div class="searchWrapper clearfix">
						<span>{{ $t('servicechargewithdrawaldetails.transactiontype') }}</span>
						<Select v-model="filterSearch.type" style="width: 200px">
							<Option v-for="(item, index) in tradeTypeArr"
										:value="index"
										:key="item">{{ item }}</Option>
						</Select>
				</div>
        <div class="searchWrapper clearfix" style="margin-left:20px;">
						<span>{{ $t('transactiondetailsinlegalcurrency.tradingtime') }}</span>
						<DatePicker
							type="daterange"
							placement="bottom-end"
              v-model="value1"
							@on-change="dateRange"
							:placeholder="$t('servicechargewithdrawaldetails.selecttimeinterval')">
						</DatePicker>
				</div>
        <div class="searchWrapper clearfix">
          <Button type="primary" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
          <Button type="success" @click="exportExcel">{{ $t('positionmanagementcontractassetmanagement.export') }}</Button>
        </div>
      </Row>

      <Row class="tableWrapper">
        <Table :columns="columns_first" :data="userpage" border :loading="ifLoading"></Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="pageNum"  :current="current"   @on-change="changePage" show-elevator></Page>
      </Row>
    </Card>
  </div>
</template>

<script>

import { perTradeAll, perTradeAllOut  } from '@/service/getData';

  export default{
    data () {
      return {
        value1:"",
        filterSearch: {
					pageNo: 1,
					pageSize: 10,
					memberId: '',
					type: '',
					minMoney: '',
					maxMoney: '',
					minFee: '',
					maxFee: '',
					startTime: '',
					endTime: '',
          isOut: 0
				},
        ifLoading: true,
        current: 1,
        pageNum: null,
        userpage: [],
        tradeTypeArr: [ this.$t('handlingchargemanagement.recharge'), this.$t('handlingchargemanagement.withdrawal'), this.$t('handlingchargemanagement.transfer'), this.$t('servicechargewithdrawaldetails.currencytransaction'), this.$t('handlingchargemanagement.purchaseinlegalcurrency'), this.$t('handlingchargemanagement.sellinginlegalcurrency'),
              this.$t('handlingchargemanagement.activityrewards'), this.$t('handlingchargemanagement.promotionreward'), this.$t('handlingchargemanagement.dividends'), this.$t('handlingchargemanagement.vote'), this.$t('handlingchargemanagement.manualrecharge'), this.$t('handlingchargemanagement.pairing'),
              this.$t('handlingchargemanagement.eventredemption'), this.$t('handlingchargemanagement.ctcbuy'), this.$t('handlingchargemanagement.ctcsales'), this.$t('handlingchargemanagement.redenvelope'), this.$t('handlingchargemanagement.receiveredenvelopes') ],
        columns_first: [
          {
            title: this.$t('realnamemanagement.memberid'),
            key:"memberId"
          },

          {
            title: this.$t('handlingchargemanagement.transactiontype'),
						key: 'type',
						render: (h, obj) => {
              	return h ('span', {}, this.tradeTypeArr[obj.row.type])
						}
          },
          {
            title: this.$t('servicechargewithdrawaldetails.handlingchargetype'),
            key:"symbol"
          },

          {
            title: this.$t('transactiondetailsinlegalcurrency.transactiontime'),
            key:"createTime"
          },
          {
            title: this.$t('handlingchargemanagement.transactionhandlingfee'),
            key:"fee"
          },
        ]
      }
    },
    methods: {
      exportExcel() {
        this.filterSearch.isOut = 1
        perTradeAllOut(this.filterSearch)
            .then(res => {
              // 文件下载
              let blob = new Blob([res], {type: 'application/vnd.ms-excel'})
              let objectUrl = URL.createObjectURL(blob)
              // window.location.href = objectUrl
              const fileName = this.$t('handlingchargemanagement.handlingfeedetails')// 导出文件名
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
      searchByFilter(){
        this.current = 1;
        console.log(this.filterSearch)
				this.filterSearch.pageNo = 1;
        this.filterSearch.isOut = 0
				this.refreshPage(this.filterSearch)
      },
      refreshPageManual() {
        this.current = 1;
        this.value1 = "";
				for(let key in this.filterSearch) {
					this.filterSearch[key] = '';
				}
				this.filterSearch.pageNo = 1;
				this.filterSearch.pageSize = 10;
				this.filterSearch.isOut = 0
				this.refreshPage(this.filterSearch);
      },
      dateRange() {
        console.log(this.value1)
          this.filterSearch.startTime = this.value1[0] && this.timeforamt(this.value1[0])
          this.filterSearch.endTime = this.value1[0] && this.timeforamt(this.value1[1])
      },
      addZero(num){
        return num>=10?num+"":"0"+num;
      },
      timeforamt(time){
         let year = time.getFullYear();
         let month = this.addZero(time.getMonth() + 1);
         let day = this.addZero(time.getDate());
         return `${year}-${month}-${day} 00:00:00`
      },
      changePage(pageIndex) {
        this.current = pageIndex;
        this.filterSearch.pageNo = pageIndex;
        this.filterSearch.isOut = 0
        this.refreshPage( this.filterSearch );
      },
      refreshPage(obj) {
        this.ifLoading = true;
        obj.outType = 1
        perTradeAll(obj).then( res => {
          if(!res.code) {
            this.ifLoading = false;
            this.pageNum = (res.data && res.data.totalElements) || 1;
						this.userpage = (res.data && res.data.content) || [];
          } else {
            this.$Message.error(res.message);
          }
        } )
      }

    },
    created () {
      this.refreshPage({});
    }
  }
</script>

<style lang="less" scoped>

</style>

