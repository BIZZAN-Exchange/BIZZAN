<template>

  <div>
    <Card>

      <p slot="title">
        {{ $t('currencyextensionmanagement.currencyextensionmanagement') }} <Button type="primary" size="small" @click="onReset">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

      <Row class="functionWrapper">
        <div class="searchWrapper clearfix">
          <div class="poptip">
            <span>{{ $t('currencywithdrawalauditmanagement.currencyname') }}</span>
            <Input :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" v-model="query.coinname"/>
          </div>
          <div class="poptip">
            <span>{{ $t('currencyextensionmanagement.contractaddress') }}</span>
            <Input :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" v-model="query.ext"/>
          </div>

          <div class="btns">
            <Button type="info" size="small" @click="onSubmit">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
          </div>
          <div class="btns">
            <Button type="primary" @click="handleForm(null,null)">{{ $t('currencyextensionmanagement.addextension') }}</Button>
          </div>
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
          <div style="font-weight: 600;margin-bottom: 10px">{{ $t('currencyextensionmanagement.basiccurrencyinformation') }}</div>
          <FormItem :label="$t('currencyextensionmanagement.enableornot')" prop="status">
            <RadioGroup v-model.trim="formData.status">
              <Radio :label="1">{{ $t('perpetualcontractustandardmanagement.enable') }}</Radio>
              <Radio :label="0">{{ $t('currencyextensionmanagement.disable') }}</Radio>
            </RadioGroup>
          </FormItem>
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
         <!-- <FormItem :label="$t('currencyextensionmanagement.primaryaddress')" prop="mainaddress">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note4') }}</div>
            <Input v-model="formData.mainaddress" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem> -->
          <FormItem :label="$t('currencyextensionmanagement.contractaddress')" prop="ext">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note7') }}</div>
            <Input v-model="formData.ext" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.currencyaccuracy')" prop="decimals">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note6') }}</div>
            <Input v-model="formData.decimals" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>

          <div style="font-weight: 600;margin-bottom: 10px">{{ $t('currencyextensionmanagement.rechargesettings') }}</div>
          <FormItem :label="$t('currencyextensionmanagement.whetheritcanberecharged')" prop="isrecharge">
            <RadioGroup v-model.trim="formData.isrecharge">
              <Radio :label="1">{{ $t('perpetualcontractustandardmanagement.enable') }}</Radio>
              <Radio :label="0">{{ $t('currencyextensionmanagement.disable') }}</Radio>
            </RadioGroup>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.minimumrechargeamount')" prop="minrecharge">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note2') }}</div>
            <Input v-model="formData.minrecharge" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.rechargeconfirmationnumber')" prop="confirms">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note8') }}</div>
            <Input v-model="formData.confirms" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>

          <div style="font-weight: 600;margin-bottom: 10px">{{ $t('currencyextensionmanagement.withdrawalsettings') }}</div>
          <FormItem :label="$t('currencyextensionmanagement.withdrawalavailable')" prop="iswithdraw">
            <RadioGroup v-model.trim="formData.iswithdraw">
              <Radio :label="1">{{ $t('perpetualcontractustandardmanagement.enable') }}</Radio>
              <Radio :label="0">{{ $t('currencyextensionmanagement.disable') }}</Radio>
            </RadioGroup>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.autoapprove')" prop="isautowithdraw">
            <RadioGroup v-model.trim="formData.isautowithdraw">
              <Radio :label="1">{{ $t('perpetualcontractustandardmanagement.enable') }}</Radio>
              <Radio :label="0">{{ $t('currencyextensionmanagement.disable') }}</Radio>
            </RadioGroup>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.withdrawalrate')" prop="withdrawfee">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note3') }}</div>
            <Input v-model="formData.withdrawfee" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.minimumhandlingcharge')" prop="minwithdrawfee">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note5') }}</div>
            <Input v-model="formData.minwithdrawfee" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.minimumwithdrawalquantity')" prop="minwithdraw">
            <Input v-model="formData.minwithdraw" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.maximumwithdrawalamount')" prop="maxwithdraw">
            <Input v-model="formData.maxwithdraw" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
          <FormItem :label="$t('currencyextensionmanagement.addressofremarkscoderecharge')" prop="memoaddress">
            <br/>
            <div style="color: red;">{{ $t('currencyextensionmanagement.note1') }}</div>
            <Input v-model="formData.memoaddress" :placeholder="$t('currencywithdrawalauditmanagement.pleaseenter')" clearable></Input>
          </FormItem>
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
import {coinextCoinList, coinextProtocolList, coinextList, coinextSave} from '../../service/getData';

const formJson = {
  id: "",
  coinid: 0,
  coinname: "",
  protocol: "",
  protocolname: "",
  //mainaddress: "",
  ext: "",
  decimals: 6,
  status: 1,
  withdrawfee: 0,
  minwithdrawfee: 0,
  iswithdraw: 1,
  isrecharge: 1,
  isautowithdraw: 0,
  minwithdraw: 0,
  maxwithdraw: 0,
  minrecharge: 0,
  confirms: 3,
  memoaddress: ""
};
export default {
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
          key: "protocolname"
        },
        {
          title: this.$t('currencyextensionmanagement.contractaddress1'),
          key: "ext"
        },
        {
          title: this.$t('currencyextensionmanagement.currencyprecision'),
          key: "decimals"
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          key: "status",
          render: (h, obj) => {
            let statusD = obj.row.status;
            let status = "";
            let btnType = "success";
            if (statusD == 1) {
              status = this.$t('perpetualcontractustandardmanagement.enable');
            } else {
              status = this.$t('currencyextensionmanagement.disable');
              btnType = "error";
            }
            return h(
                "Button",
                {
                  props: {
                    type: btnType,
                    size: "small"
                  }
                },
                status
            );
          }
        },
        {
          title: this.$t('currencyextensionmanagement.withdrawalstatus'),
          key: "iswithdraw",
          render: (h, obj) => {
            let statusD = obj.row.iswithdraw;
            let status = "";
            let btnType = "success";
            if (statusD == 1) {
              status = this.$t('perpetualcontractustandardmanagement.enable');
            } else {
              status = this.$t('currencyextensionmanagement.disable');
              btnType = "error";
            }
            return h(
                "Button",
                {
                  props: {
                    type: btnType,
                    size: "small"
                  }
                },
                status
            );
          }
        },
        {
          title: this.$t('currencyextensionmanagement.rechargestatus'),
          key: "isrecharge",
          render: (h, obj) => {
            let statusD = obj.row.isrecharge;
            let status = "";
            let btnType = "success";
            if (statusD == 1) {
              status = this.$t('perpetualcontractustandardmanagement.enable');
            } else {
              status = this.$t('currencyextensionmanagement.disable');
              btnType = "error";
            }
            return h(
                "Button",
                {
                  props: {
                    type: btnType,
                    size: "small"
                  }
                },
                status
            );
          }
        },
        {
          title: this.$t('currencyextensionmanagement.confirmednumber'),
          key: "confirms"
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
        ext: "",
        coinname: "",
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
        ext: "",
        coinname: "",
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
      coinextCoinList()
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
      coinextProtocolList()
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
      coinextList(this.query)
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
          coinextSave(data).then(response => {
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
