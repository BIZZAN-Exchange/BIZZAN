<template>
	<div>
		<Row class="btns">
			<Button type="primary"  @click="refreshPage"> 
				<Icon type="refresh"></Icon> {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} 
			</Button>
		</Row>
		<Card>
			<div slot="title">
				{{ $t('essentialinformation.essentialinformation') }}	</div>
			<div class="baseInfo">
				<Row>
					<Col span="6">
						<p>{{ $t('currencywithdrawalauditmanagement.status') }}<span>{{ !baseInfo.status ? $t('backstageadvertising.ontheshelf') : $t('backstageadvertising.offtheshelf')  }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('servicechargewithdrawaldetails.transactiontype') }}<span>{{ !baseInfo.advertiseType ?  $t('backstageadvertising.buy') : $t('backstageadvertising.sell') }}</span></p>
					</Col span="6">
					<Col span="6">
						<p>{{ $t('essentialinformation1.transactioncurrency') }}<span>{{ !baseInfo.coin ? '' : baseInfo.coin.unit  }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('essentialinformation1.transactioncurrency1') }}<span>{{ !baseInfo.country ? '' : baseInfo.country.localCurrency }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>{{ $t('essentialinformation1.transactionterm') }}<span>{{baseInfo.timeLimit}}{{ $t('controlpanelrobot.minute') }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('essentialinformation1.purchasevolume') }}<span>{{ baseInfo.dealAmount }}</span></p>
					</Col span="6">
					<Col span="6">
						<p>{{ $t('essentialinformation1.country') }}<span>{{ !baseInfo.country ? '' : baseInfo.country.zhName }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('essentialinformation1.paymentmethod') }}<span>{{ baseInfo.payMode }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>{{ $t('essentialinformation1.minimumtransactionamount') }}<span>{{ baseInfo.minLimit }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('essentialinformation1.maximumtransactionvolume') }}<span>{{ baseInfo.maxLimit }}</span></p>
					</Col span="6">
					<Col span="6">
						<p>{{ $t('essentialinformation1.fixedprice') }}<span>{{ baseInfo.price }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>{{ $t('essentialinformation1.remarks') }}<span>{{ baseInfo.remark }}</span></p>
					</Col>
					<Col span="6" offset="6">
						<p>{{ $t('essentialinformation1.autoreply') }}<span>{{ !baseInfo.auto ? $t('addeditredenvelopes.yes') : $t('addeditredenvelopes.no')}}</span></p>
					</Col span="6">
				</Row>
			</div>
		</Card>
			<br>
			<br>
		<Card>
			<div slot="title">
				{{ $t('essentialinformation1.orderdetails') }}	<!-- <Button type="primary" size="small" @click="refreshPageManual">
					<Icon type="refresh"></Icon> 刷新
				</Button> -->
			</div>
			<Row>
				<Table
					:loading="ifLoading"
					:columns="columnsList"
					:data="userpage"
					border>
				</Table>
			</Row>
		
			<Row class="pageWrapper" >
				<Page :total="cbData.totalElements" 
							:current="currentPageIdx"   
							@on-change="changePage" 
							show-elevator>
				</Page>
			</Row>
			
		</Card>				
	</div>
</template>

<script>
import { advDetailOtc, queryOtcOrder } from '@/service/getData'
export default {
	data() {
		return {
			cbData: {},
			ifLoading: false,
			currentPageIdx: 1,
			id: '',
			memberName: '',
			baseInfo: {},
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				memberName: ''
			},
			columnsList: [
				{
					title: this.$t('detailsofcurrencyentrustment.orderno'),
					key: 'orderSn'
				},
				{
					title: this.$t('transactiondetailsinlegalcurrency.trader'),
					key: 'customerName',
					render: (h, obj) => {
						let cName = obj.row.customerName;
						let mName = obj.row.memberName;
						return h('span',{}, `${cName}(${mName})`)
					}
				},
				{
					title: this.$t('transactiondetailsinlegalcurrency.transactiontime'),
					key: 'createTime'
				},
				{
					title: this.$t('transactiondetailsinlegalcurrency.orderquantity'),
					key: 'number'
				},
				{
					title: this.$t('essentialinformation1.orderamount'),
					key: 'money'
				},
				{
					title: this.$t('transactiondetailsinlegalcurrency.handlingcharges'),
					key: 'fee'
				},
				{
					title: this.$t('transactiondetailsinlegalcurrency.paymentmethod'),
					key: 'payMode'
				},
				{
					title: this.$t('essentialinformation1.orderstatus'),
					key: 'status'

				},
			],
			userpage: []
		}
	},
	methods: {
		refreshPage() {
			advDetailOtc({id: this.id})
			.then(res => {
				if(!res.code) {
					console.log(res);
					this.baseInfo = res.data;
					this.filterSearch.memberName = res.data.member.username;
					this.otcOrderTabel(this.filterSearch);
				}
				else this.$Message.error(res.message);
			})
			.catch(err => { console.log(err)})
		},
		otcOrderTabel(obj) {
			this.ifLoading = true;
			queryOtcOrder(obj)
			.then(res => {
				if(!res.code) { 
					console.log(res);
					this.cbData = res.data;
					this.userpage = res.data.content;
					this.ifLoading = false;
				}
				else this.$Message.error(res.message);
			})
		},
		changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
			this.otcOrderTabel(this.filterSearch)
		}
	},
	created () {
		this.id = this.$route.query.advID;
		this.refreshPage();
	}
}
</script>

<style lang="less" scoped>
.btns{
	line-height: 50px;
	text-align: right;
}
	.baseInfo {
		p {
			line-height: 30px;
			font-size: 15px;
			color: #adabab;
			span{
				color: #444;
			}
		}
	}
</style>


