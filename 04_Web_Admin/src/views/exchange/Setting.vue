<template>
 <div>
	 
    <Card>
			<p slot="title">
			  {{ $t('currencysetting.currencysetting') }} <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>{{ $t('perpetualcontractcurrencystandardmanagement.refresh') }}
        </Button>
			</p>
      <Row class="functionWrapper" style='padding:0px 8px 8px 8px'>
        <Col span="18">
          <div class="searchWrapper" style="float:left;">
              <div class="poptip">
                  <Input :placeholder="$t('collectionconfigurationmanagement.currencyname1')" v-model="searchSymbol" /></Input>
              </div>
              <div class="poptip">/</div>
              <div class="poptip">
                  <Input :placeholder="$t('currencysetting.nameofbasecurrency')" v-model="searchBase" /></Input>
              </div>
              <div class="btns">
                  <Button type="info" @click="search">{{ $t('positionmanagementcontractassetmanagement.search') }}</Button>
              </div>
          </div>
        </Col>
        <Col span="6">
          <div style="float: right" class="clearfix">
            <Button type="error" @click="delcoin" >{{ $t('currencysetting.deletetransactionpair') }}</Button>
            <Button type="primary"@click="addcoin">{{ $t('perpetualcontractcurrencystandardmanagement.addtransactionpair') }}</Button>

          </div>
        </Col>
      </Row>

      <Modal
          class="auditModel"
          v-model="fixModel"
          :title="$t('currencysetting.modifyinformation')"
          @on-ok="confirmClicked">
          <ul>
            <li><span><i>*</i> {{ $t('perpetualcontractcurrencystandardmanagement.transactionpair') }}</span>
              <p>
                <Input v-model="fixSymbol" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>

            <li><span><i>*</i> {{ $t('transactiondetailsinlegalcurrency.servicecharge') }}</span>
                <p><Input v-model="fee"
                 :class="{'errorFormatBorder': feeClass}"
                  @on-change="checkFee(fee)"></Input>
                </p>
                <em v-show="feeClass">{{ $t('currencysetting.incorrectformat') }}</em>
            </li>

            <li><span><i>*</i> {{ $t('currencywithdrawalauditmanagement.status') }}</span>
              <p>
                <RadioGroup v-model="fixEnable">
                  <Radio label="1"><em>{{ $t('currencysetting.enableontheshelf') }}</em></Radio>
                  <Radio label="2"><em>{{ $t('currencysetting.forbiddenofftheshelf') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> {{ $t('currencysetting.frontenddisplay') }}</span>
              <p>
                <RadioGroup v-model="fixVisible">
                  <Radio label="1"><em>{{ $t('perpetualcontractcurrencystandardmanagement.display') }}</em></Radio>
                  <Radio label="2"><em>{{ $t('perpetualcontractcurrencystandardmanagement.hide') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> {{ $t('currencysetting.tradable') }}</span>
              <p>
                <RadioGroup v-model="fixExchangeable">
                  <Radio label="1"><em>{{ $t('perpetualcontractcurrencystandardmanagement.yes') }}</em></Radio>
                  <Radio label="2"><em>{{ $t('perpetualcontractcurrencystandardmanagement.no') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> {{ $t('currencysetting.marketprice') }}</span>
              <p>
                <RadioGroup v-model="fixEnableMarketBuy">
                  <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                  <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> {{ $t('currencysetting.marketprice1') }}</span>
              <p>
                <RadioGroup v-model="fixEnableMarketSell">
                  <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                  <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>

            <li><span><i>*</i> {{ $t('currencysetting.available') }}</span>
              <p>
                <RadioGroup v-model="fixEnableBuy">
                  <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                  <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>

            <li><span><i>*</i> {{ $t('currencysetting.availableforsale') }}</span>
              <p>
                <RadioGroup v-model="fixEnableSell">
                  <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                  <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> {{ $t('perpetualcontractcurrencystandardmanagement.sort') }}</span>
              <p><Input v-model="fixSort"
                        :class="{'errorFormatBorder': sortClass}"
                        @on-change="checksort(fixSort)" :placeholder="$t('currencysetting.note44')"></Input>
              </p>
              <em v-show="sortClass">{{ $t('currencysetting.incorrectformat') }}</em>
            </li>

            <li><span><i>*</i> {{ $t('currencysetting.maximumpurchaseprice') }}</span>
                <p><Input v-model="fixMaxBuyPrice"></Input>
                </p>
            </li>
            <li><span><i>*</i> {{ $t('currencysetting.minimumorderamount') }}</span>
                <p><Input v-model="fixMinTurnover"></Input>
                </p>
            </li>
          </ul>

      </Modal>
      <Modal
          class="auditModel"
          v-model="auditModal"
          :title="$t('currencymanagement1.addcurrency')"
          width="850"
          @on-ok="confirmAudit">
          <Row :gutter="30">
            <Col span="10">
              <p class="setting-title">{{ $t('currencysetting.basicinformationsettings') }}</p>
              <ul>
                <li><span><i>*</i> {{ $t('perpetualcontractcurrencystandardmanagement.transactionpair') }}</span>
                  <p>
                    <Input v-model="symbol"
                           :class="{'errorFormatBorder': checkSymbolClass}"
                           @on-change="checkPair(symbol)" :placeholder="$t('currencysetting.examplebtcrequired')"></Input>
                  </p>
                  <em v-show="checkSymbolClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i> {{ $t('essentialinformation1.transactioncurrency') }}</span>
                  <p><Input v-model="coinSymbol"
                            :class="{'errorFormatBorder': coinSymbolClass}"
                            @on-change="checkCoinSymbol(coinSymbol)"  :placeholder="$t('currencysetting.examplebtcrequired')"></Input>
                  </p>
                  <em v-show="coinSymbolClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>

                <li><span><i>*</i> {{ $t('currencysetting.settlementcurrency') }}</span>
                  <p><Input v-model="baseSymbol"
                            :class="{'errorFormatBorder': baseSymbolClass}"
                            @on-change="checkbaseSymbol(baseSymbol)" :placeholder="$t('currencysetting.exampleusdtrequired')"></Input>
                  </p>
                   <em v-show="baseSymbolClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i> {{ $t('transactiondetailsinlegalcurrency.servicecharge') }}</span>
                  <p><Input v-model="fee"
                   :class="{'errorFormatBorder': feeClass}"
                    @on-change="checkFee(fee)" :placeholder="$t('currencysetting.note35')"></Input>
                  </p>
                  <em v-show="feeClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>

                <li><span><i>*</i> {{ $t('currencyextensionmanagement.currencyaccuracy') }}</span>
                  <p><Input v-model="coinScale"
                            :class="{'errorFormatBorder': coinScaleClass}"
                            @on-change="checkCoinScale(coinScale)" :placeholder="$t('currencysetting.example4required')"></Input>
                  </p>
                  <em v-show="coinScaleClass">{{ $t('currencysetting.incorrectformat') }}</em>

                </li>

                <li><span><i>*</i> {{ $t('perpetualcontractcurrencystandardmanagement.decimalprecisioninbasecurrency') }}</span>
                  <p><Input v-model="baseCoinScale"
                            :class="{'errorFormatBorder': baseCoinScaleClass}"
                            @on-change="checkbaseCoinScale(baseCoinScale)" :placeholder="$t('currencysetting.example6required')"></Input>
                  </p>
                  <em v-show="baseCoinScaleClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i> {{ $t('currencysetting.minimumsellingprice') }}</span>
                  <p><Input v-model="minSellPrice"
                            :class="{'errorFormatBorder': minSellPriceClass}"
                            @on-change="checkminSellPrice(minSellPrice)" :placeholder="$t('currencysetting.note42')"></Input>
                  </p>
                  <em v-show="minSellPriceClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i> {{ $t('currencysetting.maximumpurchaseprice') }}</span>
                  <p><Input v-model="maxBuyPrice"
                            :class="{'errorFormatBorder': maxBuyPriceClass}"
                            @on-change="checkmaxBuyPrice(maxBuyPrice)" :placeholder="$t('currencysetting.note42')"></Input>
                  </p>
                  <em v-show="maxBuyPriceClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i> {{ $t('currencysetting.minimumorderquantity') }}</span>
                  <p><Input v-model="minVolume"
                            :class="{'errorFormatBorder': minVolumeClass}"
                            @on-change="checkminVolume(minVolume)" :placeholder="$t('currencysetting.note42')"></Input>
                  </p>
                  <em v-show="minVolumeClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i> {{ $t('currencysetting.maximumorderquantity1') }}</span>
                  <p><Input v-model="maxVolume"
                            :class="{'errorFormatBorder': maxVolumeClass}"
                            @on-change="checkmaxVolume(maxVolume)" :placeholder="$t('currencysetting.note42')"></Input>
                  </p>
                  <em v-show="maxVolumeClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i> {{ $t('currencysetting.tradingarea') }}</span>
                  <p><Input v-model="zone"
                            :class="{'errorFormatBorder': zoneClass}"
                            @on-change="checkzone(zone)" :placeholder="$t('currencysetting.note20')"></Input>
                  </p>
                  <em v-show="zoneClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i>{{ $t('currencysetting.minimumcharge') }}</span>
                  <p><Input v-model="minTurnover"
                            :class="{'errorFormatBorder': minTurnoverClass}"
                            @on-change="checkminTurnover(minTurnover)" :placeholder="$t('currencysetting.note42')"></Input>
                  </p>
                  <em v-show="minTurnoverClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
                <li><span><i>*</i>{{ $t('perpetualcontractcurrencystandardmanagement.sort') }}</span>
                  <p><Input v-model="sort"
                            :class="{'errorFormatBorder': sortClass}"
                            @on-change="checksort(sort)" :placeholder="$t('currencysetting.note44')"></Input>
                  </p>
                  <em v-show="sortClass">{{ $t('currencysetting.incorrectformat') }}</em>
                </li>
              </ul>
            </Col>
            <Col span="12">
              <Row :gutter="30">
                <p class="setting-title">{{ $t('currencysetting.currencyactivitysettings') }}</p>
                <ul>
                  <li><span><i>*</i>{{ $t('currencysetting.activitytype') }}</span>
                    <p>
                      <RadioGroup v-model="publishType">
                        <Radio label="1"><em>{{ $t('currencysetting.noactivity') }}</em></Radio>
                        <Radio label="2"><em>{{ $t('currencysetting.rushpurchaseandissuance') }}</em></Radio>
                        <Radio label="3"><em>{{ $t('currencysetting.distribution') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span>{{ $t('currencysetting.activitystarttime') }}</span>
                    <p>
                      <DatePicker v-model="startTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
                    </p>
                    <em>{{ $t('currencysetting.note6') }}</em>
                  </li>
                  <li><span>{{ $t('currencysetting.activityendtime') }}</span>
                    <p>
                      <DatePicker v-model="endTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
                    </p>
                    <em>{{ $t('currencysetting.note6') }}</em>
                  </li>
                  <li><span>{{ $t('currencysetting.windingupendtime') }}</span>
                    <p>
                      <DatePicker v-model="clearTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
                    </p>
                    <em>{{ $t('currencysetting.note6') }}</em>
                  </li>
                  <li><span>{{ $t('currencysetting.apportionedissueprice') }}</span>
                    <p><Input v-model="publishPrice"
                               style="width: 200px"
                              :class="{'errorFormatBorder': publishPriceClass}"
                              @on-change="checkpublishPrice(publishPrice)" :placeholder="$t('currencysetting.note42')"></Input>
                    </p>
                    <em>{{ $t('currencysetting.note6') }}</em>
                  </li>
                  <li><span>{{ $t('currencysetting.numberofissues') }}</span>
                    <p><Input v-model="publishAmount"
                               style="width: 200px" :placeholder="$t('currencysetting.note42')"></Input>
                    </p>
                    <em>{{ $t('currencysetting.note6') }}</em>
                  </li>
                </ul>
              </Row>
              <Row :gutter="30">
                <p class="setting-title">{{ $t('currencysetting.currencystatussetting') }}</p>
                <ul>
                  <li><span><i>*</i> {{ $t('currencywithdrawalauditmanagement.status') }}</span>
                    <p>
                      <RadioGroup v-model="enable">
                        <Radio label="1"><em>{{ $t('perpetualcontractustandardmanagement.enable') }}</em></Radio>
                        <Radio label="2"><em>{{ $t('perpetualcontractcurrencystandardmanagement.prohibit') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> {{ $t('currencysetting.frontenddisplay') }}</span>
                    <p>
                      <RadioGroup v-model="visible">
                        <Radio label="1"><em>{{ $t('perpetualcontractcurrencystandardmanagement.display') }}</em></Radio>
                        <Radio label="2"><em>{{ $t('perpetualcontractcurrencystandardmanagement.hide') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> {{ $t('currencysetting.tradable') }}</span>
                    <p>
                      <RadioGroup v-model="exchangeable">
                        <Radio label="1"><em>{{ $t('perpetualcontractcurrencystandardmanagement.yes') }}</em></Radio>
                        <Radio label="2"><em>{{ $t('perpetualcontractcurrencystandardmanagement.no') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> {{ $t('currencysetting.marketprice') }}</span>
                    <p>
                      <RadioGroup v-model="enableMarketBuy">
                        <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                        <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> {{ $t('currencysetting.marketprice1') }}</span>
                    <p>
                      <RadioGroup v-model="enableMarketSell">
                        <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                        <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> {{ $t('currencysetting.available') }}</span>
                    <p>
                      <RadioGroup v-model="enableBuy">
                        <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                        <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> {{ $t('currencysetting.availableforsale') }}</span>
                    <p>
                      <RadioGroup v-model="enableSell">
                        <Radio label="1"><em>{{ $t('currencysetting.sure') }}</em></Radio>
                        <Radio label="0"><em>{{ $t('currencysetting.maynot') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> {{ $t('currencysetting.robottype') }}</span>
                    <p>
                      <RadioGroup v-model="robotType">
                        <Radio label="0"><em>{{ $t('currencysetting.commonly') }}</em></Radio>
                        <Radio label="1"><em>{{ $t('currencysetting.dashboard') }}</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                </ul>
              </Row>
            </Col>
          </Row>
      </Modal>

      <Table
        border
        :columns="columns_first"
        :data="exchange"
        @on-selection-change="selected"
        :loading="ifLoading">
      </Table>

      <Row class="pageWrapper">
        <Page
        :total="pageNum"
        class="buttomPage"
        :current="current"
        @on-change="changePage"
        show-elevator></Page>
      </Row>
      <p style="font-size:11px;">{{ $t('currencysetting.note18') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note14') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note27') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note45') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note16') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note9') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note33') }}{{ $t('currencysetting.marketengineetc') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note8') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note34') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note24') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note47') }}</p>
      <hr>
      <p style="font-size:11px;">{{ $t('currencysetting.note26') }}</p>
      <p style="font-size:11px;">{{ $t('currencysetting.note3') }}</p>
      <Modal
        v-model="ifDelete"
        :title="$t('currencysetting.deletetransactionpair')"
        @on-ok="confirmDel"
        @on-cancel="$Message.info('已取消！')">
        <p>是否删除选中的{{ deleteArr.length }}项</p>
       </Modal>
			 <Modal
					class="auditModel"
					v-model="loginPassModal"
					:title="$t('currencysetting.pleaseentertheloginpassword')"
					width="350"
					@on-cancle="loginPW = ''"
					@on-ok="confirmLoginPass">
					<Input v-model="loginPW" type="password" :placeholder="$t('currencysetting.pleaseentertheloginpassword')"></Input>
			 </Modal>

       <Modal
          class="auditModel"
          v-model="startEngineModel"
          :title="$t('currencysetting.startthematchingtradingengineforconfirmation')"
          width="350"
          @on-cancle="$Message.info('已取消！')"
          @on-ok="confirmClicked">
          <p style="font-size:20px;font-weight:bold;text-align:center;">{{ $t('currencysetting.startmatchingtransaction') }}<span style="color: #FF0000;">{{targetSymbol}}</span></p>
          <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note5') }}</p>
       </Modal>

       <Modal
          class="auditModel"
          v-model="stopEngineModel"
          :title="$t('currencysetting.stopmatchingtransactionengineconfirmation')"
          width="350"
          @on-cancle="$Message.info('已取消！')"
          @on-ok="confirmClicked">
          <p style="font-size:20px;font-weight:bold;text-align:center;">{{ $t('currencysetting.stopmatchingtransactions') }}<span style="color: #FF0000;">{{targetSymbol}}</span></p>
          <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note46') }}</p>
       </Modal>
       <Modal
          class="auditModel"
          v-model="cancelAllModel"
          :title="$t('currencysetting.revokealldelegatedconfirmations')"
          width="350"
          @on-cancle="$Message.info('已取消！')"
          @on-ok="confirmClicked">
          <p style="font-size:20px;font-weight:bold;text-align:center;">{{ $t('currencysetting.revokeallmandates') }}<span style="color: #FF0000;">{{targetSymbol}}</span></p>
          <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note38') }}</p>
       </Modal>
       <Modal
          class="auditModel"
          v-model="resetModel"
          :title="$t('currencysetting.resetengineconfirmation')"
          width="350"
          @on-cancle="$Message.info('已取消！')"
          @on-ok="confirmClicked">
          <p style="font-size:20px;font-weight:bold;text-align:center;">{{ $t('currencysetting.resettradingengine') }}<span style="color: #FF0000;">{{targetSymbol}}</span></p>
          <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.areyousuretoresetthetradingengine') }}</p>
       </Modal>

       <Modal
          class="auditModel"
          v-model="exchangeEngineModel"
          :title="$t('currencysetting.transactionengineentrustmentdetails')"
          width="350"
          @on-cancle="confirmExchangeEngineMoel"
          @on-ok="">
          <p style="font-size:20px;font-weight:bold;text-align:center;margin-bottom:20px;"><span style="color: #FF0000;">{{engineTargetSymbol}}</span></p>
          <Row :gutter="30">
            <Col span="12">
              <p style="font-size:14px;font-weight:bold;text-align:center;margin-bottom: 10px;padding-bottom:10px;border-bottom: 1px solid #B7B7B7;"><span>{{ $t('currencysetting.billoverview') }}</span></p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note2', [engineLimitBuyCount]) }}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note41', [engineMarketBuyCount]) }}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note43', [engineBuyDepth]) }}</p>
            </Col>
            <Col span="12">
              <p style="font-size:14px;font-weight:bold;text-align:center;margin-bottom: 10px;padding-bottom:10px;border-bottom: 1px solid #B7B7B7;"><span>{{ $t('currencysetting.salesorderoverview') }}</span></p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note25', [engineLimitSellCount]) }}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note25', [engineMarketSellCount]) }}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">{{ $t('currencysetting.note39', [engineSellDepth]) }}</p>
            </Col>
          </Row>
       </Modal>
       <!-- 一般机器人 -->
       <Modal
          class="auditModel"
          v-model="robotModel"
          :title="$t('currencysetting.modifyrobotparameters')"
          @on-ok="alterRobotClicked">
          <ul>
            <li><span><i>*</i>{{ $t('perpetualcontractcurrencystandardmanagement.transactionpair') }}</span>
              <p>
                <Input v-model="robotParams.symbol" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>
            <li><span><i>*</i> {{ $t('currencysetting.starttherobot') }}</span>
              <p>
                <RadioGroup v-model="robotParams.isHalt">
                  <Radio label="1"><em>{{ $t('currencysetting.stopit') }}</em></Radio>
                  <Radio label="0"><em>{{ $t('currencysetting.startup') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.minimumtradingvolume') }}</span>
                <p> <Input v-model="robotParams.startAmount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note1') }}</span>
                <p> <Input v-model="robotParams.randRange0"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note28') }}</span>
                <p> <Input v-model="robotParams.randRange1"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="robotParams.randRange2"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="robotParams.randRange3"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="robotParams.randRange4"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="robotParams.randRange5"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note37') }}</span>
                <p> <Input v-model="robotParams.randRange6"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.priceaccuracyrequirements') }}</span>
                <p> <Input v-model="robotParams.scale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.quantityaccuracyrequirements') }}</span>
                <p> <Input v-model="robotParams.amountScale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.maximumbidaskspread') }}</span>
                <p> <Input v-model="robotParams.maxSubPrice"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note10') }}</span>
                <p> <Input v-model="robotParams.initOrderCount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.pricechangestep') }}</span>
                <p> <Input v-model="robotParams.priceStepRate"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note23') }}</span>
                <p> <Input v-model="robotParams.runTime"></Input> </p>
            </li>
          </ul>
      </Modal>

      <!-- 固定价格机器人 -->
      <Modal
          class="auditModel"
          v-model="priceRobotModel"
          :title="$t('currencysetting.note15')"
          @on-ok="alterPriceRobotClicked">
          <ul>
            <li><span><i>*</i>{{ $t('perpetualcontractcurrencystandardmanagement.transactionpair') }}</span>
              <p>
                <Input v-model="priceRobotParams.symbol" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>
            <li><span><i>*</i> {{ $t('currencysetting.starttherobot') }}</span>
              <p>
                <RadioGroup v-model="priceRobotParams.isHalt">
                  <Radio label="1"><em>{{ $t('currencysetting.stopit') }}</em></Radio>
                  <Radio label="0"><em>{{ $t('currencysetting.startup') }}</em></Radio>
                </RadioGroup>
              </p>
            </li>

            <li>
                <span><i>*</i>{{ $t('currencysetting.fixedprice') }}</span>
                <p> <Input v-model="priceRobotParams.price"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.minimumtradingvolume') }}</span>
                <p> <Input v-model="priceRobotParams.startAmount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note1') }}</span>
                <p> <Input v-model="priceRobotParams.randRange0"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note28') }}</span>
                <p> <Input v-model="priceRobotParams.randRange1"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="priceRobotParams.randRange2"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="priceRobotParams.randRange3"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="priceRobotParams.randRange4"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note32') }}</span>
                <p> <Input v-model="priceRobotParams.randRange5"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note37') }}</span>
                <p> <Input v-model="priceRobotParams.randRange6"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.priceaccuracyrequirements') }}</span>
                <p> <Input v-model="priceRobotParams.scale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.quantityaccuracyrequirements') }}</span>
                <p> <Input v-model="priceRobotParams.amountScale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.maximumbidaskspread') }}</span>
                <p> <Input v-model="priceRobotParams.maxSubPrice"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note10') }}</span>
                <p> <Input v-model="priceRobotParams.initOrderCount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.pricechangestep') }}</span>
                <p> <Input v-model="priceRobotParams.priceStepRate"></Input> </p>
            </li>
            <li>
                <span><i>*</i>{{ $t('currencysetting.note23') }}</span>
                <p> <Input v-model="priceRobotParams.runTime"></Input> </p>
            </li>
          </ul>
      </Modal>
    </Card>
 </div>
</template>

 <script>
import dtime from 'time-formater'
import { transactionPair } from '@/caculate/caculate'
import { delBBSetting, addBBSetting, queryBBSetting, fixBBSetting, startBBTrader, stopBBTrader, cancelBBAllOrders, overviewBB, getRobotConfig, setRobotConfig, createRobotConfig, createPriceRobotConfig, setPriceRobotConfig, resetBBTrader} from '@/service/getData'

export default {
  data() {
    return {
			loginPW: '',
			loginPassModal: false,
      ifLoading: true,
      // currentPageIdx: 1,
      coinScaleClass: false,
      baseSymbolClass: false,
      checkSymbolClass: false,
      coinSymbolClass: false,
      feeClass: false,
      baseCoinScaleClass: false,
      minVolumeClass: false,
      maxVolumeClass: false,
      zoneClass: false,
      minSellPriceClass: false,
      maxBuyPriceClass: false,
      minTurnoverClass: false,
      sortClass: false,
      publishPriceClass: false,

      operation: 1, // 1: 设置   2：启动引擎   3：停止引擎   4：撤销委托

      fixSymbol: null,
      fixEnable: 1,
      fixModel: false,
      fixSort: null,
      fixVisible: 1,
      fixExchangeable: 1,
      fixEnableMarketSell: "1",
      fixEnableMarketBuy: "1",
      fixEnableBuy: "1",
      fixEnableSell: "1",
      fixMaxBuyPrice: null,
      fixMinTurnover: null,

      startEngineModel: false,
      stopEngineModel: false,
      cancelAllModel: false,
      targetSymbol: null,
      resetModel: false,

      searchSymbol: "",
      searchBase: "",

      robotModel: false,// 机器人参数
      robotMode: 0, // 0:新建  1：修改
      robotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0,
        randRange0: 0,
        randRange1: 0,
        randRange2: 0,
        randRange3: 0,
        randRange4: 0,
        randRange5: 0,
        randRange6: 0,
        scale: 0,
        amountScale: 0,
        maxSubPrice: 0,
        initOrderCount: 0,
        priceStepRate: 0,
        runTime: 0
      },
      defaultRobotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0.001,
        randRange0: 50,
        randRange1: 20,
        randRange2: 10,
        randRange3: 5,
        randRange4: 1,
        randRange5: 0.1,
        randRange6: 0.01,
        scale: 4,
        amountScale: 6,
        maxSubPrice: 1,
        initOrderCount: 30,
        priceStepRate: 0.005,
        runTime: 20000
      },
      priceRobotModel: false,// 机器人参数
      priceRobotMode: 0, // 0:新建  1：修改
      priceRobotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0,
        randRange0: 0,
        randRange1: 0,
        randRange2: 0,
        randRange3: 0,
        randRange4: 0,
        randRange5: 0,
        randRange6: 0,
        scale: 0,
        price: 0,
        amountScale: 0,
        maxSubPrice: 0,
        initOrderCount: 0,
        priceStepRate: 0,
        runTime: 0
      },
      defaultPriceRobotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0.001,
        randRange0: 50,
        randRange1: 20,
        randRange2: 10,
        randRange3: 5,
        randRange4: 1,
        randRange5: 0.1,
        randRange6: 0.01,
        scale: 4,
        amountScale: 6,
        price: 1,
        maxSubPrice: 1,
        initOrderCount: 30,
        priceStepRate: 0.005,
        runTime: 20000
      },
      symbol: null,
      coinSymbol: null,
      baseSymbol: null,
      enable: "1",
      fee: null,
      coinScale: null,
      baseCoinScale: null,
      minVolume: null, // add
      maxVolume: null, // add
      zone: null, // add
      minSellPrice: null, // add
      maxBuyPrice: null, // add
      minTurnover: null, // add
      sort: null, // add
      publishPrice: null, // add  分摊抢购初始价格
      publishType: "1", // 发行活动：1-无活动  2-抢购   3-分摊
      publishAmount: 0,
      startTime: null,
      endTime: null,
      clearTime: null,
      visible: "1",
      exchangeable: "1",
      robotType: "0",
      enableMarketBuy: "1",
      enableMarketSell: "1",
      enableBuy: "1",
      enableSell: "1",

      auditModal: false,
      pageNum: null,
      current: 1,
      deleteArr: false,
      ifDelete: false,

      exchangeEngineModel: false,
      engineTargetSymbol: "",
      engineLimitSellCount: "",
      engineLimitBuyCount: "",
      engineMarketSellCount: "",
      engineMarketBuyCount: "",
      engineBuyDepth: 0,
      engineSellDepth: 0,
      columns_first: [
        {
          type: "selection",
          width: 55,
          align: 'center'
        },
        {
          title: "#",
          width: 50,
          type: "index"
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.transactionpair'),
          width: 120,
          key: "symbol",
          render: (h, params) => {
            const row = params.row;
            return h("div", {
                style:{
                  textAlign: "left"
                }
              }, [
                h("span", {style:{fontWeight:"bold"}}, row.symbol)
              ]);
          }
        },
        {
          title: this.$t('currencysetting.marketpricetrading'),
          width: 65,
          key: "fee",
          render: (h, params) => {
            const row = params.row;
            const marketBuyText = row.enableMarketBuy == 1 ? "☑" : "✖";
            const marketSellText = row.enableMarketSell == 1 ? "☑" : "✖";
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {style:{fontSize:"10px"}}, "" + marketBuyText + " / " + marketSellText)
              ]);
          }
        },
        {
          title: this.$t('currencysetting.tradable1'),
          width: 65,
          key: "fee",
          render: (h, params) => {
            const row = params.row;
            const marketBuyText = row.enableBuy == 1 ? "☑" : "✖";
            const marketSellText = row.enableSell == 1 ? "☑" : "✖";
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {style:{fontSize:"10px"}}, "" + marketBuyText + " / " + marketSellText)
              ]);
          }
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.display'),
          width:45,
          key: "visible",
          render: (h, params) => {
            const row = params.row;
            const visible = row.visible == "1" ? this.$t('perpetualcontractcurrencystandardmanagement.yes') : this.$t('perpetualcontractcurrencystandardmanagement.no');
            if(row.visible == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, visible);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, visible);
          }
        },
        {
          title: this.$t('currencysetting.tradable'),
          width: 45,
          key: "exchangeable",
          render: (h, params) => {
            const row = params.row;
            const exchangeable = row.exchangeable == "1" ? this.$t('perpetualcontractcurrencystandardmanagement.yes') : this.$t('perpetualcontractcurrencystandardmanagement.no');
            if(row.exchangeable == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, exchangeable);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, exchangeable);
          }
        },
        {
          title: this.$t('currencysetting.robot'),
          width: 60,
          key: "robotType",
          render: (h, params) => {
            const row = params.row;
            var robot = this.$t('currencysetting.commonly');
            if(row.robotType == "1"){
              robot = this.$t('currencysetting.dashboard');
            }
            return h("span", {
              style:{
                color:'#42b983'
              }
            }, robot);
          }
        },{
          title: this.$t('currencysetting.robotparameters'),
          width: 80,
          key: "engineStatus",
          render: (h, params) => {
            const row = params.row;
            var btnType = row.exEngineStatus == 1 ? "primary" : "default";
            var btnText = row.exEngineStatus == 1 ? this.$t('currencysetting.settings') : this.$t('currencysetting.new');
            return h("div", [
              h(
                "Button",
                  {
                    props: {type: btnType, size: "small"},
                    style: {marginRight: "5px"},
                    on: {
                      click: () => {
                        this.$Loading.start();

                        this.robotMode = row.exEngineStatus; // 0:创建   1:修改

                        let obj ={
                          symbol:  row.symbol
                        }
                        getRobotConfig(obj).then( res => {
                          if (!res.code) {
                              this.robotParams.symbol = row.symbol;
                              if(res.data.halt) {
                                this.robotParams.isHalt = "1";
                              }else{
                                this.robotParams.isHalt = "0";
                              }
                              this.robotParams.startAmount = res.data.startAmount;
                              this.robotParams.randRange0 = res.data.randRange0;
                              this.robotParams.randRange1 = res.data.randRange1;
                              this.robotParams.randRange2 = res.data.randRange2;
                              this.robotParams.randRange3 = res.data.randRange3;
                              this.robotParams.randRange4 = res.data.randRange4;
                              this.robotParams.randRange5 = res.data.randRange5;
                              this.robotParams.randRange6 = res.data.randRange6;
                              this.robotParams.scale = res.data.scale;
                              this.robotParams.amountScale = res.data.amountScale;
                              this.robotParams.maxSubPrice = res.data.maxSubPrice;
                              this.robotParams.initOrderCount = res.data.initOrderCount;
                              this.robotParams.priceStepRate = res.data.priceStepRate;
                              this.robotParams.runTime = res.data.runTime;

                              this.robotModel = true;
                              this.$Loading.finish();
                          }else{
                              this.robotParams = this.defaultRobotParams;
                              this.robotParams.symbol = row.symbol;
                              this.robotModel = true;
                              this.$Notice.info({
                                    title: this.$t('currencysetting.createrobot'),
                                    desc: this.$t('currencysetting.thistransactionpairhasnorobots'),
                                    duration: 10
                                });
                              this.$Loading.finish();
                          }
                        });
                      }
                    }
                  },
                  btnText
                )
            ]);
          }
        },
        {
          title: this.$t('currencysetting.tradingengine'),
          width: 100,
          key: "engineStatus",
          render: (h, params) => {
            const row = params.row;
            var engineStatus = this.$t('currencysetting.noengine');
            if(row.engineStatus == "1"){
              engineStatus = this.$t('currencysetting.running');
            }
            if(row.engineStatus == "2"){
              engineStatus = this.$t('currencysetting.paused');
            }
            if(row.engineStatus == "1") {
              return h(
                "Button",
                  {
                    props: {type: "success",size: "small", icon:"ios-information-outline"},
                    style: {marginRight: "5px"},
                    on: {
                      click: () => {
                        this.$Loading.start();

                        this.engineLimitBuyCount = "-";
                        this.engineMarketBuyCount = "-";
                        this.engineLimitSellCount = "-";
                        this.engineMarketSellCount = "-";
                        this.engineBuyDepth = "-";
                        this.engineSellDepth = "-";

                        this.exchangeEngineModel = true;
                        this.engineTargetSymbol = params.row.symbol;

                        let obj ={
                          symbol:  this.engineTargetSymbol
                        }
                        overviewBB(obj).then( res => {
                          if (!res.code) {

                            this.engineLimitBuyCount = res.data.bid.limit_price_order_count;
                            this.engineMarketBuyCount = res.data.bid.market_price_order_count;
                            this.engineLimitSellCount = res.data.ask.limit_price_order_count;
                            this.engineMarketSellCount = res.data.ask.market_price_order_count;
                            this.engineBuyDepth = res.data.bid.depth;
                            this.engineSellDepth = res.data.ask.depth;

                            this.$Loading.finish();
                          }else{
                            this.$Notice.error({
                                  title: this.$t('currencysetting.getfailed'),
                                  desc: res.message,
                                  duration: 10
                              });
                            this.$Loading.error();
                          }
                        });
                      }
                    }
                  },
                  engineStatus
                );
            }
            return h("div", {
              style:{
                  color:'#FF0000',
                  borderRadius: "4px",
                  border: "1px solid #FF0000",
                  padding: "2px 4px",
                  fontSize:"11px"
                }
            }, engineStatus);
          }
        },
        {
          title: this.$t('currencysetting.marketengine'),
          width: 100,
          key: "engineStatus",
          render: (h, params) => {
            const row = params.row;
            var marketEngineStatus = this.$t('currencysetting.noengine');
            if(row.marketEngineStatus == "1"){
              marketEngineStatus = this.$t('currencysetting.running');
            }
            if(row.marketEngineStatus == "2"){
              marketEngineStatus = this.$t('currencysetting.paused');
            }

            if(row.marketEngineStatus == "1" && row.engineStatus == "2") {
              return h("div", [
              h(
                "Button",
                {
                  props: {type: "error",size: "small", loading:true},
                  style: {marginRight: "5px"},
                },
                this.$t('currencysetting.stopping')
              )]);
            }

            if(row.marketEngineStatus == "1") {
              return h("div", {
                    style:{
                      color:'#19be6b',
                      borderRadius: "4px",
                      border: "1px solid #19be6b",
                      padding: "2px 8px",
                      fontSize:"11px",
                      display: "inline-block"
                    }
                  },
                  marketEngineStatus
                );
            }

            if(row.marketEngineStatus == "2" && row.engineStatus == "1"){
              return h("div", [
              h(
                "Button",
                {
                  props: {type: "success",size: "small", loading:true},
                  style: {marginRight: "5px"},
                },
                this.$t('currencysetting.starting')
              )]);
            }
            return h("div", {
              style:{
                  color:'#FF0000',
                  borderRadius: "4px",
                  border: "1px solid #FF0000",
                  padding: "2px 8px",
                  fontSize:"11px",
                  display: "inline-block"
                }
            }, marketEngineStatus);
          }
        },
        {
          title: this.$t('managementofoptioncontractsineachperiod.status'),
          width: 70,
          key: "enable",
          render: (h, params) => {
            const row = params.row;
            const enable = row.enable == "1" ? this.$t('backstageadvertising.ontheshelf') : this.$t('backstageadvertising.offtheshelf');
            if(row.enable == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, enable);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, enable);
          }
        },
        {
          title: this.$t('currencymanagement1.sort'),
          key:"sort",
          width: 60
        },
        {
          title: this.$t('currencysetting.releaseactivities'),
          width: 150,
          key: "publishType",
          render: (h, params) => {
            const row = params.row;
            const currentTime = dtime(row.currentTime).format('YYYY-MM-DD HH:mm:ss');
            var publishType = this.$t('currencysetting.unknown');
            const txtColor = currentTime > row.clearTime ? "#B7B7B7" : "#42b983";

            if(row.publishType == "NONE"){
              publishType = this.$t('currencysetting.noactivity');
            }
            if(row.publishType == "QIANGGOU"){
              publishType = currentTime > row.clearTime ? this.$t('currencysetting.rushpurchaseended') : this.$t('currencysetting.rushpurchaseinprogress');
            }
            if(row.publishType == "FENTAN"){
              publishType = currentTime > row.clearTime ? this.$t('currencysetting.allocationended') : this.$t('currencysetting.allocationinprogress');
            }

            if(row.publishType == "QIANGGOU" || row.publishType == "FENTAN") {
                if(currentTime > row.clearTime){
                    return h("div", {
                      style:{
                        color: txtColor,
                        textAlign: "center"
                      }
                    }, [
                      h("span", {}, publishType)
                    ]);
                }else{
                    return h("div", {
                      style:{
                        color: txtColor,
                        textAlign: "left"
                      }
                    }, [
                      h("span", {}, publishType),
                      h("br", {}, ""),
                      h("span", {style:{fontSize:"8px"}}, this.$t('currencysetting.start')+params.row.startTime.substr(0, 16)),
                      h("br", {}, ""),
                      h("span", {style:{fontSize:"8px"}}, this.$t('currencysetting.end')+params.row.endTime.substr(0, 16)),
                      h("br", {}, ""),
                      h("span", {style:{fontSize:"8px"}}, this.$t('currencysetting.consolidation')+params.row.clearTime.substr(0, 16))
                    ]);
                }
            }
            return h("span", {
              style:{
                  color:'#b7b7b7'
                }
            }, publishType);
          }
        },
        {
          title: this.$t('transactiondetailsinlegalcurrency.handlingcharges'),
          width: 80,
          key: "symbol",
          render: (h, params) => {
            const row = params.row;
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {}, (row.fee * 1000 + "‰"))
              ]);
          }
        },
        {
          title: this.$t('currencysetting.recommended'),
          key: "flag",
          width: 60,
          render: (h, params) => {
            const row = params.row;
            const aFlag = row.flag === 1 ? "√" : "×";
            return h("span", {}, aFlag);
          }
        },
        {
          title: this.$t('currencyextensionmanagement.currencyprecision'),
          key:"coinScale",
          width: 90
        },
        {
          title: this.$t('currencysetting.basecurrencyprecision'),
          key:"baseCoinScale",
          width: 90
        },
        {
          title: this.$t('currencysetting.minimumturnover'),
          key:"minTurnover",
          width: 100
        },
        {
          title: this.$t('currencysetting.minimumorderquantity1'),
          key:"minVolume",
          width: 100
        },
        {
          title: this.$t('currencysetting.maximumorderquantity'),
          key:"maxVolume",
          width: 100
        },
        {
          title: this.$t('currencysetting.lowestpriceofsalesorder'),
          key:"minSellPrice",
          width: 100
        },
        {
          title: this.$t('currencysetting.paythehighestprice'),
          key:"maxBuyPrice",
          width: 100
        },
        {
          title: this.$t('perpetualcontractcurrencystandardmanagement.operation'),
          key: "xx",
          width: 330,
          fixed: 'right',
          render: (h, obj) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {type: "info",size: "small",icon:"android-settings"},
                  style: {marginRight: "5px"},
                  on: {
                    click: () => {
                      this.operation = 1;
                      this.fixModel = true;
                      this.fixSymbol = obj.row.symbol;
                      this.fee = obj.row.fee;
                      this.fixMaxBuyPrice = obj.row.maxBuyPrice;
                      this.fixMinTurnover = obj.row.minTurnover;
                      this.fixSort = obj.row.sort;
                      this.fixEnable = String(obj.row.enable);
                      this.fixVisible = String(obj.row.visible);
                      this.fixExchangeable = String(obj.row.exchangeable);
                      this.fixEnableMarketBuy = String(obj.row.enableMarketBuy);
                      this.fixEnableMarketSell = String(obj.row.enableMarketSell);
                      this.fixEnableBuy = String(obj.row.enableBuy);
                      this.fixEnableSell = String(obj.row.enableSell);
                    }
                  }
                },
                this.$t('currencysetting.settings')
              ),
              h(
                "Button",
                {
                  props: {type: "success",size: "small", icon:"ios-play"},
                  style: {marginRight: "5px", display: obj.row.engineStatus == "1" ? "none" : "inline-block"},
                  on: {
                    click: () => {
                      this.operation = 2;
                      this.startEngineModel = true;
                      this.targetSymbol = obj.row.symbol;
                    }
                  },
                  key: 'loading'
                },
                this.$t('currencysetting.startengine')
              ),
              h(
                "Button",
                {
                  props: {type: "error",size: "small", icon:"stop"},
                  style: {marginRight: "5px", display: obj.row.engineStatus != "1" ? "none" : "inline-block"},
                  on: {
                    click: () => {
                      this.operation = 3;
                      this.stopEngineModel = true;
                      this.targetSymbol = obj.row.symbol;
                    }
                  }
                },
                this.$t('currencysetting.stopengine')
              ),
              h(
                "Button",
                {
                  props: {type: "warning",size: "small"},
                  style: {marginRight: "5px"},
                  on: {
                    click: () => {
                      if(obj.row.engineStatus != "1"){
                        this.$Notice.error({
                            title: this.$t('currencysetting.failedtorevokealldelegates'),
                            desc: this.$t('currencysetting.note17'),
                            duration: 10
                        });
                        return;
                      }
                      if(obj.row.exchangeable == "1"){
                        this.$Notice.error({
                            title: this.$t('currencysetting.failedtorevokealldelegates'),
                            desc: this.$t('currencysetting.note29'),
                            duration: 10
                        });
                        return;
                      }
                      this.operation = 4;
                      this.cancelAllModel = true;
                      this.targetSymbol = obj.row.symbol;
                    }
                  }
                },
                this.$t('currencysetting.revokealldelegates')
              ),
              h(
                "Button",
                {
                  props: {type: "default",size: "small"},
                  style: {marginRight: "5px"},
                  on: {
                    click: () => {
                      if(obj.row.exchangeable != "1"){
                        this.$Notice.error({
                            title: this.$t('currencysetting.resetprompt'),
                            desc: this.$t('currencysetting.note7'),
                            duration: 10
                        });
                        return;
                      }
                      this.operation = 5;
                      this.resetModel = true;
                      this.targetSymbol = obj.row.symbol;
                    }
                  }
                },
                this.$t('currencysetting.resetengine')
              )
            ]);
          }
        }
      ],
      exchange: []
    };
  },
  methods: {
    checkbaseCoinScale(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.baseCoinScaleClass =  bol;
    },
    checkCoinScale(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.coinScaleClass =  bol;
    },
    checkFee(str) {
      let re = /\d|\d+.\d+$/g;
      this.feeClass = !re.test(String(str)) || !(str*1>0);
    },
    checkbaseSymbol(str) {
      let re = /[^A-Z]/g;
      this.baseSymbolClass =  re.test(str);
    },
    checkCoinSymbol(str) {
      let re = /[^A-Z]/g;
      this.coinSymbolClass =  re.test(str);
    },
    checkPair(str) {
     let re = /^[A-Z]+\/[A-Z]+$/g;
      this.checkSymbolClass = !re.test(str);
      console.log(this.checkSymbolClass);
		},
    checkminVolume(str) {
      let re = /\d|\d+.\d+$/g;
      this.minVolumeClass = !re.test(String(str));
    },
    checkmaxVolume(str) {
      let re = /\d|\d+.\d+$/g;
      this.maxVolumeClass = !re.test(String(str));
    },
    checkzone(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.zoneClass =  bol;
    },
    checkminSellPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.minSellPriceClass = !re.test(String(str));
    },
    checkmaxBuyPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.maxBuyPriceClass = !re.test(String(str));
    },
    checkminTurnover(str) {
      let re = /\d|\d+.\d+$/g;
      this.minTurnoverClass = !re.test(String(str));
    },
    checksort(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.sortClass =  bol;
    },
    checkpublishPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.publishPriceClass = !re.test(String(str));
    },
    confirmExchangeEngineMoel(){
      this.exchangeEngineModel = false;
    },
		confirmLoginPass() {
      this.$Loading.start();
      if(this.operation == 1){
          // 修改币币信息
  			  this.feeClass = false;
          let obj ={
            symbol:  this.fixSymbol,
            fee: this.fee,
  					enable: this.fixEnable,
            visible: this.fixVisible,
            exchangeable: this.fixExchangeable,
            enableMarketSell: this.fixEnableMarketSell,
            enableMarketBuy: this.fixEnableMarketBuy,
            enableSell: this.fixEnableSell,
            enableBuy: this.fixEnableBuy,
            maxBuyPrice: this.fixMaxBuyPrice,
            minTurnover: this.fixMinTurnover,
            sort: this.fixSort,
  					password: this.loginPW
  				}

          fixBBSetting(obj).then( res => {
            if (!res.code) {
  						this.$Message.success(this.$t('perpetualcontractcurrencystandardmanagement.modificationsucceeded'));
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else {
              this.$Message.error(this.$t('currencysetting.modificationfailed'));
              this.$Loading.error();
            }
          });
      }else if(this.operation == 2){
          // 启动交易引擎
          let obj ={
            symbol:  this.targetSymbol,
            password: this.loginPW
          }

          startBBTrader(obj).then( res => {
            if (!res.code) {
              this.$Notice.success({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.modificationsucceeded'),
                    desc: res.message+ this.$t('currencysetting.note11'),
                    duration: 10
                });
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else{
              this.$Notice.error({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.startfailed'),
                    desc: res.message,
                    duration: 10
                });
              this.$Loading.error();
            }
          });

      }else if(this.operation == 3){
          // 停止交易引擎
          let obj ={
            symbol:  this.targetSymbol,
            password: this.loginPW
          }

          stopBBTrader(obj).then( res => {
            if (!res.code) {
              this.$Notice.success({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.stopsuccessfully'),
                    desc: res.message+this.$t('currencysetting.note40'),
                    duration: 10
                });
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else{
              this.$Notice.error({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.stopfailed'),
                    desc: res.message,
                    duration: 10
                });
              this.$Loading.error();
            }
          });
      }else if(this.operation == 4){
          // 撤销所有委托
          let obj ={
            symbol:  this.targetSymbol,
            password: this.loginPW
          }
          cancelBBAllOrders(obj).then( res => {
            if (!res.code) {
              this.$Notice.success({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.revocationsucceeded'),
                    desc: res.message,
                    duration: 10
                });
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else{
              this.$Notice.error({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.revocationfailed'),
                    desc: res.message,
                    duration: 10
                });
              this.$Loading.error();
            }
          });
      }else if(this.operation == 5) {
        // 撤销所有委托
          let obj ={
            symbol:  this.targetSymbol,
            password: this.loginPW
          }
          resetBBTrader(obj).then( res => {
            if (!res.code) {
              this.$Notice.success({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.resetsucceeded'),
                    desc: res.message,
                    duration: 10
                });
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else{
              this.$Notice.error({
                    title: "【" + this.targetSymbol+ this.$t('currencysetting.resetfailed'),
                    desc: res.message,
                    duration: 10
                });
              this.$Loading.error();
            }
          });
      }
		},
    alterRobotClicked(){
      if(this.robotMode == 0) {
        // 创建机器人
        createRobotConfig(this.robotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: this.$t('currencysetting.createdsuccessfully'),
                      desc: res.message,
                      duration: 10
                  });
              this.robotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: this.$t('currencysetting.creationfailed'),
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }else{
        setRobotConfig(this.robotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: this.$t('perpetualcontractcurrencystandardmanagement.modificationsucceeded'),
                      desc: res.message,
                      duration: 10
                  });
              this.robotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: this.$t('perpetualcontractcurrencystandardmanagement.modificationfailed'),
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }
    },
    alterPriceRobotClicked(){
      if(this.priceRobotMode == 0) {
        // 创建机器人
        createPriceRobotConfig(this.priceRobotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: this.$t('currencysetting.createdsuccessfully'),
                      desc: res.message,
                      duration: 10
                  });
              this.robotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: this.$t('currencysetting.creationfailed'),
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }else{
        setPriceRobotConfig(this.priceRobotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: this.$t('perpetualcontractcurrencystandardmanagement.modificationsucceeded'),
                      desc: res.message,
                      duration: 10
                  });
              this.priceRobotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: this.$t('perpetualcontractcurrencystandardmanagement.modificationfailed'),
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }
    },
    confirmClicked() {
			this.loginPassModal =  true;
    },
    confirmAudit() {
      let judgeCondition = this.baseCoinScaleClass || this.coinScaleClass || this.feeClass ||
                  this.baseSymbolClass ||this.coinSymbolClass ||this.checkSymbolClass || this.minVolumeClass || this.maxVolumeClass ||
                  this.zoneClass || this.minSellPriceClass || this.maxBuyPriceClass;
      let judgeEmpty = !this.symbol || !this.coinSymbol || !this.baseSymbol
                      || !this.fee || !this.coinScale || !this.baseCoinScale;

      if(judgeCondition || judgeEmpty) {
        this.$Message.warning(this.$t('currencysetting.informationinputisincorrect'));
      } else {
        let obj= {
          symbol: this.symbol,
          coinSymbol: this.coinSymbol,
          baseSymbol: this.baseSymbol,
          enable: this.enable,
          fee: this.fee,
          coinScale: this.coinScale,
          baseCoinScale: this.baseCoinScale,
          minVolume: this.minVolume,
          maxVolume: this.maxVolume,
          zone: this.zone,
          minSellPrice: this.minSellPrice,
          maxBuyPrice: this.maxBuyPrice,
          minTurnover: this.minTurnover,
          sort: this.sort,
          publishPrice: this.publishPrice,
          publishType: this.publishType,
          publishAmount: this.publishAmount,
          startTime: dtime(this.startTime).format('YYYY-MM-DD HH:mm:ss'),
          endTime: dtime(this.endTime).format('YYYY-MM-DD HH:mm:ss'),
          clearTime: dtime(this.clearTime).format('YYYY-MM-DD HH:mm:ss'),
          visible: this.visible,
          robotType: this.robotType,
          exchangeable: this.exchangeable,
          enableMarketBuy: this.enableMarketBuy,
          enableMarketSell: this.enableMarketSell,
          enableBuy: this.enableBuy,
          enableSell: this.enableSell,
        }
        addBBSetting(obj).then( res => {
          if(!res.code) {
            this.$Notice.success({
                    title: this.$t('currencysetting.addedsuccessfully'),
                    desc: res.message,
                    duration: 10
                });
            this.current = 1;
            this.refreshdata(1);
          }else{
            this.$Notice.error({
                    title: this.$t('currencysetting.addfailed'),
                    desc: res.message,
                    duration: 10
                });
          }
        })
      }
    },
    selected(selection) {
      this.deleteArr = [];
      selection.forEach(item => {
        this.deleteArr.push(item.symbol)
      });
    },
    confirmDel() {
      delBBSetting({ ids: this.deleteArr }).then( res =>{
        if(!res.code) {
          this.$Notice.success({
                    title: this.$t('currencysetting.deletionsucceeded'),
                    desc: res.message,
                    duration: 10
                });
          this.current = 1;
          this.refreshdata(1);
        }else {
          this.$Notice.error({
                    title: this.$t('currencysetting.deletionfailed'),
                    desc: res.message,
                    duration: 10
                });
        }
      })
    },
    refreshPageManual() {
      this.ifLoading = true;
      this.refreshdata(this.current);
    },
    changePage(pageIndex) {
      this.ifLoading = true;
      this.current = pageIndex;
      this.refreshdata(pageIndex);
    },
    search(){
      this.refreshdata(1);
    },
    refreshdata(pageIndex) {

      queryBBSetting({ pageNo: pageIndex, pageSize: 50, coinSymbol: this.searchSymbol, baseSymbol: this.searchBase }).then( res => {
        this.exchange = res.data.content;
        this.pageNum = res.data.totalPages;
        this.ifLoading = false;
      });
    },
    delcoin() {
      if(!this.deleteArr.length) {
        this.$Message.warning(this.$t('currencysetting.noitemshavebeenselected'));
      } else  this.ifDelete = true;
    },
    addcoin() {
      this.auditModal = true;

      this.symbol = null;
      this.coinSymbol = null;
      this.baseSymbol = null;
      this.enable = "1";
      this.fee = null;
      this.coinScale = null;
      this.baseCoinScal = null;
      this.minVolume = null;
      this.maxVolume = null;
      this.zone = null;
      this.minSellPrice = null;
      this.maxBuyPrice = null;
      this.minTurnover = null;
      this.sort = null;
      this.publishPrice = null; // add  分摊抢购初始价格
      this.publishType = "1"; // 发行活动：1-无活动  2-抢购   3-分摊
      this.publishAmount = 0;
      this.startTime = null;
      this.endTime = null;
      this.clearTime = null;
      this.visible = "1";
      this.robotType = "0";
      this.exchangeable = "1";
      this.enableMarketBuy = "1";
      this.enableMarketSell = "1";
      this.enableBuy = "1";
      this.enableSell = "1";
    },
  },
  created() {
    this.refreshdata(1);
  }
};
</script>

<style lang="less" scoped>
  .auditModel{
    ul {
      padding-left: 20px;
      li {
        position: relative;
        margin-bottom: 18px;
        span{
          position: absolute;
          top: 0;
          left: 0;
          display: inline-block;
          width: 95px;
          text-align: right;
          i{
            font-size: 14px;
            font-weight: 700;
            color: #ec0909;
          }
        }
        p{
          padding-left: 100px;
          min-width: 300px;
          line-height: 32px;
          em{
            position: static;
            color: unset;
          }
        }
      }
    }
  }
  .setting-title{
    font-size:14px;font-weight:bold;padding-bottom:20px;
  }
.auditModel ul li>em{
    position: absolute;
    bottom: -15px;
    font-size:10px;
    margin-left: 100px;
    line-height:12px;
    font-style: normal;
    color: #ec0909;
  }
</style>
