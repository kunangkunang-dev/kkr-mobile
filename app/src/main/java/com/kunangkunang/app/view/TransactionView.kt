package com.kunangkunang.app.view

import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.transaction.TransactionResponse

interface TransactionView<T> {
    fun loadData(data: T?)
    fun loadDataFailed()
    fun loadTransaction(data: TransactionResponse?)
    fun loadTransactionFailed()
}