
<template>
  <div>
    <Card>
      <p slot="title">
        {{ $t('systeminformationmaintenance.systeminformationmaintenance') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>
			<Row class="functionWrapper">
				<div class="btnsWrapper clearfix ">
					<Button type="primary" @click="addNew">{{ $t('collectionconfigurationmanagement.new') }}</Button>
				</div>
			</Row>		
      <Row>
        <Table 
          :columns="columns_first" 
          :data="userpage" 
          border
          :loading="ifLoading">
				</Table>
      </Row>

      <div class="pageWrapper">
        <Page  
          :total="totalNum" 
          :current="currentPageIdx"   
          @on-change="changePage" 
          show-elevator></Page>
      </div>

			<Modal 
				v-model="subModal"
				:title="$t('systeminformationmaintenance.createdictionary')"
				@on-ok="comfirmSub"
				@on-cancel="cancelSub"
				:closable='false'
				:mask-closable='false'
				:width="400"
			>
			<Form 
				:model="formObj" 
				label-position="right" 
				:label-width="50" 
				:rules="validateForm">
				<FormItem :label="$t('systeminformationmaintenance.key')" prop="bond">
					<Input v-model="formObj.bond"></Input>
				</FormItem>
				<FormItem :label="$t('systeminformationmaintenance.value')" prop="value">
					<Input v-model="formObj.value"></Input>
				</FormItem>
				<FormItem :label="$t('orderdetails.remarks')">
					<Input type="textarea" v-model="formObj.comment"></Input>
				</FormItem>
			</Form>
			</Modal>


    </Card>
    </div>
</template>

<script>

import { dictionaryQuery, createDictionary, updateDictionary } from '@/service/getData';

export default {
	name: 'AccessLog',
  data() {
		
    return {
			validateForm: {
				bond: [
					{ required: true, message: this.$t('systeminformationmaintenance.pleasefillintheinformation'), type: 'string', trigger: 'blur' }
				],
				value: [
					{ required: true, message: this.$t('systeminformationmaintenance.pleasefillintheinformation'), type: 'string', trigger: 'blur' }
				]
			},
			isCreateNew: true,
			subModal: false,
			formObj: {
				bond: '',
				value: '',
				comment: ''
			},
			filterSearch: {
				pageNo: 1,
				pageSize: 10,
				property: [],
				direction: [],
			},
      currentPageIdx: 1,
      ifLoading: true,
      totalNum: null,
      columns_first: [
        {
					title: 'id',
					key: 'id'
        },
        {
          title: this.$t('systeminformationmaintenance.key1'),
          key: "bond"
        },
        {
          title: this.$t('systeminformationmaintenance.value1'),
          key: "value"
        },
				{
          title: this.$t('systeminformationmaintenance.remarks1'),
          key: "comment"
        },
        {
					title: this.$t('secondcontractcompensationsetting.creationtime'),
					key: 'creationTime'
        },
        {
          title: this.$t('systeminformationmaintenance.updatetime'),
          key: "updateTime"
				},
				{
					title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
					key: 'opration',
					render: (h, obj) =>{
						return h('Button',{
							props: {
								size: 'small',
								type: 'info'
							},
							on: {
								click: () => {
									this.subModal = true;
									this.isCreateNew = false;
									for(let key in this.formObj)  {
										this.formObj[key] = obj.row[key];
									}
								}
							}
						},this.$t('perpetualcontractcurrencystandardmanagement.modify'))
					}
				}
      ],
      userpage: [],
    };
  },
  methods: {
		addNew() {
			this.subModal = this.isCreateNew = true;
		},
		comfirmSub() {
			if(!String(this.formObj.value).trim().length 
			|| !String(this.formObj.bond).trim().length) 
			{return this.$Message.warning(this.$t('systeminformationmaintenance.pleaseimprovetheinformation'));}
			let fn = Object;
			if(this.isCreateNew) {
				fn = createDictionary(this.formObj);
			}else{
				let obj = { value: this.formObj.value, comment: this.formObj.comment }
				fn = updateDictionary( this.formObj.bond, obj);
			}
			fn.then(res => {
				if(!res.code) {
					this.$Message.success(res.message);
					this.cancelSub();
				}else this.$Message.error(res.message);
				this.refreshPage();
			})
			.catch(err => {
				console.log(err);
			})
		},
		cancelSub() {
			for(let key in this.formObj)  {
				this.formObj[key] = '';
			}
		},
		searchByFilter() {
			this.filterSearch.pageNo = 1;
			this.currentPageIdx = 1;
      this.refreshPage(this.filterSearch);
		},
    refreshPageManual() {
			this.currentPageIdx = 1;
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.filterSearch.adminName = '';
			this.filterSearch.module = -1;
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
			this.ifLoading = true;
      dictionaryQuery('', obj)
      .then(res => {
        if(!res.code){
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
				}else this.$Message.error(res.message);
      });
    },
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
      this.refreshPage(this.filterSearch);
    },
  },
  created() {
		this.refreshPage()
  }
}
</script>

<style scoped lang='less'>
 
</style>
