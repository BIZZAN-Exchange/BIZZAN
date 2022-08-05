<template>
<div>
  <Card>
    <p slot="title">
      {{ $t('systemhelp.systemhelp') }} </p>
    <div class="formWrapper">
      <div class="titleWrapper">{{ $t('editannouncement.title') }}
        <p class="title"><Input v-model="title"  style="width:500px"></Input></p>
      </div>

      <div class="createTimeWrapper" v-if="!!queryDetailId" >
        {{ $t('advertisingmanagement.createdon') }} <p class="title"><Input v-model="createTime" disabled></Input></p>
      </div>

      <div>{{ $t('editannouncement.classification') }}
        <Select v-model="klass" style="width:200px">
          <Option v-for=" perKlass in klassArr " :value="perKlass.value" :key="perKlass.value">{{ perKlass.name }}</Option>
        </Select>
      </div>
      <div class="titleWrapper">{{ $t('perpetualcontractcurrencystandardmanagement.sort') }}
        <p class="title"><Input v-model="sort"  style="width:200px"></Input></p>
      </div>
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
      <div>{{ $t('currencywithdrawalauditmanagement.status') }}
        <RadioGroup v-model="status" @on-change="changeAdStatus">
          <Radio :label="0">
            <span>{{ $t('perpetualcontractcurrencystandardmanagement.display') }}</span>
          </Radio>
          <Radio :label="1">
            <span>{{ $t('announcementmanagement.dontshow') }}</span>
          </Radio>
        </RadioGroup>
      </div>
    </div>
    <smeditor :config='config' ref="smeditor" @isUploading = "ifUploading"></smeditor>

    <div class="btnWrapper">
      <Button type="success" long size='large' @click="upload">
        {{ $t('addeditredenvelopes.submit') }} </Button>
    </div>

    <div class="circleWrapper" v-show="uploading">
      <p>{{ $t('redpacketmanagement.pictureuploading') }}</p>
    </div>
  </Card>

</div>

</template>

<script>

import smeditor from '@/SMEditor/SMEditor.vue'


import { BASICURL,  addHelpManage, helpManageDetail, updateHelpManage } from '@/service/getData';
import { getStore, removeStore, setStore } from '@/config/storage';


  export default {

    data () {
      return {
        klassArr: [
          { name: this.$t('systemhelp.beginnersguide'), value: 0 },
          { name: this.$t('systemhelp.frequentlyaskedquestions'), value: 1 },
          { name: this.$t('systemhelp.transactionguide'), value: 2 },
          { name: this.$t('systemhelp.currencydata'), value: 3 },
          { name: this.$t('systemhelp.markettechnology'), value: 4 },
          { name: this.$t('systemhelp.termagreement'), value: 5 },
          { name: this.$t('editannouncement.other'), value: 6 },
        ],
        uploading: false,
        ifAdd: true,
        queryDetailId: null,
        createTime: null,
        title: null,
        klass: null,
        status: 0,
        lang: "zh_CN",
        sort: null,
        basicUrl: BASICURL,
        config :{
          uploadUrl: `${BASICURL}admin/common/upload/oss/image`,
          uploadName: 'file',
          parentName: 'helpManage',
          uploadParams: {

          },

          uploadCallback: (data) => {
            this.uploading = false;
            if(!data.code){
              this.$Message.success(this.$t('addeditredenvelopes.uploadsuccessful'));
              return data.data;
            }else{
              this.$Message.error(this.$t('addeditredenvelopes.uploadfailed'));
            }
          },
          uploadFailed: (err) => {
            console.log(err)
            this.uploading = false;
            this.$Message.error(this.$t('addeditredenvelopes.uploadfailed'));

          }
        }
      }
    },
    methods: {
      ifUploading(val) {
        this.uploading = val;
      },
      changeAdStatus() {

      },
      upload() {
        this.$refs.smeditor.$emit('saveInner');

        let uploadObj = {
          title: this.title,
          sysHelpClassification: this.klass,
          status: this.status,
          lang: this.lang,
          sort: this.sort,
          content: getStore('smeditor')
        };

        let fn = null;
        if(this.ifAdd)  fn = addHelpManage;
        else {
					uploadObj.id = this.queryDetailId;
					uploadObj.createTime = this.createTime;
          fn = updateHelpManage;
        }

        if ( this.title ==='' || this.title ===null ||
            this.klass ==='' || this.klass ===null ||
            getStore('smeditor') ==='' || getStore('smeditor') === null ) {
              this.$Message.warning(this.$t('systeminformationmaintenance.pleaseimprovetheinformation'))
        } else {
          fn(uploadObj)
          .then( res => {
            if(!res.code) {
              this.$Message.success(this.$t('advertisingmanagement.operationsucceeded'));
              this.$router.push('/content/helpManage');
              removeStore('smeditor');
            }  else this.$Message.error(this.$t('addeditredenvelopes.abnormalerror'));
          })
        }
      }
    },
    created() {
      removeStore('smeditor');
      this.queryDetailId = getStore('manageID');

      if (!!this.queryDetailId) {
        this.ifAdd = false;

        helpManageDetail({ id: this.queryDetailId })
       .then(res => {
         console.log(res);
         this.createTime = res.data.createTime;
         this.title = res.data.title;
         this.klass = res.data.sysHelpClassification;
         this.status = res.data.status;
         this.lang = res.data.lang;
         this.sort = res.data.sort;
          setStore('smeditor', res.data.content);

       })

      } else this.ifAdd = true;
      // this.textareaInner = getStore('smeditor');
    },

    components: {
      smeditor
    },


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
    margin: 20px auto;
    width: 30%;

  }
  .formWrapper{
    p, div{
      margin: 5px 0;
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
</style>

