<style lang="less">
   .own-space{
    &-btn-box{
        margin-bottom: 10px;
        button{
            padding-left: 0;
            span{
                color: #2D8CF0;
                transition: all .2s;
            }
            span:hover{
                color: #0C25F1;
                transition: all .2s;
            }
        }
    }
    &-tra{
        width:10px;
        height:10px;
        transform:rotate(45deg);
        position:absolute;
        top:50%;
        margin-top:-6px;
        left:-3px;
        box-shadow:0 0 2px 3px rgba(0,0,0,.1);
        background-color:white;z-index:100;
    }
    &-input-identifycode-con{
        position:absolute;
        width:200px;
        height:100px;
        right:-220px;
        top:50%;
        margin-top:-50px;
        border-radius:4px;
        box-shadow:0 0 2px 3px rgba(0,0,0,.1);
    }
}

</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="person"></Icon>
                {{ $t('personalinformation.personalinformation') }} </p>
            <div>
                <Form
                    ref="userForm"
                    :model="userForm"
                    :label-width="100"
                    label-position="right"
                    :rules="inforValidate"
                >
                    <FormItem :label="$t('personalinformation.username')" prop="name">
                        <div style="display:inline-block;width:300px;">
                            <Input v-model="userForm.name" ></Input>
                        </div>
                    </FormItem>
                    <FormItem :label="$t('personalinformation.usermobile')" prop="cellphone" >
                        <div style="display:inline-block;width:204px;">
                            <Input v-model="userForm.cellphone" @on-keydown="hasChangePhone"></Input>
                        </div>
                        <div style="display:inline-block;position:relative;">
                            <Button @click="getIdentifyCode" :disabled="canGetIdentifyCode">{{ gettingIdentifyCodeBtnContent }}</Button>
                            <div class="own-space-input-identifycode-con" v-if="inputCodeVisible">
                                <div style="background-color:white;z-index:110;margin:10px;">
                                    <Input v-model="securityCode" :placeholder="$t('personalinformation.pleasefillinthesmsverificationcode')" ></Input>
                                    <div style="margin-top:10px;text-align:right">
                                        <Button type="ghost" @click="cancelInputCodeBox">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
                                        <Button type="primary" @click="submitCode" :loading="checkIdentifyCodeLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </FormItem>
                    <FormItem :label="$t('personalinformation.company')">
                        <span>{{ userForm.company }}</span>
                    </FormItem>
                    <FormItem :label="$t('personalinformation.department')">
                        <span>{{ userForm.department }}</span>
                    </FormItem>
                    <FormItem :label="$t('personalinformation.loginpassword')">
                        <Button type="text" size="small" @click="showEditPassword">{{ $t('personalinformation.changepassword') }}</Button>
                    </FormItem>
                    <div>
                        <Button type="text" style="width: 100px;" @click="cancelEditUserInfor">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
                        <Button type="primary" style="width: 100px;" :loading="save_loading" @click="saveEdit">{{ $t('personalinformation.preservation') }}</Button>
                    </div>
                </Form>
            </div>
        </Card>
        <Modal v-model="editPasswordModal" :closable='false' :mask-closable=false :width="500">
            <h3 slot="header" style="color:#2D8CF0">{{ $t('personalinformation.changepassword') }}</h3>
            <Form ref="editPasswordForm" :model="editPasswordForm" :label-width="100" label-position="right" :rules="passwordValidate">
                <FormItem :label="$t('personalinformation.originalpassword')" prop="oldPass" :error="oldPassError">
                    <Input v-model="editPasswordForm.oldPass" :placeholder="$t('personalinformation.note2')" ></Input>
                </FormItem>
                <FormItem :label="$t('personalinformation.newpassword')" prop="newPass">
                    <Input v-model="editPasswordForm.newPass" :placeholder="$t('personalinformation.note1')" ></Input>
                </FormItem>
                <FormItem :label="$t('personalinformation.confirmnewpassword')" prop="rePass">
                    <Input v-model="editPasswordForm.rePass" :placeholder="$t('personalinformation.pleaseenterthenewpasswordagain')" ></Input>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="text" @click="cancelEditPass">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
                <Button type="primary" :loading="savePassLoading" @click="saveEditPass">{{ $t('personalinformation.preservation') }}</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
