<template>
  <div class="login_form">
    <div class="login_right">
      <Form ref="formInline" :model="formInline" :rules="ruleInline" inline>
        <!--        <div class="login_title">{{$t('uc.login.login')}}</div>-->
        <FormItem style="text-align:center;">
          <ButtonGroup>
            <Button
                v-for="(list, index) in buttonLists"
                :key="list.text"
                :class="{ active: changeActive == index }"
                @click="actives(index)"
            >{{ list.text }}
            </Button
            >
          </ButtonGroup>
        </FormItem>
        <FormItem prop="user">
          <Input type="text" v-model="formInline.user" :placeholder="key" v-if="changeActive == 0">
            <Select v-model="country" slot="prepend" style="width: 92px;border-bottom: 1px solid #27313e;"
                    @on-select="changeImage">
              <template #prefix>
                <Avatar :src="countryImage" size="14"/>
              </template>
              <Option v-for="item in areas" :value="item.zhName" :label="'+'+item.areaCode" :key="item"
              ><img style="width: 12px" :src="item.countryImageUrl"></img><span
                  style="margin-left:10px;color:#ccc">{{ item.name }}</span> <span>{{ '  +' + item.areaCode }}</span>
              </Option
              >
            </Select>
          </Input>
          <Input type="text" v-model="formInline.user" :placeholder="key" v-if="changeActive == 1"> </Input>
        </FormItem>
        <!--        <FormItem prop="user">-->
        <!--          <Input name="user" type="text" v-model="formInline.user" :placeholder="$t('uc.login.usertip')" class="user">-->
        <!--          </Input>-->
        <!--        </FormItem>-->
        <FormItem prop="password" class="password">
          <Input type="password" v-model="formInline.password" autocomplete="new-password"
                 :placeholder="$t('uc.login.pwdtip')" @on-keyup="onKeyup">
          </Input>
        </FormItem>
        <!-- <p id="notice" class="hide">{{$t('uc.login.validatemsg')}}</p> -->
        <p style="height:30px;">
          <router-link to="/findPwd" style="color:#979797;float:right;padding-right:10px;font-size:12px;">
            {{ $t('uc.login.forget') }}
          </router-link>
        </p>
        <FormItem style="margin-bottom:15px;">
          <Button class="login_btn">{{ $t('uc.login.login') }}</Button>
        </FormItem>
        <div class='to_register'>
          <span>{{ $t('uc.login.noaccount') }}</span>
          <router-link to="/register">{{ $t('uc.login.goregister') }}</router-link>
        </div>
      </Form>

    </div>
    <Modal v-model="modal" width="280">
      <p slot="header">
        {{ $t('uc.forget.googleAuth') }}
      </p>
      <Form ref="formInline" :model="formInline" :rules="ruleInline" inline>
        <FormItem prop="user"></FormItem>
        <FormItem prop="code" class="password">
          <InputNumber style="width: 200px" v-model="formInline.code" :placeholder="$t('uc.forget.smscode')">
          </InputNumber>
        </FormItem>
      </Form>
      <div slot="footer">
        <span style="margin-right:50px">{{ $t('common.close') }}</span>
        <span
            style="background:#f0ac19;color:#fff;width:80px;border-radius:30px;display:inline-block;text-align:center;height:30px;line-height: 30px;"
            @click="loginWithCode">{{ $t('common.ok') }}</span>
      </div>
    </Modal>
  </div>

</template>

<style scoped lang="scss">
/* 验证码 */
.login_form {
  background: #0b1520 url(../../assets/images/login_bg.png) no-repeat center center;
  height: 760px;
  position: relative;
  overflow: hidden;

  .login_right {
    padding: 20px 30px 20px 30px;
    position: absolute;
    background: #17212e;
    width: 350px;
    height: 330px;
    left: 50%;
    top: 50%;
    margin-left: -175px;
    margin-top: -165px;
    border-top: 4px solid #f0ac19;
    border-radius: 5px;

    form.ivu-form.ivu-form-label-right.ivu-form-inline {
      .login_title {
        height: 70px;
        color: #fff;
      }

      .ivu-form-item {
        .ivu-form-item-content {
          .login_btn.ivu-btn {
            width: 100%;
            background-color: #f0ac19;
            outline: none;
            border-color: #f0ac19;
            color: #fff;
            font-size: 18px;
            border-radius: 5px;

            &:focus {
              -moz-box-shadow: 0px 0px 0px #fff, 0px 0px 0px #fff;
              -webkit-box-shadow: 0px 0px 0px #fff, 0px 0px 0px #fff;
              box-shadow: 0px 0px 0px #fff, 0px 0px 0px #fff;
            }
          }
        }
      }
    }
  }

  .to_register {
    overflow: hidden;
    font-size: 12px;

    span {
      float: left;
    }

    a {
      float: right;
      color: #f0ac19;
    }
  }
}

