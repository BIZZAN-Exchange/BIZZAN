<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('transactiondetails.transactiondetails') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row class="functionWrapper">
				<div class="searchWrapper clearfix">

					<div class="poptip">
						<Poptip trigger="hover"
										:content="$t('transactiondetails.pleaseenterthememberid')"
										placement="bottom-start">
							<Input :placeholder="$t('transactiondetails.pleaseenterthememberid')"
										v-model="filterSearch.memberId"/>
							</Input>
						</Poptip>
					</div>

					<!-- <div class="poptip">
						<Poptip trigger="hover"
										content="请输入会员昵称"
										placement="bottom-start">
							<Input placeholder="请输入会员昵称"
										v-model="filterSearch.account"/>
							</Input>
						</Poptip>
					</div> -->

					<div class="poptip">
						<span>{{ $t('servicechargewithdrawaldetails.transactiontype') }}</span>
						<Select v-model="filterSearch.type">
							<Option v-for="(item, index) in tradeTypeArr"
										:value="index"
										:key="item">{{ item }}</Option>
						</Select>
					</div>
					<br>
					<div class="poptip range">
						<Poptip trigger="hover"
										:content="$t('transactiondetails.minimumtransactionamountsearch')"
										placement="bottom-start">
							<Input :placeholder="$t('transactiondetails.minimumtransactionamountsearch')"
										v-model="filterSearch.minMoney"/>
							</Input>
						</Poptip>
						~
						<Poptip trigger="hover"
										:content="$t('transactiondetails.maximumtransactionamountsearch')"
										placement="bottom-start">
							<Input :placeholder="$t('transactiondetails.maximumtransactionamountsearch')"
										v-model="filterSearch.maxMoney"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip range">
						<Poptip trigger="hover"
										:content="$t('transactiondetails.minimumfeesearch')"
										placement="bottom-start">
							<Input :placeholder="$t('transactiondetails.minimumfeesearch')"
										v-model="filterSearch.minFee"/>
							</Input>
						</Poptip>
						~
						<Poptip trigger="hover"
										:content="$t('transactiondetails.maximumhandlingchargesearch')"
										placement="bottom-start">
							<Input :placeholder="$t('transactiondetails.maximumhandlingchargesearch')"
										v-model="filterSearch.maxFee"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<DatePicker
							type="daterange"
							placement="bottom-end"
							@on-change="dateRange"
							:placeholder="$t('servicechargewithdrawaldetails.selecttimeinterval')">
						</DatePicker>
					</div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
            <Button type="success" size="small" @click="exportExcel">{{ $t('positionmanagementcontractassetmanagement.export') }}</Button>
					</div>

				</div>
			</Row>

      <Row class="tableWrapper">
        <Table :columns="columns_first" :data="userpage" border :loading="ifLoading"></Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="pageNum" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
      </Row>

    </Card>
  </div>
</template>

<script>
import { perTradeAll, perTradeAllOut  } from '@/service/getData';
  export default{
    data () {
      return {
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
		tradeTypeArr: [ this.$t('handlingchargemanagement.recharge'), this.$t('handlingchargemanagement.withdrawal'), this.$t('handlingchargemanagement.transfer'), this.$t('servicechargewithdrawaldetails.currencytransaction'), this.$t('handlingchargemanagement.purchaseinlegalcurrency'), this.$t('handlingchargemanagement.sellinginlegalcurrency'), this.$t('handlingchargemanagement.activityrewards'), this.$t('handlingchargemanagement.promotionreward'), this.$t('handlingchargemanagement.dividends'), this.$t('handlingchargemanagement.vote'), this.$t('handlingchargemanagement.manualrecharge'), this.$t('handlingchargemanagement.pairing'),
              this.$t('handlingchargemanagement.eventredemption'), this.$t('transactiondetails.c2cbuy'), this.$t('transactiondetails.c2csales'), this.$t('transactiondetails.redenvelopeissued'), this.$t('transactiondetails.redenvelopecollection'),this.$t('essentialinformation.withdrawalcodewithdrawal'),this.$t('essentialinformation.cashwithdrawalcoderecharge'),this.$t('transactiondetails.servicechargeofperpetualcontract'),this.$t('transactiondetails.profitfromperpetualcontracts'),this.$t('transactiondetails.lossonperpetualcontracts'),this.$t('essentialinformation.optioncontractfailed'),this.$t('essentialinformation.optioncontractfee'),this.$t('essentialinformation.optioncontractbonus'),this.$t('essentialinformation.contractrebate'),this.$t('essentialinformation.peerrewards'),this.$t('essentialinformation.platformfeeincome'),this.$t('transactiondetails.secondscontractfailed'),this.$t('transactiondetails.secondcontractBonus'),this.$t('transactiondetails.Financialinterest'),this.$t('transactiondetails.spendingmoney'),this.$t('transactiondetails.getFundedcosts'),
      this.$t('transactiondetails.buyin'),this.$t('transactiondetails.Settosell'),this.$t('transactiondetails.buyregularly'),this.$t('transactiondetails.Regularredemption') ],
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        userpage: [],
        columns_first: [
          {
            title: this.$t('essentialinformation.memberid'),
            key:"memberId"
          },
          {
            title: this.$t('handlingchargemanagement.transactiontype'),
            render: (h ,obj) => {
              let type =  obj.row.type;
              return h('span',{
              },this.tradeTypeArr[type])
            }
          },
          {
            title: this.$t('essentialinformation.transactionamount'),
            render: (h ,obj) => {
              let  amount =  obj.row.amount;
              let  symbol =  obj.row.symbol;
              return h('span',{},amount+' '+symbol)
            }
          },
          {
            title: this.$t('handlingchargemanagement.transactionhandlingfee'),
            key:"fee"
          },
          {
            title: this.$t('transactiondetailsinlegalcurrency.transactiontime'),
            key:"createTime"
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
              const fileName = this.$t('transactiondetails.transactiondetails')// 导出文件名
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
			searchByFilter() {
				let reg = /\D/;
				if(reg.test(this.filterSearch.memberId)) {
					this.$Message.warning(this.$t('transactiondetails.note1'));
					return;
				}
				this.currentPageIdx = 1;
				this.filterSearch.pageNo = 1;
				this.filterSearch.isOut = 0
				this.refreshPage(this.filterSearch)
			},
			dateRange(val) {
				this.filterSearch.startTime = val[0] + " 00:00:00";
				this.filterSearch.endTime = val[1] + " 00:00:00";
			},
      refreshPageManual() {
				this.currentPageIdx = 1;
				for(let key in this.filterSearch) {
					this.filterSearch[key] = '';
				}
				this.filterSearch.pageNo = 1;
				this.filterSearch.pageSize = 10;
				this.filterSearch.isOut = 0
				this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
				this.currentPageIdx = pageIndex;
				this.filterSearch.pageNo = pageIndex;
				this.filterSearch.isOut = 0
				this.refreshPage(this.filterSearch)
      },
      refreshPage(obj) {
				this.ifLoading = true;
        obj.outType = 0
        perTradeAll(obj).then( res => {
          if(!res.code) {
						this.ifLoading = false;
						this.pageNum = (res.data && res.data.totalElements) || 1;
						this.userpage = (res.data && res.data.content) || [];
          }else{
						this.$Message.error(res.message)
					}
        })
      }
    },
    created () {
      this.refreshPage({});
    }
  }
</script>

<style lang="less" scoped>

</style>
