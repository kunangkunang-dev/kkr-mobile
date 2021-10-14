package com.kunangkunang.app.presenter

import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.model.amenities.Amenities
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.view.TransactionView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AmenitiesPresenter(private val scope: CoroutineScope,
                         private val view: TransactionView<Amenities?>,
                         private val repository: AppRepository) {

    fun loadAmenities(): Job {
        return scope.launch {
            repository.getAmenities(object : AppRepositoryCallback<Amenities?> {
                override fun onDataLoaded(data: Amenities?) {
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