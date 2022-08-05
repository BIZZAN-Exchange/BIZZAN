<template>
    <div>
      <Card>
        <p slot="title">
          {{ $t('modifyuser.modifyuser') }} </p>

        <Row class="mainEmployeeContent">
          <Form ref="formCustom" 
          :model="formWrapperObj" 
          :rules="ruleFrom" 
          :label-width="90"
          label-position="right">

            <FormItem :label="$t('auditdetails.username')" prop="username">
              <Input type="text" v-model="formWrapperObj.username"></Input>
            </FormItem>

            <FormItem :label="$t('collectionconfigurationmanagement.password')" prop="password" class="resetRequired" :class="{ 'resetFromErr': uerpsCheck}">
              <Input type="password" v-model="formWrapperObj.password"></Input>
              <div  v-if="uerpsCheck" class="ivu-form-item-error-tip">{{ $t('modifyuser.thetwopasswordsareinconsistent') }}</div>
            </FormItem>

            <FormItem v-if="!employeeID" :label="$t('modifyuser.confirmpassword')" prop="passwdCheck" class="resetRequired" :class="{ 'resetFromErr': uerpsCheck}">
              <Input type="password" v-model="formWrapperObj.passwdCheck"></Input>
              <div  v-if="uerpsCheck" class="ivu-form-item-error-tip">{{ $t('modifyuser.thetwopasswordsareinconsistent') }}</div>
            </FormItem>

            <FormItem :label="$t('auditdetails.realname')" prop="realName">
              <Input type="text" v-model="formWrapperObj.realName"></Input>
            </FormItem>

             <FormItem :label="$t('modifyuser.contactnumber')" prop="mobilePhone">
              <Input type="text" v-model="formWrapperObj.mobilePhone"></Input>
              <!-- <div  v-show="true" class="ivu-form-item-error-tip">请输入正确的手机号！</div> -->
            </FormItem>

            <FormItem :label="$t('modifyuser.role')" prop="role">
              <Select v-model="formWrapperObj.roleId" >
                <Option v-for="role in roleArr" :key="role.id" :value="role.id">{{ role.role }}</Option>
              </Select>
							<router-link to="/system/rolemanage">{{ $t('rolemanagement.addrole') }}</router-link>
            </FormItem>

            <FormItem :label="$t('personalinformation.department')" prop="depart">
              <Select v-model="formWrapperObj.departmentId">
                <Option v-for="depart in departArr" :key="depart.id" :value="depart.id">{{ depart.name }}</Option>
              </Select>
							<router-link to="/system/departmanage">{{ $t('departmentmanagement.adddepartment') }}</router-link>
            </FormItem>

            <Row class="dashLine">
              <Button type="text" @click=" swtichMore = !swtichMore ">
                <span v-if="!swtichMore">{{ $t('membermanagement.more') }} <Icon type="chevron-down"></Icon></span>
                <span v-else>{{ $t('modifyuser.putaway') }} <Icon type="chevron-up"></Icon></span>
              </Button>
            </Row>  

            <Row v-show="swtichMore">
              <FormItem :label="$t('modifyuser.userstatus')">
                <RadioGroup v-model="formWrapperObj.enable">
                  <Radio label="0">{{ $t('querymarginstrategy.normal') }}</Radio>
                  <Radio label="1">{{ $t('currencyextensionmanagement.disable') }}</Radio>
                </RadioGroup>
              </FormItem>

              <FormItem label="QQ：" prop="tencentQQ">
                <Input type="text" v-model="formWrapperObj.qq"></Input>
              </FormItem>

              <FormItem :label="$t('currencywithdrawalauditmanagement.email')" prop="email">
                <Input type="text" v-model="formWrapperObj.email" ></Input>
              </FormItem>

              <FormItem :label="$t('modifyuser.headportrait')" prop="avatar">
                <Input type="text" v-model="formWrapperObj.avatar"></Input>
              </FormItem>
            </Row>

            <FormItem>
              <Button type="success" long @click="confrimSub">{{ $t('modifyuser.confirm') }}</Button>
            </FormItem>
          </Form>
        </Row>
      </Card>
    </div>
</template>

<script>

import { setStore, getStore, removeStore } from '@/config/storage';
import { roleManage, departmentManage, addAuditEmployee, employeeDetail } from '@/service/getData'



