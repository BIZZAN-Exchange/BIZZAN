<template>
    <div class="nav-rights uc_account">
        <div class="nav-right col-xs-12 col-md-10 padding-right-clear">
            <div class="bill_box rightarea padding-right-clear record account-box">
                <section class="trade-group merchant-top">
                    <i class="merchant-icon tips"></i>
                    <span class="tips-word" style="width: 30%;">{{$t('uc.account.pagetitle')}}</span>
                    <span class="tips-g" style="width: 68%; text-align: left;">{{$t('uc.account.pagetip')}}</span>
                  <div style="width: 100%;height: 50px;">
                    <Button type="primary" @click="onBindClick" style="padding: 6px 15px;margin-right:30px;letter-spacing:2px;color:#f0ac19;background-color:transparent;border:1px solid #f0ac19;float:right;">{{$t('uc.safe.bind')}}</Button>
                  </div>
                </section>
                <section class="accountContent">
                    <div class="account-in">
                        <div class="account-item">
                            <div v-for="(item,index) in paymentTypeRecords" class="account-item-in" :key="index">
                              <i :class=" 'merchant-icon tips color-'+(index%6)"></i>
                                <span class="card-number" style="width:160px">{{item.typeName}}</span>
                                <p class="bankInfo" style="color: #fff;width: 230px;height:40px;line-height:40px;margin-right:15px;border-right:1px dashed #27313e">
                                    {{item.field_1}}
                                </p>
                                <p class="bankInfo" style="color: #fff;width: 330px;height:40px;line-height:40px;border-right:1px dashed #27313e">
                                  {{item.field_2}}
                                </p>
                                <p class="bankInfo" style="color: #fff;width: 330px;height:40px;line-height:40px;border-right:1px dashed #27313e">
                                  {{item.field_3}}
                                </p>
                                <div style="width: 15%;">
                                  <a class="btn" @click="onUpdateClick(item)" >{{$t('uc.account.modify')}}</a>
                                  <a class="btn" @click="onDelClick(item)" >{{$t("uc.finance.withdraw.delete")}}</a>
                                </div>

                            </div>
<!--                          <Row v-for="(item,index) in paymentTypeRecords" :bordered="false" class="account-item-in" style="margin-bottom: 10px;background-color: #192330">-->
<!--                            <Col span="11">-->
<!--                              <Card style="margin-bottom: 10px;background-color: #192330">-->
<!--                                <template #title>-->
<!--                                  <i :class=" 'merchant-icon tips color-'+(index%6)"></i>-->
<!--                                  <span class="card-number" style="width:150px">{{item.typeName}}</span>-->
<!--                                  <a class="btn" @click="onUpdateClick(item)" style="width: 15%;">{{$t('uc.account.modify')}}</a>-->
<!--                                </template>-->
<!--                                <Row>-->
<!--                                  <Col span="8">col-8</Col>-->
<!--                                  <Col span="8">col-8</Col>-->
<!--                                  <Col span="8">col-8</Col>-->
<!--                                </Row>-->
<!--                                <Row>-->
<!--                                  <Col span="8">col-8</Col>-->
<!--                                  <Col span="8">col-8</Col>-->
<!--                                  <Col span="8">col-8</Col>-->
<!--                                </Row>-->
<!--                              </Card>-->
<!--                            </Col>-->
<!--                          </Row>-->

                        </div>
