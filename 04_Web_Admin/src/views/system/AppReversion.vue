<template>
    <div >
      <Card>
        <p slot="title">
        {{ $t('versionmanagement.versionmanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
        </p>

        <!--<Row class="functionWrapper">-->
        <!--  <div class="btnsWrapper clearfix">-->
        <!--    <Button type="primary" @click="handleForm(null,null)">添加</Button>-->
        <!--  </div>-->
        <!--</Row>-->
        <Row >
          <Table
          :columns="column_frist"
          :data="appreversion"
          :loading="ifLoading"
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
            :title="formMap[formName]"
            v-model="formVisible"
            :mask-closable="false"
        >
          <Form :model="formData" :rules="formRules" ref="dataForm">
            <FormItem :label="$t('versionmanagement.platform1')" prop="platform">
              <RadioGroup v-model.trim="formData.platform">
                <Radio label="0">{{ $t('versionmanagement.androidapp') }}</Radio>
                <Radio label="1">{{ $t('versionmanagement.appleapp') }}</Radio>
              </RadioGroup>
            </FormItem>
            <FormItem :label="$t('versionmanagement.edition')" prop="version">
              <Input v-model="formData.version" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
            </FormItem>
            <FormItem :label="$t('versionmanagement.downloadaddress')" prop="downloadUrl">
              <Input :disabled="downloadUrlLoading" v-model="formData.downloadUrl" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
              <div class="demo-spin-col">
                <Spin fix v-if="downloadUrlLoading">
                  <Icon type="load-c" size=18 class="demo-spin-icon-load"></Icon>
                  <div>{{ $t('versionmanagement.uploading') }}</div>
                </Spin>
                <Upload v-else :action="basicUrl+'admin/common/upload/oss/app'"
                        :on-success = 'uploadSuccess'
                        :on-error = "uploadFailed"
                        :on-progress = "uploadLoading"
                        :show-upload-list = "false">
                  <Button type="ghost" icon="ios-cloud-upload-outline">{{ $t('versionmanagement.uploadfile') }}</Button>
                </Upload>
              </div>
            </FormItem>
          </Form>
          <div slot="footer">
            <Button @click="hideForm">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
            <Button type="primary" @click="formSubmit" :loading="formLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
          </div>
        </Modal>
      </Card>
    </div>
</template>

<script>

import { sysAppRevision, sysAppRevisionSave, BASICURL } from '@/service/getData'

const formJson = {
  id: "",
  platform: "",
  version: "",
  downloadUrl: "",
  remark: ""
};

export default {
  data() {
    return {
      pageNum: null,
      currentPageIdx: 1,
      ifLoading: true,
      showForm: false,
      basicUrl: BASICURL,
      column_frist: [
        {
          title: 'ID',
          key: 'id',
          width: 80
        },
        {
          title: this.$t('versionmanagement.platform'),
          key: 'platform',
          render: (h, obj) => {
            console.log(obj)
            let str = obj.row.platform == 0 ? this.$t('versionmanagement.androidapp') : this.$t('versionmanagement.appleapp')
            return h ( 'div', str)
          }
        },
        {
          title: this.$t('versionmanagement.currentversion'),
          key: 'version'
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          render: (h, obj) => {
            return h ( 'div', [
              h('Button',{
                props: {
                  type: 'info',
                  size: 'small'
                },
                style: {
                  'marginRight': '5px'
                },
                on:{
                  click: () =>{
                    this.handleForm(obj.index, obj.row)
                  }

                }
              }, this.$t('perpetualcontractcurrencystandardmanagement.modify'))
            ] )
          }
        },
      ],
      appreversion: [],
      index: null,
      formName: null,
      formMap: {
        add: this.$t('collectionconfigurationmanagement.new'),
        edit: this.$t('collectionconfigurationmanagement.edit')
      },
      formData: formJson,
      formRules: {
        platform: [
          {required: true, message: this.$t('versionmanagement.pleaseselectaplatform'), trigger: "charge"}
        ],
        version: [
          {required: true, message: this.$t('versionmanagement.pleaseenterversion'), trigger: "blur"}
        ],
        downloadUrl: [
          {required: true, message: this.$t('versionmanagement.pleaseenterthedownloadlink'), trigger: "blur"}
        ],
      },
      formVisible: false,
      formLoading: false,
      downloadUrlLoading: false,
    }
  },

  methods: {
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    cancelChange() {
      this.$Message.info(this.$t('versionmanagement.modificationcanceled'));
    },
    refreshPageManual() {
      this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10 });
    },
    refreshPage() {
      this.ifLoading = true;
      sysAppRevision()
      .then( res => {
        if(!res.code){
          this.appreversion =  res.data.content;
          this.ifLoading = false;
        }else this.$Message.error(res.message);
      }, err => {
        console.log(err);
      })
    },
    // 显示表单
    handleForm(index, row) {
      this.formVisible = true;
      this.formData = JSON.parse(JSON.stringify(formJson));
      if (row !== null) {
        row.platform = row.platform + ""
        this.formData = Object.assign({}, row);
    }
      this.formName = "add";
      if (index !== null) {
        this.index = index;
        this.formName = "edit";
      }
    },
    // 隐藏表单
    hideForm() {
      // 更改值
      this.formVisible = false;
      return true;
    },
    formSubmit() {
      if (this.formLoading) {
        return false
      }
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.formLoading = true;
          let data = Object.assign({}, this.formData);
          if (this.formName === "add") {
            delete data.id
          }
          console.log(data)
          sysAppRevisionSave(data).then(response => {
            this.formLoading = false;
            if (response.code) {
              this.$Message.error(response.message);
              return false;
            }
            this.$Message.success(this.$t('currencywithdrawalauditmanagement.operationsuccessful'));
            this.formVisible = false;
            // 刷新表单
            this.refreshPageManual();
          });
        }
      });
    },
    uploadLoading() {
      this.downloadUrlLoading = true
      this.$Loading.start()
    },
    uploadSuccess(response) {
      this.downloadUrlLoading = false
      this.$Loading.finish()
      this.formData.downloadUrl = response.data
      this.$Message.success(this.$t('advertisingmanagement.uploadsuccessful'));
    },
    uploadFailed(error) {
      this.downloadUrlLoading = false
      this.$Loading.finish()
      this.$Message.error(this.$t('advertisingmanagement.uploadfailed'));
    },
  },
  created () {
    this.refreshPage();
  }
}
</script>

<style lang="less" scoped>
  .demo-spin-col{
    position: relative;
  }
  .demo-spin-icon-load{
    animation: ani-demo-spin 1s linear infinite;
  }
  .permissionWrapper{
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .2);
    p{
      position: absolute;
      top: 50%;
      left: 45%;
      transform: -50%;
      font-size: 25px;
      font-style: '黑体';
      text-align: center;
      color: #fff;

    }
  }
</style>
