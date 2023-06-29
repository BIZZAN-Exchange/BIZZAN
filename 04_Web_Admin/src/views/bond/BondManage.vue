<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('querymarginstrategy.querymarginstrategy') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('querymarginstrategy.refresh') }} </Button>
      </p>

			<Row class="functionWrapper">
				<div class="btnsWrapper clearfix ">
					<Button type="primary" @click="addNew">{{ $t('querymarginstrategy.newlyadded') }}</Button>
				</div>
        <div class="searchWrapper clearfix">

				</div>
			</Row>		
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

			<Modal 
				v-model="authModal"
				:title="$t('querymarginstrategy.editmargin')"
				width="400"
				@on-ok="confirmSub"
				@on-cancel="cancelSub"
			>
				<Form :model="businessAuth" :label-width="50">
					<FormItem :label="$t('querymarginstrategy.currency')" v-if="isNew">
						<Select v-model="businessAuth.coinUnit">
							<Option v-for=" (coin, index) in allCoinArr " :value="coin.unit" :key="index">{{ coin.unit }}</Option>
						</Select>
					</FormItem>

					<FormItem :label="$t('querymarginstrategy.status')" v-if="!isNew">
						<RadioGroup v-model="businessAuth.status">
							<Radio :label="0">
								<span>{{ $t('querymarginstrategy.normal') }}</span>
							</Radio>
							<Radio :label="1">
								<span>{{ $t('querymarginstrategy.disable') }}</span>
							</Radio>
						</RadioGroup>
					</FormItem>

					<FormItem :label="$t('querymarginstrategy.number')">
						<Input v-model="businessAuth.amount"></Input>
					</FormItem>


				</Form>

			</Modal>
    </Card>
    </div>
</template>

<script>

import { MemberRealNameList, accessLog, queryBusinessAuth, createBusinessAuth, updateBusinessAuth, getCoinName } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
	name: 'BondManage',
  data() {
    return {
			isNew: true,
			authModal: false,
			allCoinArr: [],
			filterSearch: {
				status: null,
				pageNo: 1,
				pageSize: 10,
				direction: [],
				property: [],
			},
			businessAuth: {
				id: '',
				status: '',
				amount: '',
				coinUnit: '',
			},
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,
      columns_first: [
        {
					title: this.$t('querymarginstrategy.currency'),
					key: 'unit',
					render: (h, ctx) => {
						return h('span', {}, ctx.row.coin.unit )
					}
        },
        {
          title: this.$t('querymarginstrategy.amountofdeposit'),
          key: "amount"
        },
         {
          title: this.$t('querymarginstrategy.creationtime'),
          key: "createTime"
        },
        {
					title: this.$t('querymarginstrategy.status'),
					key: 'status',
					render: (h, ctx) => {
						return h('span', {}, !ctx.row.status ? this.$t('querymarginstrategy.normal') : this.$t('querymarginstrategy.disable') )
					}
        },
        {
          title: this.$t('querymarginstrategy.operation'),
					key: "operation",
					render: (h, ctx) => {
						return h ('Button', {
							props: {
								type: 'info',
								size: 'small'
							},
							on: {
								click: () => {
									this.isNew = false;
									this.authModal = true;

									this.businessAuth.id = ctx.row.id;
									this.businessAuth.status = ctx.row.status;
									this.businessAuth.amount = ctx.row.amount;
									
								}
							}
						}, this.$t('querymarginstrategy.viewedit'))
					}
        },
      ],
      userpage: [],
    };
  },
  methods: {
		confirmSub() {
			let fn = Function;
			if(this.isNew) {
				fn = createBusinessAuth;
			}else{
				fn = updateBusinessAuth;
			}
			fn(this.businessAuth)
			.then(res => {
				if(!res.code) {
					this.$Message.success(res.message);
					this.cancelSub();
					this.refreshPage();
				}else this.$Message.error(res.message);
			})
			.catch(err => console.log(err))
		},
		cancelSub() {
			for(let key in this.businessAuth)  {
				this.businessAuth[key] = '';
			}
		},
		addNew() {
			this.isNew = true;
			this.authModal = true;
		},
		searchByFilter() {
			this.filterSearch.pageNo = 1;
			this.currentPageIdx = 1;
      this.refreshPage(this.filterSearch);
		},
    refreshPageManual() {
			this.currentPageIdx = 1;
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.filterSearch.status = null;
			this.filterSearch.direction = [];
			this.filterSearch.property = [];
      this.refreshPage(this.filterSearch);
    },
    refreshPage() {
			this.ifLoading = true;
      queryBusinessAuth(this.filterSearch)
      .then(res => {
        if(!res.code){
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
				}else this.$Message.error(res.message);
      });
    },
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
      this.refreshPage(this.filterSearch);
    },
  },
  created() {
		getCoinName()
			.then(res => {
				if(!res.code) {
					this.allCoinArr = res.data;
				}else this.$Message.error(res.message)
			})
			.catch(err => {console.log(err)})
		this.refreshPage()
  }
}
</script>

<style scoped lang='less'>
 
</style>