<template>
  <div>
      <Row>
          <Card>
                <p slot="title">
                {{ $t('activitymanagement.activitymanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
                        <Icon type="refresh"></Icon>{{ $t('activitymanagement.refresh') }}
                    </Button>
                </p>

                <Row class="functionWrapper">
                    <div class="btnsWrapper clearfix">
                        <Button type="primary" @click="newActivity">{{ $t('activitymanagement.newactivity') }}</Button>
                    </div>
                </Row>
                <Row>
                    <Table
                        :columns="columns"
                        :data="list"
                        border
                        :loading="ifLoading">
                    </Table>
                </Row>
                <div class="pageWrapper">
                    <Page
                        :total="pageNum"
                        :current="currentPageIdx"
                        @on-change="changePage"
                        show-elevator>
                    </Page>
                </div>
                <div style="font-size:12px;">
                  <p>{{ $t('activitymanagement.note1') }}</p>
                  <p>{{ $t('activitymanagement.prerequisitesforactivityinitiation') }}</p>
                  <p>{{ $t('activitymanagement.note2') }}</p>
                  <p>{{ $t('activitymanagement.note3') }}</p>
                  <br>
                  <p>{{ $t('activitymanagement.note4') }}</p>
                  <p>{{ $t('activitymanagement.note5') }}</p>
                  <p>{{ $t('activitymanagement.note6') }}</p>
                  <p>{{ $t('activitymanagement.note7') }}</p>
                </div>
          </Card>

      </Row>
      <Modal
            class="auditModel"
            v-model="showProgressModel"
            :title="$t('activitymanagement.pleaseentertheprogress')"
            width="550"
            @on-cancle="progress = ''"
            @on-ok="submitProgress">
            <Input v-model="progress" type="text" :placeholder="$t('activitymanagement.pleaseentertheprogressvalue')"></Input>
     </Modal>
     <Modal
            class="auditModel"
            v-model="showDetailModel"
            :title="$t('activitymanagement.participationdetails')"
            width="750"
            @on-cancle=""
            @on-ok="">
            <Row>
                <Table
                    :columns="columnsDetail"
                    :data="orderList"
                    border
                    :loading="ifLoading">
                </Table>
            </Row>
     </Modal>
  </div>
</template>

<script>

  import { activityList, addActivity, modifyActivity, modifyActivityProgress,activityOrderList, distributeOrder, BASICURL } from '@/service/getData';
  import { getStore, removeStore, setStore } from '@/config/storage';

  export default{
    data () {
      return {
        loginPassModal: false,
        showProgressModel: false,
        showDetailModel: false,
        progress: "",
        loginPW: '',
        activityForm:{
            title: "",
            detail: "",
            activityLink: "",
            noticeLink: "",
            settings: "",
            step: "0",
            type: "1",
            status: "0",
            imageUrl: ""
        },
        id: null,
        type: 0,
        ifShowPercentCircle: false,
        percentage: 0,
        picUrl: "",
        picUrlIcon: false,
        basicUrl: BASICURL,
        filterSearch: {
            pageNo: 1,
            pageSize: 10
        },
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        orderList: [],
        columnsDetail: [
          {
            title: this.$t('activitymanagement.userid'),
            key:"memberId",
            width: 120
          },
          {
            title: this.$t('activitymanagement.subscriptionquantity'),
            key:"amount"
          },
          {
            title: this.$t('activitymanagement.openinterest'),
            key:"freezeAmount"
          },
          {
            title: this.$t('activitymanagement.turnoverfrozenassets'),
            key:"turnover",
            width: 140
          },
          {
            title: this.$t('activitymanagement.status'),
            render: (h ,obj) => {
              let state =  obj.row.state;
              let txt = this.$t('activitymanagement.temporary');
              let color = "#000";
              if(state == 1) {txt = this.$t('activitymanagement.nodeal');color = "#FF0000";}
              if(state == 2) {txt = this.$t('activitymanagement.closed');color = "#19be6b";}
              if(state == 3) {txt = this.$t('activitymanagement.revoked');color = "#EEE";}

              return h('span',{
                style:{
                  color: color
                }
              },txt)
            }
          },
          {
              title: this.$t('activitymanagement.operation'),
              key: "xx",
              fixed: 'right',
              width: 150,
              render: (h, obj) => {
                let showProgress = "("+obj.row.progress+")";
                let disabled = true;
                if(obj.row.state == 1){
                  disabled = false;
                }
                let btnText = this.$t('activitymanagement.distributeactivitycurrency');
                if(this.type == 5) {
                  btnText = this.$t('activitymanagement.deployminer');
                }
                if(this.type == 6) {
                  btnText = this.$t('activitymanagement.agreetolockup');
                }
                return h("div", [
                  h(
                    'Button',
                    {
                      props: {type: "success",size: "small",disabled: disabled},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          let param = {};
                          param["oid"] = obj.row.id;
                          console.log(param);
                          distributeOrder(param).then( res => {
                            if(!res.code) {
                                this.$Message.success(res.message);
                                this.getDetailOrderList();
                            }else{
                                this.$Message.error(res.message);
                            }
                          });
                        }
                      }
                    },
                    btnText
                  )
                ]);
              }
           }
        ],
        list: [],
        columns: [
          {
            title: this.$t('activitymanagement.title'),
            key:"title",
            width: 300
          },
          {
            title: this.$t('activitymanagement.thumbnail'),
            width:70,
            render:(h, obj) => {
              let smallImageUrl = obj.row.smallImageUrl;
              return  h('img',{
                attrs: {src: smallImageUrl},
                style: {height: "45px", padding:'5px 0px'}
              },"");
            }
          },
          {
            title: this.$t('activitymanagement.display'),
            width: 60,
            render: (h ,obj) => {
              let status =  obj.row.status;
              let txt = status == 1 ? this.$t('activitymanagement.display'):this.$t('activitymanagement.hide');
              let color = status == 1 ? "#19be6b":"#FF0000";
              return h('span',{
                style:{
                  color: color
                }
              },txt)
            }
          },
          {
            title: this.$t('activitymanagement.status'),
            width: 75,
            render: (h ,obj) => {
              let step =  obj.row.step;
              let txt = this.$t('activitymanagement.inpreparation');
              let color = "#2db7f5";
              if(step == 1) {txt = this.$t('activitymanagement.inprogress');color="#19be6b";}
              if(step == 2) {txt = this.$t('activitymanagement.sending');color="#F90";}
              if(step == 3) {txt = this.$t('activitymanagement.ended');color="#AFAFAF";}

              return h('span',{
                style:{
                  color: color
                }
              },txt)
            }
          },
          {
            title: this.$t('activitymanagement.firstlevelinvitation'),
            key:"leveloneCount",
            width: 60
          },
          {
            title: this.$t('activitymanagement.type'),
            width: 115,
            render: (h ,obj) => {
              let type =  obj.row.type;
              let txt = this.$t('activitymanagement.unknownactivity');
              if(type == 1) txt = this.$t('activitymanagement.firsttimeonlinerushpurchase');
              if(type == 2) txt = this.$t('activitymanagement.firsttimeonlinesplitequally');
              if(type == 3) txt = this.$t('activitymanagement.positionsharing');
              if(type == 4) txt = this.$t('activitymanagement.freesubscription');
              if(type == 5) txt = this.$t('activitymanagement.miningmachinesubscription');
              if(type == 6) txt = this.$t('activitymanagement.lockupactivity');
              return h('span',{
              },txt)
            }
          },
          {
            title: this.$t('activitymanagement.quantityprice'),
            width: 160,
            render: (h ,obj) => {
              return h('span',{
              },obj.row.totalSupply + " / " + obj.row.price + obj.row.acceptUnit)
            }
          },
          {
            title: this.$t('activitymanagement.starttime'),
            key:"startTime",
            width: 140,
            render: (h ,obj) => {
              let sTime = obj.row.startTime.substr(0, 10) + " " + obj.row.startTime.substr(11, 8);
              let curTime = new Date().Format('YYYY-MM-DD HH:mm:ss');
              let text = this.$t('activitymanagement.abouttostart');
              if(sTime < curTime) {
                text = this.$t('activitymanagement.started');
              }
              return h('span',{
              },sTime + text);
            }
          },
          {
            title: this.$t('activitymanagement.endtime'),
            key:"endTime",
            width: 140,
            render: (h ,obj) => {
              let eTime = obj.row.endTime.substr(0, 10) + " " + obj.row.endTime.substr(11, 8);
              let curTime = new Date().Format('YYYY-MM-DD HH:mm:ss');
              let color = "#19be6b";
              let text = this.$t('activitymanagement.notfinished');
              if(eTime < curTime) {
                text = this.$t('activitymanagement.ended');
                color = "#000";
              }
              return h('span',{
                style:{
                  color: color
                }
              },eTime + text);
            }
          },
          {
            title: this.$t('activitymanagement.announcementlink'),
            width: 90,
            render: (h ,obj) => {

              return h('a',{
                attrs: {
                    href: obj.row.noticeLink,
                    target: "_blank"
                }
              },this.$t('activitymanagement.viewannouncements'));
            }
          },
          {
            title: this.$t('activitymanagement.activelinks'),
            width: 90,
            render: (h ,obj) => {

              return h('a',{
                attrs: {
                    href: obj.row.activityLink,
                    target: "_blank"
                }
              },this.$t('activitymanagement.activepage'));
            }
          },
          {
            title: this.$t('activitymanagement.creationtime'),
            key:"createTime",
            width: 140
          },
          {
              title: this.$t('activitymanagement.operation'),
              key: "xx",
              fixed: 'right',
              width: 220,
              render: (h, obj) => {
                let showProgress = "("+obj.row.progress+")";
                let disabled = true;
                if(obj.row.type == 3 || obj.row.type == 4 || obj.row.type == 5 || obj.row.type == 6){
                  disabled = false;
                }
                return h("div", [
                  h(
                    'Button',
                    {
                      props: {type: "info",size: "small"},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          removeStore('manageID');
                          setStore('manageID',  obj.row.id);
                          this.$router.push('/activity/activity/addActivity');
                        }
                      }
                    },
                    this.$t('activitymanagement.modify')
                  ),
                  h(
                    'Button',
                    {
                      props: {type: "error",size: "small"},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          this.showProgressModel = true;
                          this.id = obj.row.id;
                          this.progress = obj.row.progress;
                        }
                      }
                    },
                    this.$t('activitymanagement.progress')+showProgress
                  ),
                  h(
                    'Button',
                    {
                      props: {type: "success",size: "small",disabled: disabled},
                      style: {marginRight: "5px"},
                      on: {
                        click: () => {
                          this.showDetailModel = true;
                          this.id = obj.row.id;
                          this.type = obj.row.type;
                          this.getDetailOrderList();
                        }
                      }
                    },
                    this.$t('activitymanagement.participationdetails')
                  )
                ]);
              }
           }
        ]
      }
    },
    methods: {
      getDetailOrderList(){
        activityOrderList(this.id)
        .then(res => {
          this.orderList = res.data;
        });
      },
      submitProgress(){
        let param = {};
        param["id"] = this.id;
        param["progress"] = this.progress;
        modifyActivityProgress(param).then( res => {
          if(!res.code) {
                this.showProgressModel = false;
                this.$Message.success(this.$t('activitymanagement.modifiedsuccessfully'));
                this.refreshPage(1);
          }else{
                this.$Message.error(res.message)
          }
        })
      },
      newActivity(){
        removeStore('manageID');
        this.$router.push('/activity/activity/addActivity');
      },
      refreshPageManual() {
                this.currentPageIdx = 1;
                for(let key in this.filterSearch) {
                    this.filterSearch[key] = '';
                }
                this.filterSearch.pageNo = 1;
                this.filterSearch.pageSize = 10;
                this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
                this.currentPageIdx = pageIndex;
                this.filterSearch.pageNo = pageIndex;
                this.refreshPage(this.filterSearch)
      },
      refreshPage(obj) {
        this.ifLoading = true;
        activityList(this.filterSearch).then( res => {
          if(!res.code) {
                this.ifLoading = false;
                this.pageNum = (res.data && res.data.totalElements) || 1;
                this.list = (res.data && res.data.content) || [];
          }else{
                this.$Message.error(res.message)
          }
        })
      }
    },
    created () {
      this.refreshPage();
    }
  }
</script>

<style scoped>
  .ivu-upload{
    display: inline-block;
  }
</style>
