package com.kunangkunang.app.presenter

import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.model.activities.Activities
import com.kunangkunang.app.model.amenities.Amenities
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.view.TransactionView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivitiesPresenter(private val scope: CoroutineScope,
                         private val view: TransactionView<Activities?>,
                         private val repository: AppRepository
) {

    fun loadActivities(): Job {
        return scope.launch {
            repository.getActivities(object : AppRepositoryCallback<Activities?> {
                override fun onDataLoaded(data: Activities?) {
                    view.loadData(data)
                }

                override fun onDataError() {
                    view.loadDataFailed()
                }
            })
        }
    }

}