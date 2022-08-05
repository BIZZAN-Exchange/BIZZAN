<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('withdrawaldetails.withdrawaldetails') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row class="functionWrapper">
				<div class="searchWrapper clearfix">
					<!-- <div class="poptip">
						<Poptip trigger="hover"
										content="请输入用户昵称查询"
										placement="bottom-start">
							<Input placeholder="请输入用户昵称查询"
										v-model="filterSearch.memberUsername"/>
							</Input>
						</Poptip>
					</div> -->

					<div class="poptip">
						<Poptip trigger="hover"
										:content="$t('withdrawaldetails.note1')"
										placement="bottom-start">
							<Input :placeholder="$t('withdrawaldetails.note1')" style="width:200px"
										v-model="filterSearch.account"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover"
										:content="$t('withdrawaldetails.pleaseenteryourmobilenumbertoquery')"
										placement="bottom-start">
							<Input :placeholder="$t('withdrawaldetails.pleaseenteryourmobilenumbertoquery')"
										v-model="filterSearch.mobilePhone"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<span>{{ $t('perpetualcontractcurrencystandardmanagement.currency') }}</span>
						<Select v-model="filterSearch.unit">
							<Option v-for="(item, index) in coinSearchArr" :value="item.unit" :key="item.unit">{{ item.unit }}</Option>
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
import { getCoinName, withdrawManage  } from '@/service/getData';
  export default{
    data () {
      return {
				coinSearchArr: [],
				filterSearch: {
					pageNo: 1,
					pageSize: 10,
					property: [],
					direction: [],
					// memberUsername: '',
					// memberRealName: '',
					account:"",
					mobilePhone: '',
					unit: ''
				},
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        userpage: [],
        columns_first: [
          {
            title: 'TXID',
            key: 'transactionNumber'
          },
		  {
            title: this.$t('collectionconfigurationmanagement.currencyname1'),
            key: 'unit'
          },
          {
            title: this.$t('withdrawaldetails.numberofcurrencywithdrawals'),
            key: 'totalAmount'
          },
          {
						title: this.$t('withdrawaldetails.actualreceipts'),
						key: 'arrivedAmount'
          },
          {
						title: this.$t('withdrawaldetails.handlingchargeforcurrencywithdrawal'),
						key: 'fee'
          },
          {
            title: this.$t('currencywithdrawalapproval.applicationtime'),
            key: 'createTime'
          },
          {
            title: this.$t('withdrawaldetails.usernickname'),
            key: 'memberUsername'
          },
          {
            title: this.$t('memberinvitationlist.mailbox'),
            key: 'email'
          },
          {
            title: this.$t('memberinvitationlist.cellphonenumber'),
            key: 'phone'
          },
          {
            title: this.$t('businessinformation.realname'),
            key: 'memberRealName'
          },
          {
            title: this.$t('withdrawaldetails.walletaddress'),
            key: 'address'
          },
          {
            title: this.$t('currencywithdrawalauditmanagement.audittime1'),
            key: 'dealTime'
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
				let reg = /1[3456789]\d{9}/g;d
				let phone = this.filterSearch.mobilePhone;
				if(phone && !reg.test(phone)){
						this.$Message.error(this.$t('withdrawaldetails.pleaseenterthecorrectmobilenumber'))
				}else{
					this.currentPageIdx = 1;
					this.filterSearch.pageNo = 1;
					this.refreshPage(this.filterSearch)
				}
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
        withdrawManage(obj).then( res => {
          if(!res.code) {
            this.ifLoading = false;
            this.pageNum = res.data.totalElements;
            this.userpage = res.data.content;
          }else this.$Message.error(res.message)
				})
      }
    },
    created () {
			getCoinName()
			.then(res => {
				if (!res.code) {
					this.coinSearchArr = res.data;
					// this.coinSearchArr.push({ name: '全部', unit: '' })
				} else this.$Message.error(res.message);
			})
			.catch(err => {
				console.log(err);
			});
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
