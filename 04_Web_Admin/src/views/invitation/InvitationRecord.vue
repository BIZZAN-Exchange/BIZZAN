<template>
    <div>
        <Card>
            <p slot="title">
                {{ $t('memberinvitationlist.memberinvitationlist') }} <Button type="primary" size="small" @click="refreshPageManual">
                    <Icon type="refresh"></Icon>
                    {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
            </p>
            <Row class="functionWrapper">
                <div class="searchWrapper">
                    <div class="poptip">
                        {{ $t('membermanagement.membername') }}:<Input :placeholder="$t('membermanagement.membername')" v-model="realName" />
                        </Input>
                    </div>
                    <div class="poptip">
                        {{ $t('currencywithdrawalauditmanagement.cellphonenumber') }}<Input :placeholder="$t('memberinvitationlist.cellphonenumber')" v-model="mobilePhone" />
                        </Input>
                    </div>
                    <div class="poptip">
                        {{ $t('currencywithdrawalauditmanagement.email') }}<Input :placeholder="$t('memberinvitationlist.mailbox')" v-model="email" />
                        </Input>
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
    </div>
</template>

<script>
import { inviteRecord } from '@/service/getData';
import expandRow from './SecondRecord';
const getParamsFun = (pageSize) => (pageNo) => (realName) => (mobilePhone) => (email) => ({
    pageNo, pageSize, realName, mobilePhone, email
});
const getParams = getParamsFun(10);
export default {
    components: { expandRow },
    data() {
        return {
            list: [],
            total: 0,
            realName: "",
            pageNo: 1,
            mobilePhone: "",
            email: "",
            currentPageIdx: 1,
            ifLoading: true,
            columns: [
                {
                    type: 'expand',
                    width: 50,
                    render: (h, params) => {
                        return h(expandRow, {
                            props: {
                                id: params.row.id
                            }
                        })
                    }
                },
                {
                    title: this.$t('announcementmanagement.no'),
                    key: "id"
                },
                {
                    title: this.$t('memberinvitationlist.username'),
                    key: 'username'
                },
                {
                    title: this.$t('memberinvitationlist.invitationcode'),
                    key: 'promotionCode'
                },
                {
                    title: this.$t('memberinvitationlist.firstlevelinvitation'),
                    render: (h, params) => {
                        return h("div", {}, params.row.firstLevel);
                    }
                },
                {
                    title: this.$t('memberinvitationlist.secondaryinvitation'),
                    render: (h, params) => {
                        return h("div", {}, params.row.secondLevel);
                    }
                },
                {
                    title: this.$t('memberinvitationlist.level3invitation'),
                    render: (h, params) => {
                        return h("div", {}, params.row.thirdLevel);
                    }
                },
                {
                    title: this.$t('memberinvitationlist.earnpoints'),
                    key: "generalizeTotal"
                }
            ]
        }
    },
    methods: {
        init() {
            const params = getParams(this.pageNo)(this.realName)(this.mobilePhone)(this.email);
            inviteRecord(params).then(res => {
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
            this.realName = "";
            this.currentPageIdx = 1;
            this.pageNo = 1;
            this.mobilePhone = "";
            this.email = "";
            this.init();
        },
        search() {
            this.pageNum = this.currentPageIdx = 1;
            this.init();
        },
        changePage(index) {
            this.pageNo = this.currentPageIdx = index;
            this.init();
        }
    },
    created() {
        this.init();
    }
}
</script>

<style lang="less" scoped>
</style>
