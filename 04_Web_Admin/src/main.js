import Vue from 'vue';
import iView from 'iview';
import { router, initRouter } from './router/index';
import { appRouter } from './router/router';
import store from './store';
import App from './app.vue';
import '@/locale';
import 'iview/dist/styles/iview.css';
import VueI18n from 'vue-i18n';
import util from '@/libs/util';
import axios from 'axios';
import qs from 'qs';
import Api from '@/config/api';
import Cookies from 'js-cookie';
import Viser from 'viser-vue';
import { setStore, getStore, removeStore } from "@/config/storage.js";
import fullscreen from 'vue-fullscreen'
import I18nConfig from "@/config/lang/config.js";

Vue.prototype.$ajax = axios;
axios.defaults.withCredentials = true;

function toFloor(number, scale = 8) {
    if (new Number(number) == 0) { //如果是"0.0000000000000000"
        return 0;
    }
    let str = number + ""; //转字符串
    if (str.indexOf('e') > -1 || str.indexOf('E') > -1) { //科学计数法
        let num = new Number(number).toFixed(scale + 1),
            str = num + "";
        return str.substring(0, str.length - 1);
    } else if (str.indexOf(".") > -1) { //小数
        if (scale == 0) {
            return str.substring(0, str.indexOf("."));
        }
        return str.substring(0, str.indexOf(".") + scale + 1); //截取指定位数
    } else { //整数
        return str;
    }
}
Vue.filter('toFloor', (number, scale) => {
    return toFloor(number, scale);
});
Vue.prototype.toFloor = toFloor;
axios.interceptors.response.use((response) => {
    const data = response.data
    let baseURL = response.config.baseURL;
    let url = response.config.url;

    if (data.code === 4000) {
        Cookies.remove('user');
        Cookies.remove('userPhone');
        Cookies.remove('userInfo');
        router.push({ name: 'login' });
    }
    if (data.code === 5000) {
        router.push({ name: 'error-403' });
    }
    return response;
});

axios.interceptors.request.use(function(config) {
    // 请求成功后返回的内容
    let token = localStorage.getItem('admin-auth-token'); //拦截器获取到token 存储到变量token中
    if (token) {
        //config是一个请求参数,他是一个对象,Authorization 是前后台约定好的那key,把获取到的token赋值给他
        config.headers['admin-auth-token'] = token;
    }else{
        config.headers['admin-auth-token'] = "000000000000000000000000000000000000";
    }
    return config;
}, function(error) { //请求失败后返回的内容
    return Promise.reject(error);
});

Date.prototype.Format = function(fmt){
    var o = {
        "M+": this.getMonth()+1,
        "d+": this.getDate(),
        "H+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "S+": this.getMilliseconds()
    };
    //因为date.getFullYear()出来的结果是number类型的,所以为了让结果变成字符串型，下面有两种方法：
    if(/(y+)/.test(fmt)){
        //第一种：利用字符串连接符“+”给date.getFullYear()+""，加一个空字符串便可以将number类型转换成字符串。
        fmt=fmt.replace(RegExp.$1,(this.getFullYear()+"").substr(4-RegExp.$1.length));
    }
    for(var k in o){
        if (new RegExp("(" + k +")").test(fmt)){
            //第二种：使用String()类型进行强制数据类型转换String(date.getFullYear())，这种更容易理解。
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(String(o[k]).length)));
        }
    }
    return fmt;
};

// axios.interceptors.request.use((config) => {
//   config.headers['x-auth-token'] = Cookies.get('x-auth-token');
//   return config;
// })

//多语言开始
const i18n = new VueI18n({
    // locale: 'en_US',
    locale: I18nConfig.default,
    messages: I18nConfig.messages,
    silentTranslationWarn: true
});
// Vue.http.interceptors.push((request, next) => {
//     //登录成功后将后台返回的TOKEN在本地存下来,每次请求从sessionStorage中拿到存储的TOKEN值
//     request.headers.set('x-auth-token', localStorage.getItem('TOKEN'));
//     let lang = localStorage.getItem('LANGUAGE');
//     if(lang!=null){
//         lang = lang.substr(1);
//         lang = lang.substr(0,lang.length-1);
//     }
//     request.headers.set('lang', lang);
//
//     next((response) => {
//         //登录极验证时需获取后台返回的TOKEN值
//         var xAuthToken = response.headers.get('x-auth-token');
//         if (xAuthToken != null && xAuthToken != '') {
//             localStorage.setItem('TOKEN', xAuthToken);
//         }
//
//         if (response.data.code == '4000' || response.data.code == '3000') {
//             store.commit('setMember', null);
//             router.push('/login');
//             return false;
//         }
//         return response;
//     });
// });
//多语言结束
Vue.prototype.numToFixed = util.numToFixed; //主要用于科学计数法转数字

Vue.prototype.api = Api;
Vue.use(VueI18n);
Vue.use(iView);
Vue.use(Viser);
Vue.use(fullscreen);

new Vue({
    el: '#app',
    router: router,
    i18n,
    store: store,
    render: h => h(App),
    data: {
        currentPageName: ''
    },
    mounted() {
        this.currentPageName = this.$route.name;
        // 显示打开的页面的列表
        this.$store.commit('setOpenedList');
        this.$store.commit('initCachepage');
        // 权限菜单过滤相关
        this.$store.commit('updateMenulist');
    },
    created() {
        let tagsList = [];
        appRouter.map((item) => {
            if (item.children.length <= 1) {
                tagsList.push(item.children[0]);
            } else {
                tagsList.push(...item.children);
            }
        });
        this.$store.commit('setTagsList', tagsList);
    }
});
