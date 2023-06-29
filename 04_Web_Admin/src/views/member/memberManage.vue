<template>
	<div>
    <Card>
      <p slot="title">
        {{ $t('membermanagement.membermanagement') }}
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>{{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
        </Button>
      </p>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix">
					<div class="poptip">
						<Poptip trigger="hover" :content="$t('membermanagement.note1')" placement="bottom-start">
							<Input :placeholder="$t('membermanagement.note1')"
										v-model="filterSearch.account"
										style="width: 300px"/>
							</Input>
						</Poptip>
					</div>

					<div class="poptip">
						<span>{{ $t('certifiedmerchants.memberstatus') }}</span>
						<Select v-model="filterSearch.commonStatus">
							<Option v-for="item in memberStatusArr"
										:value="item.status"
										:key="item.status">{{ item.text }}</Option>
						</Select>
					</div>

          <div class="poptip">
            <span>{{ $t('membermanagement.agentornot') }}</span>
            <Select v-model="filterSearch.superPartner">
              <Option v-for="item in superPartnerArr"
                    :value="item.value"
                    :key="item.value">{{ item.text }}</Option>
            </Select>
          </div>

					<div class="btns">
						<Button type="info" size="small" @click="searchByFilter">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
					</div>

        </div>
        <div class="btnsWrapper clearfix">
          <Button type="success" @click="exportExcel">{{ $t('positionmanagementcontractassetmanagement.export') }}</Button>
        </div>
      </Row>

      <Row class="margin-top-10 searchable-table-con1">
          <Table
            :columns="columns_first"
            :data="userpage"
            border
            :loading="ifLoading"
            ref="tabel"
            class='user_center'>
          </Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="totalNum" style='margin-top:8px' :current="currentPageIdx"   @on-change="changePage" show-elevator></Page>
      </Row>
    </Card>
       <Modal  :title="$t('membermanagement.setinvitees')" v-model="detailModel" :mask-closable="false"  @on-ok="setInviterDetail">
            <ul>
                <li><span><i>*</i>ID：</span>
                    <p>
                        <Input v-model="this.userId" disabled></Input>
                        <span>{{ }}</span>
                    </p>
                </li>
                <li>
                    <span><i>*</i>{{ $t('membermanagement.settheidoftheinvitee') }}</span>
                    <p> <Input v-model="inviterId"></Input> </p>
                </li>
            </ul>
        </Modal>
<!-- 		<Modal  title="层级迁移"  v-model="inviteFormVisible" :mask-closable="false">
		  <Form :model="inviteFormData" :rules="inviteFormRules" ref="inviteForm">
		    <FormItem label="会员名称：" prop="username">
		      <span>{{inviteFormData.username}}</span>
		    </FormItem>
		    <FormItem label="迁移至：" prop="toUsername">
		      <Input v-model="inviteFormData.toUsername" placeholder="请输入邮箱或手机号" clearable></Input>
		    </FormItem>
		  </Form>
		  <div slot="footer">
		    <Button @click="hideInviteForm">取消</Button>
		    <Button type="primary" @click="inviteFormSubmit" :loading="inviteFormLoading">确定</Button>
		  </div>
		</Modal> -->
  </div>
</template>

<script>

import { setStore, getStore, removeStore } from '@/config/storage';
import { memberManage, forbiddenMember, forbiddenMemberTrans, setSuperPartner,setInviter,memberUpdateInvite } from '@/service/getData';

export default {
  data () {
    return {
			currentPageIdx: 1,
			filterSearch: {
				account: '',
				commonStatus: '',
                superPartner: '',
			},
            //弹窗和信息
            detailModel: false,
            userId: "",
            inviterId: null,
			memberStatusArr: [
				{ status: 0, text: this.$t('querymarginstrategy.normal') },
				{ status: 1, text: this.$t('membermanagement.illegal') },
				{ status: '', text: this.$t('transactiondetailsinlegalcurrency.all') },
			],
      superPartnerArr: [
        { value: '0', text: this.$t('perpetualcontractcurrencystandardmanagement.no') },
        { value: '1', text: this.$t('perpetualcontractcurrencystandardmanagement.yes') }
      ],
      totalNum: null,
      ifLoading: true,
      usemmuber:'',
      pageIndex:1,
      loading:true,
      columns_first: [
        {
          type: 'selection',
          width: 60
        },
        {
          title: this.$t('auditdetails.memberid'),
          key: 'id',
          width: 80
        },
        {
          title: this.$t('memberinvitationlist.membername'),
          key: 'username',
          width: 180
        },
        {
          title: this.$t('membermanagement.mobilenumber'),
          key: 'mobilePhone',
          width: 110
        },
        {
          title: this.$t('certifiedmerchants.memberlevel'),
          key: 'memberLevel',
          width:90,
          render: (h, obj) => {
            let memberLevel = obj.row.memberLevel;
            let memberLevelTxt = null;
            if (!memberLevel) memberLevelTxt = this.$t('certifiedmerchants.ordinarymember')
            else if (memberLevel===1) memberLevelTxt = this.$t('membermanagement.realname')
            else if (memberLevel===2) memberLevelTxt = this.$t('certifiedmerchants.certification')
            return h('span',{
            }, memberLevelTxt)
          }
        },
        {
          title: this.$t('businessinformation.realname'),
          key: 'realName',
          width:90
        },
        {
          title: this.$t('membermanagement.inviteeid'),
          key: 'inviterId',
          width:90
        },
        {
          title: this.$t('realnamemanagement.registrationtime'),
          width: 150,
          key: 'registrationTime'
				},
				{
          title: this.$t('membermanagement.transactionstatus'),
          key: 'transactionStatus',
          width:90,
          render: (h, obj) => {
            let  userStatus = obj.row.transactionStatus;
           	let statusTxt = !userStatus ? this.$t('currencyextensionmanagement.disable') : this.$t('querymarginstrategy.normal');
            return h('span', {
            }, statusTxt);
          }
        },

        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          width:90,
          key: 'status',
          render: (h, obj) => {
            let  userStatus = obj.row.status;
            let statusTxt = null;
            userStatus === 0 ? statusTxt = this.$t('querymarginstrategy.normal') : statusTxt = this.$t('membermanagement.illegal');

            return h('span', {
            },statusTxt);
          }
        },
        {
          title: this.$t('surrendermanagement.membermailbox'),
          key: 'email',
          width: 200
        },
        {
         title:this.$t('membermanagement.agent'),
		 width:120,
         key:"superPartner",
         render:(h, obj) =>{
           let superPartner = obj.row.superPartner;
           let text = null;
           superPartner == 0 && (text = this.$t('certifiedmerchants.ordinarymember'));
           superPartner == 1 && (text = this.$t('membermanagement.superagent'));
           superPartner == 2 && (text = this.$t('membermanagement.superpartner'));
           return h('span',{},text);
         }
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          key: 'action',
          width: 345,
          fixed: 'right',
          align: 'center',
          render: (h, obj) => {
						let memberStatus = !obj.row.status ? 1 : 0;
						let memberTxt = !obj.row.status ? this.$t('currencyextensionmanagement.disable') : this.$t('membermanagement.unblock');
						let memberStatusTrans = !obj.row.transactionStatus ? 1 : 0;
						let memberTxtTrans = !obj.row.transactionStatus ? this.$t('perpetualcontractcurrencystandardmanagement.allowtransactions') : this.$t('membermanagement.prohibittransactions');
            let memberSuperSpartner = obj.row.superPartner === '0' ? '1' : '0';
            let setSuperSpartnerTxt = obj.row.superPartner === '0' ? this.$t('membermanagement.authorizedagents') : this.$t('membermanagement.cancelagent');
						// let
						return h('div', {
						}, [
							h('Button', {
								props: {
									type: 'info',
									size: 'small',
								},
								on: {
									click: () => {
										removeStore('memberID');
										setStore('memberID', obj.row.id);
										this.$router.push('/member/memberdetail');
									}
								}
							}, this.$t('transactiondetailsinlegalcurrency.view')),
                            h(
                                "Button",
                                {
                                    props: {
										type: "primary",
										size: "small",
									}, 
                                    on: {
                                        click: () => {
                                            if(obj.row.inviterId!=null){
                                                this.$Message.error(this.$t('membermanagement.inviteealreadyexists'));
                                                return;
                                            }
                                            this.detailModel = true;
                                            this.userId = obj.row.id;
                                            // this.inviterId = obj.row.inviterId;
                                        }
                                    }
                                },
                                this.$t('membermanagement.setinvitees')
                            ),
							// h('Button', {
							// 	props: {
							// 		type: 'info',
							// 		size: 'small',
							// 	},
							// 	on: {
							// 		click: () => {
							// 			this.clickInviteForm(obj.row)
							// 		}
							// 	}
							// }, '层级迁移'),
							h('dropdown', {
								props: {
									transfer: true
								},
								on: {
									'on-click': (name) => {
										if(name === 'forbidden') {

											forbiddenMember({ memberId: obj.row.id, status: memberStatus })
											.then(res => {
												if(!res.code) {
													this.$Message.success(res.message)
												}else this.$Message.error(res.message)
												this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10, property: 'registrationTime', direction: 1 })
											})
											.catch( err => { console.log(err)})
										}else if(name === 'forbiddenTrans') {
											forbiddenMemberTrans({ memberId: obj.row.id, status: memberStatusTrans })
											.then(res => {
												if(!res.code) {
													this.$Message.success(res.message)
												}else this.$Message.error(res.message)
												this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10, property: 'registrationTime', direction: 1 })
											})
										}else if(name === 'setSuperSpartner') {
                      setSuperPartner({ memberId: obj.row.id, superPartner: memberSuperSpartner })
                      .then(res => {
                        if(!res.code) {
                          this.$Message.success(res.message)
                        }else this.$Message.error(res.message)
                        this.refreshPage({ pageNo: this.currentPageIdx, pageSize: 10, property: 'registrationTime', direction: 1 })
                      })
                    }
									}
								}
							}, [
								h('DropdownMenu', {
									slot: 'list'
								}, [
										h("DropdownItem",{
											props: {
												name: 'forbidden'
											}
										}, memberTxt),
										h("DropdownItem",{
											props: {
												name: 'forbiddenTrans'
											}
										}, memberTxtTrans),
                    h("DropdownItem",{
                      props: {
                        name: 'setSuperSpartner'
                      }
                    }, setSuperSpartnerTxt)
								]),
								h("Button",{
									props: {
										type: "text",
										size: "small"
									},
									style: {
										color: '#2d8cf0',
										marginRight: "5px"
									},
								},this.$t('membermanagement.more'))
							])
						])

          }
        }
      ],
      userpage: [],
      inviteFormVisible: false,
      inviteFormData: {
			  memberId: "",
			  username: "",
			  toUsername: "",
      },
      inviteFormRules: {
        toUsername: [
          {required: true, message: this.$t('membermanagement.pleaseenteremailormobilenumber'), trigger: "blur"}
        ],
      },
      inviteFormLoading: false
    }
  },
  methods: {
    clickInviteForm(item) {
      this.inviteFormData.memberId = item.id
      this.inviteFormData.username = item.username
      this.inviteFormVisible = true
    },
    hideInviteForm() {
      this.inviteFormVisible = false
    },
    inviteFormSubmit() {
      if (this.inviteFormLoading) {
        return false
      }
      this.$refs["inviteForm"].validate(valid => {
        if (valid) {
          this.inviteFormLoading = true;
          let data = Object.assign({}, this.inviteFormData);
          memberUpdateInvite(data).then(response => {
            this.inviteFormLoading = false;
            if (response.code) {
              this.$Message.error(response.message);
              return false;
            }
            this.$Message.success(this.$t('currencywithdrawalauditmanagement.operationsuccessful'));
            this.inviteFormVisible = false;
          })
          .catch(() => {
            this.inviteFormLoading = false
            this.$Message.error(this.$t('membermanagement.networkerror'));
          });
        }
      });
    },
    refreshPageManual() {
    for(let val in this.filterSearch)  {
			this.filterSearch[val] = '';
		}
		this.currentPageIdx = 1;
      this.refreshPage({ pageNo: 1, pageSize: 10, property: 'registrationTime', direction: 1 });
    },
    exportExcel () {
      this.$refs.tabel.exportCsv({
        filename: 'MemberManage'
      });
    },
    searchByFilter(){
			this.$store.commit('switchLoading', true);
      memberManage(this.filterSearch)
      .then( res => {
        if (!res.code) {
          this.ifLoading = false;
          this.userpage = res.data.content;
					this.totalNum = res.data.totalElements;
					this.$store.commit('switchLoading', false);
        }else console.log(this.$Message.error(res.message))
			})
			.catch(err => console.log(err))
    },

    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
			this.ifLoading = true;
			let obj =Object.assign({ pageNo: pageIndex, pageSize: 10, property: 'registrationTime', direction: 1 }, this.filterSearch);
      this.refreshPage(obj);
    },
      setInviterDetail() {
          let params = {
              id: this.userId,
              inviterId: this.inviterId
          };
          setInviter(params).then(res => {
              if (!res.code) {
                  this.$Message.success(this.$t('perpetualcontractcurrencystandardmanagement.modificationsucceeded'));
                  this.refreshPageManual();
              } else {
                  this.$Message.error(res.message);
              }
          })
      },
    refreshPage(obj = {}) {
			this.ifLoading = true;
      memberManage(obj)
      .then( res => {
        if (!res.code) {
          this.ifLoading = false;
          this.userpage = res.data.content;
          this.totalNum = res.data.totalElements;
        }
      })
		}
  },
  created() {
    this.refreshPage({  property: 'registrationTime', direction: 1 });
  },
  // watch: {
  //   // account( newval, oldVal ) {
  //   //   if(!String(newval).length ) {
  //   //     this.refreshPage({  property: 'registrationTime', direction: 1 });
  //   //   }
  //   // }
  // }
}
</script>

<style lang="less" scoped>
  .search-mask{
    position: absolute;
    z-index: 8888;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, .2);
  }
</style>
