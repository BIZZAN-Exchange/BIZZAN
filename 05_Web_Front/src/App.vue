<template>
  <div :class="pageView">
    <div class="page-content">
      <div class="time_download" style="display: none;">
        <div class="leftwrapper">
          <!-- <img src="../src/assets/images/clock.png" alt="" class="clock"> -->
          <Icon type="ios-clock-outline" class="clock"></Icon>
          <span>{{ time | dateFormat }}&#160;&#160;{{ utc }}</span>
        </div>
      </div>
      <div class="layout">
        <div class="layout-ceiling">
          <router-link to="/">
            <div class="layout-logo"></div>
          </router-link>
          <div class="layout-ceiling-main">
            <!-- 导航条 -->

            <div class="header_nav">
              <Menu :active-name="activeNav" width="auto" :open-names="['1']">
                <!-- <Submenu name="1"> -->
                <div style="float:left;margin-left: 10px;">
                  <router-link to="/">
                    <MenuItem name="nav-index">{{
                      $t("header.index")
                    }}</MenuItem>
                  </router-link>
                </div>

                <div style="float:left;margin-left: 10px;">
                  <Menu
                    mode="horizontal"
                    width="auto"
                    @on-select="changelanguage"
                    style="height: 50px;line-height:50px;"
                  >
                    <Submenu name="lang">
                      <template slot="title" class="lang-title">
                        <span>{{ $t("header.fiatCurrency") }}</span>
                      </template>
                       <router-link to="/ctc">
                         <MenuItem name="nav-ctc">{{
                           $t("header.ctc")
                         }}</MenuItem>
                       </router-link>
                        <router-link to="/otc/trade/usdt">
                          <MenuItem name="nav-otc">{{
                            $t("header.otc")
                          }}</MenuItem>
                        </router-link>
                    </Submenu>
                  </Menu>
                </div>
                <div style="float:left;margin-left: 10px;">
                  <Menu
                      mode="horizontal"
                      width="auto"
                      @on-select="changelanguage"
                      style="height: 50px;line-height:50px;"
                  >
                    <Submenu name="lang">
                      <template slot="title" class="lang-title">
                        <span>{{ $t("header.trade") }}</span>
                      </template>
                      <router-link to="/exchange">
                        <MenuItem name="nav-invite">{{
                            $t("header.exchange")
                          }}</MenuItem>
                      </router-link>
                    </Submenu>
                  </Menu>
                </div>
                <div style="float:left;margin-left: 10px;">
                  <Menu
                    mode="horizontal"
                    width="auto"
                    @on-select="changelanguage"
                    style="height: 50px;line-height:50px;"
                  >
                    <Submenu name="lang">
                      <template slot="title" class="lang-title">
                        <span>{{ $t("header.swapExchange") }}</span>
                      </template>
                        <router-link to="/swap">
                          <MenuItem name="nav-swap">&nbsp;&nbsp;{{
                            $t("header.swap1")
                          }}&nbsp;&nbsp;</MenuItem>
                        </router-link>
                        <router-link to="/coinswap">
                          <MenuItem name="nav-coinswap">{{
                              $t("header.swap2")
                            }}</MenuItem>
                        </router-link>
                        <router-link to="/option">
                          <MenuItem name="nav-option">{{
                            $t("header.option")
                          }}</MenuItem>
                        </router-link>
                        <router-link to="/second">
                          <MenuItem name="nav-second">{{
                            $t("header.second")
                          }}</MenuItem>
                        </router-link>
                    </Submenu>
                  </Menu>
                </div>
                <div style="float:left;margin-left: 10px;">
                  <Menu
                    mode="horizontal"
                    width="auto"
                    @on-select="changelanguage"
                    style="height: 50px;line-height:50px;"
                  >
                    <Submenu name="lang">
                      <template slot="title" class="lang-title">
                        <span>{{ $t("header.finance") }}</span>
                      </template>
                        <router-link to="/invite">
                          <MenuItem name="nav-invite">{{
                            $t("header.invite")
                          }}</MenuItem>
                        </router-link>
                    </Submenu>
                  </Menu>
                </div>
                <div style="float:left;margin-left: 10px;">
                  <router-link to="/announcement/0">
                    <MenuItem name="nav-service">{{
                      $t("header.service")
                    }}</MenuItem>
                  </router-link>
                </div>
              </Menu>
            </div>
            <div class="header_nav_mobile_triggle" @click="toggleMemu()">
              <Icon type="md-menu" style="font-size: 25px;color:#cccccc;" />
            </div>
            <div class="header_nav" style="float:right;margin-left: 10px;">
              <Menu
                mode="horizontal"
                width="auto"
                @on-select="changelanguage"
                style="height: 50px;line-height:50px;"
              >
                <Submenu name="lang">
                  <template slot="title" class="lang-title">
                    <span>{{ languageValue }}</span>
                    <img class="lang-img" v-if="lang === 'en_US'" src="./assets/images/lang-en.png" alt="">
                    <img class="lang-img" v-if="lang === 'zh_CN'" src="./assets/images/lang-zh.png" alt="">

                  </template>
                  <MenuItem name="en_US" class="lang-item"><img src="./assets/images/lang-en.png" alt="">English</MenuItem>
                  <MenuItem name="zh_CN" class="lang-item"><img src="./assets/images/lang-zh.png" alt="">简体中文</MenuItem>
                </Submenu>
              </Menu>
            </div>
            <div class="rightwrapper">
              <poptip
                placement="bottom"
                width="120"
                class="appdownload"
                trigger="hover"
              >
                <a href="javascript:" style="font-size:14px;"
                  >{{ $t("header.appdownlaod") }}
                  <Icon type="md-arrow-dropdown" size="18" />
                </a>
                <div class="api" slot="content">
                  <div class="ios">
                    <img src="../src/assets/images/appdownload.png" alt="" />
                    <div class="tips">
                      <span>{{ $t("header.scandownload") }}</span>
                    </div>
                  </div>
                </div>
              </poptip>
            </div>
            <div class="rr login-container">
              <!-- 判断是否登录 -->
              <!-- 登录 -->
              <div class="login_register isLogin" v-if="isLogin">
                <Dropdown>
                  <a href="javascript:void(0)">
                    <Icon type="md-person" size="20" />
                    <span>{{ strpo(member.username) }}</span>
                    <Icon type="md-arrow-dropdown" size="16" />
                  </a>
                  <DropdownMenu slot="list">
                    <DropdownItem>
                      <router-link to="/uc/money">
                        <Icon type="md-person" /> &nbsp;{{
                          $t("header.usercenter")
                        }}
                      </router-link>
                    </DropdownItem>
                    <DropdownItem>
                      <router-link to="/uc/money">
                        <Icon type="logo-bitcoin" /> &nbsp;{{
                          $t("header.assetmanage")
                        }}
                      </router-link>
                    </DropdownItem>
                    <DropdownItem>
                      <router-link to="/uc/entrust/current">
                        <Icon type="md-swap" /> &nbsp;{{
                          $t("header.trademanage")
                        }}
                      </router-link>
                    </DropdownItem>
                    <DropdownItem>
                      <router-link to="/uc/innovation/myorders">
                        <Icon type="md-swap" /> &nbsp;{{
                          $t("header.innovationmanage")
                        }}
                      </router-link>
                    </DropdownItem>
                    <DropdownItem>
                      <div @click="logout">
                        <Icon type="md-log-out" /> &nbsp;{{
                          $t("common.logout")
                        }}
                      </div>
                    </DropdownItem>
                  </DropdownMenu>
                </Dropdown>
              </div>
              <!-- 未登录 -->
              <div class="login_register" v-else>
                <Menu active-name11="1-1" width="auto" :open-names="['2']">
                  <Submenu name="2" id="login_register_theme">
                    <router-link to="/login" id="login">
                      <MenuItem name="1-1">{{ $t("common.login") }}</MenuItem>
                    </router-link>
                    <router-link to="/register" id="register">
                      <MenuItem name="1-2">{{
                        $t("common.register")
                      }}</MenuItem>
                    </router-link>
                  </Submenu>
                </Menu>
              </div>
            </div>
          </div>
        </div>
      </div>
      <router-view v-if="isRouterAlive"></router-view>
      <!-- </div> -->
    </div>
    <Drawer
      :closable="true"
      width="40"
      v-model="navDrawerModal"
      class="header_nav_mobile"
    >
      <Menu :active-name="activeNav" width="auto" @on-select="changelanguage">
        <!-- 未登录 -->
        <div class="login_register" v-if="!isLogin" style="display: flex;">
          <!-- <Menu active-name11="1-1" width="auto" :open-names="['2']">
            <Submenu name="2" id="login_register_theme"> -->
          <router-link to="/login" id="login">
            <MenuItem name="1-1">{{ $t("common.login") }}</MenuItem>
          </router-link>
          <router-link to="/register" id="register">
            <MenuItem name="1-2">{{ $t("common.register") }}</MenuItem>
          </router-link>
          <!-- </Submenu>
          </Menu> -->
        </div>
        <router-link to="/">
          <MenuItem name="nav-index" style="text-align:left;color:#bdc2ca;">{{
            $t("header.index")
          }}</MenuItem>
        </router-link>
        <router-link to="/ctc">
          <MenuItem name="nav-ctc" style="text-align:left;color:#bdc2ca;">{{
            $t("header.ctc")
          }}</MenuItem>
        </router-link>
        <router-link to="/otc/trade/usdt">
          <MenuItem name="nav-otc" style="text-align:left;color:#bdc2ca;">{{
            $t("header.otc")
          }}</MenuItem>
        </router-link>
        <router-link to="/exchange">
          <MenuItem
            name="nav-exchange"
            style="text-align:left;color:#bdc2ca;"
            >{{ $t("header.exchange") }}</MenuItem
          >
        </router-link>
        <router-link to="/invite">
          <MenuItem name="nav-invite" style="text-align:left;color:#bdc2ca;">{{
            $t("header.invite")
          }}</MenuItem>
        </router-link>
        <router-link to="/news" style="display: none;">
          <MenuItem name="nav-news" style="text-align:left;color:#bdc2ca;">{{
            $t("header.news")
          }}</MenuItem>
        </router-link>
        <router-link to="/announcement/0">
          <MenuItem name="nav-service" style="text-align:left;color:#bdc2ca;">{{
            $t("header.service")
          }}</MenuItem>
        </router-link>
        <Submenu name="nav_personal" v-if="isLogin">
          <template slot="title" class="lang-t  itle">
            <span style="color:#bdc2ca;">{{ $t("header.usercenter") }}</span>
          </template>
          <router-link to="/uc/safe">
            <MenuItem
              name="nav_safe"
              class="lang-item"
              style="padding-left:20px!important;"
              >{{ $t("uc.member.securitysetting") }}</MenuItem
            >
          </router-link>
          <router-link to="/uc/money">
            <MenuItem
              name="nav_assets"
              class="lang-item"
              style="padding-left:20px!important;"
              >{{ $t("header.assetmanage") }}</MenuItem
            >
          </router-link>
          <router-link to="/uc/innovation/myminings">
            <MenuItem
              name="nav_innnovationmanage"
              class="lang-item"
              style="padding-left:20px!important;"
              >{{ $t("header.innovationmanage") }}</MenuItem
            >
          </router-link>
          <div @click="logout">
            <Icon type="md-log-out" /> &nbsp;{{ $t("common.logout") }}
          </div>
        </Submenu>
        <div
          style="height: 1px;width:100%;background:rgb(59, 69, 85);margin-top:10px;margin-bottom:10px;"
        ></div>
        <Submenu name="lang">
          <template slot="title" class="lang-title">
            <span style="color:#bdc2ca;">{{ languageValue }}</span>
          </template>
          <MenuItem
            name="en_US"
            class="lang-item"
            style="padding-left:20px!important;"
            >English</MenuItem
          >
          <MenuItem
            name="zh_CN"
            class="lang-item"
            style="padding-left:20px!important;"
            >简体中文</MenuItem
          >
        </Submenu>
        <router-link to="/app">
          <MenuItem
            name="nav-appdownload"
            style="text-align:left;color:#bdc2ca;"
            >{{ $t("header.appdownlaod") }}</MenuItem
          >
        </router-link>
      </Menu>
    </Drawer>
    <!-- footer -->
    <div class="footer">
      <Row class="footer_content">
        <!-- 左边链接 -->
        <Col class="footer_left" :xs="24" :sm="8">
          <img src="./assets/images/logo-bottom.png"  alt=""/>
          <p style="letter-spacing:2px;">{{ $t("footer.gsmc") }}</p>
          <p>Copyright ©2022 BIZZZAN.BIZ All rights reserved.</p>
          <div class="social-list">
            <ul>

              <Tooltip content="https://t.me/bizzanhevin" theme="dark" placement="top">
                <li>
                  <Icon custom="i-icon iconfont icontelegram1" />
                </li>

              </Tooltip>

              <Tooltip
                content="https://twitter.com/bizzanhevin"
                theme="dark"
                placement="top"
              >
                <li>
                  <Icon type="logo-twitter" />
                </li>
              </Tooltip>

            </ul>
          </div>
        </Col>
        <Col class="footer_right" :xs="13">
          <Row>
            <!-- 关于我们 -->
            <Col :xs="6">
              <ul>
                <li class="footer_title">
                  <span>{{ $t("footer.gsjj") }}</span>
                </li>
                <li>
                  <router-link target="_blank" to="/about-us">{{
                    $t("footer.gywm")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(1)">{{
                    $t("footer.jrwm")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" to="/announcement/0">{{
                    $t("footer.notice")
                  }}</router-link>
                </li>
                <li>
                  <router-link to="/whitepaper">{{
                    $t("header.whitepaper")
                  }}</router-link>
                </li>
              </ul>
            </Col>
            <!-- 帮助中心 -->
            <Col :xs="6">
              <ul>
                <li class="footer_title">
                  <span>{{ $t("footer.bzzx") }}</span>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(2)">{{
                    $t("footer.xszn")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(3)">{{
                    $t("footer.cjwt")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(4)">{{
                    $t("footer.jyzn")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(5)">{{
                    $t("footer.bzzl")
                  }}</router-link>
                </li>
              </ul>
            </Col>
            <!-- 条款协议 -->
            <Col :xs="6">
              <ul>
                <li class="footer_title">
                  <span>{{ $t("footer.tkxy") }}</span>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(6)">{{
                    $t("footer.mztk")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(7)">{{
                    $t("footer.ystk")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(8)">{{
                    $t("footer.fwtk")
                  }}</router-link>
                </li>
                <li>
                  <router-link target="_blank" :to="getUrl(9)">{{
                    $t("footer.fltk")
                  }}</router-link>
                </li>
              </ul>
            </Col>
            <!-- 联系我们 -->
            <Col :xs="6">
              <ul>
                <li class="footer_title">
                  <span>{{ $t("footer.lxwm") }}</span>
                </li>
                <li class="wechatclick">
                  <poptip width="200" trigger="hover" placement="right">
                    <a href="javascript:" class="wechat">{{
                      $t("footer.kfyx")
                    }}</a>
                    <div slot="content">
                      <p style="text-align:center;">service@bizzan.biz</p>
                    </div>
                  </poptip>
                </li>
                <li class="wechatclick">
                  <poptip width="200" trigger="hover" placement="right">
                    <a href="javascript:" class="wechat">{{
                      $t("footer.swhz")
                    }}</a>
                    <div slot="content">
                      <p style="text-align:center;">support@bizzan.biz</p>
                    </div>
                  </poptip>
                </li>
                <li class="wechatclick">
                  <poptip width="200" trigger="hover" placement="right">
                    <a href="javascript:" class="wechat">{{
                      $t("footer.sbsq")
                    }}</a>
                    <div slot="content">
                      <p style="text-align:center;">list@bizzan.biz</p>
                    </div>
                  </poptip>
                </li>
                <li class="wechatclick">
                  <poptip width="200" trigger="hover" placement="right">
                    <a href="javascript:" class="wechat">{{
                      $t("footer.tsjb")
                    }}</a>
                    <div slot="content">
                      <p style="text-align:center;">ceo@bizzan.biz</p>
                    </div>
                  </poptip>
                </li>
              </ul>
            </Col>
          </Row>
        </Col>

      </Row>
    </div>
    <template>
      <BackTop :bottom="50"></BackTop>
    </template>
  </div>
