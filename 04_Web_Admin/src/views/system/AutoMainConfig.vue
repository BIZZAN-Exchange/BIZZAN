<template>

  <div>
    <Card>

      <p slot="title">
        {{ $t('collectionconfigurationmanagement.collectionconfigurationmanagement') }} <Button type="primary" size="small" @click="onReset">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>


      <Row class="functionWrapper">
        <div class="btnsWrapper clearfix">
          <Button type="primary" @click="handleEncrypt(null,null)">{{ $t('collectionconfigurationmanagement.encryptiontool') }}</Button>
          <Button type="primary" @click="handleUpdateContract(null,null)">{{ $t('collectionconfigurationmanagement.synchronizecurrency') }}</Button>
          <Button type="primary" @click="handleSetPassword(null,null)">{{ $t('collectionconfigurationmanagement.setthecollectionpassword') }}</Button>
          <Button type="primary" @click="handleForm(null,null)">{{ $t('collectionconfigurationmanagement.addcollectionconfiguration') }}</Button>
        </div>
      </Row>

      <Row>
        <Table
            :columns="columnList"
            :data="list"
            :loading="loading"
            border>
        </Table>
      </Row>


      <Row class="pageWrapper">
        <Page :total="total"
              :pageSize="query.pageSize"
              class="buttomPage"
              @on-change="changePage"
              :current="query.page"
              show-elevator></Page>
      </Row>

      <Modal
          :title="formMap[formName]"
          v-model="formVisible"
          :mask-closable="false"
      >
        <Form :model="formData" :rules="formRules" ref="dataForm">
          <FormItem :label="$t('currencywithdrawalauditmanagement.currencyname')" prop="coinname">
            <Select v-model="formData.coinname" style="width:260px">
              <Option v-for="item in coinList" :value="item.name" :key="item.name">{{ item.name }}</Option>
            </Select>
          </FormItem>
          <FormItem :label="$t('currencywithdrawalauditmanagement.currencyagreement')" prop="protocol">
            <Select v-model="formData.protocol" style="width:260px">
              <Option v-for="item in protocolList" :value="item.protocol + ''" :key="item.protocol">{{ item.protocolname }}</Option>
            </Select>
          </FormItem>
          <FormItem :label="$t('collectionconfigurationmanagement.minimumcollectionquantity')" prop="minnum">
            <Input v-model="formData.minnum" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('collectionconfigurationmanagement.collectionaddress')" prop="address">
            <br/>
<!--            <div style="color: red;">请使用加密工具加密后填写</div>-->
            <Input v-model="formData.address" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button @click="hideForm">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
          <Button type="primary" @click="formSubmit" :loading="formLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
        </div>
      </Modal>

      <Modal
          :title="$t('collectionconfigurationmanagement.collect')"
          v-model="collectVisible"
          :mask-closable="false"
      >
        <Form :model="formData" :rules="formRules" ref="dataForm">
          <FormItem :label="$t('currencywithdrawalauditmanagement.currencyname')" prop="coinname">
            <Select v-model="formData.coinname" disabled="disabled" style="width:260px">
              <Option v-for="item in coinList" :value="item.name" :key="item.name">{{ item.name }}</Option>
            </Select>
          </FormItem>
          <FormItem :label="$t('currencywithdrawalauditmanagement.currencyagreement')" prop="protocol">
            <Select v-model="formData.protocol" disabled="disabled" style="width:260px">
              <Option v-for="item in protocolList" :value="item.protocol + ''" :key="item.protocol">{{ item.protocolname }}</Option>
            </Select>
          </FormItem>
