<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('transferindetails.transferindetails') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row>
        <Table 
          :columns="columns_first" 
          :data="userpage" 
          border
          :loading="ifLoading">
				</Table>
      </Row>

      <div class="pageWrapper">
        <Page  
          :total="totalNum" 
          :current="currentPageIdx"   
          @on-change="changePage" 
          show-elevator></Page>
      </div>
    </Card>
    </div>
</template>

<script>

import { coinTransferDetail } from '@/service/getData';

export default {
	name: 'AccessLog',
  data() {
    return {
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,
      columns_first: [
        {
					title: 'ID',
					key: 'id'
				},
				{
          title: this.$t('transferindetails.transferincurrency'),
          key: "unit"
				},
        {
          title: this.$t('transferindetails.transferintime'),
          key: "transferTime"
        },
         {
          title: this.$t('transferindetails.transferinquantity'),
          key: "amount"
        },
        {
					title: this.$t('transferindetails.walletbalance'),
					key: 'balance',
					render: (h, obj) => {
							let num = obj.row.balance;
							if(num<1) {
								num = num.toFixed(8);
							}
							return h('span', {}, num)
						}
        },
        {
          title: this.$t('transferindetails.minimumhandlingcharge'),
					key: "minerFee",
					render: (h, obj) => {
						let num = obj.row.minerFee;
						if(num<1) {
							num = num.toFixed(8);
						}
						return h('span', {}, num)
					}
        },
				{
          title: this.$t('transferindetails.operator'),
          key: "adminName"
				},
				{
          title: this.$t('transferindetails.coldwalletaddress'),
          key: "coldAddress"
				},
				{
          title: this.$t('transferindetails.transferinno'),
          key: "transactionNumber"
				},
				
			],
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				unit: '',
				transactionNumber: ''
			},
      userpage: [],
    };
  },
  methods: {
    refreshPageManual() {
			this.currentPageIdx = 1;
			this.filterSearch.pageNo = 1;
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
			this.ifLoading = true;
      coinTransferDetail(obj)
      .then(res => {
        if(!res.code){
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
				}else this.$Message,error(res.message);
      });
    },
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
      this.refreshPage(this.filterSearch);
    },
  },
  created() {
		this.filterSearch.unit = this.$route.query.unit;
		this.refreshPage(this.filterSearch);
  }
}
</script>

<style scoped lang='less'>
 
</style>