</template>
<script>
export default {
  name: "app",
  provide() {
    return {
      reload: this.reload,
    };
  },
  data() {
    return {
      isRouterAlive: true,
      pageView: "page-view",
      xsShow: false, //手机显示
      activeWidth: window.innerWidth,
      utc: null,
      time: null,
      content: " ",
      navDrawerModal: false,
      wechat: this.$t("footer.wechat"),
    };
  },
  watch: {
    activeNav: function() {
      switch (this.activeNav) {
        case "nav-exchange":
          window.document.title =
            (this.lang === "zh_CN" ? "交易中心" : "Exchange") +
            " - BIZZAN | BIZZAN | Global digital currency trading platform";
          break;
        case "nav-service":
          window.document.title =
            (this.lang === "zh_CN" ? "公告" : "Announcement") +
            " - BIZZAN | BIZZAN | Global digital currency trading platform";
          break;
        case "nav-about":
          window.document.title =
            (this.lang === "zh_CN" ? "关于" : "About") +
            " - BIZZAN | BIZZAN | Global digital currency trading platform";
          break;
        case "nav-lab":
          window.document.title =
            (this.lang === "zh_CN" ? "创新实验室" : "Lab") +
            " - BIZZAN | BIZZAN | Global digital currency trading platform";
          break;
        case "nav-invite":
          window.document.title =
            (this.lang === "zh_CN" ? "推广合伙人" : "Promotion") +
            " - BIZZAN | BIZZAN | Global digital currency trading platform";
          break;
        case "nav-cexchange":
          window.document.title =
            (this.lang === "zh_CN" ? "活动" : "Activity") +
            " - BIZZAN | Global digital currency trading platform";
          break;
        default:
          window.document.title =
            "BIZZAN | BIZZAN官网 - 全球比特币交易平台 | 全球数字货币交易平台";
          break;
      }
    },
    $route(to) {
      this.pageView = "page-view";
      if (to.path === "/reg") {
        this.pageView = "page-view2";

      }

      if (
        to.path === "/" ||
        to.path === "/index" ||
        to.path === "/ctc" ||
        to.path === "/exchange"
      ) {
      }

      if (to.path === "/app") {
        this.pageView = "page-view2";
      }

      // 红包页面
      if (to.path.length > 11 && to.path.substr(0, 9) === "/envelope") {
        this.pageView = "page-view3";
      }
    },
    exchangeSkin() {},
    activeWidth: {
      handler(val) {
        this.xsShow = val <= 416;
      },
      deep: true, //true 深度监听
      immediate: true,
    },
  },
  computed: {
    activeNav: function() {
      return this.$store.state.activeNav;
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    },
    member: function() {
      return this.$store.getters.member;
    },
    languageValue: function() {
      const curlang = this.$store.getters.lang;
      this.$i18n.locale = curlang;
      if (curlang === "en_US") {
        return "English";
      }
      if (curlang === "zh_CN") {
        return "简体中文";
      }
      return curlang;
    },
    lang() {
      return this.$store.state.lang;
    },
    exchangeSkin() {
      return this.$store.state.exchangeSkin;
    },
  },
  created: function() {
    this.initialize();
    // zE('webWidget', 'hide')
    const d = new Date(),
        gmtHours = d.getTimezoneOffset() / 60;
    this.utc = "GMT " + (gmtHours > 0 ? "-" : "+") + " " + String(gmtHours)[1];
    setInterval(() => {
      this.time = new Date().getTime();
    }, 1000);

    // 隐藏加载层
    let initLoading = document.getElementById("initLoading");
    if (initLoading != null) {
      document.body.removeChild(initLoading);
    }
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function() {
        this.isRouterAlive = true;
      });
    },
    isMobile() {
      return navigator.userAgent.match(
          /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
      );
    },
    toggleMemu() {
      this.navDrawerModal = !this.navDrawerModal;
    },
    strpo(str) {
      if (str.length > 4) {
        str = str.slice(0, 4) + "···";
      } else {
        str = str;
      }
      return str;
    },
    getUrl(type) {
      if (type === 1) {
        return "/helpdetail?cate=6&id=39&cateTitle=" + this.$t("footer.jrwm");
      } else if (type === 2) {
        return "/helplist?cate=0&cateTitle=" + this.$t("footer.xszn");
      } else if (type === 3) {
        return "/helplist?cate=1&cateTitle=" + this.$t("footer.cjwt");
      } else if (type === 4) {
        return "/helplist?cate=2&cateTitle=" + this.$t("footer.jyzn");
      } else if (type === 5) {
        return "/helplist?cate=3&cateTitle=" + this.$t("footer.bzzl");
      } else if (type === 6) {
        return "/helpdetail?cate=5&id=2&cateTitle=" + this.$t("footer.mztk");
      } else if (type === 7) {
        return "/helpdetail?cate=5&id=3&cateTitle=" + this.$t("footer.ystk");
      } else if (type === 8) {
        return "/helpdetail?cate=5&id=5&cateTitle=" + this.$t("footer.fwtk");
      } else if (type === 9) {
        return "/helpdetail?cate=5&id=38&cateTitle=" + this.$t("footer.fltk");
      }
    },
    initialize() {
      this.$store.commit("navigate", "nav-index");
      this.$store.commit("recoveryMember");
      this.$store.commit("initLang");
      this.$store.commit("initLoginTimes");

      this.checkLogin();
    },
    logout() {
      this.$http.post(this.host + "/uc/loginout", {}).then((response) => {
        const resp = response.body;
        if (resp.code === 0) {
          this.$Message.success(resp.message);
          this.$store.commit("setMember", null);
          setTimeout(() => {
            location.href = "/";
          }, 1500);
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    checkLogin() {
      this.$http.post(this.host + "/uc/check/login", {}).then((response) => {
        const result = response.body;
        if (result.code === 0 && result.data === false) {
          this.$store.commit("setMember", null);
        }
      });
    },
    changelanguage: function(name) {

      if (
        name !== "en_US" &&
        name !== "zh_CN"
      ) {
      } else {
        this.$store.commit("setlang", name);
        this.$i18n.locale = name;
        this.reload();
      }
    },
  },
};
</script>

<style lang="scss">
@import "../src/assets/scss/app.scss";
</style>
