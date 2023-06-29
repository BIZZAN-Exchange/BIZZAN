<template>
<div>
  <Card>
    <p slot="title">
      {{ $t('addactivity.activity') }} </p>
    <div class="formWrapper">
      <Form :model="activityForm" :label-width="150" class='form'>
          <FormItem :label="$t('addactivity.activitytitle')">
              <Input v-model="activityForm.title"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.activityintroduction')">
              <Input v-model="activityForm.detail"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.displaystatus')">
                  <RadioGroup v-model="activityForm.status">
                    <Radio label="0"><em>{{ $t('addactivity.hide') }}</em></Radio>
                    <Radio label="1"><em>{{ $t('addactivity.display') }}</em></Radio>
                  </RadioGroup>
          </FormItem>
          <FormItem :label="$t('addactivity.activitystatus')">
                  <RadioGroup v-model="activityForm.step">
                    <Radio label="0"><em>{{ $t('addactivity.inpreparation') }}</em></Radio>
                    <Radio label="1"><em>{{ $t('addactivity.haveinhand') }}</em></Radio>
                    <Radio label="2"><em>{{ $t('addactivity.indistribution') }}</em></Radio>
                    <Radio label="3"><em>{{ $t('addactivity.ended') }}</em></Radio>
                  </RadioGroup>
          </FormItem>

          <FormItem :label="$t('addactivity.activitytype')">
                  <RadioGroup v-model="activityForm.type">
                    <Radio label="1"><em>{{ $t('addactivity.firsttimeonlinerushpurchase') }}</em></Radio>
                    <Radio label="2"><em>{{ $t('addactivity.firsttimeonlinesplitequally') }}</em></Radio>
                    <Radio label="3"><em>{{ $t('addactivity.shareofpositions') }}</em></Radio>
                    <Radio label="4"><em>{{ $t('addactivity.freesubscription') }}</em></Radio>
                    <Radio label="5"><em>{{ $t('addactivity.cloudminer') }}</em></Radio>
                    <Radio label="6"><em>{{ $t('addactivity.lockup') }}</em></Radio>
                  </RadioGroup>
          </FormItem>

          <FormItem :label="$t('addactivity.miningcycle')" v-if="activityForm.type==5">
                  <RadioGroup v-model="activityForm.miningPeriod">
                    <Radio label="0"><em>{{ $t('addactivity.day') }}</em></Radio>
                    <Radio label="1"><em>{{ $t('addactivity.week') }}</em></Radio>
                    <Radio label="2"><em>{{ $t('addactivity.month') }}</em></Radio>
                    <Radio label="3"><em>{{ $t('addactivity.year') }}</em></Radio>
                  </RadioGroup>
          </FormItem>
          <FormItem :label="$t('addactivity.miningcycles')" v-if="activityForm.type==5">
              <Input v-model="activityForm.miningDays"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.miningcycleoutput')" v-if="activityForm.type==5">
              <Input v-model="activityForm.miningDaysprofit"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.miningcurrency')" v-if="activityForm.type==5">
              <Input v-model="activityForm.miningUnit"></Input>
          </FormItem>


          <FormItem :label="$t('addactivity.lockcurrency')" v-if="activityForm.type==6">
              <Input v-model="activityForm.lockedUnit"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.locktype')" v-if="activityForm.type == 6">
                  <RadioGroup v-model="activityForm.releaseType">
                    <Radio label="0"><em>{{ $t('addactivity.equalrelease') }}</em></Radio>
                    <Radio label="1"><em>{{ $t('addactivity.proportionalrelease') }}</em></Radio>
                  </RadioGroup><br>
                  <span style="font-size:10px;color:#FF0000;">{{ $t('addactivity.mean') }}</span>
          </FormItem>

          <FormItem :label="$t('addactivity.cyclereleaseratio')" v-if="activityForm.type == 6 && activityForm.releaseType == 1">
              <Input v-model="activityForm.releasePercent"></Input>
              <span style="font-size:10px;color:#FF0000;">{{ $t('addactivity.rate') }}</span>
          </FormItem>

          <FormItem :label="$t('addactivity.periodicreleaselimit')" v-if="activityForm.type == 6 && activityForm.releaseType == 0">
              <Input v-model="activityForm.releaseAmount"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.releasecycle')" v-if="activityForm.type == 6">
                  <RadioGroup v-model="activityForm.lockedPeriod">
                    <Radio label="0"><em>{{ $t('addactivity.day') }}</em></Radio>
                    <Radio label="1"><em>{{ $t('addactivity.week') }}</em></Radio>
                    <Radio label="2"><em>{{ $t('addactivity.month') }}</em></Radio>
                    <Radio label="3"><em>{{ $t('addactivity.year') }}</em></Radio>
                  </RadioGroup>
          </FormItem>

          <FormItem :label="$t('addactivity.totallockingcycles')" v-if="activityForm.type == 6">
              <Input v-model="activityForm.lockedDays"></Input>
              <span style="font-size:10px;color:#FF0000;">{{ $t('addactivity.rate1') }}</span>
          </FormItem>

          <FormItem :label="$t('addactivity.participationthreshold')" v-if="activityForm.type == 6">
              <Input v-model="activityForm.lockedFee"></Input>
              <span style="font-size:10px;color:#FF0000;">{{ $t('addactivity.rate2') }}</span>
          </FormItem>

          <FormItem :label="$t('addactivity.releasemultiple')" v-if="activityForm.type == 6">
              <Input v-model="activityForm.releaseTimes"></Input>
              <span style="font-size:10px;color:#FF0000;">{{ $t('addactivity.rate3') }}</span>
          </FormItem>

          <FormItem :label="$t('addactivity.rate4')" v-if="activityForm.type==5 || activityForm.type == 6">
              <Input v-model="activityForm.miningInvite" :placeholder="$t('addactivity.rate5')"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.rate6')" v-if="activityForm.type==5 || activityForm.type == 6">
              <Input v-model="activityForm.miningInvitelimit" :placeholder="$t('addactivity.rate7')"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.rate7')">
              <Input v-model="activityForm.leveloneCount" :placeholder="$t('addactivity.rate9')"></Input>
              <span style="font-size:10px;color:#FF0000;">{{ $t('addactivity.rate10') }}</span>
          </FormItem>

          <FormItem :label="$t('addactivity.startandendtime')">
              <DatePicker v-model="activityForm.startTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;~&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <DatePicker v-model="activityForm.endTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
          </FormItem>

          <FormItem :label="$t('addactivity.totalsupply')">
              <Input v-model="activityForm.totalSupply"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.issueprice')">
              <Input v-model="activityForm.price"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.priceaccuracy')">
              <Input v-model="activityForm.priceScale"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.currencyunit')">
              <Input v-model="activityForm.unit"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.acceptedcurrency')">
              <Input v-model="activityForm.acceptUnit"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.quantityaccuracy')">
              <Input v-model="activityForm.amountScale"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.maximumpurchaselimit')">
              <Input v-model="activityForm.maxLimitAmout"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.minimumpurchase')">
              <Input v-model="activityForm.minLimitAmout"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.numberofpurchaserestrictions')">
              <Input v-model="activityForm.limitTimes"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.currencyrequiredforposition')">
              <Input v-model="activityForm.holdUnit"></Input>
              <span style="font-size:10px;color:#FF0000;">{{ $t('addactivity.rate11') }}</span>
          </FormItem>
          <FormItem :label="$t('addactivity.minimumrequirementsforpositions')">
              <Input v-model="activityForm.holdLimit"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.activitylink')">
              <Input v-model="activityForm.activityLink"></Input>
          </FormItem>
          <FormItem :label="$t('addactivity.announcementlink')">
              <Input v-model="activityForm.noticeLink"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.configurejson')">
              <Input v-model="activityForm.settings" type="textarea" :rows="5"></Input>
          </FormItem>

          <FormItem :label="$t('addactivity.listthumbnail')">
            <Upload :action="basicUrl+'admin/common/upload/oss/image'"
                    :on-success = 'uploadSuccessedSmall'
                    :on-error = "uploadFailed"
                    :on-progress = "imageUploading"
                    :show-upload-list = "false">
              <Button type="ghost" icon="ios-cloud-upload-outline">{{ $t('addactivity.addpicture') }}</Button>
            </Upload>
          </FormItem>

          <FormItem :label="$t('addactivity.addressoflistdiagram')">
              <Input @on-blur="listenValUrl" v-model="activityForm.smallImageUrl" disabled style="width: 100%;"></Input>
              <span v-if="picUrlIcon">
                  <Icon style="color: green" type="checkmark-round" v-if="!!activityForm.smallImageUrl"></Icon>
                  <Icon type="close-round" v-else></Icon>
              </span>
          </FormItem>

          <FormItem :label="$t('addactivity.bannerpicture')">
            <Upload :action="basicUrl+'admin/common/upload/oss/image'"
                    :on-success = 'uploadSuccessedBanner'
                    :on-error = "uploadFailed"
                    :on-progress = "imageUploading"
                    :show-upload-list = "false">
              <Button type="ghost" icon="ios-cloud-upload-outline">{{ $t('addactivity.addpicture') }}</Button>
            </Upload>
          </FormItem>

          <FormItem :label="$t('addactivity.bannermapaddress')">
              <Input @on-blur="listenValUrl" v-model="activityForm.bannerImageUrl" disabled style="width: 100%;"></Input>
              <span v-if="picUrlIcon">
                  <Icon style="color: green" type="checkmark-round" v-if="!!activityForm.bannerImageUrl"></Icon>
                  <Icon type="close-round" v-else></Icon>
              </span>
          </FormItem>

          <FormItem :label="$t('addactivity.activitydetails')">
              <smeditor :config='config' ref="smeditor" @isUploading = "ifUploading" style="width:100%;"></smeditor>
          </FormItem>
      </Form>
      <div class="circleWrapper" v-show="ifShowPercentCircle">
        <i-circle :percent="percentage" :size="60" :stroke-width="10">
          <span class="demo-Circle-inner" style="font-size:24px">{{ percentage.toFixed(1) }}%</span>
        </i-circle>
      </div>
    </div>
    <div class="btnWrapper">
      <Button type="success" :disabled="false" long size='large' @click="submit">
        {{ $t('addactivity.submit') }} </Button>
    </div>
  </Card>

    <Modal
          class="auditModel"
          v-model="loginPassModal"
          :title="$t('addactivity.pleaseentertheloginpassword')"
          width="350"
          @on-cancle="loginPW = ''"
          @on-ok="confirmLoginPass">
          <Input v-model="loginPW" type="password" :placeholder="$t('addactivity.pleaseentertheloginpassword')"></Input>
   </Modal>