<!--                        <div class="account-item">-->
<!--                            <div class="account-item-in">-->
<!--                                <i class="icons alipay"></i>-->
<!--                                <span class="card-number">{{$t('uc.account.zfbaccount')}}</span>-->
<!--                                <p v-if="user.aliVerified==1" class="bankInfo" style="color: #fff;">-->
<!--                                    {{user.alipay.aliNo}}-->
<!--                                </p>-->
<!--                                <p v-else class="bankInfo">-->
<!--                                  {{$t('uc.account.zfbaccounttip')}}-->
<!--                                </p>-->
<!--                                <a class="btn" v-if="user.aliVerified==1" @click="showItem(2)" style="width: 15%;">{{$t('uc.account.modify')}}</a>-->
<!--                                <a class="btn" v-else @click="showItem(2)" style="width: 15%;">{{$t('uc.account.bind')}}</a>-->
<!--                            </div>-->
<!--                            <div class="account-detail" v-show="choseItem==2">-->
<!--                                <div class="detail-list">-->
<!--                                    <Form ref="formValidate2" :model="formValidate2" :rules="ruleValidate" :label-width="95">-->
<!--                                      <Row>-->
<!--                                        <Col span="8">-->
<!--                                        <input type="hidden" name="aliPreview" :value="aliPreview" />-->
<!--                                        <img v-if="aliImg" :alt="$t('uc.account.imgtip')" style="width: 200px;height: 200px;" :src="aliImg">-->
<!--                                        <img v-else :alt="$t('uc.account.imgtip')" style="width: 200px;height: 200px;" src="../../assets/images/upload_placeholder.png">-->
<!--                                        <div class="acc_sc">-->
<!--                                          <Upload ref="upload1" :on-success="aliHandleSuccess" :headers="uploadHeaders" :action="uploadUrl">-->
<!--                                            <Button icon="ios-cloud-upload-outline">{{$t('uc.safe.upload')}}</Button>-->
<!--                                          </Upload>-->
<!--                                        </div>-->
<!--                                        </Col>-->

<!--                                        <Col span="16">-->
<!--                                        &lt;!&ndash; name &ndash;&gt;-->
<!--                                        <FormItem :label="$t('uc.account.name')" prop="name">-->
<!--                                            <Input disabled size="large" v-model="formValidate2.name"></Input>-->
<!--                                        </FormItem>-->
<!--                                        &lt;!&ndash; alipay &ndash;&gt;-->
<!--                                        <FormItem :label="$t('uc.account.zfbaccount')" prop="alipay">-->
<!--                                            <Input v-model="formValidate2.alipay" size="large"></Input>-->
<!--                                        </FormItem>-->
<!--                                        &lt;!&ndash; passwd &ndash;&gt;-->
<!--                                        <FormItem :label="$t('uc.account.fundpwd')" prop="password">-->
<!--                                            <Input v-model="formValidate2.password" type="password" size="large"></Input>-->
<!--                                        </FormItem>-->
<!--                                        &lt;!&ndash; Button &ndash;&gt;-->
<!--                                        <FormItem>-->
<!--                                          <Button type="primary" @click="handleSubmit('formValidate2')">{{$t('uc.account.save')}}</Button>-->
<!--                                          &lt;!&ndash; <Button type="ghost" @click="handleReset('formValidate2')" style="margin-left: 8px">Reset</Button> &ndash;&gt;-->
<!--                                        </FormItem>-->
<!--                                        </Col>-->

<!--                                      </Row>-->

