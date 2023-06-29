<template>
	<Card class="businessDetail">
		<p slot="title">
			{{ $t('businessinformation.businessinformation') }}	<Button type="primary" size="small" @click="refreshPageManual">
				<Icon type="refresh"></Icon>
				{{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}	</Button>
		</p>
		<Spin class="loading" v-if="ifLoading" size="large"></Spin>
		<Row>
			<Col span="6">
				{{ $t('realnamemanagement.auditstatus') }}<span class="status">{{ userInfo.status | filterStatus(statusArrS) }}</span>
			</Col>
			<Col span="6">
				{{ $t('auditdetails.realname') }}<span>{{ userInfo.realName }}</span>
			</Col>
			<Col span="6">
				{{ $t('common.margin') }}<span>{{ userInfo.amount + userInfo.info.coinSymbol }}</span>
			</Col>
		</Row>
		<Row>
			<Col span="6">
				{{ $t('currencywithdrawalauditmanagement.cellphonenumber') }}<span>{{ userInfo.info.areaCode }}{{ userInfo.info.telno }}</span>
			</Col>
			<Col span="6">
				{{ $t('common.wechat') }}<span>{{ userInfo.info.wechat }}</span>
			</Col>
			<Col span="6">
				{{ $t('common.qq') }}<span>{{ userInfo.info.qq }}</span>
			</Col>
		</Row>
		<Row>
			{{ $t('businessinformation.reasonforfailure') }}<span>{{ !userInfo.detail ? '无' : userInfo.detail }}</span>
		</Row>

		<Row class="imgs" type="flex" justify="space-around">
			<Col span="11">
				<img :src="userInfo.info.assetData" :alt="$t('businessinformation.personaldigitalassetcertificate')"><br>
				<p style="color:#333">{{ $t('businessinformation.personaldigitalassetcertificate') }}</p>
			</Col>
			<Col span="11">
				<img :src="userInfo.info.tradeData" :alt="$t('businessinformation.digitalassettransactioncertificate')"><br>
				<p style="color:#333">{{ $t('businessinformation.digitalassettransactioncertificate') }}</p>
			</Col>

		</Row>
	</Card>
</template>

<script>
import { authBusinessDetail } from '@/service/getData'
export default {
	name: 'BusinessDetail',
	data() {
		return {
			ifLoading: false,
			userInfo: {
				info: {}
			},
      statusArrS:[this.$t('businessinformation.notcertified'), this.$t('businessinformation.certificationpendingapproval'), this.$t('businessinformation.authenticationauditsuccessful'), this.$t('businessinformation.authenticationauditfailed'), this.$t('businessinformation.insufficientmargin')]
		}
	},
	methods: {
		refreshPageManual() {
			this.refreshPage();
		},
		refreshPage() {
			this.ifLoading = true;
			authBusinessDetail({id: this.$route.query.id})
			.then(res => {
				if(!res.code){
					this.userInfo = res.data;
					console.log(this.userInfo);
				}else this.$Message.error(res.message)
				this.ifLoading = false;
			})
			.catch(err => console.log(err))
		}
	},
	created() {
		this.refreshPage();
	},
	filters: {
		filterStatus(val,arr) {
			return arr[val];
		}
	}
}
</script>

<style lang="less" scoped>
	.businessDetail{
		font-size: 16px;
		color: #adadad;
		.ivu-row {
			.status{
				color: #ec0909;
			}
			line-height: 60px;
		}
		.ivu-row-flex.imgs{
			margin-top: 70px;
			img{
				width: 100%;
				height: 300px;
				background-color: red;
			}
			p{
				text-align: center;
			}
		}
		.loading{
			position: absolute;
			z-index: 100;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background: rgba(255, 255, 255, .8);
		}
	}
</style>
