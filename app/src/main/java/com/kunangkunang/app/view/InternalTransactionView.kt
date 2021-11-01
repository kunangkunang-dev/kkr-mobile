package com.kunangkunang.app.view

import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.transaction.TransactionResponse

interface InternalTransactionView {
    fun loadRoom(data: Room?)
    fun roomFailed()
    fun loadCustomerInfo(data: Customer?)
    fun customerFailed()

}