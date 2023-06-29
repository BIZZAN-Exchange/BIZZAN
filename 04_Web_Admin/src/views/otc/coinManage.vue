<template>
	<div>
		<Card>
			<p slot="title">
				{{ $t('app.currencyManagement') }}	<Button type="primary" size="small" @click="refreshPageManual">
					<Icon type="refresh"></Icon> {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
				</Button>
			</p>

			<Row class="functionWrapper">
				<div class="btnsWrapper clearfix">
					<Button type="error" @click="delCoin">{{ $t('secondcontractcompensationsetting.delete') }}</Button>
					<Button type="info" @click="addCoin">{{ $t('announcementmanagement.addto') }}</Button>
				</div>
			</Row>

			<Row>
				<Table :loading="ifloading"
								:columns="columnsList"
								:data="cbData.content"
								@on-selection-change="selectionChange"
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

			<Modal v-model="showEditModal"
						:mask-closable='false'
						:width="450"
						@on-ok="saveEditPass"
						@on-cancel="cancelEdit"
						:title="$t('currencymanagement1.editcurrency')">
				<Form :model="coinInformation"
							:rules="ruleValidate"
							:label-width="100"
							label-position="right">
					<FormItem :label="$t('currencymanagement1.currencynumber')" prop="id" v-show="false">
						<Input v-model="coinInformation.id" ></Input>
					</FormItem>
					<FormItem :label="$t('currencywithdrawalauditmanagement.currencyname')" prop="nameCn">
						<Input v-model="coinInformation.nameCn"></Input>
					</FormItem>
					<FormItem :label="$t('currencymanagement.englishname')" prop="name">
						<Input v-model="coinInformation.name"></Input>
					</FormItem>
					<FormItem :label="$t('currencymanagement1.currencyunit')" prop="unit">
						<Input v-model="coinInformation.unit"></Input>
					</FormItem>
					<FormItem :label="$t('currencymanagement1.note8')" prop="jyRate">
						<Input v-model="coinInformation.jyRate"></Input>
					</FormItem>
					<FormItem :label="$t('currencymanagement1.note7')" prop="sellMinAmount">
						<Input v-model="coinInformation.sellMinAmount"></Input>
					</FormItem>
					<FormItem :label="$t('currencymanagement1.note2') + ':'" prop="buyMinAmount">
						<Input v-model="coinInformation.buyMinAmount"></Input>
					</FormItem>
					<FormItem :label="$t('currencywithdrawalauditmanagement.status')" prop="status">
						<i-switch v-model="switchStatus" size="large">
						  <span slot="open">{{ $t('querymarginstrategy.normal') }}</span>
							<span slot="close">{{ $t('currencymanagement1.invalid') }}</span>
						</i-switch>
					</FormItem>
				</Form>
			</Modal>
		</Card>
	</div>
</template>
<script>

import { queryOtcCoin, addOtcCoin, updateOtcCoin, delOtcCoin } from "@/service/getData";

export default {
  name: "CoinManage",
  data() {
    return {
			selectArr: [],
			createOrUpdate: true,
			cbData: {},
			ifloading: false,
			currentPageIdx: 1,
			showEditModal: false,
			switchStatus: true,
			coinInformation: {
        id: null,
        nameCn: '',
        name: '',
        unit: '',
        jyRate: '',
        sellMinAmount: '',
        buyMinAmount: '',
        status: 0
      },
      ruleValidate: {
        nameCn: [
          { required: true, message: this.$t('currencymanagement1.currencynamecannotbeempty'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('currencymanagement1.englishnamecannotbeempty'),trigger: 'blur' }
        ],
        unit: [
          { required: true, message: this.$t('currencymanagement1.currencyunitcannotbeempty'),trigger: 'blur'}
        ],
        jyRate: [
          { required: true, message: this.$t('currencymanagement1.note6'),trigger: 'blur'}
        ],
        sellMinAmount: [
          { required: true, message: this.$t('currencymanagement1.note10'),trigger: 'blur' }
        ],
        buyMinAmount: [
          { required: true,message: this.$t('currencymanagement1.note5'),trigger: 'blur'}
        ],
        status: [
          { required: true, message: this.$t('currencymanagement1.statuscannotbeempty'), trigger: 'blur' }
        ]
      },
      columnsList: [
        {
          type: "selection",
          width: 60,
        },
        {
          title: this.$t('currencymanagement1.currencynumber1'),
          key: "id"
        },
        {
          title: this.$t('collectionconfigurationmanagement.currencyname1'),
          key: "nameCn"
        },
        {
          title: this.$t('currencymanagement.englishname1'),
          key: "name"
        },
        {
          title: this.$t('currencymanagement1.currencyunit1'),
          key: "unit"
        },
        {
          title: this.$t('currencymanagement1.transactionhandlingrate'),
          key: "jyRate",
          render(h, params) {
            const row = params.row;
            return h("span", {}, row.jyRate + "%");
          }
        },
        {
          title: this.$t('currencymanagement1.note17'),
          width: 180,
          key: "sellMinAmount"
        },
        {
          title: this.$t('currencymanagement1.note2'),
          width: 180,
          key: "buyMinAmount"
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          key: "status",
          render(h, params) {
            const row = params.row;
            const s = !row.status ? this.$t('querymarginstrategy.normal') : this.$t('currencymanagement1.invalid');
            return h("span", {}, s);
          }
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          align: "center",
          key: "handle",
          render: (h, obj) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  on: {
                    click: () => {
											this.showEditModal = true;
											this.createOrUpdate = false;
											for (let val in this.coinInformation) {
												this.coinInformation[val] = obj.row[val];
											}
											!obj.row.status ? this.switchStatus = true : this.switchStatus = false;
                    }
                  }
                },
                this.$t('perpetualcontractcurrencystandardmanagement.modify')
              )
            ]);
          }
        }
      ],
    };
  },
  methods: {
		selectionChange(selection) {
			this.selectArr = selection;
		},
		refreshPageManual() {
			this.refreshPage()
		},
    delCoin() {
			let ids = [];
			this.selectArr.forEach(item => {
				ids.push(item.id);
			})
			if(!ids.length) {
				this.$Message.warning(this.$t('currencymanagement1.note4'))
			}else{
				this.$Modal.confirm({
						title: this.$t('currencymanagement1.deletecurrency'),
						content: this.$t('currencymanagement1.note3', [this.selectArr.length]),
						onOk: () => {
							delOtcCoin({ids: ids})
							.then(res => {
								if(!res.code) {
									this.$Message.success(res.message);
								}else this.$Message.error(res.message);
								this.refreshPage();
							})
							.catch(err => console.log(err))
						},
						onCancel: () => {
							this.$Message.info(this.$t('secondcontractcompensationsetting.cancelled'));
						}
				});
			}
    },
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    addCoin() {
			this.createOrUpdate = true;
      this.showEditModal = true;
    },
    cancelEdit() {
			for(let val in this.coinInformation) {
				this.coinInformation[val] = '';
			}
			this.coinInformation.id = null;
			this.switchStatus = this.coinInformation.status = true;
    },
    saveEditPass() {
			this.switchStatus ? this.coinInformation.status = 0 : this.coinInformation.status = 1;
			let subFn = '';
			this.createOrUpdate ? subFn = addOtcCoin :  subFn = updateOtcCoin;
			subFn(this.coinInformation)
			.then(res => {
				if(!res.code) {
					this.refreshPage();
					this.$Message.success(res.message);
				}else this.$Message.error(res.message);
				this.cancelEdit();
			})
			.catch(err => {console.log(res.message)	})
    },
		refreshPage(obj = {}) {
      this.ifloading = true;
      queryOtcCoin(obj)
			.then(res => {
				if (!res.code) {
					this.cbData = res.data;
				} else this.$Message.error(res.message);
				this.ifloading = false;
			})
			.catch(err => {
				console.log(err);
			});
    }
  },
  created() {
		this.refreshPage();
  },
}
</script>
