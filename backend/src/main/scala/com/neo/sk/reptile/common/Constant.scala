package com.neo.sk.reptile.common

/**
  * Created by dry on 2018/4/27.
  **/
object Constant {

  object EquipState {
    val normal = 0
  }

  object OrderState {
    val normal = 0
    val canceled = 1
    val updated = 2
    val renew = 3 //续约
  }

  object AbnormalType {
    val useWithoutRent = 0
    val rentWithoutUse = 1
  }

  object CoinRecordType {
    val consumption = 0
    val income = 1
    val consumptionByAdmin = 2
    val incomeByAdmin = 3
  }

}
