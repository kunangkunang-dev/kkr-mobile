package com.kunangkunang.app.presenter

import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.model.laundry.Laundry
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.view.TransactionView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LaundryPresenter(private val scope: CoroutineScope,
                       private val view: TransactionView<Laundry?>,
                       private val repository: AppRepository) {

    fun loadLaundry(): Job {
        return scope.launch {
            repository.getLaundry(object : AppRepositoryCallback<Laundry?> {
                override fun onDataLoaded(data: Laundry?) {
                    view.loadData(data)
                }

                override fun onDataError() {
                    view.loadDataFailed()
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