<!--          <FormItem label="最低归集数量：" prop="minnum">-->
<!--            <Input v-model="formData.minnum" placeholder="请输入" clearable></Input>-->
<!--          </FormItem>-->
          <FormItem :label="$t('collectionconfigurationmanagement.collectionaddress')" prop="address">
            <br/>
            <Input disabled="disabled" v-model="formData.address" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')"></Input>
          </FormItem>
          <FormItem :label="$t('collectionconfigurationmanagement.password')" prop="password">
            <br/>
            <Input type="password" v-model="formData.password" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button @click="hideCollectForm">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
          <Button type="primary" @click="collectSubmit" :loading="formLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
        </div>
      </Modal>

      <Modal
          :title="$t('collectionconfigurationmanagement.setthecollectionpassword')"
          v-model="setPasswordVisible"
          :mask-closable="false"
      >
        <Form :model="formData" :rules="formRules" ref="dataForm">
          <FormItem :label="$t('currencywithdrawalauditmanagement.currencyagreement')" prop="protocol">
            <Select v-model="formData.protocol" style="width:260px">
              <Option v-for="item in protocolList" :value="item.protocol + ''" :key="item.protocol">{{ item.protocolname }}</Option>
            </Select>
          </FormItem>
          <FormItem :label="$t('collectionconfigurationmanagement.password')" prop="password">
            <br/>
            <Input type="password" v-model="formData.password" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button @click="hideSetPasswordForm">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
          <Button type="primary" @click="setPasswordSubmit" :loading="formLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
        </div>
      </Modal>

      <Modal
          :title="$t('collectionconfigurationmanagement.synchronizecurrency')"
          v-model="updateContractVisible"
          :mask-closable="false"
      >
        <Form :model="formData" :rules="formRules" ref="dataForm">
          <FormItem :label="$t('currencywithdrawalauditmanagement.currencyagreement')" prop="protocol">
            <Select v-model="formData.protocol" style="width:260px">
              <Option v-for="item in protocolList" :value="item.protocol + ''" :key="item.protocol">{{ item.protocolname }}</Option>
            </Select>
          </FormItem>
          <FormItem :label="$t('collectionconfigurationmanagement.password')" prop="password">
            <br/>
            <Input type="password" v-model="formData.password" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button @click="hideUpdateContractForm">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
          <Button type="primary" @click="updateContractSubmit" :loading="formLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
        </div>
      </Modal>

      <Modal
          :title="$t('collectionconfigurationmanagement.encryptiontool')"
          v-model="encryptVisible"
          :mask-closable="false"
      >
        <Form :model="formData" :rules="formRules" ref="dataForm">

          <FormItem :label="$t('collectionconfigurationmanagement.password')" prop="password">
            <br/>
            <Input type="password" v-model="formData.password" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('collectionconfigurationmanagement.originaltext')" prop="message">
            <br/>
            <Input v-model="formData.message" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('collectionconfigurationmanagement.ciphertext')">
            <br/>
            <textarea cols="75" rows="3" v-model="formData.messageEncrypt" clearable></textarea>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button @click="hideEncryptForm">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
          <Button type="primary" @click="encryptSubmit" :loading="formLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
        </div>
      </Modal>

    </Card>
  </div>

</template>

<script>
import {
  automainconfigCoinList,
  automainconfigProtocolList,
  automainconfigList,
  automainconfigSave,encryptMessage,
  collectCoin, setPassword,updateContract
} from '../../service/getData';