export default {
  
  data() {
    
    const telPass = (rule, value, callback) => {
      console.log(value);
      
    }
    return {  
      uerpsCheck: false,
      employeeID: null,
      swtichMore: false,
      roleArr: [],
      departArr: [],
      formWrapperObj: {
        id: null,//f  //11 不需要
        username: null, //11
        password: null, //隐藏
        enable: 0,//f //11
        passwdCheck: null, //隐藏
        roleId: null,// 11
        departmentId: null,//11
        realName: null,//11
        mobilePhone: null,//11
        qq: null,//f//11
        email: null,//f//11
        avatar: null,//f//11
      },
      ruleFrom: {
        username: [
          { required: true, message: this.$t('modifyuser.usernamecannotbeempty'), trigger: 'blur' }
          // { required: true, validator: validatePass, trigger: 'change' }
          // { required: true, message: 'The name cannot be empty', trigger: 'change' }
          //  { type: 'email', message: 'Incorrect email format', trigger: 'change' }
        ],
        role: [
          { required: true, message: this.$t('modifyuser.pleaseselectarole'), trigger: 'blur' }
        ],
        depart: [
          { required: true, message: this.$t('modifyuser.pleaseselectyourdepartment'), trigger: 'blur' }
        ],
        realName: [
          { required: true, message: this.$t('modifyuser.pleasefillinthecorrectformat'), trigger: 'blur' }
        ],
        mobilePhone: [
          { required: true, message: this.$t('modifyuser.pleasefillinthecorrectmobilenumber'), trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: this.$t('modifyuser.pleasefillinthecorrectemailaddress'), trigger: 'blur' }
        ]
      }
    }
      },
  methods: {
    confrimSub() {
      let checkSubObj = JSON.parse(JSON.stringify(this.formWrapperObj));
      let arr = [ 'id', 'qq', 'email', 'avatar', 'enable', 'passwdCheck'];

      for(let i = 0; i < arr.length; i++) {
            delete checkSubObj[arr[i]]
      }
      
      // console.log(checkSubObj);

      for ( let x in checkSubObj ) {
        if( !checkSubObj[x] ) {
          this.$Message.warning(this.$t('systeminformationmaintenance.pleaseimprovetheinformation'));
          return;
        }else if (!this.employeeID) {
          if(this.formWrapperObj.password!==this.formWrapperObj.passwdCheck){
            this.$Message.warning(this.$t('modifyuser.thetwopasswordsareinconsistent'));
            return;
          }
        }
      } 
    addAuditEmployee(this.formWrapperObj)
    .then( res => {
      if(!res.code) {
        console.log(res);
        this.$router.push('/system/employee');
        this.$Message.success(res.message);
      }else this.$Message.error(res.message);
    })

    }
  },
  created () {
    roleManage()
    .then( res => {
      if(!res.code){
        this.roleArr = res.data.content;
      }else this.$Message.error(res.message)
		})
		
    departmentManage()
    .then( res => {
      if(!res.code){
				
       this.departArr = res.data.content;
      }else this.$Message.error(res.message)
		})
		
		this.employeeID = getStore('employeeID');
		
    if(!!this.employeeID){
      employeeDetail({ id: this.employeeID })
      .then( res => {
        if(!res.code) {
          this.formWrapperObj = res.data;
        }else this.$Message.error(res.message);
        console.log(res);
      })
    }
    
  },
  watch: {
    formWrapperObj: {
      handler(newVal, oldVal) {
        if(!this.employeeID) {
          if(newVal.password !== newVal.passwdCheck) {
            this.uerpsCheck = true;
          }else this.uerpsCheck = false;
        }
      },
      deep: true
    }
  }
}
</script>

<style lang="less" scoped>
  .mainEmployeeContent {
    .ivu-form-item {
      margin: 25px auto;
      width: 380px;
		}
		.ivu-form-item-content {
			a{
				position: absolute;
				right: -60px;
				top: 0;
			}
		}
    .dashLine{
      text-align: center;
      border-top: 1px dashed #d2d2d2;
      Button{
        color: #b3b1b1;
        &:hover{
          color: #57a3f3;
        }

      }
    }
  }
</style>