package com.kunangkunang.app.presenter

import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.fnb.FnbCategory
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.view.InternalTransactionView
import com.kunangkunang.app.view.TransactionView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FnbPresenter(private val scope: CoroutineScope,
                   private val view: TransactionView<FnbCategory?>,
                   private val viewInternal: InternalTransactionView,
                   private val repository: AppRepository) {

    fun loadFnbCategory(): Job {
        return scope.launch {
            repository.getFoodCategory(object : AppRepositoryCallback<FnbCategory?> {
                override fun onDataLoaded(data: FnbCategory?) {
                    view.loadData(data)
                }

                override fun onDataError() {
                    view.loadDataFailed()
                }
            })
        }
    }

    fun loadRoom(): Job {
        return scope.launch {
            repository
                .getRoom(object : AppRepositoryCallback<Room?> {
                    override fun onDataLoaded(data: Room?) {
                        viewInternal.loadRoom(data)
                    }

                    override fun onDataError() {
                        viewInternal.roomFailed()
                    }
                })
        }
    }

    fun loadCustomerInfo(id: Int): Job {
        return scope.launch {
            repository.getCustomerInfo(id, object : AppRepositoryCallback<Customer?> {
                override fun onDataLoaded(data: Customer?) {
                    viewInternal.loadCustomerInfo(data)
                }

                override fun onDataError() {
                    viewInternal.customerFailed()
                }

            })
        }
    }

    fun postTransaction(transaction: Transaction): Job {
        return scope.launch {
            repository.postTransaction(transaction, object : AppRepositoryCallback<TransactionResponse?> {
                override fun onDataLoaded(data: TransactionResponse?) {
                    view.loadTransaction(data)
                }

                override fun onDataError() {
                    view.loadDataFailed()
                }
            })
        }
    }
}