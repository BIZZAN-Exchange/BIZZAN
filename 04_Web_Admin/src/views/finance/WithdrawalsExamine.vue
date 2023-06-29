<template>
	<div>
		<Card>
			<p slot="title">
				{{ $t('currencywithdrawalapproval.currencywithdrawalapproval') }}	<Button type="primary" size="small" @click="refreshPageManual">
					<Icon type="refresh"></Icon>
					{{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}	</Button>
			</p>
			<Row class="functionWrapper">
				<div class="btnsWrapper clearfix ">
					<Button type="error" @click="ifPassAll(true)">{{ $t('currencywithdrawalapproval.oneclickreviewfailed') }}</Button>
					<Button type="success" @click="ifPassCoin = true">{{ $t('currencywithdrawalapproval.oneclickcoinrelease') }}</Button>
				</div>

				<div class="searchWrapper clearfix">
					<div class="poptip">
						<Poptip trigger="hover" :content="$t('currencywithdrawalapproval.pleaseenteryournicknamerealnametosearch')" placement="bottom-start">
							<Input :placeholder="$t('currencywithdrawalapproval.pleaseenteryournicknamerealnametosearch')" v-model="filterSearch.account" />
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" :content="$t('currencywithdrawalapproval.pleaseenterthememberidtosearch')" placement="bottom-start">
							<Input :placeholder="$t('currencywithdrawalapproval.pleaseenterthememberidtosearch')" v-model="filterSearch.memberId" />
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" :content="$t('currencywithdrawalapproval.pleaseenterthewithdrawaladdresstosearch')" placement="bottom-start">
							<Input :placeholder="$t('currencywithdrawalapproval.pleaseenterthewithdrawaladdresstosearch')" v-model="filterSearch.address" />
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" :content="$t('currencywithdrawalapproval.pleaseentercurrencyunitsearch')" placement="bottom-start">
							<Input :placeholder="$t('currencywithdrawalapproval.pleaseentercurrencyunitsearch')" v-model="filterSearch.unit" />
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<Poptip trigger="hover" :content="$t('currencywithdrawalapproval.pleaseentertheordernumbertosearch')" placement="bottom-start">
							<Input :placeholder="$t('currencywithdrawalapproval.pleaseentertheordernumbertosearch')" v-model="filterSearch.orderSn" />
							</Input>
						</Poptip>
					</div>
					<br>
					<div class="poptip">
						<span>{{ $t('currencywithdrawalapproval.withdrawalmethod') }}</span>
						<Select v-model="filterSearch.isAuto">
							<Option v-for="item in orderAutoArr" :value="item.status" :key="item.status">{{ item.text }}</Option>
						</Select>
					</div>

					<div class="poptip">
						<span>{{ $t('currencywithdrawalauditmanagement.status') }}</span>
						<Select v-model="filterSearch.status">
							<Option v-for="item in orderStatusArr" :value="item.status" :key="item.status">{{ item.text }}</Option>
						</Select>
					</div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
					</div>

				</div>
			</Row>

			<Row class="tableWrapper">
				<Table :columns="columns_first" :data="userpageCopy" border :loading="ifLoading" @on-selection-change="selectionChange">
				</Table>
			</Row>
			<Row class="pageWrapper">
				<Page :total="pageNum" :current="currentPageIndex" @on-change="changePage" show-elevator>
				</Page>
			</Row>

			<Modal width="400" :title="$t('currencywithdrawalapproval.oneclickcoinrelease')" v-model="ifPassCoin" @on-ok="confrimPass">
				<Row>
					<p>{{ $t('currencywithdrawalapproval.note1') }}</p>
				</Row>
				<br>
				<Form v-model="passAllCoin">
					<FormItem>
						<Input type="password" :placeholder="$t('currencywithdrawalapproval.pleaseenteryourloginpassword')" v-model="passAllCoin.password"></Input>
					</FormItem>
					<FormItem>
						<Input :placeholder="$t('currencywithdrawalapproval.pleaseenterflow')" type="textarea" v-model="passAllCoin.transactionNum"></Input>
					</FormItem>
				</Form>
			</Modal>

		</Card>
	</div>
</template>

<script>
import {withdrawManage,auditNoPass,passCoin} from "@/service/getData";
import { setStore, getStore, removeStore } from "@/config/storage";
import Cookies from "js-cookie";

export default {
  data() {
    return {
      ifPassCoin: false,
      passAllCoin: {
        transactionNumber: "",
        password: ""
      },
      filterSearch: {
        pageNo: 1,
        pageSize: 10,
        account: "",
        memberId: "",
        address: "",
        unit: "",
        isAuto: "",
        status: "",
        orderSn: ""
        // direction: '1',
        // property: 'status'
      },
      currentPageIndex: 1,

      ifLoading: true,
      pageNum: null,
      orderAutoArr: [
        { status: 0, text: this.$t('currencywithdrawalapproval.manualreviewwithdrawal') },
        { status: 1, text: this.$t('currencywithdrawalapproval.automaticwithdrawal') },
        { status: "", text: this.$t('transactiondetailsinlegalcurrency.all') }
      ],
      orderStatusArr: [
        { status: 0, text: this.$t('currencywithdrawalapproval.underreview') },
        { status: 1, text: this.$t('currencywithdrawalapproval.waitingforcashrelease') },
        { status: 2, text: this.$t('currencywithdrawalauditmanagement.fail') },
        { status: 3, text: this.$t('currencywithdrawalapproval.success') },
        { status: "", text: this.$t('transactiondetailsinlegalcurrency.all') }
      ],
      userpage: [],
      userpageCopy: [],
      selectedNumArr: [],
      columns_first: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          title: this.$t('currencywithdrawalauditmanagement.withdrawalcurrency'),
          key: "coin",
          width: 100,
          render: (h, obj) => {
            let unit = obj.row.unit;
            return h("span", {}, unit);
          }
        },
        {
          title: this.$t('currencywithdrawalapproval.withdrawaladdress'),
          key: "address",
          width: 180
        },
        {
          title: this.$t('currencywithdrawalapproval.remarksmemo'),
          key: "remark",
          width: 100
        },
        {
          title: this.$t('currencywithdrawalapproval.applicationtime'),
          key: "createTime",
          width: 140
        },
        {
          title: this.$t('currencywithdrawalauditmanagement.withdrawalquantity'),
          key: "totalAmount",
          width: 100
        },
        {
          title: this.$t('currencywithdrawalapproval.actualarrival'),
          key: "arrivedAmount",
          width: 100
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          key: "status",
          width: 100,
          render: (h, obj) => {
            let status = obj.row.status * 1;
            let statusInner = String;

            if (status === 0) statusInner = this.$t('currencywithdrawalapproval.pendingreview');
            else if (status === 1) statusInner = this.$t('currencywithdrawalapproval.waitingforcashrelease');
            else if (status === 2) statusInner = this.$t('currencywithdrawalauditmanagement.fail');
            else if (status === 3) statusInner = this.$t('currencywithdrawalapproval.success');

            return h("span", {}, statusInner);
          }
        },
        {
          title: this.$t('currencywithdrawalapproval.withdrawalmethod1'),
          width: 110,
          render: (h, obj) => {
            const isAuto =
              obj.row.isAuto * 1 === 0 ? this.$t('currencywithdrawalapproval.manualreviewwithdrawal') : this.$t('currencywithdrawalapproval.automaticwithdrawal');

            return h("span", {}, isAuto);
          }
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.handlingcharges'),
          key: "fee",
          width: 100,
          render: (h, obj) => {
            let fee = obj.row.fee;
            let unit = obj.row.unit;

            return h("span", {}, fee + unit);
          }
        },
        {
          title: "TXID",
          key: "transactionNumber",
          width: 180
        },
        {
          title: this.$t('currencywithdrawalapproval.membernickname'),
          key: "memberUsername",
          width: 100
        },
        {
          title: this.$t('realnamemanagement.memberid'),
          key: "memberId",
          width: 100
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          fixed: 'right',
          width: 100,
          key: "action",
          render: (h, obj) => {
            let status = obj.row.status;
            let statusInner = String;
            let btnType = "info";

            if (status === 0) {
              statusInner = this.$t('currencywithdrawalauditmanagement.audit');
              btnType = "info";
            } else {
              statusInner = this.$t('transactiondetailsinlegalcurrency.view');
              btnType = "success";
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
                    Cookies.set("financePage", this.filterSearch.pageNo);
                    this.$router.push({ path: "/finance/auditdetail" });
                    removeStore("userDetails");
                    setStore("userDetails", obj.row);
                  }
                }
              },
              statusInner
            );
          }
        }
      ]
    };
  },
  methods: {
    confrimPass() {
      this.ifPassAll(false);
    },
    checkable() {
	  this.userpageCopy = [];
	  console.log(this.userpage)
      this.userpage.forEach(item => {
        if (item.status !== 1) {
			item._disabled = true;
		}else{
			item._disabled = false;
		}
        this.userpageCopy.push(item);
      });
    },

    ifPassAll(bol) {
      if (!this.selectedNumArr.length) {
        this.$Message.warning(this.$t('currencywithdrawalapproval.noitemshavebeenselected'));
      } else {
        let fn = "";
        let obj = { ids: this.selectedNumArr };
        if (bol) {
          fn = auditNoPass;
        } else {
          fn = passCoin;
          Object.assign(obj, this.passAllCoin);
		}
        fn(obj).then(res => {
          if (!res.code) {
            this.$Message.success(res.message);
          } else this.$Message.error(res.message);
          withdrawManage({ pageNo: this.pageIndex, pageSize: 10 }).then(res => {
            this.userpage = res.data.content;
            this.checkable();
          });
        });
      }
    },
    selectionChange(selection) {
      this.selectedNumArr = [];
      selection.forEach(item => {
        this.selectedNumArr.push(item.id);
      });
    },
    searchByFilter() {
      let reg = /\D/;
      if (reg.test(this.filterSearch.memberId)) {
        this.$Message.warning(this.$t('currencywithdrawalapproval.pleaseenterthecorrectmemberid'));
        return;
      }
      this.resetTableContain(1);
    },
    refreshPageManual() {
      this.currentPageIndex = 1;
      for (let val in this.filterSearch) {
        this.filterSearch[val] = "";
      }
      this.resetTableContain(1);
    },
    changePage(pageIndex) {
      this.currentPageIndex = pageIndex;
      this.resetTableContain(pageIndex);
    },
    refreshPage(obj = {}) {
      this.ifLoading = true;
      withdrawManage(obj).then(res => {
        if (!res.code) {
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.pageNum = res.data.totalElements;
          this.userpageCopy = [...this.userpage];
          this.checkable();
		} else{
			this.$Message.error(res.message);
		}
      });
    },
    resetTableContain(pageNo) {
      this.filterSearch.pageNo = pageNo;
      this.filterSearch.pageSize = 10;
      this.refreshPage(this.filterSearch);
    }
  },
  created() {
    if (!!Cookies.get("financePage")) {
      this.currentPageIndex = this.filterSearch.pageNo = Number(Cookies.get("financePage"));
    }
    this.refreshPage(this.filterSearch);
    Cookies.remove("financePage");
  }
};
</script>

<style scoped>
</style>
