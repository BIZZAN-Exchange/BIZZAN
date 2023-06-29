<template>
<!-- 提币明细 -->
  <div>
    <Card>
      <p slot="title">
        {{ $t('withdrawaldetails.withdrawaldetails') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          {{ $t('perpetualcontractcurrencystandardmanagement.refresh') }} </Button>
      </p>

       <Row>
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
        <Page :total="totalNum"
        :current="currentPageIdx"   
        @on-change="changePage" show-elevator></Page>
      </Row>

    </Card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      columns_first: [
          {
            title: this.$t('auditdetails.memberid'),
            key: 'id',
          },
          {
            title: this.$t('memberinvitationlist.username'),
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
            key: 'username',
          },
          {
            title: this.$t('withdrawaldetails.walletaddress'),
            key: 'username',
          },
          {
            title: this.$t('withdrawaldetails.numberofcurrencywithdrawals'),
            key: 'username',
          },
          {
            title: this.$t('withdrawaldetails1.actualnumberofaccountsreceived'),
            key: 'username',
          },
          {
            title: this.$t('transactiondetailsinlegalcurrency.handlingcharges'),
            key: 'username',
          },
          {
            title: this.$t('withdrawaldetails1.currencywithdrawalapplicationtime'),
            key: 'username',
          },
           {
            title: this.$t('withdrawaldetails1.approvalpassedtime'),
            key: 'username',
          },
           {
            title: this.$t('withdrawaldetails1.reviewedby'),
            key: 'username',
          }
      ],
      userpage: [],
      ifLoading: true,
      totalNum: null,
      currentPageIdx: 1,
    }
  },
  mehods: {
    refreshPageManual() {

    },
    changePage() {

    }
  }
}
</script>

<style lang="less" scoped>

</style>


