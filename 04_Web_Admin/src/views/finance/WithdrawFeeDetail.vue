<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('servicechargewithdrawaldetails.servicechargewithdrawaldetails') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

     <Row class="functionWrapper">
        <div class="searchWrapper clearfix">
						<span>{{ $t('servicechargewithdrawaldetails.transactiontype') }}</span>
						<Select v-model="tradeType" style="width: 200px">
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
							@on-change="dateRange"
							:placeholder="$t('servicechargewithdrawaldetails.selecttimeinterval')">
						</DatePicker>
				</div>
        <div class="searchWrapper clearfix">
          <Button type="primary" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
        </div>
      </Row>

      <Row class="tableWrapper">
        <Table :columns="columns_first"
							@on-sort-change="definedOrder"
							:data="userpage" border
							:loading="ifLoading">
				</Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="pageNum" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
      </Row>

    </Card>
  </div>
</template>

<script>
import { withdrawFeeDetail  } from '@/service/getData';
  export default{
    data () {
      return {
      	tradeType: -1,
      	tradeTypeArr: [this.$t('servicechargewithdrawaldetails.all'), this.$t('servicechargewithdrawaldetails.currencytransaction'), this.$t('servicechargewithdrawaldetails.perpetualcontract'), this.$t('servicechargewithdrawaldetails.optioncontract')],
		filterSearch: {
			pageNo: 1,
			pageSize: 10,
			bizType: '',
			startTime: '',
			endTime: ''
		},
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        userpage: [],
        columns_first: [
          {
            title: this.$t('servicechargewithdrawaldetails.businesstype'),
            key: 'bizType',
            render(h, obj) {
            	if(obj.row.bizType=="echange") {
            		return h("span", {}, this.$t('servicechargewithdrawaldetails.currencytransaction'));
            	} else if(obj.row.bizType=="swap") {
					return h("span", {}, this.$t('servicechargewithdrawaldetails.perpetualcontract'));
            	} else {
            		return h("span", {}, this.$t('servicechargewithdrawaldetails.optioncontract'));
            	}
          	}
          },
          {
            title: this.$t('servicechargewithdrawaldetails.handlingchargetype'),
            key: 'feeType',
            render(h, obj) {
            	if(obj.row.bizType=="echange") {
            		return h("span", {}, obj.row.feeType=="left"? this.$t('servicechargewithdrawaldetails.handlingchargeforbuyingcurrency'): this.$t('servicechargewithdrawaldetails.handlingchargeforsellingcurrency'));
            	} else if(obj.row.bizType=="swap") {
					return h("span", {}, obj.row.feeType=="left"? this.$t('perpetualcontractcurrencystandardmanagement.openingfee'): this.$t('perpetualcontractcurrencystandardmanagement.closingfee'));
            	} else {
            		return h("span", {}, obj.row.feeType=="left"? this.$t('servicechargewithdrawaldetails.optionpumping'): this.$t('servicechargewithdrawaldetails.optionfee'));
            	}
          	}
          },
          {
            title: this.$t('managementofoptioncontractsineachperiod.transactionpair'),
            key: 'symbol'
          },
          {
            title: this.$t('servicechargewithdrawaldetails.extractedquantity'),
            key: 'feeAmount',
            render(h, obj) {
            	return h("span", {}, obj.row.feeAmount+"("+obj.row.feeCoin+")");
          	}
          },
          {
            title: this.$t('servicechargewithdrawaldetails.extractiontime'),
            key: 'createTime'
          }
        ]
      }
    },
    watch: {
    	tradeType: function(news, olds) {
    		console.log(olds, news);
    		if(news==0) {
				this.filterSearch.bizType = "";
    		} else if(news==1) {
				this.filterSearch.bizType = "exchange";
    		} else if(news==2) {
				this.filterSearch.bizType = "swap";
    		} else if(news==3) {
    			this.filterSearch.bizType = "option";
    		}
    	}
    },
    methods: {
    	 dateRange(val) {
	        this.filterSearch.startTime = val[0];
			this.filterSearch.endTime = val[1];
	      },
		definedOrder(obj) {
			let direction = obj.order==='desc' ? 1 : 0;
			let propertyIndex = this.filterSearch.property.indexOf(obj.key);

			if(propertyIndex!==-1){
				this.filterSearch.direction[propertyIndex] = direction;
			}else{
				this.filterSearch.property.push(obj.key);
				this.filterSearch.direction.push(direction);
			}

			this.refreshPage(this.filterSearch);
		},
		searchByFilter() {
			this.currentPageIdx = 1;
			this.filterSearch.pageNo = 1;
			this.refreshPage(this.filterSearch);
		},
      	refreshPageManual() {
			this.currentPageIdx = 1;
			for(let key in this.filterSearch) {
				this.filterSearch[key] = '';
			}
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
			this.refreshPage(this.filterSearch)
      },
      refreshPage(obj) {
		this.ifLoading = true;
        withdrawFeeDetail(obj).then( res => {
          if(!res.code) {
            this.ifLoading = false;
            this.pageNum = res.data.totalElements;
            this.userpage = res.data.content;
          }else {
          	this.$Message.error(res.message)
          }
		});
      }
    },
    created () {
      this.refreshPage();
    }
  }
</script>

<style>
/* .ivu-select-item-selected, .ivu-select-item-selected:hover{
	background: #fff;
	color: #000;
} */
</style>
