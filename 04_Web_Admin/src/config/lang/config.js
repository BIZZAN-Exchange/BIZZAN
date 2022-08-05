const SIGN = 'LANGUAGE'
/**
 * 语言列表
 * shortName：简称
 * key：标记
 * path：文件路径
 */
const list = [
  {shortName:"中文",key:"zh_CN",path:require('./cn.js')},
  {shortName:"English",key:"en_US",path:require('./en.js')},
]
//组装i18n必要的数据
function packageNeed(){
  let messages = {}
  list.forEach(item=>{
    messages[item.key] = item.path
  })
  return messages
}
//获取语言简称列表和当前简称
function getShortName(){
  let index = list.map(item=>item.key).indexOf(getLanguage())
  return {
    default: index!=-1?list[index].shortName:"",
    list
  }
}
//设置语言
function setLanguage(name){
  localStorage.setItem("LANGUAGE", name);
}
//获取语言
function getLanguage(){
  //默认语言 从本地存储中取，如果没有默认为中文
  return localStorage.getItem(SIGN) || "zh_CN"
}

export default {
  setLanguage,
  default: getLanguage(),
  messages: packageNeed(),
  shortName: getShortName()
}