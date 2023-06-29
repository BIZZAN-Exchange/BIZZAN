<template>
    <div>
			<Card>
				<p slot="title">
					{{ $t('backstageappeal.backstageappeal') }}	<Button type="primary" size="small" @click="refreshPageManual">
						<Icon type="refresh"></Icon> {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
					</Button>
				</p>

				<Row class="functionWrapper">
					<div class="searchWrapper clearfix">

					<div class="poptip">
						<Poptip trigger="hover" 
									:content="$t('backstageappeal.pleaseentercurrencysearch')" 
									placement="bottom-start">
							<Input :placeholder="$t('backstageappeal.pleaseentercurrencysearch')" 
										v-model="filterSearch.unit"/> 
							</Input>      
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" 
									:content="$t('backstageappeal.pleaseenterthecomplainantsearch')" 
									placement="bottom-start">
						<Input :placeholder="$t('backstageappeal.pleaseenterthecomplainantsearch')" 
										v-model="filterSearch.negotiant"/> 
						</Input>      
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" 
										:content="$t('backstageappeal.pleaseenteradvertisersearch')" 
										placement="bottom-start">
							<Input :placeholder="$t('backstageappeal.pleaseenteradvertisersearch')" 
										v-model="filterSearch.complainant"/> 
							</Input>      
						</Poptip>
					</div>

						<div class="poptip">
							<span>{{ $t('backstageappeal.advertisingtype') }}</span>
							<Select v-model="filterSearch.advertiseType">
								<Option v-for="item in orderTypeArr" 
											:value="item.status" 
											:key="item.status">{{ item.text }}</Option>
							</Select>
					</div>

					<div class="poptip">
						<span>{{ $t('backstageappeal.orderstatus') }}</span>
						<Select v-model="filterSearch.success">
							<Option v-for="item in orderStatusArr" 
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
					<Table :loading="ifLoading" 
									:columns="columnsList" 
									:data="cbData.content"
									@on-sort-change="definedOrder"  
									border></Table>
				</Row>

				<Row class="pageWrapper" >
					<Page :total="cbData.totalElements" 
								:current="currentPageIdx"   
								@on-change="changePage" 
								show-elevator></Page>
				</Row>

				<Modal v-model="showEditModal" 
							 width="400"
							 class="modelInfo">
					<h3 slot="header">{{ $t('backstageappeal.grievanceinformation') }}</h3>
					<ul>
						<li><span>{{ $t('orderdetails.advertiser') }}</span>{{ `${modelInner.advertiseCreaterName}(${modelInner.advertiseCreaterUserName})` }}</li>
						<li><span>{{ $t('backstageappeal.undertaker') }}</span>{{`${modelInner.customerUserName}(${modelInner.customerName})`  }}</li>
						<li><span>{{ $t('backstageappeal.ordernumber') }}</span>{{ modelInner.orderSn }}</li>
						<li><span>{{ $t('backstageappeal.appealtime') }}</span>{{ modelInner.createTime }}</li>
						<li><span>{{ $t('backstageappeal.complaintremarks') }}</span>{{ modelInner.remark }}</li>
						<li><span>{{ $t('backstageappeal.disabletheaccountoftheviolator') }}</span><Checkbox v-model="forbidden">{{ $t('currencyextensionmanagement.disable') }}</Checkbox></li>
					</ul>
					<div slot="footer" align="middle">
						<Button type="primary" @click="coinOperation(true)">{{ $t('backstageappeal.putmoney') }}</Button>
						<Button type="error" @click="coinOperation(false)">{{ $t('backstageappeal.cancellationoforder') }}</Button>
					</div>
				</Modal>

			</Card>
    </div>
</template>
<script>
import { queryAppeal, releaseAppealCoin, cancelAppealOrder } from "@/service/getData";

import util from "@/libs/util";
import axios from "axios";
import qs from "qs";
axios.defaults.withCredentials = true;

export default {
  name: "AppealManage",
  data() {
    return {
			orderTypeArr: [
				{ status: 0, text: this.$t('transactiondetailsinlegalcurrency.buy') },
				{ status: 1, text: this.$t('transactiondetailsinlegalcurrency.sell') },
				{ status: '', text: this.$t('transactiondetailsinlegalcurrency.all') }
			],
			sortSearch: {
				direction: [],
				property: []
			},
      modelInner: {},
      ifLoading: false,
      cbData: {},
      currentPageIdx: 1,
      filterSearch: {
        account: "",
				success: ""
      },
      orderStatusArr: [
        { status: 0, text: this.$t('backstageappeal.lost') },
        { status: 1, text: this.$t('backstageappeal.win') },
        { status: "", text: this.$t('transactiondetailsinlegalcurrency.all') }
      ],
      showEditModal: false,
      forbidden: false,
      columnsList: [
        {
          title: this.$t('transactiondetailsinlegalcurrency.orderno'),
          width: 108,
          key: "orderSn"
        },
        {
          title: this.$t('backstageappeal.adtype'),
          key: "advertiseType",
          render(h, obj) {
            let advertiseType = obj.row.advertiseType;
            return h("span", {}, !advertiseType ? this.$t('transactiondetailsinlegalcurrency.buy') : this.$t('transactiondetailsinlegalcurrency.sell'));
          }
        },
        {
          title: this.$t('backstageappeal.advertiser'),
          key: "advertiseCreaterName",
          render(h, obj) {
            let userName = obj.row.advertiseCreaterUserName;
            let realName = obj.row.advertiseCreaterName;
            return `${userName}(${realName})`;
          }
        },
        {
          title: this.$t('backstageappeal.successor'),
          key: "customerUsername",
          render(h, obj) {
            let userName = obj.row.customerUserName;
            let realName = obj.row.customerName;
            return `${userName}(${realName})`;
          }
        },
        {
          title: this.$t('backstageappeal.complainant'),
          key: "initiatorName"
				},
        {
          title: this.$t('transactiondetailsinlegalcurrency.currency'),
          key: "coinName"
				},
				
        {
          title: this.$t('backstageappeal.appealtime1'),
          key: "createTime",
					width: 108,
					sortable: 'custom'
        },
        {
          title: this.$t('backstageappeal.numberoforders'),
          key: "number",
          render(h, obj) {
            return h(
              'tooltip',
              {
                props: {
                  trigger: "hover",
                  placement: "top",
                  content: this.$t('backstageappeal.note1', [obj.row.fee, obj.row.coinName])
                },
                style: {
                  cursor: "pointer"
                }
              },
              obj.row.number
            );
          }
        },
        {
          title: this.$t('backstageappeal.orderamountyuan'),
          key: "money"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.paymentmethod'),
          key: "payMode"
				},
				{
          title: this.$t('backstageappeal.orderstatus'),
					key: "result",
					render: (h, obj) => {
						let text = this.$t('backstageappeal.notprocessed');
						if(!obj.row.dealWithTime){
							text = this.$t('backstageappeal.notprocessed');
						}else{
							if(!obj.row.isSuccess) text = this.$t('backstageappeal.appealfailed');
							else text = this.$t('backstageappeal.appealsuccessful');
						}
						return h("span", {}, text);
					}	
        },
        // {
        //   title: "订单状态",
        //   key: "orderStatus",
        //   render(h, obj) {
        //     let index = obj.row.orderStatus;
        //     let orderStatus = [
        //       "已取消",
        //       "未付款",
        //       "已付款",
        //       "已完成",
        //       "申诉中"
        //     ];
        //     return h("span", {}, orderStatus[index]);
        //   }
        // },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          key: "isSuccess",
          render: (h, obj) => {
            let ifSuccess = obj.row.isSuccess;
            let orderStatus = obj.row.orderStatus;
            let ifDealTime = obj.row.dealWithTime;
            let inner = "";
            let btnType = "primary";
            if (!!ifDealTime && !ifSuccess) {
              inner = this.$t('transactiondetailsinlegalcurrency.view');
							btnType = "error";
            } else if (!!ifDealTime && ifSuccess === 1) {
              inner = this.$t('transactiondetailsinlegalcurrency.view');
              btnType = "success";
            } else if (!ifDealTime) {
              btnType = "primary";
              inner = this.$t('backstageappeal.processing');
            }
            return h(
              "Button",
              {
                props: {
                  type: btnType,
                  size: "small"
                },
                on: {
                  click: () => {
                    if (ifDealTime === null) {
                      this.modelInnerFn(obj);
                    }else if (inner === this.$t('transactiondetailsinlegalcurrency.view')) {
											this.$router.push({path: '/otc/appealmanage/appealdetail', query: { id: obj.row.appealId }})
										}
                  }
                }
              },
              inner
            );
          }
        }
      ]
    };
  },
  methods: {
		definedOrder(obj) {
			let direction = obj.order==='desc' ? 1 : 0;
			let propertyIndex = this.sortSearch.property.indexOf(obj.key);

			if(propertyIndex!==-1){
				this.sortSearch.direction[propertyIndex] = direction;
			}else{
				this.sortSearch.property.push(obj.key);
				this.sortSearch.direction.push(direction);
			}

			let subObj = { pageNo: 1, pageSize: 10};
      Object.assign(subObj, this.filterSearch, this.sortSearch);
			
			this.refreshPage(subObj)
		},
		coinOperation(bol) {
			let subFn = null;
			let title = '';
			let content = '';
			if(bol){
				title = this.$t('backstageappeal.confirmcashrelease');
				content = this.$t('backstageappeal.cashin')
				subFn = releaseAppealCoin;
			} else {
				title = this.$t('backstageappeal.cancellationoforder');
				content = this.$t('backstageappeal.cancelorder')
				subFn = cancelAppealOrder;
			}
			
			this.showEditModal = false;
			let subObj = {
				orderSn: this.modelInner.orderSn,
				appealId: this.modelInner.appealId,
				banned: this.forbidden
			}
			
			this.$Modal.confirm({
				title: title,
				content: content,
				onOk: () => {
					subFn(subObj)
					.then(res => {
						if(!res.code) this.$Message.success(res.message);
						else this.$Message.error(res.message);
						this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 })
					})
					.catch( err => console.log(err))
				},
				onCancel: () => {
					this.$Message.info(this.$t('backstageappeal.youcanceledtheoperation'));
				}
			});
		},
    modelInnerFn(obj) {
			this.modelInner = obj.row;
			this.showEditModal = true;
    },
    searchByFilter() {
			this.currentPageIdx = 1;
			let subObj = { pageNo: 1, pageSize: 10};
      Object.assign(subObj, this.filterSearch);
			this.refreshPage(subObj);
		},
    refreshPageManual() {
			this.currentPageIdx = 1;
			for(let val in this.filterSearch)  {
				this.filterSearch[val] = '';
			}
			for(let val in this.sortSearch)  {
				this.filterSearch[val] = [];
			}

      this.refreshPage();
    },
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			let obj = Object.assign({pageNo: pageIndex, pageSize: 10}, this.filterSearch, this.sortSearch);
      this.refreshPage(obj);
    },
    refreshPage(obj = {}) {
      this.ifLoading = true;
      queryAppeal(obj)
        .then(res => {
          if (!res.code) {
            this.cbData = res.data;
          } else this.$Message.error(res.message);

          this.ifLoading = false;
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

<style lang="less">
	.modelInfo{
		h3{
			color: #2D8CF0;
		}
		ul{
			li {
				margin: 5px 0;
				font-size: 14px;
				&>span{
					margin: 0 25px;
					font-weight: 700;
				}
			}
		}
	}
</style>