#captcha {
  width: 100%;
  display: inline-block;
}

.show {
  display: block;
}

.hide {
  display: none;
}

#notice {
  color: red;
}

#wait {
  text-align: left;
  color: #666;
  margin: 0;
}

.geetest_wait_dot geetest_dot_1 {
  color: red;
}

.user .ivu-btn,
.ivu-btn:active,
.ivu-btn:focus {
  border-color: #d7dde4;
  box-shadow: none;
}

/*  */
</style>
<script>
import gtInit from "../../assets/js/gt.js";
import $ from "jquery";

export default {
  data() {
    return {
      country: "美国",
      countryImage: "",
      changeActive: 0,
      key: this.$t('uc.regist.telno'),
      modal: false,
      captchaObj: null,
      _captchaResult: null,
      areas: [],
      formInline: {
        user: "",
        password: "",
        code: null
      },
      buttonLists: [
        {
          text: this.$t('uc.regist.tellogin'),
        },
        {
          text: this.$t('uc.regist.emaillogin'),
        },
      ],
      ruleInline: {
        user: [
          {
            required: true,
            message: this.$t("uc.login.loginvalidate"),
            trigger: "blur"
          }
        ],
        password: [
          {
            required: true,
            message: this.$t("uc.login.pwdvalidate1"),
            trigger: "blur"
          },
          {
            type: "string",
            min: 6,
            message: this.$t("uc.login.pwdvalidate2"),
            trigger: "blur"
          }
        ]
      }
    };
  },
  created: function () {
    this.init();
  },
  computed: {
    isLogin: function () {
      return this.$store.getters.isLogin;
    }
  },
  methods: {
    getAreas() {
      this.$http.post(this.host + "/uc/support/country").then(response => {
        var resp = response.body;
        this.areas = resp.data;
        this.country = this.areas[0].zhName;
        this.countryImage = this.areas[0].countryImageUrl;
        this.form.rmb = this.areas[0].localCurrency;
      });
    },
    changeImage(item) {
      for (var i = 0; i < this.areas.length; i++) {
        if (this.areas[i].zhName == item.value) {
          this.countryImage = this.areas[i].countryImageUrl;
        }
      }
    },
    actives: function (index) {
      this.changeActive = index
      if (this.changeActive == 0) {
        this.key = this.$t('uc.regist.telno')
      } else {
        this.key = this.$t('uc.regist.email')
      }
    },
    init() {
      this.$store.commit("navigate", "nav-other");
      this.$store.state.HeaderActiveName = "0";
      this.getAreas();
      if (this.isLogin) {
        this.$router.push("/uc/safe");
      } else {
        this.initGtCaptcha();
      }
    },
    onKeyup(ev) {
      if (ev.keyCode == 13) {
        $(".login_btn").click();
      }
    },
    initGtCaptcha() {
      var that = this;
      this.$http.get(this.host + this.api.uc.captcha).then(function (res) {
        window.initGeetest(
            {
              // 以下配置参数来自服务端 SDK
              gt: res.body.gt,
              challenge: res.body.challenge,
              offline: !res.body.success, //表示用户后台检测极验服务器是否宕机
              new_captcha: res.body.new_captcha, //用于宕机时表示是新验证码的宕机
              product: "bind",
              width: "100%"
            },
            this.handler
        );
      });
    },
    loginWithCode() {
      this.modal = false;
      this.handleSubmit("formInline");

    },
    handler(captchaObj) {
      captchaObj.onReady(() => {
        $("#wait").hide();
      }).onSuccess(() => {
        let result = (this._captchaResult = captchaObj.getValidate());
        if (!result) {
          this.$Message.error("请完成验证");
        } else {
          this.handleSubmit("formInline");
        }
      });
      $(".login_btn").click(() => {
        let reg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/,
            tel = this.formInline.user,
            flagtel = reg.test(tel),
            flagpass = this.formInline.password.length >= 6 ? true : false;
        flagtel && flagpass; // && captchaObj.verify();
        // (!flagtel || !flagpass) && this.$Message.error("请填写完整的信息");
        this.handleSubmit("formInline"); // 屏蔽了验证
      });
    },
    logout() {
      this.$http.post(this.host + "/uc/logout", {}).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          localStorage.setItem("MEMBER", JSON.stringify(null));
          localStorage.setItem("TOKEN", null);
          localStorage.removeItem("USERKEY", null);
        } else {
          // this.$Message.error(resp.message);
        }
      });
    },
    handleSubmit(name) {
      // 不带验证
      this.$refs[name].validate(valid => {
        if (valid) {
          var params = {};
          params["username"] = this.formInline.user;
          params["password"] = this.formInline.password;
          params['country'] = this.country //"中国";
          params["code"] = this.formInline.code;
          this.$http.post(this.host + this.api.uc.login, params).then(response => {
            var resp = response.body;
            if (resp.code == 0) {
              this.$Message.success(this.$t("uc.login.success"));
              this.$store.commit("setMember", response.body.data);
              if (this.$route.query.key != null && this.$route.query.key != "") {
                localStorage.setItem("USERKEY", this.$route.query.key);
              }
              this.$router.push("/uc/safe");
            } else {
              if (resp.code == 505) {
                this.modal = true;
                this.formInline.code = "";
              } else {
                this.$Message.error(resp.message);
                this.formInline.code = "";
              }

            }
          });
        } else {

        }
      });
      /* 带验证
      var result = this._captchaResult;
      if (!result) {
        $("#notice").show();
        setTimeout(function() {
          $("#notice").hide();
        }, 2000);
      } else {
        this.$refs[name].validate(valid => {
          if (valid) {
            var params = {};
            params["username"] = this.formInline.user;
            params["password"] = this.formInline.password;
            this.$http.post(this.host + this.api.uc.login, params).then(response => {
                var resp = response.body;
                if (resp.code == 0) {
                  this.$Message.success(this.$t("uc.login.success"));
                  this.$store.commit("setMember", response.body.data);
                  if (this.$route.query.key != null && this.$route.query.key != "") {
                    localStorage.setItem("USERKEY", this.$route.query.key);
                  }
                  this.$router.push("/uc/safe");
                } else {
                  this.$Message.error(resp.message);
                }
              });
          } else {

          }
        });
      }
      */
    }
  }
};
</script>
<style lang="scss">
.login_form {
  .login_right {
    form.ivu-form.ivu-form-label-right.ivu-form-inline {
      .ivu-form-item {
        .ivu-form-item-content {
          .ivu-input-wrapper.ivu-input-type {
            .ivu-input {
              background-color: transparent;
              font-size: 14px;
              border: none;
              border-bottom: 1px solid #27313e;
              border-radius: 0;
              // color:#fff;
              &:focus {
                border: none;
                border-bottom: 1px solid #27313e;
                -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
                -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
                box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              }
            }
          }
        }
      }
    }
  }
}

