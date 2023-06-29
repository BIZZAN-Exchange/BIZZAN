<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('orderdetails.orderdetails') }} <span class="orderDetail">{{ cbData.orderSn }}</span>
      </p>
			<div class="baseInfo">
				<Row>
					<Col span="6">
						<p>{{ $t('currencywithdrawalauditmanagement.status') }}<span class="status">{{ !cbData.isSuccess ?  $t('backstageappeal.lost') : $t('backstageappeal.win') }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('essentialinformation1.transactioncurrency') }}<span>{{ cbData.coinName }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('servicechargewithdrawaldetails.transactiontype') }}<span>{{ !cbData.advertiseType ? $t('backstageadvertising.buy') : $t('backstageadvertising.sell') }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('orderdetails.paymentmethod') }}<span>{{ cbData.payMode }}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>{{ $t('orderdetails.complainant') }}<span>{{ cbData.initiatorName }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('orderdetails.advertiser') }}<span>{{ `(${cbData.advertiseCreaterName})${cbData.advertiseCreaterUserName}` }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('backstageappeal.undertaker') }}<span>{{ `(${cbData.customerUserName})${cbData.customerName}`}}</span></p>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<p>{{ $t('transactiondetailsinlegalcurrency.orderquantity1') }}<span>{{ cbData.number }}</span></p>
					</Col>
					<Col span="6">
						<p>{{ $t('transactiondetailsinlegalcurrency.orderamount') }}<span>{{ cbData.money }}</span></p>
					</Col>
				</Row>
				<Row>
						<p>{{ $t('orderdetails.remarks') }}<span>{{ cbData.remark }}</span></p>
				</Row>
			</div>
    </Card>
    </div>
</template>

<script>

import { appealManageDetail } from '@/service/getData';

export default {
	name: 'AppealDetail',
  data() {
   return {
		 cbData: {}
	 }
	},
	methods: {
		refreshPage(obj) {
			appealManageDetail(obj)
			.then(res => {
				if(!res.code) {
					this.cbData = res.data;
				}else this.$Message.error(res.message);
			})
			.catch(err => console.log(err))
		}
	},
  created() {
		this.refreshPage({ id: this.$route.query.id })
  }
}
</script>

<style scoped lang='less'>
@import '../../styles/common.less';
	.orderDetail {
		color: @primary;
	}
	.status{
		color: @light-primary !important;
	}
</style>