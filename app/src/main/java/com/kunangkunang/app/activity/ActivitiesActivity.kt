package com.kunangkunang.app.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.R
import com.kunangkunang.app.adapter.ItemAdapter
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.hideSystemBar
import com.kunangkunang.app.model.activities.Activities
import com.kunangkunang.app.model.activities.ActivitiesData
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.presenter.ActivitiesPresenter
import com.kunangkunang.app.view.OrderView
import com.kunangkunang.app.view.TransactionView
import kotlinx.android.synthetic.main.activity_activities.*
import kotlinx.coroutines.*
import kotlin.properties.Delegates


class ActivitiesActivity : AppCompatActivity(), TransactionView<Activities?>, OrderView<Order?> {
    private lateinit var presenter: ActivitiesPresenter
    private lateinit var repository: AppRepository
    private lateinit var activities: MutableList<ActivitiesData?>
    private lateinit var adapter: ItemAdapter<ActivitiesData?>
    private lateinit var loadActivities: Job

    private var roomId by Delegates.notNull<Int>()
    private var customerId by Delegates.notNull<Int>()
    private var checkInNumber by Delegates.notNull<String>()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_activities)
        setSupportActionBar(tb_activities)

        // Starting task
        initiateTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive) {
            scope.cancel()
        }
    }

    private fun initiateTask() {
        // Get shared preferences
        val sharedPreferences =
            getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        // Get room id
        sharedPreferences.getString("room", null)?.let { it ->
            val login = Gson().fromJson(it, Login::class.java)

            login.data?.id?.let {
                roomId = it
            }
        }

        // Get customer id
        sharedPreferences.getString("customer", null)?.let { it ->
            val customer = Gson().fromJson(it, Customer::class.java)

            customer.data?.customerId?.let {
                customerId = it
            }

            customer.data?.orderNumber?.let {
                checkInNumber = it
            }
        }

        // Initiate collection
        activities = mutableListOf()

        // Initiate repository
        repository = AppRepository()

        // Initiate presenter
        presenter = ActivitiesPresenter(scope, this, repository)
        adapter = ItemAdapter(this, activities, roomId, Constants.ACTIVITIES, this)

        // Initiate recyclerView for activities
        rv_activities.layoutManager = GridLayoutManager(this, 2)
        rv_activities.setHasFixedSize(true)
        rv_activities.adapter = adapter

        // Start downloading data
        loadActivities = presenter.loadActivities()
    }

    override fun loadData(data: Activities?) {
        data?.data?.let {
            activities.clear()
            activities.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun loadDataFailed() {
        Log.e("ACTIVITIES", "Error")
    }

    override fun addOrder(data: Order?) {
        // empty
    }

    override fun removeOrder(index: Int?) {
        // empty
    }

    override fun addNotes(index: Int?) {
        // empty
    }

    override fun loadTransaction(data: TransactionResponse?) {
        // empty
    }

    override fun loadTransactionFailed() {
        // empty
    }


}