const formJson = {
  id: "",
  coinid: 0,
  coinname: "",
  protocol: "",
  protocolname: "",
  minnum: 0,
  address: "",
  password: "",
  message: "",
  messageEncrypt: "",
};
export default {
  computed: {
    comProtocolName() {
      return (protocol) => {
        let item = this.protocolList.find(o => o.protocol == protocol)
        return item.protocolname
      }
    }
  },
  components: {},
  data() {
    return {
      columnList: [
        {
          title: this.$t('collectionconfigurationmanagement.currencyname1'),
          key: "coinname",
          width: 100
        },
        {
          title: this.$t('currencywithdrawalauditmanagement.agreementname'),
          key: "protocolname",
          render: (h, obj) => {
            let statusD = obj.row.protocol;
            return h('div', this.comProtocolName(statusD))
          }
        },
        {
          title: this.$t('collectionconfigurationmanagement.minimumcollectionquantity'),
          key: "minnum"
        },
        {
          title: this.$t('collectionconfigurationmanagement.collectionaddress1'),
          key: "address"
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          width: 140,
          render: (h, obj) => {
            return (
                "div",
                    [
                      h(
                          "Button",
                          {
                            props: {
                              type: "info",
                              size: "small"
                            },
                            on: {
                              click: () => {
                                this.handleCollect(obj.index, obj.row)
                              }
                            }
                          },
                          this.$t('collectionconfigurationmanagement.collection')
                      ),
                      h(
                          "Button",
                          {
                            props: {
                              type: "info",
                              size: "small"
                            },
                            on: {
                              click: () => {
                                this.handleForm(obj.index, obj.row)
                              }
                            }
                          },
                          this.$t('perpetualcontractcurrencystandardmanagement.modify')
                      )
                    ]
            );
          }
        }
      ],
      query: {
        status: "-1",
        pageNo: 1,
        pageSize: 10,
      },
      list: [],
      total: 0,
      loading: true,
      coinList: [],
      protocolList: [],
      index: null,
      formName: null,
      formMap: {
        add: this.$t('collectionconfigurationmanagement.new'),
        edit: this.$t('collectionconfigurationmanagement.edit')
      },
      formData: formJson,
      formRules: {
        coinname: [
          {required: true, message: this.$t('currencywithdrawalauditmanagement.pleaseselectacurrency'), trigger: "charge"}
        ],
        protocol: [
          {required: true, message: this.$t('currencywithdrawalauditmanagement.pleaseselectanagreement'), trigger: "charge"}
        ],
        password: [
          {required: true, message: this.$t('collectionconfigurationmanagement.pleaseenterpassword'), trigger: "charge"}
        ]
      },
      formVisible: false,
      collectVisible: false,
      setPasswordVisible: false,
      updateContractVisible: false,
      encryptVisible: false,
      formLoading: false,
    };
  },
  methods: {
    changePage(pageIndex) {
      this.query.pageNo = pageIndex
      this.getList()
    },
    onReset() {
      this.query = {
        status: "-1",
        pageNo: 1,
        pageSize: 10,
      };
      this.getList();
    },
    onSubmit() {
      this.$router.push({
        path: "",
        query: this.query
      });
      this.getList();
    },
    getCoinList() {
      automainconfigCoinList()
          .then(response => {
            if (response.code) {
              this.$Message.error(response.message);
            }
            this.coinList = response.data || [];
          })
          .catch(() => {
          });
    },
    getProtocolList() {
      automainconfigProtocolList()
          .then(response => {
            if (response.code) {
              this.$Message.error(response.message);
            }
            this.protocolList = response.data || [];
          })
          .catch(() => {
          });
    },
    getList() {
      this.loading = true;
      automainconfigList(this.query)
          .then(response => {
            this.loading = false;
            if (response.code) {
              this.$Message.error(response.message);
            }
            let list = response.data.content || [];
            let tempList = []
            for (let item of list) {
              item.protocol = item.protocol + ""
              tempList.push(item)
            }
            this.list = tempList
            this.total = response.data.totalElements || 0;
          })
          .catch(() => {
            this.loading = false;
            this.list = [];
            this.total = 0;
            this.roles = [];
          });
    },
    // 刷新表单
    resetForm() {
      if (this.$refs["dataForm"]) {
        // 刷新表单
        this.$refs["dataForm"].resetFields();
      }
    },
    handleEncrypt(index, row) {
      // 加载协议列表
      this.encryptVisible = true;
      this.formData = JSON.parse(JSON.stringify(formJson));
      if (row !== null) {
        this.formData = Object.assign({}, row);
      }
      this.resetForm()
    },
    handleUpdateContract(index, row) {
      // 加载协议列表
      this.getProtocolList()
      this.updateContractVisible = true;
      this.formData = JSON.parse(JSON.stringify(formJson));
      if (row !== null) {
        this.formData = Object.assign({}, row);
      }
      this.resetForm()
    },
    handleSetPassword(index, row) {
      // 加载协议列表
      this.getProtocolList()
      this.setPasswordVisible = true;
      this.formData = JSON.parse(JSON.stringify(formJson));
      if (row !== null) {
        this.formData = Object.assign({}, row);
      }
      this.resetForm()
    },

    // 显示表单
    handleCollect(index, row) {
      // 加载币种列表
      this.getCoinList()
      // 加载协议列表
      this.getProtocolList()
      this.collectVisible = true;
      this.formData = JSON.parse(JSON.stringify(formJson));
      if (row !== null) {
        this.formData = Object.assign({}, row);
      }
      this.formName = "add";
      this.resetForm()
      if (index !== null) {
        this.index = index;
        this.formName = "edit";
      }
    },

    // 显示表单
    handleForm(index, row) {
      // 加载币种列表
      this.getCoinList()
      // 加载协议列表
      this.getProtocolList()
      this.formVisible = true;
      this.formData = JSON.parse(JSON.stringify(formJson));
      if (row !== null) {
        this.formData = Object.assign({}, row);
      }
      this.formName = "add";
      this.resetForm()
      if (index !== null) {
        this.index = index;
        this.formName = "edit";
      }
    },
    // 隐藏表单
    hideForm() {
      // 更改值
      this.formVisible = false;
      return true;
    },
    hideCollectForm() {
      // 更改值
      this.collectVisible = false;
      return true;
    },
    hideSetPasswordForm() {
      // 更改值
      this.setPasswordVisible = false;
      return true;
    },
    hideUpdateContractForm() {
      // 更改值
      this.updateContractVisible = false;
      return true;
    },
    hideEncryptForm() {
      // 更改值
      this.encryptVisible = false;
      return true;
    },
    formSubmit() {
      if (this.formLoading) {
        return false
      }
      this.formData.password="111";
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.formLoading = true;
          let data = Object.assign({}, this.formData);
          if (this.formName === "add") {
            delete data.id
          }
          automainconfigSave(data).then(response => {
            this.formLoading = false;
            if (response.code) {
              this.$Message.error(response.message);
              return false;
            }
            this.$Message.success(this.$t('currencywithdrawalauditmanagement.operationsuccessful'));
            this.formVisible = false;
            if (this.formName === "add") {
              // 向头部添加数据
              if (response.data && response.data.id) {
                data.id = response.data.id;
                this.list.unshift(data);
              }
            } else {
              this.list.splice(this.index, 1, data);
            }
            // 刷新表单
            this.getList();
          });
        }
      });
    },
    collectSubmit() {
      if (this.formLoading) {
        return false
      }
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.formLoading = true;
          let data = Object.assign({}, this.formData);
          collectCoin(data).then(response => {
            this.formLoading = false;
            if (response.code) {
              this.$Message.error(response.message);
              return false;
            }
            this.$Message.success(this.$t('currencywithdrawalauditmanagement.operationsuccessful'));
            this.formVisible = false;
          });
        }
      });
    },
    setPasswordSubmit() {
      if (this.formLoading) {
        return false
      }
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.formLoading = true;
          let data = Object.assign({}, this.formData);
          setPassword(data).then(response => {
            this.formLoading = false;
            if (response.code) {
              this.$Message.error(response.message);
              return false;
            }
            this.$Message.success(this.$t('currencywithdrawalauditmanagement.operationsuccessful'));
            this.formVisible = false;
          });
        }
      });
    },
    updateContractSubmit() {
      if (this.formLoading) {
        return false
      }
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          // this.formLoading = true;
          let data = Object.assign({}, this.formData);
          updateContract(data).then(response => {
            this.formLoading = false;
            if (response.code) {
              this.$Message.error(response.message);
              return false;
            }
            this.$Message.success(this.$t('currencywithdrawalauditmanagement.operationsuccessful'));
            this.formVisible = false;
          });
        }
      });
    },
    encryptSubmit() {
      if (this.formLoading) {
        return false
      }
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          // this.formLoading = true;
          let data = Object.assign({}, this.formData);
          encryptMessage(data).then(response => {
            this.formLoading = false;
            if (response.code) {
              this.$Message.error(response.message);
              return false;
            }
            this.formData.messageEncrypt=response.data;
            this.formVisible = false;
          });
        }
      });
    },
  },
  mounted() {
  },
  created() {
    // 将参数拷贝进查询对象
    let query = this.$route.query;
    this.query = Object.assign(this.query, query);
    this.query.limit = parseInt(this.query.limit);
    // 加载表格数据
    this.getList();
    this.getProtocolList();
  }
};
</script>

<style>
</style>
