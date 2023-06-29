<template>
    <div>
        <Card>
            <p slot="title">
                {{ $t('rankingmanagement.rankingmanagement') }} <Button type="primary" size="small" @click="refreshPageManual">
                    <Icon type="refresh"></Icon>
                    {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
            </p>
            <Row class="functionWrapper">
                <div class="searchWrapper">
                    <div class="poptip">
                        {{ $t('currencywithdrawalauditmanagement.cellphonenumber') }}<Input :placeholder="$t('memberinvitationlist.cellphonenumber')" v-model="mobilePhone" />
                        </Input>
                    </div>
                    <div class="poptip">
                        <span>{{ $t('rankingmanagement.sorttype') }}</span>
                        <Select v-model="rankType">
                            <Option value="0" key="0">{{ $t('rankingmanagement.numberranking') }}</Option>
                            <Option value="1" key="1">{{ $t('rankingmanagement.rebateranking') }}</Option>
                        </Select>
                    </div>
                    <div class="btns">
                        <Button type="info" @click="search">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
                    </div>
                </div>
            </Row>
            <Row class="tableWrapper">
                <Table :columns="columns" :data="list" border :loading="ifLoading">
                </Table>
            </Row>
            <Row class="pageWrapper">
                <Page :total="total" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
            </Row>

        </Card>
        <Modal
          class="auditModel"
          v-model="detailModel"
          :title="$t('rankingmanagement.modifyparameters')"
          @on-ok="alterRankDetail">
          <ul>
            <li><span><i>*</i>ID：</span>
              <p>
                <Input v-model="detailRank.id" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('rankingmanagement.rebateequivalenttousdt') }}</span>
                <p> <Input v-model="detailRank.estimatedReward"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('rankingmanagement.note1') }}</span>
                <p> <Input v-model="detailRank.extraReward"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('rankingmanagement.numberoffirstlevelinvitees') }}</span>
                <p> <Input v-model="detailRank.levelOne"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('rankingmanagement.numberofsecondaryinvitees') }}</span>
                <p> <Input v-model="detailRank.levelTwo"></Input> </p>
            </li>
          </ul>
      </Modal>
    </div>
</template>

<script>
import { inviteRank, alterRank } from '@/service/getData';
import expandRow from './SecondRecord';
const getParamsFun = (pageSize) => (pageNo) => (rankType) => (mobilePhone) => ({
    pageNo, pageSize, rankType, mobilePhone
});
const getParams = getParamsFun(20);
export default {
    components: { expandRow },
    data() {
        return {
            list: [],
            total: 0,
            memberId: "",
            rankType: 0,
            pageNo: 1,
            mobilePhone: "",
            email: "",
            currentPageIdx: 1,
            ifLoading: true,
            detailModel: false,
            detailRank: {
                id: "",
                estimatedReward: null,
                extraReward: null,
                levelOne: null,
                levelTwo: null
            },
            columns: [
                {
                    title: "ID",
                    key: "id"
                },
                {
                    title: this.$t('positionmanagement.userid'),
                    key: 'memberId'
                },
                {
                    title: this.$t('memberinvitationlist.cellphonenumber'),
                    key: 'userIdentify'
                },
                {
                    title: this.$t('memberinvitationlist.firstlevelinvitation'),
                    key: 'levelOne'
                },
                {
                    title: this.$t('memberinvitationlist.secondaryinvitation'),
                    key: 'levelTwo'
                },
                {
                    title: this.$t('rankingmanagement.rebateconversion'),
                    key: 'estimatedReward'
                },
                {
                    title: this.$t('rankingmanagement.bonus'),
                    key: 'extraReward'
                },
                {
                  title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
                  key: "age",
                  width: 150,
                  render: (h, obj) => {
                    let params = obj.row;
                    return h("div", [
                      h(
                        "Button",
                        {
                          props: {type: "primary",size: "small"}, style: {marginRight: "10px"},
                          on: {
                            click: () => {
                              this.detailModel = true;
                              this.detailRank.id = params.id;
                              this.detailRank.estimatedReward = params.estimatedReward;
                              this.detailRank.extraReward = params.extraReward;
                              this.detailRank.levelOne = params.levelOne;
                              this.detailRank.levelTwo = params.levelTwo;
                            }
                          }
                        },
                        this.$t('backstageadvertising.details')
                      )
                    ]);
                  }
                }
            ]
        }
    },
    methods: {
        init() {
            const params = getParams(this.pageNo)(this.rankType)(this.mobilePhone);
            inviteRank(params).then(res => {
                if (!res.code) {
                    this.ifLoading = false;
                    this.list = res.data.content;
                    this.total = res.data.totalElements;
                } else {
                    this.$Message.error(res.message);
                }
            })
        },
        refreshPageManual() {
            this.currentPageIdx = 1;
            this.pageNo = 1;
            this.mobilePhone = "";
            this.init();
        },
        search() {
            this.pageNum = this.currentPageIdx = 1;
            this.init();
        },
        changePage(index) {
            this.pageNo = this.currentPageIdx = index;
            this.init();
        },
        alterRankDetail() {
            let params = {
                id: this.detailRank.id,
                estimatedReward: this.detailRank.estimatedReward,
                extraReward: this.detailRank.extraReward,
                levelOne: this.detailRank.levelOne,
                levelTwo: this.detailRank.levelTwo
            };
            alterRank(params).then(res => {
                if (!res.code) {
                    this.$Message.success(this.$t('perpetualcontractcurrencystandardmanagement.modificationsucceeded'));
                    this.refreshPageManual();
                } else {
                    this.$Message.error(res.message);
                }
            })
        }
    },
    created() {
        this.init();
    }
}
</script>

<style lang="less" scoped>
</style>
