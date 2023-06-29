<template>
  <div class="ExitBond">
    <Card>
      <p slot="title">
        {{ $t('surrendermanagement.surrendermanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

			<Row class="functionWrapper">
        <div class="searchWrapper clearfix">

					<div class="poptip">
						<Poptip trigger="hover" :content="$t('surrendermanagement.note2')" placement="bottom-start">
							<Input :placeholder="$t('surrendermanagement.note2')" v-model="filterSearch.account"/></Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Select v-model="filterSearch.status" :placeholder="$t('surrendermanagement.pleaseselectastatus')">
							<Option v-for="item in statusArr" :value="item.value" :key="item.value">{{item.name}}</Option>
						</Select>
					</div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
					</div>
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
				class="bondCheck"
				:title="$t('surrendermanagement.surrenderreview')"
				v-model="bondCheck"
				@on-cancel="cancelSub"
			>
				<Row>{{$t('surrendermanagement.surrenderamount')}}{{
					!exitBondDetail.depositRecord ? '' : exitBondDetail.depositRecord.amount+exitBondDetail.depositRecord.coin.unit }}</Row>
				<Row>{{$t('surrendermanagement.note6')}} {{ (!exitBondDetail.businessCancelApply ? $t('surrendermanagement.none') : exitBondDetail.businessCancelApply.reason) | reasonFilter(vtext) }}</Row>
				<br>
				<Row>
					<Col span="8">
						{{$t('surrendermanagement.note3')}}{{ exitBondDetail.advertiseNum }}
					</Col>
					<Col span="8">
						{{$t('surrendermanagement.note1')}}{{ exitBondDetail.complainantNum }}
					</Col>
					<Col span="8">
						{{$t('surrendermanagement.note5')}}{{ exitBondDetail.defendantNum }}
					</Col>
				</Row>
				<br>
				<Row>
					<Col span="12">
						{{$t('surrendermanagement.note7')}}{{ !exitBondDetail.money ? 0 : exitBondDetail.money }}
					</Col>
					<Col span="12" >
						{{$t('surrendermanagement.note4')}}{{ !exitBondDetail.fee ? 0 : exitBondDetail.fee }}
					</Col>
				</Row>
				<br>
				<br>
				<p>{{ $t('surrendermanagement.note8') }}</p>

				<div slot="footer">
					<Row>
						<Col span="8" offset="3">
							<Button long type="info" @click="confirmPass(1)">{{ $t('surrendermanagement.approved') }}</Button>
						</Col>
						<Col span="8" offset="3">
							<Button long type="error" @click="confirmPass(0)">{{ $t('surrendermanagement.failedtopassthereview') }}</Button>
						</Col>
					</Row>
				</div>
			</Modal>

			<Modal :title="$t('surrendermanagement.reasonforrejectionoptional')"
				v-model="rejectModal"
				width="400"
				@on-ok="subReject"
			>
				<Input v-model="subCheck.reason" type="textarea" placeholder="$t('surrendermanagement.note9')">
				</Input>
			</Modal>
    </Card>
    </div>
</template>

<script>

import { cancelBusinessStatus, MemberRealNameList, accessLog, queryCancelApply, cancelApplyDetail, checkApply } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
	name: 'ExitBond',
  data() {
    return {
			rejectModal: false,
      vtext: this.$t('surrendermanagement.none'),
			exitBondDetail: {},
			subCheck: {
				id: '',
				success: '',
				reason: '',

			},
			bondCheck: false,
			statusArr: [],
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				status: '',
				account: '',
				direction: [],
				property: []
			},
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,
      columns_first: [
        {
					title: this.$t('currencywithdrawalapproval.membernickname'),
					key: 'username',
					render: (h, ctx) => {
						return h('span', {}, ctx.row.member.username)
					}
        },
        {
          title: this.$t('surrendermanagement.membermailbox'),
					key: "email",
					render: (h, ctx) => {
						return h('span', {}, ctx.row.member.email)
					}
        },
         {
          title: this.$t('surrendermanagement.membermobilenumber'),
					key: "mobilePhone",
					render: (h, ctx) => {
						return h('span', {}, ctx.row.member.mobilePhone)
					}
        },
        {
					title: this.$t('surrendermanagement.guaranteedgoldcoins'),
					key: 'unit',
					render: (h, ctx) => {
						return h('span', {}, ctx.row.depositRecord.coin.unit)
					}
        },
        {
          title: this.$t('querymarginstrategy.amountofdeposit'),
					key: "amount",
					render: (h, ctx) => {
						return h('span', {}, ctx.row.depositRecord.amount)
					}
        },
        {
          title: this.$t('surrendermanagement.submitforreviewtime'),
          key: "cancelApplyTime"
				},
				{
          title: this.$t('currencywithdrawalauditmanagement.audittime1'),
					key: "handleTime",
					render: (h, ctx) => {
						return h('span', {}, !ctx.row.handleTime ? '-' : ctx.row.handleTime);
					}
				},
				{
          title: this.$t('managementofoptioncontractsineachperiod.status'),
					key: "status",
					render: (h, ctx) => {
						let status = '';
						if(ctx.row.status === 6) status = this.$t('currencywithdrawalauditmanagement.fail');
						else if(ctx.row.status === 5) status = this.$t('currencywithdrawalapproval.pendingreview');
						else if(ctx.row.status === 7) status = this.$t('currencywithdrawalapproval.success');
						return h('span', {}, status);
					}
				},
				// {
        //   title: "审核人",
        //   key: "module"
				// },
				{
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
					key: "module",
					render: (h, ctx) => {
						let btnText = this.$t('currencywithdrawalauditmanagement.audit');
						let btnType = 'info';
						if(ctx.row.status === 5) {
							 btnText = this.$t('currencywithdrawalauditmanagement.audit');
							 btnType = 'info';
						}else{
							 btnText = this.$t('transactiondetailsinlegalcurrency.view');
						   btnType = 'success';
						}
						return h('Button', {
							props: {
								type: btnType,
								size: 'small'
							},
							on: {
								click: () => {
									if(ctx.row.status === 5) {
										cancelApplyDetail({id: ctx.row.id})
										.then(res => {
											if(!res.code) {
												this.exitBondDetail = res.data;
												this.bondCheck = true;
												this.subCheck.id = ctx.row.id;
											}else this.$Message.error(res.message);
										})
									}else{
										this.$router.push({path: '/otc/adManage/exitbonddetail', query: { id: ctx.row.id }});
									}
								}
							}
						}, btnText)
					}
				}
      ],
      userpage: [],
    };
  },
  methods: {
		cancelSub() {
			for(let key in this.subCheck)  {
				this.subCheck[key] = '';
			}
		},
		subReject() {
			checkApply(this.subCheck)
			.then(res => {
				if(!res.code) {
					this.$Message.success(res.message);
					this.refreshPage(this.filterSearch);
					this.cancelSub();
					this.rejectModal = this.bondCheck = false;
				}else this.$Message.error(res.message)
			})
		},
		confirmPass(n) {
			this.subCheck.success = n;
			if(!n) this.rejectModal = true;
			else{
				checkApply(this.subCheck)
				.then(res => {
					if(!res.code) {
						this.$Message.success(res.message);
						this.refreshPage(this.filterSearch);
						this.cancelSub();
						this.rejectModal = this.bondCheck = false;
					}else this.$Message.error(res.message)
				})
			}
		},
		searchByFilter() {
			this.filterSearch.pageNo = 1;
			this.currentPageIdx = 1;
			console.log(this.filterSearch);

      this.refreshPage(this.filterSearch);
		},
    refreshPageManual() {
			this.currentPageIdx = 1;
			for(let key in this.filterSearch)  {
				this.filterSearch[key] = '';
			}
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.filterSearch.direction = [];
			this.filterSearch.property = [];
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
			this.ifLoading = true;
      queryCancelApply(obj)
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
		cancelBusinessStatus().then(res => {
			if(!res.code){
				this.statusArr = res.data;
				this.statusArr.push({ value: '', name: this.$t('transactiondetailsinlegalcurrency.all') });
			}else this.$Message.error(res.message);
		});
		this.refreshPage()
	},
	filters: {
		reasonFilter(val,vtext) {
			if(!val) return vtext;
			else return val;
		}
	}
}
</script>

<style scoped lang='less'>
	.bondCheck{
		.ivu-row{
			font-size: 16px;
			line-height: 32px;
		}
		p{
			color: #afafaf;
			text-align: center;
		}
	}
</style>
