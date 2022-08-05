<template>
    <div >
      <Card>
        <p slot="title">
        {{ $t('usermanagement.usermanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
        </p>

        <Row class="functionWrapper">
          <div class="btnsWrapper clearfix">
            <Button type="error" @click="ifDelete = true">{{ $t('usermanagement.deleteuser') }}</Button>
            <Button type="primary" @click="addUserBtn">{{ $t('usermanagement.adduser') }}</Button>
          </div>
        </Row>

        <Row >
          <Table 
            :columns="column_frist" 
            :data="userpage" 
            :loading="ifLoading"
            @on-selection-change="select"
            border>
          </Table>
        </Row>

        <Row class="pageWrapper">
          <Page :total="pageNum" 
                class="buttomPage"
                @on-change="changePage"
								:current="currentPageIdx"
                show-elevator></Page>
        </Row>

        <Modal
          v-model="ifDelete"
          :title="$t('usermanagement.deleteuser')"
          @on-ok="confirmDel"
          @on-cancel="$Message.info('已取消！')">
          <p>是否删除所选的{{ delArr.length }}项?</p>
       </Modal>
      </Card>
    </div>
</template>

<script>
import { setStore, getStore, removeStore } from '@/config/storage';
import { queryEmployee, addAuditEmployee, employeeDetail, delEmployee, departmentManage } from "@/service/getData";


export default {
  data() {
    return {
      currentPageIdx: 1,
      pageNum: null,
      delArr: [],

      ifLoading: true,
      ifDelete: false,

      departName: null,

      column_frist: [
        {
          type: "selection",
          width: 80,
          align: "center"
        },
        {
          title: this.$t('usermanagement.userid'),
          key: "id",
          width: 80
        },
        {
          title: this.$t('memberinvitationlist.username'),
          key: "username"
        },
        {
          title: this.$t('businessinformation.realname'),
          key: "realName"
        },
        {
          title: this.$t('personalcenter.departmentName'),
          render: (h, obj) => {
            let departName = obj.row.department.name;
            return "span", {}, departName;
          }
        },
        {
          title: this.$t('usermanagement.role'),
          key: "roleName"
        },
        {
          title: this.$t('memberinvitationlist.cellphonenumber'),
          key: "mobilePhone"
        },
        {
          title: this.$t('memberinvitationlist.mailbox'),
          key: "email"
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          key: "enable",
          render: (h, obj) => {
            let enable = obj.row.enable;
            let status = "";
            let btnType = "success";
            if (!enable) {
              status = this.$t('querymarginstrategy.normal');
            } else {
              status = this.$t('currencyextensionmanagement.disable');
              btnType = "error";
            }
            return h(
              "Button",
              {
                props: {
                  type: btnType,
                  size: "small"
                }
              },
              status
            );
          }
        },

        {
          title: this.$t('usermanagement.operation'),
          render: (h, obj) => {
            return (
              "div",
              [
                h(
                  "Button",
                  {
                    props: {
                      type: "info",
                      size: "small"
                    },
                    on: {
                      click: () => {
                        removeStore('employeeID');
												setStore('employeeID', obj.row.id);
                        this.$router.push("/system/employee/auditEmployee");
                      }
                    }
                  },
                  this.$t('usermanagement.viewedit')
                )
              ]
            );
          }
        }
      ],
      userpage: []
    }
  },
  methods: {
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      this.freshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    select(solutions) {
      this.delArr = [];
      solutions.forEach(item => {
        this.delArr.push(item.id);
      });
      console.log(solutions);
    },
    addUserBtn () {
      this.$router.push("/system/employee/auditEmployee");
    },
    confirmDel() {
      delEmployee({ ids: this.delArr })
      .then(res => {
        if (!res.code) {
          this.freshPage();
          this.$Message.success(this.$t('currencysetting.deletionsucceeded'));
        } else this.$Message.error(this.$t('currencysetting.deletionfailed'));
      })
      .catch( err =>{ console.log(err)})
    },
   
    refreshPageManual() {
      this.freshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
    },
    freshPage(obj) {
      this.ifLoading = true;
      queryEmployee(obj)
      .then(res => {
        console.log(res);
        
        if (!res.code) {
          this.userpage = res.data.content;
          this.ifLoading = false;
          this.pageNum = res.data.totalElements;
        } else this.$Message.error(res.message);
      })
      .catch( err => console.log( err ));
    }
  },
  created() {
    removeStore('employeeID');
    this.freshPage({ pageNo: 1, pageSize: 10 });
  }
};
</script>

<style lang="less" scoped></style>