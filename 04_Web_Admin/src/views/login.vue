<style lang="less">
@import "./login.less";
</style>

<template>
	<div class="login" @keydown.enter="handle">
		<div class="login-con">
			<Card :bordered="false">
				<p slot="title">
					<Icon type="log-in"></Icon> {{$t('login.welcometologin')}}
				</p>
				<div class="form-con" v-if="!phoneNum">
					<Form ref="loginForm" :model="form" :rules="rules">
						<FormItem prop="username">
							<Input v-model="form.username" :disabled="btnDisable" :placeholder="$t('login.usernameplaceholder')">
								<span slot="prepend">
									<Icon :size="16" type="person"></Icon>
								</span>
							</Input>
						</FormItem>

						<FormItem prop="password">
							<Input type="password" v-model="form.password" :disabled="btnDisable" :placeholder="$t('login.captchapplaceholder')">
								<span slot="prepend">
									<Icon :size="14" type="locked"></Icon>
								</span>
							</Input>
						</FormItem prop="captcha">

						<Row v-show="false">
							<Col span="12">
								<Input v-model="form.captcha" :placeholder="$t('login.captchap')" >
								<span slot="prepend">
									<Icon :size="14" type="locked"></Icon>
								</span></Input>
							</Col>
							<Col span="10" offset="2">
								<img :src="logimg" style='vertical-align: middle;width:95%;height:95%' @click="chanloimg"/>
							</Col>
						</Row>

						<FormItem style='margin-top:40px'>
							<Button @click="handle" type="warning" long>{{$t('common.login')}}</Button>
						</FormItem>

						<p style='color:red;text-align:center' v-if="messshow">{{errormessage}}</p>
					</Form>
				</div>
				<div v-if="!!phoneNum">
					<Form>
 						<FormItem>
							<p class="phone-num">{{ phoneNum | hidePhoneNum }}</p>
						</FormItem>
						<FormItem>
							<Input :placeholder="$t('login.captchapplaceholder')" v-model="code" :class="{appendBtn: count===0}">
							 	<Button slot="append" v-if="count>0" :disabled="count>0">{{count}}s{{$t('login.getcaptchaps')}}</Button>
							 	<Button slot="append" v-else-if="count===0" type="success" @click="getCodeTwice">{{$t('login.getcaptchap')}}</Button>
							</Input>
						</FormItem>
						<FormItem>
							<Row>
								<Col span="11">
									<Button @click="handle" type="warning" long>{{$t('common.login')}}</Button>
								</Col>
								<Col span="11" offset="2">
									<Button @click="cancelSignIn" long>{{$t('common.cancel')}}</Button>
								</Col>
							</Row>
						</FormItem>
					</Form>

				</div>
			</Card>
		</div>
    <div style=" position:absolute;top: 15px;font-size: 15px;margin-top: 10px;margin-left: 600px;color: rgb(255, 255, 255); background-image: linear-gradient(1350deg, rgba(253,101,133,0.42) 30%, rgb(13, 37, 185) 100%); text-align: center; height: 50px; line-height: 50px; letter-spacing: 1px;"><a href="https://t.me/bizzanhevin" target="_blank" style="color: #ddd;text-decoration: none;">{{$t('common.connect1')}} </a><a href="https://t.me/bizzancom" target="_blank" style="color: #ddd;text-decoration: none;">{{$t('common.connect2')}} </a><a href="https://t.me/bzengineer" target="_blank" style="color: #ddd;text-decoration: none;">{{$t('common.connect3')}} </a></div>
  </div>
</template>

<style>
	.login .ivu-card{
		background: #ffffffd9;
	}
</style>
<script>
import Cookies from "js-cookie";
import store from "../store";

import { setStore, getStore, removeStore } from "@/config/storage";
import { otherRouter, appRouter } from "@/router/router.js";
import { BASICURL, login, signIn, getCodeAgain } from "@/service/getData";

export default {
  data() {
    return {
			timer: '',
			btnDisable: false,
			count: 0,
      form: {
        username: null,
        password: null,
        captcha: null
			},
			code: null,
			phoneNum: null,
      messshow: false,
      errormessage: null,
      logimg: `${BASICURL}/admin/captcha?cid=ADMIN_LOGIN`,
      rules: {
        username: [{ required: true, message: this.$t('common.notblank'), trigger: "blur" }],
        captcha: [{ required: true, message: this.$t('common.notblank'), trigger: "blur" }],
        password: [{ required: true, message: this.$t('common.notblank'), trigger: "blur" }]
      },
      permissions: {}
    };
  },
  methods: {
		cancelSignIn() {
			Cookies.remove('userPhone');
			window.location.reload();
		},
		getCodeTwice() {
			this.count = 60;
			getCodeAgain({phone: this.phoneNum})
			.then(res => {
			})
			.catch(err => {console.log(err)})
		},
    chanloimg() {
      this.logimg = `${BASICURL}admin/captcha?cid=ADMIN_LOGIN&a=${Math.random().toFixed(
        2
      )}`;
    },
    processPermission(menu) {
      if (menu.name != "") this.permissions[menu.name] = 1;
      if (menu.subMenu != null && menu.subMenu.length > 0) {
        for (var i = 0; i < menu.subMenu.length; i++) {
          this.processPermission(menu.subMenu[i]);
        }
      }
		},
    handle() {
		login(this.form)
				 .then(res => {
					  if (!res.code) {
							Cookies.set('user', res.data.admin.username, { expires: 7 });
							Cookies.set('userInfo', res.data.admin, { expires: 7 });
							setStore("leftSidebarList", res.data.permissions);
							localStorage.setItem("admin-auth-token", res.data.authToken);
							this.$router.push({ name: "home_index" });
                  	   } else this.$Message.error(res.message);
					})
					.catch(err => console.log(err));
    }
  },
  beforeRouteLeave(to, from, next) {
		clearInterval(this.timer)
    window.location.reload();
    next();
  },
  created() {
		this.phoneNum = Cookies.get('userPhone');
		clearInterval(this.timer)
    this.logimg = `${BASICURL}admin/captcha?cid=ADMIN_LOGIN`;
	},
	filters: {
		hidePhoneNum(val) {
			return val.split('').fill('*',3,7).join('');
		}
	},
	watch: {
		count(newVal, oldVal) {
			if(newVal>0){
				this.timer = setTimeout(()=> {
					this.count--;
				}, 1000)
			}else{
				clearInterval(this.timer)
			}
		},
		phoneNum(newVal, oldVal) {
			if(!!newVal) this.count = 0;
		}
	}
};
</script>


