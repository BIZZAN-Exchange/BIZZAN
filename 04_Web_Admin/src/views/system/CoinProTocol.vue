<template>

  <div>
    <Card>

      <p slot="title">
        {{ $t('currencyagreementmanagement.currencyagreementmanagement') }} <Button type="primary" size="small" @click="onReset">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>


      <Row class="functionWrapper">
        <div class="btnsWrapper clearfix">
          <Button type="primary" @click="handleForm(null,null)">{{ $t('currencyagreementmanagement.addprotocols') }}</Button>
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
          <FormItem :label="$t('currencyagreementmanagement.agreementid')" prop="protocol">
            <Input v-model="formData.protocol" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyagreementmanagement.agreementname1')" prop="protocolname">
            <Input v-model="formData.protocolname" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
<!--          <FormItem label="RPCServer：" prop="rpcserver">
            <Input v-model="formData.rpcserver" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem label="RPCUser：" prop="rpcuser">
            <Input v-model="formData.rpcuser" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem label="RPCPassword：" prop="rpcpassword">
            <Input v-model="formData.rpcpassword" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem> -->
          <FormItem :label="$t('currencyagreementmanagement.blockbrowser1')" prop="browser">
            <Input v-model="formData.browser" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyagreementmanagement.nameofagreementprimarycurrency')" prop="symbol">
            <Input v-model="formData.symbol" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyagreementmanagement.chainid')" prop="chainid">
            <Input v-model="formData.chainid" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
<!--          <FormItem label="状态：" prop="status">
           <RadioGroup v-model.trim="formData.status">
             <Radio :label="1">正常</Radio>
             <Radio :label="0">禁用</Radio>
           </RadioGroup>
          </FormItem> -->
        </Form>
        <div slot="footer">
          <Button @click="hideForm">{{ $t('currencywithdrawalauditmanagement.cancel') }}</Button>
          <Button type="primary" @click="formSubmit" :loading="formLoading">{{ $t('collectionconfigurationmanagement.determine') }}</Button>
        </div>
      </Modal>

    </Card>
  </div>

</template>

<script>
import {coinprotocolList, coinprotocolSave} from '../../service/getData';

const formJson = {
  id: "",
  protocol: "",
  protocolname: "",
  // rpcserver: "",
  // rpcuser: "",
  // rpcpassword: "",
  browser: "",
  symbol: "",
  chainid: 0,
};
export default {
  components: {},
  data() {
    return {
      columnList: [
        {
          title: this.$t('currencyagreementmanagement.agreementno'),
          key: "protocol",
          width: 100
        },
        {
          title: this.$t('currencywithdrawalauditmanagement.agreementname'),
          key: "protocolname"
        },
        // {
        //   title: "RPCServer",
        //   key: "rpcserver"
        // },
        // {
        //   title: "RPCUser",
        //   key: "rpcuser"
        // },
        // {
        //   title: "RPCPassword",
        //   key: "rpcpassword"
        // },
        {
          title: this.$t('currencyagreementmanagement.blockbrowser'),
          key: "browser"
        },
        {
          title: this.$t('currencyagreementmanagement.agreementprimarycurrencyname'),
          key: "symbol"
        },
        {
          title: this.$t('currencyagreementmanagement.chainid1'),
          key: "chainid"
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          width: 80,
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
      index: null,
      formName: null,
      formMap: {
        add: this.$t('collectionconfigurationmanagement.new'),
        edit: this.$t('collectionconfigurationmanagement.edit')
      },
      formData: formJson,
      formRules: {
        protocol: [
          {required: true, message: this.$t('currencyagreementmanagement.pleaseenterauniqueagreementid'), trigger: "blur"}
        ],
        protocolname: [
          {required: true, message: this.$t('currencyagreementmanagement.pleaseentercontent'), trigger: "blur"}
        ],
      },
      formVisible: false,
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
    getList() {
      this.loading = true;
      coinprotocolList(this.query)
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
    // 显示表单
    handleForm(index, row) {
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
    formSubmit() {
      if (this.formLoading) {
        return false
      }
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.formLoading = true;
          let data = Object.assign({}, this.formData);
          if (this.formName === "add") {
            delete data.id
          }
          coinprotocolSave(data).then(response => {
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
  }
};
</script>

<style>
</style>
