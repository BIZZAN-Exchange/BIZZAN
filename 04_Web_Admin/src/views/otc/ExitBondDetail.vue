<template>
	<div class="detail">
		<Row>
			<Card class="card">
				<p slot="title">
					<Button type="primary" size="small" @click="refreshPageManual">
						<Icon type="refresh" @click="refreshPageManual"></Icon>
						{{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
					</Button>
				</p>
				<Row>
					<h3>{{ $t('refresh.surrenderapplication') }}</h3>
					<Row>
						<Col span="6">{{ $t('refresh.note1') }}{{ !exitBondDetail.depositRecord ? '' : exitBondDetail.depositRecord.amount+exitBondDetail.depositRecord.coin.unit }}</Col>
						<Col span="6">{{ $t('refresh.note6') }}{{ (!exitBondDetail.businessCancelApply ? '' :exitBondDetail.businessCancelApply.status) | filterStatus }}</Col>
						<Col span="6">{{ $t('refresh.note14') }}{{ !exitBondDetail.businessCancelApply ? '' :exitBondDetail.businessCancelApply.cancelApplyTime }}</Col>
						<Col span="6">{{ $t('refresh.note11') }}{{ !exitBondDetail.businessCancelApply ? '' :exitBondDetail.businessCancelApply.handleTime }}</Col>
					</Row>
					<Row>
						<Col span="6">{{ $t('refresh.note4') }}{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.reason | reasonFilter}}</Col>
					</Row>
				</Row>
				<p class="line"></p>
				<Row>
					<h3>{{ $t('refresh.userinformation') }}</h3>
					<Row>
						<Col span="6">{{ $t('refresh.note9') }}{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.realName}}</Col>
						<Col span="6">{{ $t('refresh.note5') }}{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.username }}</Col>
						<Col span="6">{{ $t('refresh.note2') }}{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.mobilePhone }}</Col>
						<Col span="6">{{ $t('refresh.note13') }}{{ !exitBondDetail.businessCancelApply ? '' : exitBondDetail.businessCancelApply.member.email }}</Col>
					</Row>
				</Row> 
				<p class="line"></p>
				<Row>
					<h3>{{ $t('refresh.transactioninformation') }}</h3>
					<Row>
						<Col span="6">{{ $t('refresh.note3') }}{{ exitBondDetail.complainantNum }}</Col>
						<Col span="6">{{ $t('refresh.note7') }}{{ exitBondDetail.defendantNum }}</Col>
						<Col span="6">{{ $t('refresh.note8') }}{{ !exitBondDetail.money ? 0 : exitBondDetail.money }}</Col>
						<Col span="6">{{ $t('refresh.surrenderapplication') }}{{ !exitBondDetail.fee ? 0 : exitBondDetail.fee }}</Col>
					</Row>
					<Row>
						<Col span="6">{{ $t('refresh.note10') }}{{ exitBondDetail.advertiseNum }}</Col>
					</Row>
				</Row>


			</Card>
		</Row>
	</div>
</template>
<script>
import { cancelApplyDetail } from '@/service/getData'
export default {
	data() {
		return {
			exitBondDetail: {}
		}
	},
	methods: {
		refreshPageManual() {
			cancelApplyDetail({id: this.$route.query.id})
			.then(res => {
				if(!res.code) {
					this.exitBondDetail = res.data;
				}else this.$Message.error(res.message);
			})
			.catch(err => console.log(err))
		}
	},
	created() {
		this.refreshPageManual();
	},
	filters: {
		filterStatus(val) {
			if(val==='') return val;
			else{
				if(val === 6) return this.$t('currencywithdrawalauditmanagement.failed');
				else if(val === 5) return this.$t('currencywithdrawalapproval.pendingreview');
				else if(val === 7) return this.$t('currencywithdrawalapproval.success');
			}
		},
		reasonFilter(val) {
			if(!val) return this.$t('surrendermanagement.none');
			else return val;
		}
	}
}
</script>

<style lang="less" scoped>
.line{
	border-top: 1px solid #dfdfdf;
}
	.detail{
		.card{
			padding: 20px;
			.ivu-card-body{
				&>.ivu-row{
					margin: 20px 0;
					.ivu-row{
						font-size: 16px;
						line-height: 50px;
					}
				}
			}
		}
	}

</style>

