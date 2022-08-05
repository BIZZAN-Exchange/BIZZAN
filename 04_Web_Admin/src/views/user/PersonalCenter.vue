<template>
  <div>
    <Card>
      <p slot="title">
      {{ $t('common.personalcenter') }} </p>

      <Row >
        <Table 
        :columns="column_frist" 
        :data="userpage"
        :loading="ifLoading" 
        border>
        </Table>
      </Row>

      <Modal
        v-model="showModel"
        :title="$t('personalcenter.changepassword')"
        width=400
        @on-ok="confirmSub"
        @on-cancel="$Message.info(this.$t('personalcenter.cancel'))">
        <Form :model="userPW" :label-width="60" label-position="right" :rules="checkPass">
          <FormItem :label="$t('personalcenter.lastPassword')" prop="lastPassword">
            <Input type="password" v-model="userPW.lastPassword" :placeholder="$t('personalcenter.lastPasswordplaceholder')"></Input>
          </FormItem>
           <FormItem :label="$t('personalcenter.newPassword')" prop="newPassword">
            <Input type="password" v-model="userPW.newPassword" :placeholder="$t('personalcenter.newPasswordplaceholder')"></Input>
          </FormItem>
        </Form>
      </Modal>

    </Card>   
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import { getStore, setStore, removeStore } from '@/config/storage'
import { employeeDetail, fixPersonalPW } from '@/service/getData'
export default {
  data() {
    return {
      checkPass: {
        lastPassword:  { required: true, message: this.$t('common.notblank'), trigger: 'change' },
        newPassword:  { required: true, message: this.$t('common.notblank'), trigger: 'change' }
      },
      column_frist: [
        {
          title: this.$t('personalcenter.username'),
          key: 'username'
        },
        {
          title: this.$t('personalcenter.departmentName'),
          key: 'departmentName'
        },
        {
          title: this.$t('personalcenter.role'),
          key: 'role'
        },
        {
          title: this.$t('personalcenter.mobilePhone'),
          key: 'mobilePhone'
        },
        {
          title: this.$t('personalcenter.email'),
          key: 'email'
        },
        {
          title: 'QQ',
          key: 'qq'
        },
        {

          title: this.$t('common.option'),
          render : (h, obj) => {
             return h ( 'Button',{
               props: {
                 type: 'info',
                 size: 'small'
               },
               on: {
                 click: () => {
                   this.showModel = true;
                 }
               }
             }, this.$t('personalcenter.changepassword') )
          }
        },
      ],
      userpage: [],
      ifLoading: true,
      showModel: false,
      userPW: {
        lastPassword: '',
        newPassword: '',
        id: ''
      }
    }

  },
  methods: {
    confirmSub() {
      let pass = true;
      for (let key in this.userPW ) {
        if( !String(key).trim().length ) pass = false;
      }
      if(pass) {
        fixPersonalPW(this.userPW)
        .then( res => {
          if( !res.code ) {
            this.$Message.success(res.message);
          }else this.$Message.error(res.message);
        })
        .catch( err => console.log(err))
      }else this.$Message.warning(this.$t('personalcenter.completeinformation'))
    }
  },
  created() {
    let personalInfo =  JSON.parse(Cookies.get('userInfo'));
    this.userPW.id = personalInfo.id;
    employeeDetail({ id: personalInfo.id })
    .then( res => {
      if(!res.code) {
        this.userpage = [res.data];
        this.ifLoading = false;
      }else this.$Message.error(res.message)
    })
    .catch( err => console.log(err))
  }
}
</script>

<style lang="less" scoped>

</style>