.ivu-select-single .ivu-select-selection .ivu-select-placeholder, .ivu-select-single .ivu-select-selection .ivu-select-selected-value {
  padding-right: 10px;
  padding-left: 3px;
}

.ivu-select-single .ivu-select-selection .ivu-select-arrow {
  right: 2px;
}

.ivu-form-item-error .ivu-input-group-append, .ivu-form-item-error .ivu-input-group-prepend {
  background-color: #17212e;
  border-color: transparent;
}

.ivu-form-item-error .ivu-select-arrow {
  color: #808695;
}

.login_right .ivu-select-dropdown {
  background: #1c2a32;
}
</style>
<style>
.ivu-select-single .ivu-select-selection .ivu-select-placeholder, .ivu-select-single .ivu-select-selection .ivu-select-selected-value {
  padding-right: 20px;
}

.ivu-select-arrow {
  right: 4px;
}

.ivu-select-item span:first-child {
  display: inline-block;
  width: 30px;
  text-align: left;
}

.ivu-btn-group > .ivu-btn.ivu-btn-default {
  background: transparent !important;
  color: #828ea1;
}

.ivu-btn-group > .ivu-btn.active,
.ivu-btn-group > .ivu-btn:active,
.ivu-btn-group > .ivu-btn:hover {
  border-color: transparent !important;
  color: #f0ac19 !important;
}

.ivu-btn-group > .ivu-btn:focus {
  box-shadow: none !important;
}

.ivu-btn-group > .ivu-btn {
  font-size: 16px;
}

.ivu-input-group-append,
.ivu-input-group-prepend {
  padding: 0 0 !important;
}

.ivu-input-default {
  border: none;
  border-bottom: 1px solid #27313e;
  font-size: 14px;
  background: transparent;
  border-radius: 0;
}

.ivu-input-group .ivu-input, .ivu-input-group .ivu-input-inner-container {
  width: 100%;
  float: left;
  margin-bottom: 0;
  position: relative;
  z-index: 2;
}
</style>
