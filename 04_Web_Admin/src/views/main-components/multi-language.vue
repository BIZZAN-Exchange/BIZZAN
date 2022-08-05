<template>
    <Dropdown @on-click="langChange">
      <a style="color:#495060" href="javascript:void(0)">{{langDefault}}<Icon type="arrow-down-b"></Icon></a>
      <Dropdown-menu slot="list">
          <Dropdown-item v-for="(item,index) in getLangList" :key="item" :name="index" :selected="langDefault==item">{{item}}</Dropdown-item>
      </Dropdown-menu>
    </Dropdown>
</template>

<script>
import I18nConfig from "@/config/lang/config.js";
export default {
    name: 'multiLanguage',
    data() {
      let { shortName } = I18nConfig;
      let { default:langDefault, list:langList } = shortName;
      return {
        langDefault,
        langList
      }
    },
    computed:{
      getLangList(){
        return this.langList.map(item=>item.shortName)
      }
    },
    methods: {
      //切换语言
      langChange(index){
        let target = this.langList[index]

        this.langDefault = target.shortName
        this.$i18n.locale = target.key
        I18nConfig.setLanguage(target.key)
      }
    }
};
</script>