<!--                                    </Form>-->
<!--                                </div>-->
<!--                            </div>-->
<!--                        </div>-->
<!--                        <div class="account-item">-->
<!--                            <div class="account-item-in">-->
<!--                                <i class="icons wechat"></i>-->
<!--                                <span class="card-number">{{$t('uc.account.wxaccount')}}</span>-->
<!--                                <p v-if="user.wechatVerified==1" class="bankInfo" style="color: #fff;">-->
<!--                                    {{user.wechatPay.wechat}}-->
<!--                                </p>-->
<!--                                <p v-else class="bankInfo">-->
<!--                                  {{$t('uc.account.wxaccounttip')}}-->
<!--                                </p>-->
<!--                                <a class="btn" v-if="user.wechatVerified==1" @click="showItem(3)" style="width: 15%;">{{$t('uc.account.modify')}}</a>-->
<!--                                <a class="btn" v-else @click="showItem(3)" style="width: 15%;">{{$t('uc.account.bind')}}</a>-->
<!--                            </div>-->
<!--                            <div class="account-detail" v-show="choseItem==3">-->
<!--                                <div class="detail-list">-->
<!--                                    <Form ref="formValidate3" :model="formValidate3" :rules="ruleValidate" :label-width="85">-->
<!--                                      <Row>-->
<!--                                        <Col span="8">-->
<!--                                          <input type="hidden" name="wePreview" :value="wePreview" />-->
<!--                                          <img v-if="weImg" :alt="$t('uc.account.imgtip')" style="width: 200px;height: 200px;" :src="weImg" >-->
<!--                                          <img v-else :alt="$t('uc.account.imgtip')" style="width: 200px;height: 200px;" src="../../assets/images/upload_placeholder.png">-->
<!--                                          <div class="acc_sc">-->
<!--                                            <Upload ref="upload2" :on-success="weHandleSuccess" :headers="uploadHeaders" :action="uploadUrl">-->
<!--                                              <Button icon="ios-cloud-upload-outline">{{$t('uc.safe.upload')}}</Button>-->
<!--                                            </Upload>-->
<!--                                          </div>-->
<!--                                        </Col>-->
<!--                                        <Col span="16">-->
<!--                                        &lt;!&ndash; name &ndash;&gt;-->
<!--                                        <FormItem :label="$t('uc.account.name')" prop="name">-->
<!--                                            <Input disabled size="large" v-model="formValidate3.name"></Input>-->
<!--                                        </FormItem>-->
<!--                                        &lt;!&ndash; wechat &ndash;&gt;-->
<!--                                        <FormItem :label="$t('uc.account.wxaccount')" prop="wechat">-->
<!--                                            <Input v-model="formValidate3.wechat" size="large"></Input>-->
<!--                                        </FormItem>-->
<!--                                        &lt;!&ndash; passwd &ndash;&gt;-->
<!--                                        <FormItem :label="$t('uc.account.fundpwd')" prop="password">-->
<!--                                            <Input v-model="formValidate3.password" type="password" size="large"></Input>-->
<!--                                        </FormItem>-->
<!--                                        &lt;!&ndash; Button &ndash;&gt;-->
<!--                                        <FormItem>-->
<!--                                            <Button type="primary" @click="handleSubmit('formValidate3')">{{$t('uc.account.save')}}</Button>-->
<!--                                            &lt;!&ndash; <Button type="ghost" @click="handleReset('formValidate3')" style="margin-left: 8px">Reset</Button> &ndash;&gt;-->
<!--                                        </FormItem>-->
<!--                                        </Col>-->
<!--                                      </Row>-->
<!--                                    </Form>-->
<!--                                </div>-->
<!--                            </div>-->
<!--                        </div>-->
                    </div>
                </section>
            </div>
        </div>
      <Modal style="width: 700px" v-model="bindModal" :title="$t('otc.bind_payment')">

        <Form ref="paymentTypeRecordData" :model="paymentTypeRecordData" :rules="ruleValidate" :label-width="120">
          <!-- name -->
          <FormItem :label="$t('otc.payment')" prop="type">
            <Select v-model="paymentTypeRecordData.type" @on-change="onSelectType" :placeholder="$t('otc.payment_placeholder')">
              <Option v-for="item in paymentTypes" :value="item.id" :key="item.id">{{ item.code }}</Option>
            </Select>
          </FormItem>
          <FormItem v-for="config in paymentTypeConfigs" :label="config.showText" :prop="config.fieldName" :key="config">
              <Input v-if="config.type== 'input'" v-model="paymentTypeRecordData[config.fieldName]" :placeholder="config.placeholder" size="large"></Input>
              <span  v-if="config.type== 'tip'" class="tips-g" style="width: 68%; text-align: left;">{{config.placeholder}}</span>
              <div v-if="config.type== 'image'">
                <input type="hidden" name="wePreview" :value="wePreview" />
                <img v-if="paymentTypeRecordData[config.fieldName]" :alt="$t('uc.account.imgtip')" style="width: 200px;height: 200px;" :src="paymentTypeRecordData[config.fieldName]" >
                <img v-else :alt="$t('uc.account.imgtip')" style="width: 200px;height: 200px;" src="../../assets/images/upload_placeholder.png">
                <div class="acc_sc">
                  <Upload ref="upload2" :on-success="handleSuccess" :headers="uploadHeaders" :action="uploadUrl">
                    <Button icon="ios-cloud-upload-outline">{{$t('uc.safe.upload')}}</Button>
                  </Upload>
                </div>
              </div>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button size="large" @click="transferModal = false">{{$t("common.close")}}</Button>
          <Button type="primary" size="large" @click="confirmOk">保存</Button>
        </div>
      </Modal>
    </div>
</template>
<script>