export default {
    name: 'ownspace_index',
    data () {
        const validePhone = (rule, value, callback) => {
            var re = /^1[0-9]{10}$/;
            if (!re.test(value)) {
                callback(new Error(this.$t('personalinformation.note3')));
            } else {
                callback();
            }
        };
        const valideRePassword = (rule, value, callback) => {
            if (value !== this.editPasswordForm.newPass) {
                callback(new Error(this.$t('personalinformation.thetwoinputpasswordsareinconsistent')));
            } else {
                callback();
            }
        };
        return {
            userForm: {
                name: '',
                cellphone: '',
                company: '',
                department: ''
            },
            uid: '', // 登录用户的userId
            securityCode: '', // 验证码
            phoneHasChanged: false, // 是否编辑了手机
            save_loading: false,
            identifyError: '', // 验证码错误
            editPasswordModal: false, // 修改密码模态框显示
            savePassLoading: false,
            oldPassError: '',
            identifyCodeRight: false, // 验证码是否正确
            hasGetIdentifyCode: false, // 是否点了获取验证码
            canGetIdentifyCode: false, // 是否可点获取验证码
            checkIdentifyCodeLoading: false,
            inforValidate: {
                name: [
                    { required: true, message: this.$t('personalinformation.pleaseentername'), trigger: 'blur' }
                ],
                cellphone: [
                    { required: true, message: this.$t('personalinformation.pleaseenteryourmobilenumber') },
                    { validator: validePhone }
                ]
            },
            editPasswordForm: {
                oldPass: '',
                newPass: '',
                rePass: ''
            },
            passwordValidate: {
                oldPass: [
                    { required: true, message: this.$t('personalinformation.pleaseentertheoriginalpassword'), trigger: 'blur' }
                ],
                newPass: [
                    { required: true, message: this.$t('personalinformation.pleaseenteranewpassword'), trigger: 'blur' },
                    { min: 6, message: this.$t('personalinformation.pleaseenteratleast6characters'), trigger: 'blur' },
                    { max: 32, message: this.$t('personalinformation.enterupto32characters'), trigger: 'blur' }
                ],
                rePass: [
                    { required: true, message: this.$t('personalinformation.pleaseenterthenewpasswordagain'), trigger: 'blur' },
                    { validator: valideRePassword, trigger: 'blur' }
                ]
            },
            inputCodeVisible: false, // 显示填写验证码box
            initPhone: '',
            gettingIdentifyCodeBtnContent: this.$t('personalinformation.getverificationcode') // “获取验证码”按钮的文字
        };
    },
    methods: {
        getIdentifyCode () {
            this.hasGetIdentifyCode = true;
            this.$refs['userForm'].validate((valid) => {
                if (valid) {
                    this.canGetIdentifyCode = true;
                    let timeLast = 60;
                    let timer = setInterval(() => {
                        if (timeLast >= 0) {
                            this.gettingIdentifyCodeBtnContent = timeLast + this.$t('personalinformation.retryinseconds');
                            timeLast -= 1;
                        } else {
                            clearInterval(timer);
                            this.gettingIdentifyCodeBtnContent = this.$t('personalinformation.getverificationcode');
                            this.canGetIdentifyCode = false;
                        }
                    }, 1000);
                    this.inputCodeVisible = true;
                    // you can write ajax request here
                }
            });
        },
        showEditPassword () {
            this.editPasswordModal = true;
        },
        cancelEditUserInfor () {
            this.$store.commit('removeTag', 'ownspace_index');
            localStorage.pageOpenedList = JSON.stringify(this.$store.state.app.pageOpenedList);
            let lastPageName = '';
            if (this.$store.state.app.pageOpenedList.length > 1) {
                lastPageName = this.$store.state.app.pageOpenedList[1].name;
            } else {
                lastPageName = this.$store.state.app.pageOpenedList[0].name;
            }
            this.$router.push({
                name: lastPageName
            });
        },
        saveEdit () {
            this.$refs['userForm'].validate((valid) => {
                if (valid) {
                    if (this.phoneHasChanged && this.userForm.cellphone !== this.initPhone) { // 手机号码修改过了而且修改之后的手机号和原来的不一样
                        if (this.hasGetIdentifyCode) { // 判断是否点了获取验证码
                            if (this.identifyCodeRight) { // 判断验证码是否正确
                                this.saveInfoAjax();
                            } else {
                                this.$Message.error(this.$t('personalinformation.note4'));
                            }
                        } else {
                            this.$Message.warning(this.$t('personalinformation.pleaseclicktogettheverificationcodefirst'));
                        }
                    } else {
                        this.saveInfoAjax();
                    }
                }
            });
        },
        cancelEditPass () {
            this.editPasswordModal = false;
        },
        saveEditPass () {
            this.$refs['editPasswordForm'].validate((valid) => {
                if (valid) {
                    this.savePassLoading = true;
                    // you can write ajax request here
                }
            });
        },
        init () {
            this.userForm.name = 'Lison';
            this.userForm.cellphone = '17712345678';
            this.initPhone = '17712345678';
            this.userForm.company = 'TalkingData';
            this.userForm.department = this.$t('personalinformation.visualizationdepartment');
        },
        cancelInputCodeBox () {
            this.inputCodeVisible = false;
            this.userForm.cellphone = this.initPhone;
        },
        submitCode () {
            let vm = this;
            vm.checkIdentifyCodeLoading = true;
            if (this.securityCode.length === 0) {
                this.$Message.error(this.$t('personalinformation.pleasefillinthesmsverificationcode'));
            } else {
                setTimeout(() => {
                    this.$Message.success(this.$t('personalinformation.verificationcodeiscorrect'));
                    this.inputCodeVisible = false;
                    this.checkIdentifyCodeLoading = false;
                }, 1000);
            }
        },
        hasChangePhone () {
            this.phoneHasChanged = true;
            this.hasGetIdentifyCode = false;
            this.identifyCodeRight = false;
        },
        saveInfoAjax () {
            this.save_loading = true;
            setTimeout(() => {
                this.$Message.success(this.$t('personalinformation.savedsuccessfully'));
                this.save_loading = false;
            }, 1000);
        }
    },
    mounted () {
        this.init();
    }
};
</script>
