<template>
	<div>
		<Card>
				<p slot="title">
					{{ $t('backstageadvertising.backstageadvertising') }}	<Button type="primary" size="small" @click="refreshPageManual">
						<Icon type="refresh"></Icon> {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
					</Button>
				</p>
			
				<Row class="functionWrapper">
					<div class="btnsWrapper">
						<Button type="error" @click="batchDel">{{ $t('backstageadvertising.batchdelete') }}</Button>
					</div>

					<div class="searchWrapper clearfix">
						<div class="poptip">
							<Poptip trigger="hover" 
											:content="$t('backstageadvertising.note1')" 
											placement="bottom-start">
								<Input :placeholder="$t('backstageadvertising.note1')" 
											v-model="filterSearch.account"/> 
								</Input>      
							</Poptip>
						</div>

						<div class="poptip">
								<Poptip trigger="hover" 
											:content="$t('backstageadvertising.pleaseenterthepaymentmethodtosearch')" 
											placement="bottom-start">
								<Input :placeholder="$t('backstageadvertising.pleaseenterthepaymentmethodtosearch')" 
											v-model="filterSearch.payModel"/> 
								</Input>      
							</Poptip>
						</div>

						<div class="poptip">
							<span>{{ $t('transactiondetailsinlegalcurrency.type') }}</span>
							<Select v-model="filterSearch.advertiseType">
								<Option v-for="item in advertiseTypeArr" 
											:value="item.status" 
											:key="item.status">{{ item.text }}</Option>
							</Select>
						</div>

						<div class="poptip">
							<span>{{ $t('currencywithdrawalauditmanagement.status') }}</span>
							<Select v-model="filterSearch.status">
								<Option v-for="item in advertiseStatusArr" 
											:value="item.status" 
											:key="item.status">{{ item.text }}</Option>
							</Select>
						</div>
					
						<div class="btns">
							<Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
						</div>
					</div>

				</Row>

				<Row>
					<Table :loading="ifloading" 
							 	 :columns="columnsList" 
							 	 :data="cbData.content"
								 @on-selection-change="selectionChange" 
								 border></Table>
				</Row>

				<Row class="pageWrapper" >
					<Page :total="cbData.totalElements" 
								:current="currentPageIdx"   
								@on-change="changePage" 
								show-elevator></Page>
				</Row>
		</Card>
	</div>
</template>
<script>
import { queryOtcAdv, upDownAdv } from "@/service/getData";

export default {
  data() {
    return {
			selection: [],
      cbData: {},
      currentPageIdx: 1,
      filterSearch: {
        account: "",
				advertiseType: "",
				status: "",
        payModel: ""
      },
      advertiseTypeArr: [
        { status: 0, text: this.$t('transactiondetailsinlegalcurrency.buy') },
        { status: 1, text: this.$t('transactiondetailsinlegalcurrency.sell') },
        { status: "", text: this.$t('transactiondetailsinlegalcurrency.all') }
			],
			advertiseStatusArr: [
        { status: 0, text: this.$t('backstageadvertising.ontheshelf') },
        { status: 1, text: this.$t('backstageadvertising.offtheshelf') },
        { status: "", text: this.$t('transactiondetailsinlegalcurrency.all') }
      ],
      ifloading: false,
      columnsList: [
        {
          type: "selection",
          width: 60
        },
        {
          title: this.$t('backstageadvertising.adno'),
          width: 108,
          key: "id"
        },
        {
          title: this.$t('secondcontractcompensationsetting.creationtime'),
          key: "createTime"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.createdby'),
          render: (h, obj) => {
            let member = obj.row.member;
            return `${member.username}(${member.realName})`;
          }
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.currency'),
          render: (h, obj) => {
            return obj.row.coin.unit;
          }
        },
        {
          title: this.$t('backstageadvertising.numberofadvertisements'),
          key: "number"
        },
        {
          title: this.$t('backstageadvertising.advertisingamount'),
          key: "price"
        },
        {
          title: this.$t('backstageadvertising.advertisingquota'),
          align: "center",
          render: (h, obj) => {
            return `${obj.row.minLimit}-${obj.row.maxLimit}`;
          }
        },
        {
          title: this.$t('entrustedmanagement.type'),
          key: "advertiseType",
          render(h, params) {
            const row = params.row;
            return h("span", {}, row.advertiseType === 0 ? this.$t('transactiondetailsinlegalcurrency.buy') : this.$t('transactiondetailsinlegalcurrency.sell'));
          }
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.paymentmethod'),
          key: "payMode"
        },
        {
          title: this.$t('backstageadvertising.advertisingstatus'),
          key: "status",
          render(h, params) {
            const row = params.row;
            return h("span", {}, !row.status ? this.$t('backstageadvertising.ontheshelf') : this.$t('backstageadvertising.offtheshelf'));
          }
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          align: "center",
          width: 150,
          key: "handle",
          render: (h, obj) => {
            let advStatus = obj.row.status;
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    size: "small",
                    type: "primary",
                    disabled: !!advStatus ? true : false
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.operation({ ids: [obj.row.id], status: 1 });
                    }
                  }
                },
                this.$t('backstageadvertising.offtheshelf')
							),
							h('Button', {
								props: {
									type: 'info',
									size: 'small'
								},
								on: {
									click: () => {
										this.$router.push({name: 'otc:adManage:advertisedetail', query: {advID : obj.row.id}})
									}
								}
							}, this.$t('backstageadvertising.details'))
              // h(
              //   "Button",
              //   {
              //     props: {
              //       type: "error",
              //       size: "small",
              //       disabled: !(advStatus === 1 || !advStatus) ? true : false
              //     }
              //   },
              //   [
              //     h(
								
              //       "Poptip",
              //       {
              //         props: {
              //           confirm: true,
              //           transfer: true,
              //           title: "您确定要删除这条数据吗"
              //         },
              //         on: {
              //           "on-ok": () => {
              //             this.operation({ ids: [obj.row.id], status: 2 });
              //           },
              //           "on-cancel": () => {
              //             this.$Message.info("已取消！");
              //           }
              //         }
              //       },
              //       advStatus === 1 || advStatus === 0 ? "删除" : "已删除"
              //     )
              //   ]
              // )
            ]);
          }
        }
      ]
    };
  },
  methods: {
		selectionChange(selection) {
			this.selection = selection;
		},
		batchDel() {
			let ids = [];
      console.log(this.selection)
			this.selection.forEach(item => {
				ids.push(item.id);
			})
			this.operation({ ids: ids, status: 2 });
		},
    operation(obj) {
      upDownAdv(obj).then(res => {
          if (!res.code) {
            this.$Message.success(res.message);
            this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
          } else this.$Message.error(res.message);
        }).catch(err => console.log(err));
    },
    searchByFilter() {
			this.currentPageIdx = 1;
      let obj = Object.assign(this.filterSearch, { pageNo: 1, pageSize: 10 });
      this.refreshPage(obj);
    },
    refreshPageManual() {
      for (let key in this.filterSearch) {
        this.filterSearch[key] = "";
      }
      this.refreshPage();
    },
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      let obj = Object.assign(this.filterSearch, {
        pageNo: pageIndex,
        pageSize: 10
      });
      this.refreshPage(obj);
    },

    refreshPage(obj = {}) {
      this.ifloading = true;
      queryOtcAdv(obj).then(res => {
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
  }
};
</script>

<style lang="less" scoped>

</style>