export default {
    components: {
    },
    data() {
        const validatePass = (rule, value, callback) => {
            if (value === '') {
                callback(new Error(this.$t('uc.account.banknomsg1')));
            // } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
            } else if (!/([0-9]){6,18}/.test(value)) {
                callback(new Error(this.$t('uc.account.banknomsg1')));
            } else {
                callback();
            }
        };
        // const validatePassCheck = (rule, value, callback) => {
        //     if (value === '') {
        //         callback(new Error(this.$t('uc.account.banknomsg1')));
        //     // } else if (!/([a-zA-Z0-9]){6,18}/.test(value)) {
        //     } else if (!/([0-9]){6,18}/.test(value)) {
        //         callback(new Error(this.$t('uc.account.banknomsg1')));
        //     } else if (value !== this.formValidate1.bankNo) {
        //         callback(new Error(this.$t('uc.account.banknomsg2')));
        //     } else {
        //         callback();
        //     }
        // };
        return {
            uploadHeaders:{'x-auth-token':localStorage.getItem('TOKEN')},
            uploadUrl:this.host+'/uc/upload/oss/image',
            paymentTypeRecordData:{},
            paymentTypeRecords:[],
            bindModal:false,
            aliPreview:'',
            imageFieldName:'',
            wePreview:'',
            isNoName: true,
            msg: '',
            choseItem: 0,
            user: {},
            paymentTypes:[],
            paymentTypeConfigs:[],

            bankNameList: [

            ],
            ruleValidate: {
              field_1: [
                    { required: true, message: this.$t('uc.account.verifiedmsg'), trigger: 'blur' }
                ],
            },

        }
    },
    methods: {
      onDelClick(item){
        this.$http.get(this.host + '/uc/payment/delRecordById?id='+item.id).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.getAccount();
          } else {
            this.$Message.error(resp.message);
          }
        })
      },
      onUpdateClick(item){
        this.$http.get(this.host + '/uc/payment/list').then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.paymentTypes=resp.data;
            this.bindModal = true;
          } else {
            this.$Message.error(resp.message);
          }
        })
        this.paymentTypeRecordData=item;
        this.bindModal = true;
        this.$http.get(this.host + '/uc/payment/findPaymentTypeConfigById?id='+this.paymentTypeRecordData.type).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.paymentTypeConfigs=resp.data;
          } else {
            this.$Message.error(resp.message);
          }
        })
      },
        confirmOk(){
          this.$refs.paymentTypeRecordData.validate((valid) => {
            if (valid) {
              this.$http.post(this.host + '/uc/payment/saveOrUpdate',this.paymentTypeRecordData).then(response => {
                var resp = response.body;
                if (resp.code == 0) {
                  this.getAccount();
                  this.bindModal = false;
                } else {
                  this.$Message.error(resp.message);
                }
              })
            } else {
              this.$Message.error(this.$t('uc.account.save_failure'));
            }
          })

        },
        handleSuccess (res, file,fileList) {
          this.$refs.upload2.fileList=[fileList[fileList.length-1]];
          this.paymentTypeRecordData[this.imageFieldName]=this.wePreview=res.data;
        },
        handleSubmit(name) {
            this.$refs[name].validate((valid) => {
                if (valid) {
                    this.submit(name)
                } else {
                    this.$Message.error(this.$t('uc.account.save_failure'));
                }
            })
        },
        onBindClick() {
          this.$http.get(this.host + '/uc/payment/list').then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.paymentTypes=resp.data;
              this.bindModal = true;
            } else {
              this.$Message.error(resp.message);
            }
          })
        },
      onSelectType() {
        if(this.paymentTypeRecordData.type==undefined || this.paymentTypeRecordData.type ==''){
          return;
        }
        let type = this.paymentTypeRecordData.type;
        this.paymentTypeRecordData={};
        this.imageFieldName="";
        this.paymentTypeRecordData.type=type;
        this.paymentTypeConfigs=[];
        this.$http.get(this.host + '/uc/payment/findPaymentTypeConfigById?id='+this.paymentTypeRecordData.type).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.paymentTypeConfigs=resp.data;
            for(let i=0;i<this.paymentTypeConfigs.length;i++){
              if(this.paymentTypeConfigs[i].type=="image"){
                this.imageFieldName=this.paymentTypeConfigs[i].fieldName;
              }
              //验证规则
              let rule = [{required: this.paymentTypeConfigs[i].require, message: this.paymentTypeConfigs[i].placeholder, trigger: 'blur' }];
              this.ruleValidate[this.paymentTypeConfigs[i].fieldName]=rule;
            }
          } else {
            this.$Message.error(resp.message);
          }
        })
      },
        handleReset(name) {
            this.$refs[name].resetFields();
        },
        submit(name) {
            //银行卡
            if (name == 'formValidate1') {
                let param = {}
                param['bank'] = this.formValidate1.bankName
                param['branch'] = this.formValidate1.bankBranch
                param['jyPassword'] = this.formValidate1.password
                param['realName'] = this.formValidate1.name
                param['cardNo'] = this.formValidate1.bankNo
              if (this.user.bankVerified==1) { //修改
                this.$http.post(this.host + '/uc/approve/update/bank', param).then(response => {
                  var resp = response.body;
                  if (resp.code == 0) {
                    this.$Message.success(this.$t('uc.account.save_success'));
                    this.getAccount()
                    this.choseItem = 0
                  } else {
                    this.$Message.error(resp.message);
                  }
                })
              }else { //设置
                this.$http.post(this.host + '/uc/approve/bind/bank', param).then(response => {
                    var resp = response.body;
                    if (resp.code == 0) {
                        this.$Message.success(this.$t('uc.account.save_success'));
                        this.getAccount()
                        this.choseItem = 0
                    } else {
                        this.$Message.error(resp.message);
                    }
                })
              }
            }
            //支付宝
            if (name == 'formValidate2') {
                let param = {}
                param['ali'] = this.formValidate2.alipay
                param['jyPassword'] = this.formValidate2.password
                param['realName'] = this.formValidate2.name
                param['qrCodeUrl'] = this.aliPreview;

                if (this.user.aliVerified==1){
                  this.$http.post(this.host + '/uc/approve/update/ali', param).then(response => {
                    var resp = response.body;
                    if (resp.code == 0) {
                      this.$Message.success(this.$t('uc.account.save_success'));
                      this.getAccount()
                      this.choseItem = 0
                    } else {
                      this.$Message.error(resp.message);
                    }
                  })
                }else {
                  this.$http.post(this.host + '/uc/approve/bind/ali', param).then(response => {
                      var resp = response.body;
                      if (resp.code == 0) {
                          this.$Message.success(this.$t('uc.account.save_success'));
                          this.getAccount()
                          this.choseItem = 0
                      } else {
                          this.$Message.error(resp.message);
                      }
                  })
                }
            }
            //微信
            if (name == 'formValidate3') {
                let param = {}
                param['wechat'] = this.formValidate3.wechat
                param['jyPassword'] = this.formValidate3.password
                param['realName'] = this.formValidate3.name
                param['qrCodeUrl'] = this.wePreview;

              if(this.user.wechatVerified==1) {
                this.$http.post(this.host + '/uc/approve/update/wechat', param).then(response => {
                  var resp = response.body;
                  if (resp.code == 0) {
                    this.$Message.success(this.$t('uc.account.save_success'));
                    this.getAccount()
                    this.choseItem = 0
                  } else {
                    this.$Message.error(resp.message);
                  }
                })
              }else{
                this.$http.post(this.host + '/uc/approve/bind/wechat', param).then(response => {
                    var resp = response.body;
                    if (resp.code == 0) {
                        this.$Message.success(this.$t('uc.account.save_success'));
                        this.getAccount()
                        this.choseItem = 0
                    } else {
                        this.$Message.error(resp.message);
                    }
                })
              }
            }
        },
        showItem(index) {
            this.choseItem = index;
        },
        noName() {
            this.$Message.error(this.msg);
        },
        getAccount() {
            //获取个人账户信息
            this.$http.post(this.host + '/uc/payment/getRecords').then(response => {
                var resp = response.body;
                // console.log(resp);
                if (resp.code == 0) {
                      this.paymentTypeRecords=resp.data;
                } else {
                    this.msg = resp.message;
                    //this.$Message.error(resp.message)
                    this.$Notice.error({
                        title: this.$t("common.tip"),
                        desc: resp.message
                      });
                    this.$router.push("/uc/safe");
                }
            })
        }

    },
    created() {
        this.getAccount()
    },
    computed: {
    }
}
</script>
<style scoped>
.account-box .account-in .account-item .account-detail {
    padding: 30px 0;
    /* background: white; */
    margin: 6px 0;
}

