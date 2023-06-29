<template>
	<div>
		<Card>
			<p slot="title">
				{{ $t('transactiondetailsinlegalcurrency.transactiondetailsinlegalcurrency') }}	<Button type="primary" size="small" @click="refreshPageManual">
					<Icon type="refresh"></Icon> {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
				</Button>
			</p>
			<Row class="functionWrapper">
				<!-- <div class="btnsWrapper clearfix">
					<a :href="aLink" target="_self">
						<Button type="success" @click="outExcel">
								导出
						</Button>
					</a>
				</div> -->

				<div class="searchWrapper clearfix">

					<div class="poptip">
						<Poptip trigger="hover" 
										:content="$t('transactiondetailsinlegalcurrency.pleaseentertradersearch')" 
										placement="bottom-start">
							<Input :placeholder="$t('transactiondetailsinlegalcurrency.pleaseentertradersearch')" 
										v-model="filterSearch.customerName"/> 
							</Input>      
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" 
										:content="$t('transactiondetailsinlegalcurrency.pleaseentercreatorsearch')" 
										placement="bottom-start">
							<Input :placeholder="$t('transactiondetailsinlegalcurrency.pleaseentercreatorsearch')" 
										v-model="filterSearch.memberName"/> 
							</Input>      
						</Poptip>
					</div>

					<div class="poptip">
						<span>{{ $t('perpetualcontractcurrencystandardmanagement.currency') }}</span>
						<Select v-model="filterSearch.unit">
							<Option v-for="(item, index) in coinSearchArr" 
										:value="item.unit==$t('chargingdetails1.all') ?'':item.unit" 
										:key="item.unit">{{ item.unit }}</Option>
						</Select>
					</div>
					<div class="poptip">
						<span>{{ $t('transactiondetailsinlegalcurrency.type') }}</span>
						<Select v-model="filterSearch.advertiseType">
							<Option v-for="item in advertiseArr" 
										:value="item.status" 
										:key="item.status">{{ item.text }}</Option>
						</Select>
					</div>
					

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
            <Button type="success" @click="exportExcel">{{ $t('positionmanagementcontractassetmanagement.export') }}</Button>
					</div>

				</div>
			</Row>

			<Row>
				<Table :loading="ifloading" 
								:columns="columnsList" 
								:data="cbData.content"
								@on-selection-change="selectionChange"
								@on-sort-change="definedOrder" 
								ref="tablel"
								border></Table>
			</Row>

			<Row class="pageWrapper" >
				<Page :total="cbData.totalElements" 
							:current="currentPageIdx"   
							@on-change="changePage" 
							show-elevator></Page>
			</Row>
				
			<Modal v-model="showModal" :width="500" class="modelInfo">
					<h3 slot="header">{{ $t('transactiondetailsinlegalcurrency.orderinformation') }}</h3>
						<ul>
							<li><span>{{ $t('ordermanagement.orderno') }}</span>{{ modelInner.orderSn }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.tradingtime') }}</span>{{ modelInner.createTime }}</li>
							<li><span>{{ $t('ordermanagement.trader') }}</span>{{ modelInner.customerName }}</li>
							<li><span>{{ $t('ordermanagement.createdby') }}</span>{{ modelInner.memberName }}</li>
							<li><span>{{ $t('perpetualcontractcurrencystandardmanagement.currency') }}</span>{{ modelInner.unit }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.type') }}</span>{{ !modelInner.advertiseType ? $t('transactiondetailsinlegalcurrency.buy1'):$t('transactiondetailsinlegalcurrency.sell1') }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.orderquantity1') }}</span>{{ modelInner.number }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.orderamount') }}</span>{{ modelInner.money }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.servicecharge') }}</span>{{ ''+modelInner.commission }}</li>
							<li><span>{{ $t('orderdetails.paymentmethod') }}</span>{{ modelInner.payMode }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.orderstatus') }}</span>{{ modelInner.status | filterOrderStatus(detailArrS) }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.paymenttime') }}</span>{{ modelInner.payTime }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.ordercancellationtime') }}</span>{{ !modelInner.cancelTime ? '--' : modelInner.cancelTime }}</li>
							<li><span>{{ $t('transactiondetailsinlegalcurrency.releasetime') }}</span>{{ !modelInner.releaseTime ? '--' : modelInner.releaseTime }}</li>
						</ul>
					<div slot="footer">
					</div>
			</Modal>
		</Card>
	</div>
</template>
<script>
import qs from 'qs';
import { setStore, getStore, removeStore } from "@/config/storage.js";
import { queryOtcOrder, queryOtcOrderOut, coinOutExcel, BASICURL, getCoinName } from "@/service/getData";

export default {
  name: "OrderManage",
  data() {
    return {
			aLink: `${BASICURL}admin/otc/order/out-excel`,
			coinSearchArr: [],
			sortSearch: {
				direction: [],
				property: []
			},
      detailArrS: [this.$t('transactiondetailsinlegalcurrency.cancelled'), this.$t('transactiondetailsinlegalcurrency.unpaid'), this.$t('transactiondetailsinlegalcurrency.paid'), this.$t('transactiondetailsinlegalcurrency.completed'), this.$t('transactiondetailsinlegalcurrency.appealing')],
			advertiseArr: [
				{ status: 0, text: this.$t('transactiondetailsinlegalcurrency.buy') },
				{ status: 1, text: this.$t('transactiondetailsinlegalcurrency.sell') },
				{ status: '', text: this.$t('transactiondetailsinlegalcurrency.all') }
			],
			filterSearch: {
				status: 3,
				unit: '',
				memberName: '',
				customerName: '',
				orderSn: '',
				minMoney: '',
				maxMoney: '',
				minNumber: '',
				maxNumber: '',
				advertiseType: '',
        isOut: 0,
			},
			modelInner: {},
			cbData: {},
			ifloading: false,
			currentPageIdx: 1,
      orderStatus: null,
      showModal: false,
      saveLoading: false,
      columnsList: [
        {
          type: "selection",
          width: 60,
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.orderno'),
          width: 90,
          key: "orderSn"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.transactiontime'),
          width: 105,
					key: "createTime",
					sortable: 'custom'
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.trader'),
          key: "customerName"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.createdby'),
          key: "memberName"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.currency'),
          key: "unit"
        },
        {
          title: this.$t('entrustedmanagement.type'),
          key: "advertiseType",
          render(h, obj) {
            return h("span", {}, obj.row.advertiseType === 0 ? this.$t('transactiondetailsinlegalcurrency.buy') : this.$t('transactiondetailsinlegalcurrency.sell'));
          }
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.orderquantity'),
          key: "number"
        },
        {
          title: this.$t('essentialinformation1.orderamount'),
          key: "money"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.handlingcharges'),
          key: "fee"
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.paymentmethod'),
          key: "payMode"
        },
        {
          title: this.$t('essentialinformation1.orderstatus'),
          key: "status",
          render(h, params) {
            let status = params.row.status;
						let arr = [this.$t('transactiondetailsinlegalcurrency.cancelled'), this.$t('transactiondetailsinlegalcurrency.unpaid'), this.$t('transactiondetailsinlegalcurrency.paid'), this.$t('transactiondetailsinlegalcurrency.completed'), this.$t('transactiondetailsinlegalcurrency.appealing')];
            return h("span", {}, arr[status]);
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
                  style: {
                    marginRight: "10px"
                  },
                  on: {
                    click: () => {
                      this.showInfoModal(obj);
                    }
                  }
                },
                this.$t('transactiondetailsinlegalcurrency.view')
              )
            ]);
          }
        }
      ]
    };
  },
  methods: {
		// outExcel() {
		// 	console.log(2222)
		// 	this.$refs.tabel.exportCsv({
		// 		file:"text"
		// 	})
		// },
    exportExcel() {
      this.filterSearch.isOut = 1
      queryOtcOrderOut(this.filterSearch)
          .then(res => {
            // 文件下载
            let blob = new Blob([res], {type: 'application/vnd.ms-excel'})
            let objectUrl = URL.createObjectURL(blob)
            // window.location.href = objectUrl
            const fileName = this.$t('transactiondetailsinlegalcurrency.transactiondetailsinlegalcurrency')// 导出文件名
            const elink = document.createElement('a') // 创建a标签
            elink.download = fileName // a标签添加属性
            elink.style.display = 'none'
            elink.href = objectUrl
            document.body.appendChild(elink)
            elink.click() // 执行下载
            URL.revokeObjectURL(elink.href) // 释放URL 对象
            document.body.removeChild(elink) // 释放标签
            this.$Message.success(this.$t('positionmanagementcontractassetmanagement.exportsuccessful'))
          })
    },
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
      this.filterSearch.isOut = 0
      Object.assign(subObj, this.filterSearch, this.sortSearch);
			
			this.refreshPage(subObj)
		},
		dateRange(val) {
			this.filterSearch.startTime = val[0];
			this.filterSearch.endTime = val[1];
		},
		searchByFilter() {
			console.log(this.filterSearch)
			this.filterSearch.isOut = 0
			let obj = Object.assign({ pageNo: 1, pageSize: 10}, this.filterSearch);
			this.aLink = `${BASICURL}admin/otc/order/out-excel?${qs.stringify(obj)}`;
			this.refreshPage(obj)
		},
		selectionChange(selection) {

		},
		refreshPageManual() {
			this.currentPageIdx = 1;
			for(let val in this.filterSearch)  {
				this.filterSearch[val] = '';
			}
			this.filterSearch.isOut = 0
			this.refreshPage();
		},
    changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.filterSearch.isOut = 0
			let obj = Object.assign({pageNo: pageIndex, pageSize: 10}, this.filterSearch, this.sortSearch);
     	this.refreshPage(obj);
    },
    showInfoModal(obj) {
			this.showModal = true;
			this.modelInner = obj.row;
			console.log(obj);
			
		},
    queryStatus(obj) {
      if (obj.query.status === undefined) {
        this.orderStatus = null;
      } else {
        this.orderStatus = obj.query.status;
      }
      this.getData();
		},
		refreshPage(obj = {}) {
      this.ifloading = true;
      queryOtcOrder(obj)
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
		getCoinName()
		.then(res => {
			if (!res.code) {
				this.coinSearchArr = res.data;
				this.coinSearchArr.push({ name: this.$t('transactiondetailsinlegalcurrency.all'), unit: this.$t('transactiondetailsinlegalcurrency.all') })
			} else this.$Message.error(res.message);
		})
		.catch(err => {
			console.log(err);
		});
    this.refreshPage({status: 3});
	},
	filters: {
		filterOrderStatus (val,arr) {
			// let arr = [this.$t('transactiondetailsinlegalcurrency.cancelled'), this.$t('transactiondetailsinlegalcurrency.unpaid'), this.$t('transactiondetailsinlegalcurrency.paid'), this.$t('transactiondetailsinlegalcurrency.completed'), this.$t('transactiondetailsinlegalcurrency.appealing')];
			return arr[val];
		},
	},
  watch: {
    $route(to, from) {
      this.queryStatus(to);
    }
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
					display: inline-block;
					margin-left: 25px;
					width: 100px;
					font-weight: 700;
				}
			}
		}
	}
</style>