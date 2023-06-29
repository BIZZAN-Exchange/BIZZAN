<template>
 	<div>
    <Card>
      <p slot="title">
        {{ $t('advertisingmanagement.advertisingmanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>{{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
        </Button>
      </p>

      <Row class="manageRow clearfix">
        <div class="manageWrapper">
          <Button type="error" @click="delAd">{{ $t('secondcontractcompensationsetting.delete') }}</Button>
          <Button type="info" @click="addAd">{{ $t('advertisingmanagement.addto') }}</Button>
        </div>
      </Row>

    <Table border
          :columns="columns_first"
          :data="advertisementArr"
          :loading="ifLoading"
          @on-selection-change="select">
    </Table>

    <Row class="buttomPageContain">
			<Page
        :total="totalNum"
        class="buttomPage"
        :current="currentPageIdx"
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
        <p>{{$t('advertisingmanagement.note4',[delArr.length])}}</p>
    </Modal>

		<!-- 添加广告 -->
		<Modal
			v-model="addModal"
			:title="$t('advertisingmanagement.addads')"
			width="430">

			<p slot="header" style="color:#5cadff;text-align:center">
				<Icon type="information-circled"></Icon>
				<span style="padding-left: 5px;" v-if="addAdStyle" >{{ $t('advertisingmanagement.addads') }}</span>
				<span style="padding-left: 5px;" v-if="!addAdStyle" >{{ $t('advertisingmanagement.viewadvertisements') }}</span>
			</p>
			<div class="addAdWrapper">
        <div v-if="!addAdStyle"><i>{{ $t('common.mul') }}</i> {{ $t('advertisingmanagement.createdon') }}
          <Input v-model="adCreateTime" disabled/>
        </div>

        <p v-if="!addAdStyle"><i>*</i> {{ $t('advertisingmanagement.adno') }}
          <Input v-model="adSerialNumber" disabled></Input>
        </p>

        <p><i>*</i> {{ $t('advertisingmanagement.advertisementsorting') }}
          <InputNumber :max="100" :min="1" v-model="sort"></InputNumber>
        </p>

				<p><i>*</i> {{ $t('advertisingmanagement.advertisementname') }}
          <Input @on-blur="listenValName" v-model="adName"></Input>
          <span v-if="adNameIcon">
            <Icon style="color: green" type="checkmark-round" v-if="!!adName"></Icon>
            <Icon type="close-round" v-else></Icon>
          </span>
        </p>

        <div><i>*</i> {{ $t('advertisingmanagement.advertisingstatus') }}
          <RadioGroup v-model="adStatus">
            <Radio label="0">
              <span>{{ $t('advertisingmanagement.goonline') }}</span>
            </Radio>
            <Radio label="1">
              <span>{{ $t('advertisingmanagement.offline') }}</span>
            </Radio>
          </RadioGroup>
				</div>

				<div> <i>*</i> {{ $t('advertisingmanagement.advertisinglocation') }}
					<Select v-model="adPosModel" style="width:200px">
						<Option v-for=" item in adPosition" :value="item.status" :key="item.status">
							{{ item.posName }}
						</Option>
					</Select>
				</div>

        <div><i>*</i> {{ $t('advertisingmanagement.advertisinglanguage') }}
          <RadioGroup v-model="adLang">
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

				<div><i>*</i> {{ $t('advertisingmanagement.starttime') }}
					<DatePicker v-model="adStartTime" type="date" format="yyyy-MM-dd HH:mm:ss" :placeholder="$t('advertisingmanagement.pleaseselectatime')" style="width: 200px"></DatePicker>
        </div>
				<div><i>*</i> {{ $t('advertisingmanagement.endtime') }}
					<DatePicker v-model="adEndTime" type="date" format="yyyy-MM-dd HH:mm:ss" :placeholder="$t('advertisingmanagement.pleaseselectatime')" style="width: 200px"></DatePicker>
					</div>

        <div><i>*</i> {{ $t('advertisingmanagement.advertisingpictures1') }}
          <!-- <Upload :action="basicUrl+'admin/common/upload/local/image'" -->
          <Upload :action="basicUrl+'admin/common/upload/oss/image'"
                  :on-success = 'uploadSuccessed'
                  :on-error = "uploadFailed"
                  :on-progress = "uploading"
                  :show-upload-list = "false">
            <Button type="ghost" icon="ios-cloud-upload-outline">{{ $t('advertisingmanagement.addpicture') }}</Button>
          </Upload>
				</div>

				<p><i>*</i> {{ $t('advertisingmanagement.pictureaddress') }}
          <Input @on-blur="listenValUrl" v-model="picUrl" disabled></Input>
          <span v-if="picUrlIcon">
            <Icon style="color: green" type="checkmark-round" v-if="!!picUrl"></Icon>
            <Icon type="close-round" v-else></Icon>
          </span>
        </p>

        <p><i>*</i> {{ $t('advertisingmanagement.jumpaddress') }}
          <Input @on-blur="bannerUrlFn" v-model="linkUrl"></Input>
          <span v-if="listenBannerUrl">
            <Icon style="color: green" type="checkmark-round" v-if="!!bannerUrlIcon"></Icon>
            <Icon type="close-round" v-else></Icon>
          </span>
        </p>

				<p><i>*</i> {{ $t('advertisingmanagement.advertisingremarks') }} <Input v-model="adRemark" type="textarea" :rows="4" :placeholder="$t('advertisingmanagement.addadnotes')"></Input></p>
		  	</div>

        <div slot="footer" style="color:#5cadff;text-align:right">
          <div v-if="addAdStyle">
            <Button type="text" @click="cancelAddAd" >{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
            <Button type="info" @click="confrimAddAd(true)" >{{ $t('advertisingmanagement.confirm') }}</Button>
          </div>
          <Button type="info" @click="confrimAddAd(false)" v-if="!addAdStyle">{{ $t('helpmanage.toupdate') }}</Button>
        </div>

        <div class="circleWrapper" v-show="ifShowPercentCircle">
          <i-circle :percent="percentage" :size="60" :stroke-width="10">
            <span class="demo-Circle-inner" style="font-size:24px">{{ percentage.toFixed(1) }}%</span>
          </i-circle>
        </div>
	  	</Modal>
   </Card>

  </div>
</template>
<script>

import dtime from 'time-formater';
import { stickTopAdv, manageAd, createAd, deleteAd, adDetail, updateAd, uploadPic, BASICURL } from '@/service/getData';
import { getStore } from '@/config/storage';

export default {
	data () {
		return {
      currentPageIdx: 1,
      linkUrl: null,
      bannerUrlIcon: false,
      listenBannerUrl: false,
      sort: 1,
      ifDelete: false,
      ifShowPercentCircle: false,
      percentage: 0,
      basicUrl: BASICURL,
      ifLoading: true,
      adSerialNumber: null,
			adName: null,
			adPosModel: null,
			picUrl: null,
			adStartTime: '',
      advertisementArr: [],
      adRemark: null,
      adStatus: 0,
      adLang:"zh_CN",
      adStatusArr: [
				{status: 0, statusName: this.$t('advertisingmanagement.goonline')},
				{status: 1, statusName: this.$t('advertisingmanagement.offline')}
			],
			adEndTime: '',
			addModal: false,
			adPosition: [
				{status: 0, posName: this.$t('advertisingmanagement.approtation')},
				{status: 1, posName: this.$t('advertisingmanagement.pcrotation')},
				{status: 2, posName: this.$t('advertisingmanagement.pcclassification')}
      ],
      showMrakContain: false,
      showMrak: false,
      serialNumberIcon: false,
      adNameIcon: false,
      picUrlIcon: false,
      delArr: [],
      adCreateTime: null,
      addAdStyle: true,
      totalNum: null,
			columns_first: [
					{
						type: 'selection',
						width: 60
					},
					{
						title: this.$t('advertisingmanagement.adname'),
						key: 'name'
					},
					{
						title: this.$t('advertisingmanagement.adlocation'),
						width: 90,
						render: (h, params) => {
							let sysAdLoc = params.row.sysAdvertiseLocation;
							let sysAdLocName = String;

							if(sysAdLoc === 0) sysAdLocName = this.$t('advertisingmanagement.approtation');
							else if(sysAdLoc === 1) sysAdLocName = this.$t('advertisingmanagement.pcrotation');
							else if(sysAdLoc === 2) sysAdLocName = this.$t('advertisingmanagement.pcclassification');

							return h('span', {
							},sysAdLocName);
						}
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
						title: this.$t('advertisingmanagement.advertisingpictures'),
						key: 'url',
            render:(h, obj) => {
              let url = obj.row.url;
              return  h('img',{
                attrs: {src: url},
                style: {height: "45px", padding:"5px 0px"}
              },"");
            }
          },
          {
						title: this.$t('advertisingmanagement.jumplink'),
						key: 'linkUrl',
            render:(h, obj) => {
              let link = obj.row.linkUrl;
              if(link == "" || link ==undefined){
                return "";
              }else{
                return  h('a',{
                  attrs: {href: link, target:"_blank"},
                  style: {}
                },this.$t('advertisingmanagement.jumplink'));
              }

            }
					},
					{
						title: this.$t('advertisingmanagement.time'),
						width: 220,
						render: (h, obj) => {
							let startTime = obj.row.startTime;
							let endTime = obj.row.endTime;
							return h('div', [
                h('p',{
                },`${startTime}`),
                h('p',{
                },`~`),
                h('p',{
                },`${endTime}`),
              ])
						}
					},
					{
						title: this.$t('advertisingmanagement.onlineoffline'),
						width: 90,
						render: (h, obj) => {
							let status=obj.row.status;
							let statuStr = String;

							if(status === 0) statuStr = this.$t('advertisingmanagement.goonline');
							else if(status === 1) statuStr = this.$t('advertisingmanagement.offline');

							return h('span', {
							}, statuStr);

						}
					},
					{
						title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
						width:180,
						render: (h, obj) => {
              return h ('div',[
                h('Button', {
                  props: {
                    type: 'info',
                    size: 'small'
                  },
                  style: {
                    marginRight: '10px'
                  },
                  on: {
                    click: () => {
                      adDetail({ serialNumber: obj.row.serialNumber })
                      .then( res => {
                        if (!res.code) {
                        this.addModal = true;
                        this.addAdStyle = false;
                        this.adName = res.data.name;
                        this.adRemark = res.data.remark;
                        this.adSerialNumber = res.data.serialNumber;
                        this.adStartTime = res.data.startTime;
                        this.adEndTime = res.data.endTime;
                        this.sort = res.data.sort;
                        this.adStatus = res.data.status.toString();
                        this.adPosModel = res.data.sysAdvertiseLocation;
                        this.picUrl =   res.data.url;
                        this.adCreateTime = res.data.startTime;
                        this.linkUrl = res.data.linkUrl;
                        this.adLang = res.data.lang;
                        }
                      })
                    }
                  }
								},this.$t('announcementmanagement.viewedit')),
              ])
            }
					}
			],
		}
	},
	methods: {
    refreshPageManual() {

    },
    bannerUrlFn() {
      this.listenBannerUrl = true;
      if(!this.linkUrl) this.bannerUrlIcon = false;
      else this.bannerUrlIcon = true;
    },
    addAdvertisement() {
      alert()
    },
    confirmDel() {
      deleteAd({ ids: this.delArr })
      .then(res => {
        if (!res.code) {
          this.delArr = [];
          this.$Message.success(this.$t('advertisingmanagement.advertisementdeletedsuccessfully'));
          this.refreshTab();
        }else {
          this.$Message.error(this.$t('advertisingmanagement.advertisementdeletionfailed'));
        }
      })

    },
    cancelDel () {
      this.$Message.success(this.$t('advertisingmanagement.cancelled'));
    },
    uploading(event, file, fileList) {
      this.ifShowPercentCircle = true;
      this.percentage = file.percentage;
    },
    uploadSuccessed(response, file, fileList) {
      this.picUrl = response.data;
      this.ifShowPercentCircle = false;
      this.$Message.success(this.$t('advertisingmanagement.uploadsuccessful'));
    },
    uploadFailed(error, file, fileList) {
      this.ifShowPercentCircle = false;
      this.$Message.error(this.$t('advertisingmanagement.uploadfailed'));
    },
    // changeAdStatus() {
    //   // alert(this.adStatus);
    // },
    refreshPageManual() {
      this.ifLoading = true;
      this.refreshTab({ pageNo: this.currentPageIdx, pageSize: 10 });
    },
    changePage(pageIndex){
      this.ifLoading = true;
      this.currentPageIdx = pageIndex;
      this.refreshTab({ pageNo: pageIndex, pageSize: 10 });
      // alert(pageIndex)
    },
    listenValName() {
      this.adNameIcon = true;
    },
    listenValUrl() {
      this.picUrlIcon = true;
    },
		addAd() {
     this.addAdStyle = this.addModal = true;
      this.adName = null;
      this.adStatus = 0;
      this.adPosModel = null;
      this.adStartTime = null;
      this.adEndTime = null;
      this.adRemark = null;
      this.picUrl =   null;
      this.linkUrl =   null;
      this.sort = null;
      this.adLang = "zh_CN";
    },
    select(selection) {
      this.delArr = [];
      selection.forEach( item => {
        this.delArr.push(item.serialNumber);
      })
    },
    delAd() {
      if(!this.delArr.length) {
        this.$Message.warning(this.$t('advertisingmanagement.note2'));
      }else this.ifDelete = true;
    },
    // 添加广告
		confrimAddAd (val) {
      let judgeSTime =  String(this.adStartTime).trim().length===0;
      let judgeETime = String(this.adEndTime).trim().length===0;
      let judgeEmpty = !this.adName || judgeSTime || judgeETime ||!this.picUrl;
      if(judgeEmpty) {
        this.$Message.warning(this.$t('systeminformationmaintenance.pleaseimprovetheinformation'));
      } else {
        let adObj = {
          name: this.adName,
          sysAdvertiseLocation: this.adPosModel,
          startTime: dtime(this.adStartTime).format('YYYY-MM-DD HH:mm:ss'),
          endTime: dtime(this.adEndTime).format('YYYY-MM-DD HH:mm:ss'),
          url: this.picUrl,
          remark: this.adRemark,
          sort:this.sort,
          status: this.adStatus,
          linkUrl: this.linkUrl,
          lang: this.adLang
        }

        let key = '';
        let pass = true;
        console.log(adObj);
        for ( key in adObj ) {
          if(key!=='remark' && key !== "linkUrl") {
            if((!String(adObj[key])) || (!String(adObj[key]).length)){
              pass = false;
            }
          }
        }
        this.addModal = false;
        let fn = null;
        if(val) fn = createAd;
        else {
          adObj.serialNumber = this.adSerialNumber;
          fn = updateAd;
        }

        if (pass) {
          fn(adObj).then( res => {
            if (!res.code) {
              this.$Message.success(this.$t('advertisingmanagement.operationsucceeded'));
            }else {
              this.$Message.error(res.message);
            }
            this.refreshTab();
          })
        }else {
          this.$Message.error(this.$t('systeminformationmaintenance.pleaseimprovetheinformation'));
        }
      }

		},
		cancelAddAd () {
			this.addModal = false;
    },
    refreshTab(obj) {
      manageAd(obj)
      .then( res => {
        if (!res.code) {
          this.advertisementArr = res.data.content;
          this.totalNum = res.data.totalElements;
          this.ifLoading = false;
        }else this.$Message.error(res.message);
      })
    }
	},
	created(){
    this.refreshTab();
  }
}
</script>

<style scoped>
	.manageRow{
		height: 45px;
	}
	.searchContain{
		display: inline-block;
	}
	.searchContain .ivu-input-wrapper {
		width: 150px;
	}
	.searchContain span{
    vertical-align: middle;
		font-size: 15px;
	}
	.searchContain i{
		vertical-align: middle;
	}
	.manageWrapper{
		float: right;
	}
	.manageWrapper Button {
		margin-right: 10px;
	}
	.addAdWrapper{
		font-size: 15px;
	}
	.addAdWrapper>p,.addAdWrapper>div{
		margin-bottom: 10px;
	}
	.addAdWrapper .ivu-input-wrapper.ivu-input-type{
		width: 70%;
	}
  .addAdWrapper i{
    color: red;
    font-size: 15px;
    font-weight: 700;
    font-style: normal;
  }
  .buttomPageContain{
    margin: 25px;
    text-align: right;
  }
  .ivu-upload{
    display: inline-block;
  }
  .circleWrapper{
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .1);
  }
  .ivu-chart-circle{
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%)
  }
  .demo-Circle-inner{
    font-size: 15px !important;
  }
</style>