.account-box .account-in .account-item .account-detail .detail-list {
    width: 40%;
    width: 80%;
    margin: 0 auto;
}

.account-box .account-in .account-item .account-detail .detail-list .input-control {
    margin-bottom: 10px;
    height: 45px;
}

.detail-list .input-control .ivu-input-group-prepend {
    width: 63px;
}

.detail-list .input-control .ivu-input {
    height: 45px;
}

.account-box .account-in .account-item {
    margin-bottom: 10px;
}

.account-box .account-in .account-item .account-item-in {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    padding: 15px 30px 15px 50px;
    -webkit-box-shadow: 0 1px 0 0 rgba(69, 112, 128, 0.06);
    box-shadow: 0 1px 0 0 rgba(69, 112, 128, 0.06);
    font-size: 14px;
    color: #fff;
}

.account-box .account-in .account-item .account-item-in .icons {
    height: 17px;
    width: 17px;
    display: inline-block;
    margin-top: -1px;
    background-size: 100% 100%;
}

.account-box .account-in .account-item .account-item-in .bankfor {
    background-image: url(../../assets/img/bankcard.png);
}

.icons.alipay {
    background-image: url(../../assets/img/alipay.png);
}

.icons.wechat {
    background-image: url(../../assets/img/wechat.png);
}

