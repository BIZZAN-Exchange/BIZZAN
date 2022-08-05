<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('realnamemanagement.realnamemanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row class="functionWrapper">
        <div class="searchWrapper clearfix">

          <div class="poptip">
            <Poptip trigger="hover" :content="$t('realnamemanagement.note1')" placement="bottom-start">
              <Input :placeholder="$t('realnamemanagement.note1')"
                     v-model="filterSearch.account"/>
              </Input>
            </Poptip>
          </div>

          <div class="poptip">
            <Poptip trigger="hover" :content="$t('realnamemanagement.pleaseenteryouridnumbertosearch')" placement="bottom-start">
              <Input :placeholder="$t('realnamemanagement.pleaseenteryouridnumbertosearch')"
                     v-model="filterSearch.cardNo"/>
              </Input>
            </Poptip>
          </div>

          <div class="orderStatus">
            <span>{{ $t('realnamemanagement.auditstatus') }}</span>
            <Select v-model="filterSearch.auditStatus">
              <Option v-for="item in applyStatusArr"
                      :value="item.status"
                      :key="item.status">{{ item.text }}</Option>
            </Select>
          </div>

          <div class="btns">
            <Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
          </div>

        </div>

        <!-- <div class="btnsWrapper clearfix">
          <Button type="success" @click="outexcel">导出</Button>
        </div> -->
      </Row>

      <Row class="margin-top-10 searchable-table-con1">
        <Table
            :columns="columns_first"
            :data="userpageCopy"
            border
            :loading="ifLoading"
            class='user_center'
            @on-sort-change="definedOrder"
            ref="outTabel"></Table>
      </Row>

      <div class="pageWrapper">
        <Page
            :total="totalNum"
            :current="currentPageIdx"
            @on-change="changePage"
            show-elevator></Page>
      </div>
    </Card>
  </div>
</template>

<script>

import { MemberRealNameList, MemberRealNameDetail, memberCheckPass, memberCheckNotPass } from '@/service/getData';
import { setStore, getStore, removeStore } from '@/config/storage';
import loginVue from '../login.vue';

export default {
  data() {
    return {
      userInfo: {},
      imgArr: [],
      sortSearch: {
        direction: [],
        property: []
      },
      filterSearch: {
        account: '',
        cardNo: '',
        auditStatus: ''
      },
      applyStatusArr: [
        { status: 0, text: this.$t('currencywithdrawalapproval.pendingreview') },
        { status: 1, text: this.$t('realnamemanagement.approvalfailed') },
        { status: 2, text: this.$t('realnamemanagement.approvedsuccessfully') },
        { status: '', text: this.$t('transactiondetailsinlegalcurrency.all') },
      ],
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,

      columns_first: [
        {
          type: 'selection',
          width: 60,
          align: 'center',
        },
        {
          title: this.$t('announcementmanagement.no'),
          key: "id",
          width: 80
        },
        {
          title: this.$t('auditdetails.memberid'),
          width: 80,
          key: "memberID",
          render: (h, obj) => {
            const row = obj.row;
            const memberID = row.member.id;
            return h("span", {}, memberID);
          }
        },
        {
          title: this.$t('memberinvitationlist.username'),
          render: (h, params) => {
            const row = params.row;
            const member = row.member.username;
            return h("span", {}, member);
          }
        },
        {
          title: this.$t('businessinformation.realname'),
          key: "realName"
        },
        {
          title: this.$t('realnamemanagement.idnumber'),
          key: "idCard",
        },
        {
          title: this.$t('realnamemanagement.registrationtime'),
          key: "member.registrationTime",
          sortable: 'custom',
          render: (h, params) => {
            const row = params.row;
            const member = row.member.registrationTime;
            return h("span", {}, member);
          }
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          render: (h, obj) => {
            const row = obj.row;
            let auditStatus = row.auditStatus;
            if (auditStatus === 0)  auditStatus  = this.$t('currencywithdrawalapproval.pendingreview');
            else if (auditStatus == 1) auditStatus = this.$t('realnamemanagement.approvalfailed');
            else if (auditStatus == 2)  auditStatus = this.$t('realnamemanagement.approvedsuccessfully');
            return h("span", {}, auditStatus);
          }
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          render: (h, obj) => {
            let status = obj.row.auditStatus;
            let statusTxt = String;
            let btnType = 'info';

            if (!status) {
              statusTxt = this.$t('currencywithdrawalapproval.pendingreview');
              btnType = 'info';
            } else {
              statusTxt = this.$t('transactiondetailsinlegalcurrency.view');
              btnType = 'success';
            }

            return h("Button", {
              props: {
                type: btnType,
                size: 'small'
              },
              on: {
                click: () => {
                  removeStore('AuthenticateID');
                  setStore('AuthenticateID',obj.row.id);
                  if(statusTxt === this.$t('transactiondetailsinlegalcurrency.view')) {
                    this.$router.push('memberaudit/auditdetail');
                  }else if(statusTxt === this.$t('currencywithdrawalapproval.pendingreview')){
                    this.$store.commit('switchMemberMask', true);
                    MemberRealNameDetail({id: obj.row.id})
                        .then(res => {
                          this.userInfo = res.data;
                          let imgArr = [
                            { front: res.data.identityCardImgFront },
                            { inHand: res.data.identityCardImgInHand },
                            { reverse: res.data.identityCardImgReverse }
                          ]
                          this.$store.commit('memeberCheckImg', {imgArr: imgArr, id: obj.row.id})
                        })
                  }
                }
              }
            }, statusTxt);
          }
        },
      ],
      userpage: [],
      userpageCopy: []
    };
  },
  methods: {
    // outexcel(){
    //   this.$refs.outTabel.exportCsv({
    //     filename:"实名",
    //   })
    // },
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
      Object.assign(subObj, this.filterSearch,this.sortSearch);

      this.refreshPage(subObj)
    },
    searchByFilter() {
      this.$store.commit('switchLoading', true);
      this.currentPageIdx = 1;

      let subObj = { pageNo: 1, pageSize: 10, auditStatus: this.status};

      Object.assign(subObj, this.filterSearch);

      this.refreshPage(subObj)
    },
    refreshPageManual() {
      for(let val in this.filterSearch)  {
        this.filterSearch[val] = '';
      }
      let subObj = { pageNo: 1, pageSize: 10, auditStatus: this.status};
      Object.assign(subObj, this.filterSearch);
      this.refreshPage(subObj);

    },
    filterUser(val) {
      this.userpageCopy = [];
      this.userpage.forEach( item => {
        if(item.auditStatus === val) {
          this.userpageCopy.push(item);
        }
      })
      if (val === 'all') this.userpageCopy = this.userpage;
    },
    refreshPage(obj) {

      this.ifLoading = true;
      MemberRealNameList(obj)
          .then(res => {
            if(!res.code){
              this.ifLoading = false;
              this.userpage = res.data.content;
              this.totalNum = res.data.totalElements;
              this.userpageCopy = [...this.userpage];
            }else this.$Message,error(res.message);

            this.$store.commit('switchLoading', false);
          });
    },
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      // this.ifLoading = true;

      let subObj = { pageNo: pageIndex, pageSize: 10, auditStatus: this.status};

      Object.assign(subObj, this.filterSearch);

      this.refreshPage(subObj);
    },
    refreshPageStatus(obj) {
      MemberRealNameList(obj)
          .then(res => {
            if(!res.code){
              this.ifLoading = false;
              this.userpage = res.data.content;
              this.userpageCopy = [...this.userpage];
              this.totalNum = res.data.totalElements;
              this.userpageCopy.forEach( item => {
                if(!!item.auditStatus) item._disabled = true;
                else item._disabled = false;
              })
            }else this.$Message.error(res.message);
          });
    },
    judgeStatus(obj) {
      if ( obj.status === undefined) this.status = null
      else  this.status = this.$route.query.status;
      let subObj = { pageNo: 1, pageSize: 10, auditStatus: this.status};
      Object.assign(subObj, this.filterSearch);
      this.refreshPageStatus(subObj);
    }
  },
  created() {
    this.judgeStatus(this.$route.query);
  },
  computed: {
    date() {
      return this.$store.state.user.date
    }
  },
  watch: {
    '$route' (to) {
      this.judgeStatus(to);
    },
    date(newVal, oldVal) {
      let subObj = { pageNo: this.currentPageIdx, pageSize: 10, auditStatus: this.status};
      Object.assign(subObj, this.filterSearch);
      this.refreshPage(subObj);
    }
  }
};
</script>

<style scoped lang='less'>

</style>
