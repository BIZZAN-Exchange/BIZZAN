<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('chargingdetails.chargingdetails') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('chargingdetails.refresh') }} </Button>
      </p>

      <Row class="functionWrapper">
				<div class="searchWrapper clearfix">
					<div class="poptip">
						<Poptip trigger="hover"
										:content="$t('chargingdetails1.pleaseenterthechargingaddresstoquery')"
										placement="bottom-start">
							<Input :placeholder="$t('chargingdetails1.pleaseenterthechargingaddresstoquery')"
										v-model="filterSearch.address"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<span>{{ $t('perpetualcontractcurrencystandardmanagement.currency') }}</span>
						<Select v-model="filterSearch.unit">
							<Option v-for="(item, index) in coinSearchArr"
										:value="item.unit=='全部'?'': item.unit"
										:key="item.unit">{{ item.unit }}</Option>
						</Select>
					</div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
					</div>

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
import { getCoinName, memberDeposit  } from '@/service/getData';
  export default{
    data () {
      return {
				coinSearchArr: [],
				filterSearch: {
					pageNo: 1,
					pageSize: 10,
					account: '',
					address: '',
					unit: '',
					property: [],
					direction: []
				},
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        userpage: [],
        columns_first: [
					{
            title: this.$t('announcementmanagement.no'),
            key: 'id'
          },
          {
            title: this.$t('currencywithdrawalapproval.membernickname'),
            key: 'username'
          },
          {
			title: this.$t('chargingdetails1.chargingcurrency'),
			key: 'unit'
          },
          {
			title: this.$t('chargingdetails1.chargingaddress'),
			key: 'address'
          },
          {
            title: this.$t('chargingdetails1.chargingquantity'),
            key: 'amount'
          },
          {
            title: this.$t('transferindetails.walletbalance'),
            key: 'walletBalance'
          },
          {
            title: this.$t('chargingdetails1.timeofreceipt'),
            key: 'createTime'
          },
        ]
      }
    },
    methods: {
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
				this.refreshPage(this.filterSearch)
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
        memberDeposit(obj)
        .then( res => {
          if(!res.code) {
            this.ifLoading = false;
            this.pageNum = res.data.totalElements;
            this.userpage = res.data.content;
          }else this.$Message.error(res.message)
				})
      }
    },
    created () {
			getCoinName().then(res => {
				if (!res.code) {
					this.coinSearchArr = res.data;
					this.coinSearchArr.push({ name: this.$t('transactiondetailsinlegalcurrency.all'), unit: this.$t('transactiondetailsinlegalcurrency.all') })
				} else this.$Message.error(res.message);
			})
			.catch(err => {
				console.log(err);
			});
      this.refreshPage();
    }
  }
</script>

<style lang="less" scoped>

</style>
