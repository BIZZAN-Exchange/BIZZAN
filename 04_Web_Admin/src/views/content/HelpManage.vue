<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('helpmanage.helpmanage') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row class="manageRow clearfix">
        <div class="manageWrapper">
          <Button type="error" @click="delManage">{{ $t('secondcontractcompensationsetting.delete') }}</Button>
          <Button type="info" @click="addManage">{{ $t('announcementmanagement.addto') }}</Button>
        </div>
      </Row>

      <Modal
      class="manageModal"
      v-model="manageModal"
      :title="$t('helpmanage.addmanagement')"
      width="430">
        <p slot="header" style="color:#5cadff;text-align:center">
          <Icon type="information-circled"></Icon>
          <span style="padding-left: 5px;" >{{ $t('helpmanage.addmanagement') }}</span>
        </p>

        <p> <span>{{ $t('announcementmanagement.title') }}<i>*</i>：</span>
          <Input v-model="manageTitle" ></Input>
        </p>
        <div><span>{{ $t('helpmanage.classification') }}<i>*</i>：</span>
          <Select v-model="manageClass" style="width:200px">
            <Option v-for=" item in manageClassArr" v-model="item.status" :key="item.status">
              {{ item.klassName }}
            </Option>
          </Select>
        </div>

        <p><span>{{ $t('helpmanage.pictureaddress') }}<i>*</i>：</span>
          <Input v-model="imgUrl" ></Input>
        </p>
        <p><span>{{ $t('perpetualcontractcurrencystandardmanagement.sort') }}</span>
          <Input v-model="sort" ></Input>
        </p>
        <div>{{ $t('editannouncement.language') }}
          <RadioGroup v-model="lang">
            <Radio label="zh_CN">
              <span>{{ $t('announcementmanagement.chinese') }}</span>
            </Radio>
            <Radio label="en_US">
              <span>English</span>
            </Radio>
            <Radio label="ja_JP">
              <span>{{ $t('announcementmanagement.japanese') }}</span>
            </Radio>
            <Radio label="ko_KR">
              <span>{{ $t('announcementmanagement.korean') }}</span>
            </Radio>
            <Radio label="de_DE">
              <span>{{ $t('announcementmanagement.german') }}</span>
            </Radio>
            <Radio label="fr_FR">
              <span>{{ $t('announcementmanagement.french') }}</span>
            </Radio>
            <Radio label="it_IT">
              <span>{{ $t('announcementmanagement.italy') }}</span>
            </Radio>
            <Radio label="es_ES">
              <span>{{ $t('announcementmanagement.spain') }}</span>
            </Radio>
            <Radio label="zh_HK">
              <span>{{ $t('advertisingmanagement.traditionalchinesecharacter') }}</span>
            </Radio>
          </RadioGroup>
        </div>
        <div><span>{{ $t('announcementmanagement.releasetime') }}<i>*</i>：</span>
          <DatePicker v-model="createTime"
                      type="date" format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('advertisingmanagement.pleaseselectatime')" style="width: 200px">
          </DatePicker>
        </div>

        <div><span>{{ $t('helpmanage.whethertodisplay') }}<i>*</i>：</span>
          <Select v-model="ifSHow" style="width:200px">
            <Option v-for=" item in ifShowArr" v-model="item.status" :key="item.status">
              {{ item.klassName }}
            </Option>
          </Select>
        </div>

        <div slot="footer" style="color:#5cadff;text-align:right">
          <div v-if="!ifUpdateBtn">
            <Button type="text" @click="cancelAdd">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
            <Button type="info" @click="addManage">{{ $t('advertisingmanagement.confirm') }}</Button>
          </div>

          <div v-if="ifUpdateBtn">
            <Button type="text" @click="cancelAdd">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
            <Button type="info" @click="updateManage">{{ $t('helpmanage.toupdate') }}</Button>
          </div>
        </div>

      </Modal>

      <Row class="test">
        <Table border
              :columns="columns_first"
              :data="helpManageArr"
              :loading="ifLoading"
              @on-selection-change="select">

        </Table>
      </Row>

      <Row class="pageWrapper">
        <Page :total="totalNum"
          class="buttomPage"
          :current="current"
          @on-change="changePage"
          show-elevator>
        </Page>
      </Row>

      <!-- 是否删除 -->
      <Modal
        v-model="ifDelete"
        :title="$t('secondcontractcompensationsetting.delete')"
        @on-ok="confirmDel"
        @on-cancel="cancelDel">
        <p>{{ $t('helpmanage.note1', [selectedArr.length]) }}</p>
      </Modal>


    </Card>
  </div>