</div>

</template>

<script>
import dtime from 'time-formater'
import smeditor from '@/SMEditor/SMEditor.vue'

import { BASICURL,  addActivity, modifyActivity, activityDetail } from '@/service/getData';
import { getStore, removeStore, setStore } from '@/config/storage';


  export default {

    data () {
      return {
        activityForm:{
            title: "",
            detail: "",
            status: "1",
            step: "1",
            type: "1",
            startTime: "",
            endTime: "",
            totalSupply: "0",
            price: "",
            priceScale: "2",
            unit: "",
            acceptUnit: "",
            amountScale: "2",
            maxLimitAmout: 0,
            minLimitAmout: 0,
            limitTimes: 0,
            activityLink: "",
            noticeLink: "",
            settings: "",
            smallImageUrl: "",
            bannerImageUrl: "",
            leveloneCount: 0,
            holdUnit: "",
            holdLimit: 0,
            lockedUnit: "",
            lockedPeriod: "0",
            lockedDays: 0,
            releaseType: "0",
            releasePercent: 0,
            lockedFee: 0,
            releaseAmount: 0,
            releaseTimes: 0,
            miningDays: 0,
            miningDaysprofit: "",
            miningUnit: "",
            miningInvite: 0,
            miningInvitelimit: 0,
            miningPeriod: "0",
            content: ""
        },
        loginPassModal: false,
        loginPW: "",
        ifShowPercentCircle: false,
        percentage: 0,
        picUrl: "",
        picUrlIcon: false,
        basicUrl: BASICURL,
        uploading: false,
        ifAdd: true,
        queryDetailId: null,
        lang: "CN",
        config :{
          uploadUrl: `${BASICURL}admin/common/upload/oss/image`,
          uploadName: 'file',
          parentName: 'activity',
          uploadParams: {

          },
          // 上传成功回调
          uploadCallback: (data) => {
            this.uploading = false;
            if(!data.code){
              this.$Message.success(this.$t('addactivity.uploadsuccessful'));
              return data.data;
            }else{
              this.$Message.error(this.$t('addactivity.uploadfailed'));
            }
          },
          // 上传失败回调, 可选
          uploadFailed: (err) => {
            this.uploading = false;
            console.log(err)
            this.$Message.error(this.$t('addactivity.uploadfailed'));
          }
        }
      }
    },
    methods: {
      confirmLoginPass(){
        console.log("passwd");
        if(!this.ifAdd){
          this.activityForm.id = this.queryDetailId;
          this.activityForm.password = this.loginPW;
          this.activityForm.startTime = dtime(this.activityForm.startTime).format('YYYY-MM-DD HH:mm:ss');
          this.activityForm.endTime = dtime(this.activityForm.endTime).format('YYYY-MM-DD HH:mm:ss');
          modifyActivity(this.activityForm)
          .then( res => {
            if (!res.code) {
              this.$Message.success(this.$t('addactivity.operationsuccessful'));
              this.$router.push('/activity/activity');
            } else this.$Message.error(this.$t('addactivity.abnormalerror'));
          });
        }else{
          this.activityForm.startTime = dtime(this.activityForm.startTime).format('YYYY-MM-DD HH:mm:ss');
          this.activityForm.endTime = dtime(this.activityForm.endTime).format('YYYY-MM-DD HH:mm:ss');
          addActivity(this.activityForm)
          .then( res => {
            if (!res.code) {
              this.$Message.success(this.$t('addactivity.operationsuccessful'));
              this.$router.push('/activity/activity');
            } else this.$Message.error(this.$t('addactivity.abnormalerror'));
          });
        }
      },
      submit(){
        this.$refs.smeditor.$emit('saveInner');
        this.activityForm.content = getStore('smeditor');

        this.loginPassModal = true;
      },
      ifUploading(val) {
        this.uploading = val;
      },
      changeAdStatus(val) {
        this.status = val*1;
      },
      listenValUrl() {
          this.picUrlIcon = true;
      },
      imageUploading(event, file, fileList) {
          this.ifShowPercentCircle = true;
          this.percentage = file.percentage;
      },
      uploadSuccessedSmall(response, file, fileList) {
          this.activityForm.smallImageUrl = response.data;
          this.ifShowPercentCircle = false;
          this.$Message.success(this.$t('addactivity.uploadsuccessful'));
      },
      uploadSuccessedBanner(response, file, fileList) {
          this.activityForm.bannerImageUrl = response.data;
          this.ifShowPercentCircle = false;
          this.$Message.success(this.$t('addactivity.uploadsuccessful'));
      },
      uploadFailed(error, file, fileList) {
          this.ifShowPercentCircle = false;
          this.$Message.error(this.$t('addactivity.uploadfailed'));
      }
    },
    created() {

      removeStore('smeditor');

      this.queryDetailId = getStore('manageID');

      if (!!this.queryDetailId) {

        this.ifAdd = false;
        this.activityForm.id = this.queryDetailId;

        activityDetail(this.queryDetailId)
       .then(res => {
            this.activityForm.id = this.queryDetailId;
            this.activityForm.title = res.data.title;
            this.activityForm.detail = res.data.detail;
            this.activityForm.status = String(res.data.status);
            this.activityForm.step = String(res.data.step);
            this.activityForm.type = String(res.data.type);
            this.activityForm.startTime = res.data.startTime;
            this.activityForm.endTime = res.data.endTime;
            this.activityForm.totalSupply = res.data.totalSupply;
            this.activityForm.price = res.data.price;
            this.activityForm.priceScale = res.data.priceScale;
            this.activityForm.unit = res.data.unit;
            this.activityForm.acceptUnit = res.data.acceptUnit;
            this.activityForm.amountScale = res.data.amountScale;
            this.activityForm.maxLimitAmout = res.data.maxLimitAmout;
            this.activityForm.minLimitAmout = res.data.minLimitAmout;
            this.activityForm.limitTimes = res.data.limitTimes;
            this.activityForm.settings = res.data.settings;
            this.activityForm.content = res.data.content;
            this.activityForm.smallImageUrl = res.data.smallImageUrl;
            this.activityForm.bannerImageUrl = res.data.bannerImageUrl;
            this.activityForm.noticeLink = res.data.noticeLink;
            this.activityForm.activityLink = res.data.activityLink;
            this.activityForm.leveloneCount = res.data.leveloneCount;
            this.activityForm.holdUnit = res.data.holdUnit;
            this.activityForm.holdLimit = res.data.holdLimit;
            this.activityForm.miningDays = res.data.miningDays;
            this.activityForm.miningDaysprofit = res.data.miningDaysprofit;
            this.activityForm.miningUnit = res.data.miningUnit;
            this.activityForm.miningInvite = res.data.miningInvite;
            this.activityForm.miningInvitelimit = res.data.miningInvitelimit;
            this.activityForm.miningPeriod = String(res.data.miningPeriod);
            this.activityForm.lockedUnit = res.data.lockedUnit;
            this.activityForm.lockedPeriod = String(res.data.lockedPeriod);
            this.activityForm.lockedDays = res.data.lockedDays;
            this.activityForm.releaseType = String(res.data.releaseType);
            this.activityForm.releasePercent = res.data.releasePercent;
            this.activityForm.lockedFee = res.data.lockedFee;
            this.activityForm.releaseAmount = res.data.releaseAmount;
            this.activityForm.releaseTimes = res.data.releaseTimes;

            setStore('smeditor', res.data.content);

       });
      }else  this.ifAdd = true;
    },

    components: {
      smeditor
    }
  }
</script>

<style lang="less" scoped>
  .wrapper{
    margin: auto;
    width: 70%;
  }
  .formWrapper{
    margin: auto;
    width: 70%;
  }
  .btnWrapper{
    margin: 40px auto;
    width: 30%;
  }
  .formWrapper{
    p, div{
      margin: 0px 0 5px 0;
      Input{
        display: inline-block;
        width: 90px;
      }
    }
  }
  .title{
    display: inline-block;
    width: 200px;
  }
  .circleWrapper{
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .3);
    p{
      font-size: 30px;
      font-family: '黑体';
      color: #fff;
      text-align: center;
      line-height: 550px;
    }
  }

  em{
    font-style: normal;
  }
</style>

