<template>
    <div >
      <Card>
        <p slot="title">
        {{ $t('departmentmanagement.departmentmanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
        </p>

        <Row class="functionWrapper">
          <div class="btnsWrapper clearfix">
            <Button type="primary" @click="addDepartBtn">{{ $t('departmentmanagement.adddepartment') }}</Button>
          </div>
        </Row>

        <Modal
          class="addDepartModal"
          v-model="showAddDepart"
          :title="$t('departmentmanagement.adddepartment')"
          @on-ok="addDepart">

          <p><span>{{ $t('departmentmanagement.departmentname') }} <i>*</i>：</span><Input v-model="departName"></Input></p>
          <p><span>{{ $t('departmentmanagement.leaderid') }}</span><Input v-model="leaderID"></Input></p>
          <p><span>{{ $t('departmentmanagement.departmentdescription') }}</span><Input type="textarea" v-model="departDscrp"> </Input></p>

        </Modal>

        <Row >
          <!-- @on-selection-change="selected" -->
          <Table 
          :columns="column_frist" 
          :data="userpage" 
          :loading="ifLoading"
          border>
          </Table>
        </Row>

        <Modal
          class="addDepartModal departDetailModal"
          v-model="showDepartDetail"
          :title="$t('departmentmanagement.departmentdetails')"
          @on-ok="updateDepart"
          @on-cancel="$Message.info(this.$t('departmentmanagement.cancel'))">

          <p><span><em>{{ $t('departmentmanagement.collar') }}</em>{{ $t('departmentmanagement.guideid') }}</span><Input v-model="departmentDetail.leaderId"></Input></p>
          <p><span><em>{{ $t('departmentmanagement.department') }}</em>{{ $t('departmentmanagement.door') }}</span><Input v-model="departmentDetail.name"></Input></p>
          <p><span>{{ $t('departmentmanagement.updatetime') }}</span><Input disabled v-model="departmentDetail.updateTime"></Input></p>
          <p><span>{{ $t('advertisingmanagement.createdon') }}</span><Input disabled v-model="departmentDetail.createTime"></Input></p>
          <p><span><em>{{ $t('departmentmanagement.prepare') }}</em>{{ $t('departmentmanagement.note') }}</span><Input type="textarea" v-model="departmentDetail.remark"></Input></p>

        </Modal>

        <Modal
        v-model="ifDelete"
        :title="$t('departmentmanagement.deletedepartment')"
        @on-ok="confirmDel"
        @on-cancel="$Message.info(this.$t('departmentmanagement.cancel'))">
        <p>{{ $t('departmentmanagement.deletetheselecteditem') }}</p>
       </Modal>

       <Row class="pageWrapper" >
          <Page :total="totalNum" 
            style='margin-top:8px' 
            :current="currentPageIdx"   
            @on-change="changePage" 
            show-elevator></Page>
        </Row> 
        

      </Card>
    </div>
</template>

<script>

import { departmentManage, addAuditDepart, departDetail, delDepart } from '@/service/getData'

export default {
  data() {
    return {
      totalNum: null,
      currentPageIdx: 1,
      ifLoading: true,
      ifDelete: false,
      delDepartID: null,

      deleteArr: [],
      departmentID: null,
      departmentDetail: {},
      showDepartDetail: false,
      leaderID: null,
      showAddDepart: false,
      departName: null,
      departDscrp: null,
      
      column_frist: [
        // {
        //   type: 'selection',
        //   width: 80,
        //   align: 'center'
        // },
        {
          title: this.$t('departmentmanagement.departmentid'),
          key: 'id',
          width: 80
        },
        {
          title: this.$t('departmentmanagement.department'),
          key: 'name'
        },
        {
          title: this.$t('secondcontractcompensationsetting.creationtime'),
          key: 'createTime'
        },
        {
          title: this.$t('systeminformationmaintenance.remarks'),
          key: 'remark'
        },
         {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          render: (h, obj) => {
            return ('div', [
              h('Button',{
                 props: {
                   type: 'info',
                   size: 'small'
                 },
                 style: {
                   'marginRight': '5px'
                 },
                 on: {
                   click: () => {
   
                     this.showDepartDetail = true;
   
                     this.departmentID = obj.row.id;
   
                     departDetail({ departmentId: obj.row.id })
                     .then( res => {
                       if(!res.code) {
                         console.log(res.data);
                         this.departmentDetail = res.data;
                         
                       }else this.$Message.error(this.$t('departmentmanagement.dataacquisitionerror'))
                     })
   
                   }
                 }
               },this.$t('usermanagement.viewedit')),
                h('Button',{
                  props: {
                    type: 'error',
                    size: 'small'
                  },
                  on: {
                    click: () => {
                      this.ifDelete = true;
                      this.delDepartID = obj.row.id;
                    }
                  }
                }, this.$t('secondcontractcompensationsetting.delete'))

            ])
          
          }
        },
      ],  
      userpage: []
    }
  },

  methods: {
    changePage() {
      this.freshPage({ pageNo: this.currentPageIdx, pageSize: 10 })
    },
    confirmDel() {
      delDepart({id: this.delDepartID})
        .then( res => {
        if(!res.code) {
          this.freshPage();
          this.$Message.success(res.message);
        }else this.$Message.error(res.message);
      })
    },
    addDepartBtn() {
      this.showAddDepart = true;
    },
    updateDepart() {
      if(this.departmentDetail.name === '' || !this.departmentDetail.name) {
        this.$Message.warning(this.$t('departmentmanagement.departmentnamecannotbeempty'));
      }else{

        addAuditDepart(this.departmentDetail)
        .then( res => {
          if (!res.code) {
            this.$Message.success(res.message);
            this.freshPage();
          }else this.$Message.error(res.message);
            
        })
      }
    },
    addDepart() {
      if(this.departName === '' || !this.departName) {
        this.$Message.warning(this.$t('departmentmanagement.departmentnamecannotbeempty'));
      }else{

        let updateObj = {
          name: this.departName, 
          leaderId: this.leaderID,
          remark: this.departDscrp
        }

        addAuditDepart(updateObj)
        .then( res => {
          if (!res.code) {
            this.departName = null;
            this.leaderID = null;
            this.departDscrp = null;

            this.$Message.success(res.message);
            this.freshPage();
          }else{
            this.$Message.error(res.message);
          }
        })
      }

    },
    refreshPageManual() {
      this.freshPage();
    },
    freshPage(obj) {
      this.ifLoading = true;
      departmentManage(obj)
      .then( res => {
        if(!res.code){
          this.userpage =  res.data.content;
          this.ifLoading = false;
          this.totalNum = res.data.totalElements;
        }else this.$Message.error(res.message)
      }, err => {
        console.log(err);
      })
    }
  },
  created () {
    this.freshPage();
  }
}
</script>

<style lang="less" scoped>

  .addDepartModal{
    p{
      position: relative;
      padding-left: 85px;
      margin-bottom: 10px;
      width: 400px;

      span{
        position: absolute;
        top: 0;
        left: 0;
        font-size: 14px;
        line-height: 32px;
        i{
          font-size: 16px;
          font-weight: 700;
          color: #ec0909;
        }
      }
    }
  }
  .departDetailModal{
    p{
      span {
        em{
          font-style: normal;
          letter-spacing: 28px;
        }
      }
    }
  }
</style>