</template>

<script>

import qs from 'qs';
import { stickTopHelp, helpManage, addHelpManage, delHelpManage, helpManageDetail, updateHelpManage,helpDown  } from '@/service/getData';
import { formatDate } from '@/caculate/caculate';
import { setStore, removeStore } from '@/config/storage';


  export default {
    data () {
      return {
        currentPageIdx: 1,
        delArr: [],
        ifDelete: false,
        ifLoading: true,
        id: null,
        ifUpdateBtn: false,
        selectedArr: [],
        helpManageArr: [],
        current: 1,
        totalNum: null,
        manageModal: false,
        manageTitle: null,
        manageClass: null,
        manageClassArr: [
          { status: 0,  klassName: this.$t('systemhelp.beginnersguide') },
          { status: 1,  klassName: this.$t('systemhelp.frequentlyaskedquestions') },
          { status: 2,  klassName: this.$t('helpmanage.rechargeguide') },
          { status: 3,  klassName: this.$t('systemhelp.transactionguide') }
        ],
        imgUrl: null,
        createTime: null,
        lang: "zh_CN",
        sort: null,
        ifSHow: null,
        ifShowArr: [
          { status: 0,  klassName: this.$t('perpetualcontractcurrencystandardmanagement.display') },
          { status: 1,  klassName: this.$t('announcementmanagement.dontshow') },
        ],
        columns_first: [
          {
            type: 'selection',
            width: 60
          },
          {
           	title: this.$t('announcementmanagement.no'),
            key: 'id',
            width: 60
          },
          {
           	title: this.$t('announcementmanagement.title'),
						key: 'title',
          },
           {
           	title: this.$t('helpmanage.classification'),
            render: (h, obj) => {
              let text = null;
              let status = obj.row.sysHelpClassification*1;
              if (status === 0) text = this.$t('systemhelp.beginnersguide');
              else if (status === 1) text = this.$t('systemhelp.frequentlyaskedquestions');
              else if (status === 2) text = this.$t('systemhelp.transactionguide');
              else if (status === 3) text = this.$t('systemhelp.currencydata');
              else if (status === 4) text = this.$t('systemhelp.markettechnology');
              else if (status === 5) text = this.$t('systemhelp.termagreement');
              else if (status === 6) text = this.$t('editannouncement.other');

              return h ( 'span', {
              }, text)
            }
          },
          {
            title: this.$t('announcementmanagement.language'),
            key: 'lang',
            width: 60
          },
          {
            title: this.$t('helpmanage.sort'),
            key: 'sort',
            width: 60
          },
           {
           	title: this.$t('perpetualcontractcurrencystandardmanagement.display'),
            key: 'status',
            width:60,
            render: (h, obj) => {
              let text = null;
              if (obj.row.status===0) text = this.$t('perpetualcontractcurrencystandardmanagement.yes');
              else text = this.$t('perpetualcontractcurrencystandardmanagement.no');
              return h ( 'span', {
              }, text)
            }
          },
           {
           	title: this.$t('announcementmanagement.top'),
            // key: 'isTop',
            width:60,
            render: (h, obj) => {
              let text = null;
              if (obj.row.isTop==="0") text = this.$t('perpetualcontractcurrencystandardmanagement.yes');
              else text = this.$t('perpetualcontractcurrencystandardmanagement.no');
              return h ( 'span', {
              }, text)
            }
          },
          {
            title: this.$t('announcementmanagement.releasetime'),
            key: 'createTime',
          },
					{
           	title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
            	render: (h, obj) => {
                return h ('div',[
                    h('Button', {
                      props: {
                        type: 'info',
                        size: 'small'
											},
											style: {
												'marginRight': '5px'
											},
                      on: {
                        click:() => {
                          removeStore('manageID');
                          setStore('manageID',  obj.row.id);
                          this.$router.push('/content/helpManage/addhelpmanage');
                        }
                      }
										},this.$t('usermanagement.viewedit')),
										h('Button', {
                      props: {
                        type: 'error',
                        size: 'small'
                      },
                      on: {
                        click:() => {
													stickTopHelp({id: obj.row.id})
													.then(res => {
														if(!res.code) {
															this.$Message.success(res.message);
															this.refreshPage();
														}else this.$Message.error(res.message)
													})
													.catch(err => console.log(err))
                        }
                      }
                    },this.$t('announcementmanagement.top')),
										h('Button', {
                      props: {
                        type: 'error',
                        size: 'small'
                      },
                      style:{
                        marginLeft:'6px',
                      },
                      on: {
                        click:() => {
													helpDown({id: obj.row.id})
													.then(res => {
														if(!res.code) {
															this.$Message.success(res.message);
															this.refreshPage();
														}else this.$Message.error(res.message)
													})
													.catch(err => console.log(err))
                        }
                      }
                    },this.$t('announcementmanagement.canceltopping'))



                  ])
              }
          },
			  ],
      }
    },
    methods: {
      refreshPageManual() {
        this.ifLoading = true;
        this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
      },
      confirmDel() {
         let delArr = [];
        this.selectedArr.forEach( item => {
          delArr.push(item.id);
        })
        let formatArr =  qs.stringify({ ids: delArr }, { arrayFormat: 'repeat' });

        delHelpManage({ ids: delArr })
        .then( res => {
          this.ifDelete = false;
          if(!res.code) {
            this.refreshPage({ pageNo: 1, pageSize: 10 });
            this.$Message.success(this.$t('secondcontractcompensationsetting.deletesuccessfully'));
          }else{
            this.$Message.error(this.$t('secondcontractcompensationsetting.deletionfailed'));
          }
        })
      },
      cancelDel() {
        this.$Message.success(this.$t('advertisingmanagement.cancelled'));
      },
      updateManage() {

        let updateObj = {
          id:  this.id,
          title: this.manageTitle,
          sysHelpClassification: this.manageClass,
          imgUrl: this.imgUrl,
          status: this.ifSHow,
          lang: this.lang,
          createTime: formatDate(this.createTime)
        }
        // console.log(this.createTime);
        // console.log(formatDate(this.createTime))

        updateHelpManage(updateObj)
        .then(res => {
          if (!res.code) {
            this.manageModal = false;
            this.$Message.success(this.$t('announcementmanagement.updatesucceeded'));
            this.refreshPage();
          }else{
             this.$Message.error(this.$t('announcementmanagement.exceptionoccurred'));
          }
        })
      },
      delManage() {
        if(!!this.selectedArr.length) {
          this.ifDelete = true;
        }else this.$Message.warning(this.$t('helpmanage.pleaseselectthecontenttodelete'))
      },
      cancelAdd() {
        this.manageModal = false;
      },
      showModel() {
        this.manageModal = true;
      },
      addManage() {
        removeStore('manageID');
        this.$router.push('/content/helpManage/addhelpmanage');
      },
      select(selection) {
        this.selectedArr = selection;
      },
      changePage(pageIndex) {
        this.ifLoading = true;
        this.currentPageIdx = pageIndex;
        this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
      },
      refreshPage(obj) {
        removeStore('manageID');
        helpManage(obj)
        .then( res => {
          this.ifLoading = false;
          this.helpManageArr = res.data.content;
          this.totalNum = res.data.totalElements;
        })
      }
    },
    created() {
      this.refreshPage({ pageNo: 1, pageSize: 10 });
    }
  }
</script>

<style lang="less" scoped>
  .manageRow{
    height: 45px;
  }
  .pageWrapper{
    text-align: right;
    margin: 25px;
  }
  .manageWrapper{
    text-align: right;
    Button {
      margin-right: 10px;
    }
  }

  .manageModal{
    p,div{
      margin-bottom: 10px;
    }
    p,div{
      span {
        display: inline-block;
        font-size: 15px;
        text-align: right;

        i{
          color: red;
          font-weight: 700;
          font-style: normal;
        }
      }
    }
    .ivu-input-wrapper.ivu-input-type{
      width: 70%;
    }
  }
</style>
<style lang="less">
.test .ivu-table-wrapper .ivu-table.ivu-table-border .ivu-table-body .ivu-table-tbody .ivu-table-row {
.ivu-table-cell{
  padding:0
}
}
</style>

