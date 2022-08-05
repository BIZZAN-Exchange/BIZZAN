<template>
 	<div>
    <Card>
      <p slot="title">
        {{ $t('app.balanceManagement') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('common.refresh') }} </Button>
      </p>

			<Row class="functionWrapper">
        <div class="searchWrapper clearfix">
          
          <div class="poptip">
            <Poptip trigger="hover" :content="$t('balancemanagement.note1')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note1')" 
                    v-model="filterSearch.account"/> 
              </Input>      
            </Poptip>
          </div>

					<div class="poptip">
            <Poptip trigger="hover" :content="$t('balancemanagement.note2')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note2')" 
                    v-model="filterSearch.walletAddress"/> 
              </Input>      
            </Poptip>
          </div>

					<div class="poptip">
            <Poptip trigger="hover" :content="$t('backstageappeal.pleaseentercurrencysearch')" placement="bottom-start">
              <Input :placeholder="$t('backstageappeal.pleaseentercurrencysearch')" 
                    v-model="filterSearch.unit"/> 
              </Input>      
            </Poptip>
          </div>
					<br>
					<div class="poptip range">
            <Poptip trigger="hover" :content="$t('balancemanagement.note3')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note4')" 
                    v-model="filterSearch.minBalance"/> 
              </Input>      
            </Poptip>
						~
						<Poptip trigger="hover" :content="$t('balancemanagement.note15')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note5')" 
                    v-model="filterSearch.maxBalance"/> 
              </Input>      
            </Poptip>
          </div>

						<div class="poptip range">
            <Poptip trigger="hover" :content="$t('balancemanagement.note6')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note7')" 
                    v-model="filterSearch.minFrozenBalance"/> 
              </Input>      
            </Poptip>
						~
						<Poptip trigger="hover" :content="$t('balancemanagement.note8')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note9')" 
                    v-model="filterSearch.maxFrozenBalance"/> 
              </Input>      
            </Poptip>
          </div>

						<div class="poptip range">
            <Poptip trigger="hover" :content="$t('balancemanagement.note10')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note11')" 
                    v-model="filterSearch.minAllBalance"/> 
              </Input>      
            </Poptip>
						~
						<Poptip trigger="hover" :content="$t('balancemanagement.note12')" placement="bottom-start">
              <Input :placeholder="$t('balancemanagement.note13')" 
                    v-model="filterSearch.maxAllBalance"/> 
              </Input>      
            </Poptip>
          </div>


          <div class="btns">
            <Button type="info" size="small" @click="searchByFilter">{{ $t('common.search') }}</Button>
            <Button type="success" size="small" @click="exportExcel">{{ $t('membermanagement.export') }}</Button>
          </div>

        </div>
			</Row>

      <Row class="margin-top-10 searchable-table-con1">
        <Table ref="selection" 
        :columns="columns_first" 
        :data="userpage"
        border
        :loading="ifLoading"
       >
        </Table>
      </Row>

      <Row class="pageWrapper">
        <Page :total="pageNum" 
         :current="currentPageIdx"
         class="buttomPage"
         @on-change="changePage"
         show-elevator></Page>
      </Row>

    </Card>
 	</div>   
</template>

<script>

import {memberAsset, memberAssetOut} from '@/service/getData'

  export default {
    data () {
      return {
				/** 
 * unit
 * walletAddress
 * account //用户名、邮箱、手机、姓名
 * 
 * property 排序字段
 * direction 0 1
*/
				currentPageIdx: 1,
				filterSearch: {
					unit: '',
					walletAddress: '',
					account: '',
					minBalance: '',
					maxBalance: '',
					minFrozenBalance: '',
					maxFrozenBalance: '',
					minAllBalance: '',
        maxAllBalance: '',
        isOut: 0
				},
        pageNum: null,
        userpage: [],
        ifLoading: true,
        columns_first: [
          {
            title: this.$t('essentialinformation.memberid'),
            key: 'memberId',
          },
          {
            title: this.$t('usermanagement.username'),
            key: 'username',
          },
          {
            title: this.$t('memberinvitationlist.mailbox'),
            key: 'email',
          },
          {
            title: this.$t('memberinvitationlist.cellphonenumber'),
            key: 'mobilePhone',
          },
           {
            title: this.$t('businessinformation.realname'),
            key: 'realName',
          },
           {
            title: this.$t('collectionconfigurationmanagement.currencyname1'),
            key: 'unit',
          },
          {
            title: this.$t('chargingdetails.walletaddress'),
            key: 'address',
          },
         {
            title: this.$t('balancemanagement.numberofcoinsavailable'),
            key: 'balance',
          },
          {
            title: this.$t('balancemanagement.frozencoins'),
            key: 'frozenBalance',
          },
          {
            title: this.$t('balancemanagement.totalcoins'),
            key: 'allBalance',
          },
        ]
      }
    },
    methods: {
    exportExcel() {
      this.filterSearch.isOut = 1
      memberAssetOut(this.filterSearch)
          .then(res => {
            // 文件下载
            console.log(res)
            let blob = new Blob([res], {type: 'application/vnd.ms-excel'})
            let objectUrl = URL.createObjectURL(blob)
            // window.location.href = objectUrl
            const fileName = this.$t('balancemanagement.memberbalancemanagement')// 导出文件名
            const elink = document.createElement('a') // 创建a标签
            elink.download = fileName // a标签添加属性
            elink.style.display = 'none'
            elink.href = objectUrl
            document.body.appendChild(elink)
            elink.click() // 执行下载
            URL.revokeObjectURL(elink.href) // 释放URL 对象
            document.body.removeChild(elink) // 释放标签
            this.$Message.success(this.$t('currentdelegation.exportsuccessful'))
          })
    },
			searchByFilter() {
				let bol = Number(this.filterSearch.minBalance) > Number(this.filterSearch.maxBalance) ||
				Number(this.filterSearch.minFrozenBalance) > Number(this.filterSearch.maxFrozenBalance) ||
        Number(this.filterSearch.minAllBalance) > Number(this.filterSearch.maxAllBalance);
        
				if(bol) {
					this.$Message.warning(this.$t('balancemanagement.note14'));
				}else{
					this.$store.commit('switchLoading', true);
					this.currentPageIdx = 1;
				
					let obj = Object.assign(this.filterSearch, { pageNo: 1, pageSize: 10 });
					this.refreshPage(obj);
				}
			},
/** 
 * unit
 * walletAddress
 * account //用户名、邮箱、手机、姓名
 * 
 * property 排序字段
 * direction 0 1
*/

      changePage(pageIndex) {
        this.ifLoading = true;
				this.currentPageIdx = pageIndex;
      this.filterSearch.isOut = 0
				let	obj = Object.assign(this.filterSearch, { pageNo: pageIndex, pageSize: 10 });
        this.refreshPage(obj);
      },
      refreshPageManual() {
				for(let val in this.filterSearch) {
					this.filterSearch[val] = '';
				}
				this.ifLoading = true;
        this.refreshPage({ pageNo: 1, pageSize: 10 });
      },
      refreshPage(obj = {}) {
      obj.isOut = 0
        memberAsset(obj)
      .then( res => {
        if (!res.code) {
          this.pageNum = res.data.totalElements;
          this.userpage = res.data.content;
          this.ifLoading = false;
					this.$store.commit('switchLoading', false);
        }else this.$Message.error(this.$t('permissionlist.errorgettingdata'))
       })
      }

    },
    created() {
      this.refreshPage();
    }

  }
</script>

<style lang="less" scoped>

</style>