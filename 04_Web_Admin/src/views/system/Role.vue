<template>
    <div >
      <Card>
        <p slot="title">
        {{ $t('rolemanagement.rolemanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>
            {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
        </p>

        <Row class="functionWrapper">
          <div class="btnsWrapper clearfix">
            <Button type="primary" @click="addRoleBtn">{{ $t('rolemanagement.addrole') }}</Button>
          </div>
        </Row>

        <Modal
          v-model="delRole"
          :title="$t('rolemanagement.deleterole')"
          @on-ok="deleteRole"
          @on-cancel="$Message.info(this.$t('rolemanagement.cancel'))">
          <p>{{ $t('rolemanagement.deletethisrole') }}</p>
      
        </Modal>

        <Modal
          v-model="showAddRole"
          :title="$t('rolemanagement.addrole')"
          @on-ok="addRole">
          <Form label-position="right" :model="formValidate" :label-width="100" :rules="ruleValidate"> 

            <FormItem :label="$t('rolemanagement.rolename')" prop="name">
              <Input v-model="formValidate.name"></Input>
            </FormItem>

            <FormItem :label="$t('rolemanagement.roledescription')">
              <Input type="textarea" v-model="formValidate.roleDscrp"> </Input>
            </FormItem>

          </Form>
          <br>
          <Tree :data="allPermission" show-checkbox @on-check-change="addRoleTree"></Tree>

        </Modal>

        <Row >
          <Table 
          :columns="column_frist" 
          :data="userpage"
          :loading="ifLoading" 
          border>
          </Table>
        </Row>
        <Modal
            v-model="showForm"
            :title="$t('rolemanagement.modifypermissions')"
            @on-ok="confirmChange"
            @on-cancel="cancelChange">
            
            <Form>
              <FormItem :label="$t('rolemanagement.roledescription')">
                <Input type="textarea" v-model="formValidate.roleDscrp"> </Input>
              </FormItem>
            </Form>
            
            <Tree :data="permissions | filterPer" show-checkbox @on-check-change="fixTreeArray"></Tree>
        </Modal>
				<Row class="pageWrapper">
          <Page :total="pageNum" 
                class="buttomPage"
                @on-change="changePage"
								:current="currentPageIdx"
                show-elevator></Page>
        </Row>
      </Card>
      <div class="permissionWrapper" v-if="permissionWrapper">
        <p>{{ $t('rolemanagement.dataupdating') }}</p>
      </div>
    </div>
</template>

<script>

import { roleManage, queryRolePermission, addAuditRole, getAllPermission, deleteRole } from '@/service/getData'
import { setStore, getStore, removeStore } from '@/config/storage';

export default {
  data() {
    return {
			pageNum: null,
			currentPageIdx: 1,
      permissionWrapper: false,
      ifLoading: true,
      deleteRoleArr: [],
      delRole: false,
      deleteRoleID: null,

      fixedRolePermission: [],
      submitArr: [], 
      allPermission: [],
      roleId: null,
      showAddRole: false,
      showForm: false,

      formValidate: {
        name: null,
        roleDscrp: null
      },
      ruleValidate: {
        name: [
          { required: true, message: this.$t('rolemanagement.rolenamecannotbeempty'), trigger: 'blur' }
        ]
      },
      column_frist: [
        {
          title: 'ID',
          key: 'id',
          width: 80
        },
        {
          title: this.$t('rolemanagement.rolename1'),
          key: 'role'
        },
        {
          title: this.$t('systeminformationmaintenance.remarks'),
          key: 'description'
        },
        //  {
        //   title: '权限',
        //   key: ' | filterPermission'
        // },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          render: (h, obj) => {
            return h ( 'div', [
              h('Button',{
                props: {
                  type: 'info',
                  size: 'small'
                },
                style: {
                  'marginRight': '5px'
                },
                on:{
                  click: () =>{
                    this.showForm = true;
                    this.filterPermission = []; 
                    this.roleId = obj.row.id;
                    this.formValidate.name = obj.row.role;
                    this.formValidate.roleDscrp = obj.row.description;
  
                    let initTree = JSON.parse(getStore('allPermission'));
                    initTree.forEach( item => {
                      item.checked = false;
                    })
  
                    queryRolePermission({ roleId: obj.row.id }).then(res => {
                      if (!res.code) {
                        let cbIdArr = [];
                        res.data.forEach( items => {
                          items.subMenu.forEach(item => {
                            cbIdArr.push({id: item.id, parentId: item.parentId });
                          })
                        } )
                        initTree.forEach(items => {
                          (items.children).forEach(item => {
                           let judge = cbIdArr.find( (val) => {
                                return val.id === item.id;
                            });
                            if (!!judge) {
                              item.checked = true;
                            }
                          })
                        })
                    this.permissions = initTree;
                      }else {
                        this.$Message.error(this.$t('rolemanagement.dataacquisitionexception'))
                      }
                    })  
                  }
  
                }
              }, this.$t('perpetualcontractcurrencystandardmanagement.modify')),

              h('Button',{
                props: {
                  type: 'error',
                  size: 'small'
                },
                on:{
                  click: () =>{
                    console.log(obj.row.id);
                    this.deleteRoleID = obj.row.id;
                    this.delRoleBtn();
                  }
                }
              }, this.$t('secondcontractcompensationsetting.delete')),

            ] ) 


          }
        },
      ],  
      permissions: [],
      userpage: [],
    }
  },

  methods: {
		
		changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      this.refreshPage({ pageNo: pageIndex, pageSize: 10 });
    },
    deleteRole() {
      deleteRole({ id: this.deleteRoleID })
      .then( res => {
        if(!res.code) {
          this.$Message.success(this.$t('currencysetting.deletionsucceeded'));
          this.refreshPage();
        }else this.$Message.error(res.message);
      } )
    },
    delRoleBtn() {
      this.delRole = true;
    },
    addRoleBtn() {
			this.submitArr = [];
      this.formValidate.roleDscrp =  this.formValidate.name = null;
      this.showAddRole = true;
			this.allPermission = JSON.parse(getStore('allPermission'));
			(this.allPermission).forEach( (allItem, allIdx) =>　{
				this.submitArr.push(allItem.id);
				if(allItem.children!==undefined) {
					allItem.children.forEach( itemChild => {
						this.submitArr.push(itemChild.id);
					})
				}
			})
    },
    addRoleTree(val) { 
			let arr = [];
			this.submitArr = [];
      val.forEach( item => {
        (this.allPermission).forEach( (allItem, allIdx) =>　{
          allItem.children.forEach( itemChild => {
            if(itemChild.id === item.id) {
              arr.push(allItem.id);
            }
          })
        })
      })

      let parentAarr = [...new Set(arr)];

      let sonArr = [];
      
      val.forEach(item => {
        if (!item.children) {
          sonArr.push(item.id);
        }
      })
      this.submitArr = [ ...parentAarr, ...sonArr ];

    },
    addRole() { 
			// console.log(this.submitArr);
			// // if(!this.submitArr.length){
			// // 	this.submitArr = this.allPermission;
			// // }
			
			if(this.submitArr.indexOf(8)===-1) {
				this.submitArr.push(...[8, 11, 12, 13, 66, 90, 116])
			}
			
      if(this.formValidate.name === '' || !this.formValidate.name) {
        this.$Message.warning(this.$t('rolemanagement.rolenamecannotbeempty'));
      }else{
				this.permissionWrapper = true;
        let obj = { 
          role: this.formValidate.name, 
          description: this.formValidate.roleDscrp, 
          permissions: this.submitArr 
        }

        addAuditRole(obj)
        .then( res => {
          if (!res.code) {
            this.$Message.success(this.$t('perpetualcontractcurrencystandardmanagement.operationsucceeded'));
            this.refreshPage();
					}else this.$Message.error(res.message);
					this.permissionWrapper = false;
					
				})
				.catch(err => {console.log(err)})
      }

    },
    selectArrayFn (val) { 
      let fromateArr = [];
      val.forEach( item => {
        if (!!item.children) {
          fromateArr.push(item);
        }
      })

      let definedArray = [];
      fromateArr.forEach(item => {
        definedArray.push(item.id);
        item.children.forEach( it => {
           definedArray.push(it.id);
        })
      })
      
     return definedArray;
      
      
    },
    fixTreeArray(val) {
      let fixedIdArr = [];
      
      val.forEach(item => {
        fixedIdArr.push(item.id);
        fixedIdArr.push(item.parentId);
      } )
      this.fixedRolePermission = [...new Set(fixedIdArr)];
    },
		
    confirmChange () {
			this.permissionWrapper = true;
			if(this.fixedRolePermission.indexOf(8)===-1) {
				this.fixedRolePermission.push(...[8, 11, 12, 13, 66, 90, 116])
			}
       let obj = {
         id: this.roleId, 
         role: this.formValidate.name, 
         description: this.formValidate.roleDscrp, 
         permissions:  this.fixedRolePermission
			 }
			 
      addAuditRole(obj)
      .then( res =>{
        if (!res.code) {
          this.$Message.success(this.$t('rolemanagement.rolepermissionsmodifiedsuccessfully'));
          this.refreshPage();
        }else{
          this.$Message.error(this.$t('rolemanagement.rolepermissionmodificationfailed'));
        }
      this.permissionWrapper = false;
        
      })

    },
    cancelChange() {
      this.$Message.info(this.$t('versionmanagement.modificationcanceled'));
    },
    getAllPermissionFn(bol = true) { 
      let allPermission = [];
     getAllPermission() 
      .then( res => {
        if( !res.code ) {
          res.data.forEach(item => {
            let firstTree = {
                title: item.title,
                id: item.id,
                checked: bol,
                children: []
              };
            (item.subMenu).forEach( it => {
              let secTree = {
                id: it.id,
                title: it.title,
                parentId: it.parentId
              };
              firstTree.children.push(secTree)
            })
            allPermission.push( firstTree );

            removeStore('allPermission');
            
            setStore('allPermission', allPermission); //处理后数据用于tree

          })
        }
      })
      
    },
    refreshPageManual() {
      this.refreshPage();
    },
    refreshPage() {
      this.ifLoading = true;
      
      roleManage()
      .then( res => {
        if(!res.code){
          this.userpage =  res.data.content;
          this.ifLoading = false;          
        }else this.$Message.error(res.message);
      }, err => {
        console.log(err);
      })
    }
  },
  created () {
		this.getAllPermissionFn();
    this.refreshPage();
  },
  filters: {
    filterPer(val) {
      val.forEach( item => {
        if(item.children.length>0){
          item.children.forEach( it => {
            if(it.id===116 || it.id===90){
              it.disableCheckbox = true;
              it.checked = true;
            }
          })
        }
      })
      return val;
      
    }
  }
}
</script>

<style lang="less" scoped>
  .permissionWrapper{
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .2);
    p{
      position: absolute;
      top: 50%;
      left: 45%;
      transform: -50%;
      font-size: 25px;
      font-style: '黑体';
      text-align: center;
      color: #fff;

    }
  }
</style>