.account-box .account-in .account-item .account-item-in .card-number {
    width: 142px;
    height: 40px;
    margin-right: 15px;
    border-right: 1px dashed #27313e;
    padding: 0 15px;
    line-height: 40px;
    text-align: left;
    display: inline-block;
}

.account-box .account-in .account-item .account-item-in .bankInfo {
    width:70%;
    text-align: left;
    color: rgb(130, 142, 161);
    font-size: 13px;
}

.account-box .account-in .account-item .account-item-in .btn {
    padding: 8px 10px;
    cursor: pointer;
    color: #f0a70a;
}

.tips-g {
    color: rgb(130, 142, 161);
    font-size: 12px;
}

.table-inner {
    position: relative;
    text-align: left;
    border-radius: 3px;
    padding: 23px 20px 20px;
}

.acb-p1 {
    font-size: 18px;
    font-weight: 600;
    line-height: 50px;
}

.acb-p2 {
    font-size: 14px;
    line-height: 24px;
}

.action-inner {
    width: 100%;
    display: table;
}

.action-inner .inner-box {
    display: table-cell;
    width: 100%;
}

.action-box .title .copy {
    user-select: text;
}

.action-box .title a.link-copy {
    font-size: 14px;
    margin-left: 20px;
}

.hb-night a {
    text-decoration: none;
    color: #f0a70a;
    transition: all .2s ease-in-out;
    cursor: pointer;
}

.action-box .title a.link-qrcode {
    margin-left: 20px;
    font-size: 14px;
    position: relative;
}

.hb-night a {
    text-decoration: none;
    color: #f0a70a;
    transition: all .2s ease-in-out;
    cursor: pointer;
}

.action-box .subtitle {
    font-size: 12px;
    margin-top: 30px;
}

.action-content {
    width: 100%;
    margin-top: 30px;
    overflow: hidden;
    display: table;
}

.action-box .title {
    margin-top: 20px;
    font-size: 20px;
    user-select: none;
}

.action-box .title .show-qrcode {
    position: absolute;
    top: -100px;
    left: 40px;
    padding: 10px;
}

.action-inner .inner-box.deposit-address {
    width: 80%;
}

p.describe {
    font-size: 16px;
    font-weight: 600;
}

.merchant-top {
    height: 50px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    padding: 0 15px;
}

.trade-group {
    margin-bottom: 20px;
    font-size: 14px;
}

.merchant-icon {
    display: inline-block;
    margin-left: 4px;
    background-size: 100% 100%;
}

.merchant-top .tips-word {
    -webkit-box-flex: 2;
    -ms-flex-positive: 2;
    flex-grow: 2;
    text-align: left;
}

.merchant-icon.tips {
    width: 4px;
    height: 22px;
    margin-right: 10px;
}
.merchant-icon.tips.color-0 {
  background: #f0a70a;
}
.merchant-icon.tips.color-1 {
  background: #e5dc2a;
}
.merchant-icon.tips.color-2 {
  background: #4fbe51;
}
.merchant-icon.tips.color-3 {
  background: #d07e3b;
}
.merchant-icon.tips.color-4 {
  background: #0a4bf0;
}
.merchant-icon.tips.color-5 {
  background: #810af0;
}
.merchant-icon.tips.color-6 {
  background: #2b9f76;
}


