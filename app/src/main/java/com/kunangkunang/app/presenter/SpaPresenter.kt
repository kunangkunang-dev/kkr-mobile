package com.kunangkunang.app.presenter

import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.model.spa.Spa
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.view.TransactionView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SpaPresenter(private val scope: CoroutineScope,
                   private val view: TransactionView<Spa?>,
                   private val repository: AppRepository) {

    fun loadSpa(): Job {
        return scope.launch {
            repository
                .getSpa(object : AppRepositoryCallback<Spa?> {
                    override fun onDataLoaded(data: Spa?) {
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