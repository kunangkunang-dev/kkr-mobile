package com.kunangkunang.app.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.R
import com.kunangkunang.app.adapter.ItemAdapter
import com.kunangkunang.app.adapter.OrderAdapter
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.Utilities
import com.kunangkunang.app.helper.hideSystemBar
import com.kunangkunang.app.helper.setDimensionLarge
import com.kunangkunang.app.model.amenities.Amenities
import com.kunangkunang.app.model.amenities.AmenitiesData
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionData
import com.kunangkunang.app.model.transaction.TransactionDetails
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.presenter.AmenitiesPresenter
import com.kunangkunang.app.view.OrderView
import com.kunangkunang.app.view.TransactionView
import kotlinx.android.synthetic.main.activity_amenities.*
import kotlinx.android.synthetic.main.dialog_transaction.view.*
import kotlinx.coroutines.*
import kotlin.properties.Delegates


class AmenitiesActivity : AppCompatActivity(), TransactionView<Amenities?>, OrderView<Order?> {
    private lateinit var presenter: AmenitiesPresenter
    private lateinit var repository: AppRepository
    private lateinit var amenities: MutableList<AmenitiesData?>
    private lateinit var adapter: ItemAdapter<AmenitiesData?>
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var order: MutableList<Order?>
    private lateinit var loadAmenities: Job

    private var roomId by Delegates.notNull<Int>()
    private var customerId by Delegates.notNull<Int>()
    private var checkInNumber by Delegates.notNull<String>()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_amenities)
        setSupportActionBar(tb_amenities)

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
        amenities = mutableListOf()
        order = mutableListOf()

        // Initiate repository
        repository = AppRepository()

        // Initiate presenter
        presenter = AmenitiesPresenter(scope, this, repository)
        adapter = ItemAdapter(this, amenities, roomId, Constants.AMENITIES, this)
        orderAdapter = OrderAdapter(this, order, this)

        // Initiate recyclerView for laundry
        rv_amenities.layoutManager = LinearLayoutManager(this)
        rv_amenities.setHasFixedSize(true)
        rv_amenities.adapter = adapter

        // Initiate recyclerView for order
        rv_amenities_order.layoutManager = LinearLayoutManager(this)
        rv_amenities_order.setHasFixedSize(true)
        rv_amenities_order.adapter = orderAdapter

        // Set click listener
        btn_amenities_order.setOnClickListener {
            if (order.isNotEmpty()) {
                openTransactionDialog()
            } else {
                Toast.makeText(this, "Order cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        // Start downloading data
        loadAmenities = presenter.loadAmenities()
    }

    override fun loadData(data: Amenities?) {
        data?.data?.let {
            amenities.clear()
            amenities.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun loadDataFailed() {
        Log.e("AMENITIES", "Error")
    }

    override fun addOrder(data: Order?) {
        // Add item to order list
        data?.let {
            if (order.isNotEmpty()) {
                for (item in order) {
                    item?.let {
                        if (it == data) {
                            val newQty = data.orderQuantity?.let { qty ->
                                item.orderQuantity?.plus(qty)
                            }

                            item.orderQuantity = newQty
                            orderAdapter.notifyDataSetChanged()
                            isOrderEmpty()
                            return
                        }
                    }
                }
            }

            order.add(data)
            orderAdapter.notifyDataSetChanged()
            isOrderEmpty()
        }
    }

    override fun removeOrder(index: Int?) {
        // Remove item from orderlist
        index?.let {
            order.removeAt(index)
            orderAdapter.notifyDataSetChanged()
            isOrderEmpty()
        }
    }

    override fun addNotes(index: Int?) {

    }

    override fun loadTransaction(data: TransactionResponse?) {
        data?.status?.let {
            if (it == 200) {
                order.clear()
                orderAdapter.notifyDataSetChanged()
                isOrderEmpty()
                Toast.makeText(this, "Your order has been placed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun loadTransactionFailed() {
        Log.e("TRANSACTION", "Error")
    }

    private fun isOrderEmpty() {
        // Check whether order list is empty or not then show/hide label
        if (order.isNotEmpty()) {
            tv_amenities_empty.visibility = View.GONE
        } else {
            tv_amenities_empty.visibility = View.VISIBLE
        }
    }

    private fun openTransactionDialog() {
        // Initiate dialog builder
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_transaction, null)
        builder.setView(view)

        // Create dialog
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        // Set click listener
        view.btn_dialog_cancel.setOnClickListener { dialog.cancel() }
        view.btn_dialog_order.setOnClickListener {
            // Create transaction
            var totalPrice = 0
            val details: MutableList<TransactionDetails> = mutableListOf()

            for (item in order) {
                item?.let {
                    val itemId = it.itemId
                    val itemName = it.orderDetail
                    val itemCategoryId = it.categoryId
                    val itemQty = it.orderQuantity
                    val price = 0

                    totalPrice += itemQty?.let { it1 -> price.times(it1) } ?: 0

                    val detail = TransactionDetails(
                        itemId,
                        itemName,
                        itemCategoryId,
                        itemQty,
                        null,
                        null,
                        null,
                        price
                    )
                    details.add(detail)
                }
            }

            val transactionData = TransactionData(
                roomId,
                view.et_dialog_notes.text.toString(),
                Constants.AMENITIES,
                Utilities.generateTransactiondate(),
                customerId,
                totalPrice,
                checkInNumber,
                details
            )
            val transaction = Transaction(transactionData)

            // Post transaction to server
            presenter.postTransaction(transaction)

            // Close dialog
            dialog.cancel()
        }

        dialog.show()
        setDimensionLarge(dialog)
    }
}