.bill_box {
    width: 100%;
    height: auto;
    overflow: hidden;
}

.rightarea {
    padding-left: 15px !important;
    padding-right: 15px !important;
    margin-bottom: 60px !important;
}

.rightarea .rightarea-top {
    line-height: 75px;
    border-bottom: #f1f1f1 solid 1px;
}

.rightarea .rightarea-con {
    padding-top: 30px;
    padding-bottom: 125px;
}

.rightarea .trade-process {
    line-height: 30px;
    padding: 0 15px;
    background: #f1f1f1;
    display: inline-block;
    position: relative;
    margin-right: 20px;
}

.rightarea .trade-process.active {
    color: #eb6f6c;
    background: #f9f5eb;
}

.rightarea .trade-process .icon {
    background: #fff;
    border-radius: 20px;
    height: 20px;
    width: 20px;
    display: inline-block;
    line-height: 20px;
    text-align: center;
    margin-right: 10px;
}

.rightarea .trade-process .arrow {
    position: absolute;
    top: 10px;
    right: -5px;
    width: 0;
    height: 0;
    border-top: 5px solid transparent;
    border-bottom: 5px solid transparent;
    border-left: 5px solid #f1f1f1;
}

.rightarea .trade-process.active .arrow {
    border-left: 5px solid #f9f5eb;
}

.rightarea .rightarea-tabs {
    border: none;
}

.rightarea .rightarea-tabs li>a {
    width: 100%;
    height: 100%;
    padding: 0;
    margin-right: 0;
    font-size: 14px;
    color: #646464;
    border-radius: 0;
    border: none;
    display: flex;
    justify-content: center;
    align-items: center;
}

.rightarea .rightarea-tabs li>a:hover {
    background-color: #fcfbfb;
}

.rightarea .rightarea-tabs li {
    width: 125px;
    height: 40px;
    position: relative;
    margin: -1px 0 0 -1px;
    border: 1px solid #f1f1f1;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}

.rightarea .rightarea-tabs li.active {
    background-color: #fcfbfb;
}

.rightarea .rightarea-tabs li:last-child {
    border-right: 1px solid #f1f1f1;
}

.rightarea .rightarea-tabs li.active>a,
.rightarea .rightarea-tabs li:hover>a {
    color: #da2e22;
    border: none;
}

.rightarea .panel-tips {
    border: 3px solid #fdfaf3;
    color: #9e9e9e;
    font-size: 12px;
}

.rightarea .panel-tips .panel-header {
    background: #fdfaf3;
    line-height: 40px;
    margin-bottom: 15px;
}

.rightarea .panel-tips .panel-title {
    font-size: 16px;
}

.rightarea .recordtitle {
    cursor: pointer;
}

.nav-right {
    /* width: 1000px; */
    height: auto;
    overflow: hidden;
    padding: 0 15px;
}

.order_box {
    width: 100%;
    background: #fff;
    height: 56px;
    line-height: 56px;
    margin-bottom: 20px;
    border-bottom: 2px solid #ccf2ff;
    position: relative;
    text-align: left;
}

.order_box a {
    color: #8994A3;
    font-size: 16px;
    padding: 0 30px;
    cursor: pointer;
    text-decoration: none;
    text-align: center;
    line-height: 54px;
    display: inline-block;
}

.order_box .active {
    border-bottom: 2px solid #f0a70a;
}

.order_box .search {
    position: absolute;
    width: 300px;
    height: 32px;
    top: 12px;
    right: 0;
    display: flex;
    /* border: #c5cdd7 solid 1px; */
}
.ivu-btn-primary{
    background-color: #f0a70a;
    border-color: #f0a70a;
}

@media screen and (max-width:768px){
    .uc_account .nav-right{
        padding: 0 0!important;
    }
    .uc_account .account-box .account-in .account-item .account-item-in .card-number{
        padding: 0px 5px !important;
    }
    .uc_account .merchant-top{
        padding: 0 0!important;
    }
}

</style>
