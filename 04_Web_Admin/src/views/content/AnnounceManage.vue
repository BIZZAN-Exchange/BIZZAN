<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('announcementmanagement.announcementmanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row class="manageRow clearfix">
        <div class="manageWrapper">
          <Button type="error" @click="delOneAnnounce">{{ $t('secondcontractcompensationsetting.delete') }}</Button>
          <Button type="info" @click="addOneAnnounce">{{ $t('announcementmanagement.addto') }}</Button>
        </div>
      </Row>

      <Row>
        <Table border
              :columns="columns_first"
              :data="announcementArr"
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
        <p>{{ $t('secondcontractcompensationsetting.note1', [selectedArr.length]) }}</p>
      </Modal>


    </Card>
  </div>
</template>

<script>

import qs from 'qs';
import { stickTopAnnounce, updateHelpManage, announceManage, addAnnounce, delAnnounce,manageDown  } from '@/service/getData';
import dtime from 'time-formater'
import { setStore, removeStore } from '@/config/storage';

  export default {
    data () {
      return {
        currentPageIdx: 1,
        ifDelete: false,
        ifLoading: true,
        id: null,
        ifUpdateBtn: false,
        selectedArr: [],
        announcementArr: [],
        current: 1,
        totalNum: null,
        manageModal: false,
        manageTitle: null,
        manageClass: null,
        manageClassArr: [
          { status: 0,  klassName: this.$t('announcementmanagement.commoncategories') }
        ],
        imgUrl: null,
        createTime: null,
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
            width: 80
          },
          {
           	title: this.$t('announcementmanagement.title'),
						key: 'title',
            width: 400
          },
          {
            title: this.$t('announcementmanagement.language'),
            key: 'lang',
            render:(h, obj) => {
              let lang = obj.row.lang;
              var langText = this.$t('announcementmanagement.chinese');
				if(lang == "en_US"){
					langText = "English";
				}
				if(lang == "ja_JP"){
					langText = this.$t('announcementmanagement.japanese');
				}
				if(lang == "ko_KR"){
					langText = this.$t('announcementmanagement.korean');
				}
				if(lang == "de_DE"){
					langText = this.$t('announcementmanagement.german');
				}
				if(lang == "fr_FR"){
					langText = this.$t('announcementmanagement.french');
				}
				if(lang == "it_IT"){
					langText = this.$t('announcementmanagement.italy');
				}
				if(lang == "es_ES"){
					langText = this.$t('announcementmanagement.spain');
				}
				if(lang == "zh_HK"){
					langText = this.$t('announcementmanagement.traditionalchinese');
				}
              return h('span', {
              },langText);
            }
          },
          {
           	title: this.$t('announcementmanagement.releasetime'),
						key: 'createTime',
            width: 140
          },
           {
           	title: this.$t('perpetualcontractcurrencystandardmanagement.display'),
            render: (h, obj) => {
              let text = null;
              if (obj.row.isShow) text = this.$t('perpetualcontractcurrencystandardmanagement.yes');
              else text = this.$t('perpetualcontractcurrencystandardmanagement.no');
              return h ( 'span', {
              }, text)
            }
          },
           {
           	title: this.$t('announcementmanagement.top'),
            render: (h, obj) => {
              let text = null;
              if (obj.row.isTop==0) text = this.$t('perpetualcontractcurrencystandardmanagement.yes');
              else text = this.$t('perpetualcontractcurrencystandardmanagement.no');
              return h ( 'span', {
              }, text)
            }
          },

           {
           	title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
            // key: 'serialNumber',
            width:250,
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

                          this.$router.push('/content/announceManage/addAnnounce');

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
													stickTopAnnounce({id: obj.row.id})
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
													manageDown({id: obj.row.id})
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
        this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 })
      },
      confirmDel () {
        let delArr = [];
        this.selectedArr.forEach( item => {
          delArr.push(item.id);
        })
        let formatArr =  qs.stringify({ ids: delArr }, { arrayFormat: 'repeat' });
        // console.log(delArr);
        delAnnounce({ ids: delArr })
        .then( res => {
          if(!res.code) {
            this.refreshPage({ pageNo: 1, pageSize: 10 });
            this.$Message.success(this.$t('secondcontractcompensationsetting.deletesuccessfully'));
          }else{
            this.$Message.error(this.$t('secondcontractcompensationsetting.deletionfailed'));
          }
        })
      },
      cancelDel () {
        this.$Message.success(this.$t('secondcontractcompensationsetting.cancelled'));
      },
      delOneAnnounce() {
        if(!!this.selectedArr.length) {
          this.ifDelete = true;
        }else{
          this.$Message.warning(this.$t('secondcontractcompensationsetting.note3'));
        }
      },
      updateManage() {

        let updateObj = {
          id:  this.id,
          title: this.manageTitle,
          sysHelpClassification: this.manageClass,
          imgUrl: this.imgUrl,
          status: this.ifSHow,
          createTime: dtime(this.createTime).format('YYYY-MM-DD HH:mm:ss')
        }

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

      cancelAdd() {
        this.manageModal = false;
      },
      showModel() {
        this.manageModal = true;
      },
      addOneAnnounce() {
        this.$router.push('/content/announceManage/addAnnounce')
      },
      select(selection) {
        this.selectedArr = selection;
      },
      changePage(pageIndex) {
         this.currentPageIdx = pageIndex;
        this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
      },
      refreshPage(obj) {
        removeStore('manageID');
        announceManage(obj)
        .then( res => {
          this.ifLoading = false;
          this.announcementArr = res.data.content;
          this.totalNum = res.data.totalElements;
        })
      }
    },
    created() {
      this.refreshPage();
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

