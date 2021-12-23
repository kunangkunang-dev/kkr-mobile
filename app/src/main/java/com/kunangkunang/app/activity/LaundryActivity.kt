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
import com.kunangkunang.app.helper.setDimensionSmall
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.laundry.Laundry
import com.kunangkunang.app.model.laundry.LaundryData
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionData
import com.kunangkunang.app.model.transaction.TransactionDetails
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.presenter.LaundryPresenter
import com.kunangkunang.app.view.OrderView
import com.kunangkunang.app.view.TransactionView
import kotlinx.android.synthetic.main.activity_laundry.*
import kotlinx.android.synthetic.main.dialog_comment.view.*
import kotlinx.android.synthetic.main.dialog_transaction.view.*
import kotlinx.android.synthetic.main.dialog_transaction.view.btn_dialog_cancel
import kotlinx.android.synthetic.main.dialog_transaction.view.et_dialog_notes
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class LaundryActivity : AppCompatActivity(), TransactionView<Laundry?>, OrderView<Order?> {
    private lateinit var presenter: LaundryPresenter
    private lateinit var repository: AppRepository
    private lateinit var laundry: MutableList<LaundryData?>
    private lateinit var adapter: ItemAdapter<LaundryData?>
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var order: MutableList<Order?>
    private lateinit var loadLaundry: Job

    private var roomId by Delegates.notNull<Int>()
    private var customerId by Delegates.notNull<Int>()
    private var checkInNumber by Delegates.notNull<String>()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_laundry)
        setSupportActionBar(tb_laundry)

        // Starting task
        initiateTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive) {
            scope.cancel()
        }
    }

    override fun loadData(data: Laundry?) {
        data?.data?.let {
            laundry.clear()
            laundry.addAll(it)

            adapter.notifyDataSetChanged()
        }
    }

    override fun loadDataFailed() {
        Log.e("LAUNDRY", "Error")
    }

    override fun addOrder(data: Order?) {
        // Add item to order list
        data?.let {
            if (order.isNotEmpty()) {
                for (item in order) {
                    item?.let {
                        if (it.itemId == data.itemId) {
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

            order[it]?.let { item ->
                val newQty = item.orderQuantity?.minus(1)
                newQty?.let { qty ->
                    if (qty <= 0) {
                        order.removeAt(index)
                    } else {
                        item.orderQuantity = qty
                    }
                }
            }

            orderAdapter.notifyDataSetChanged()
            isOrderEmpty()
        }
    }

    override fun addNotes(index: Int?) {
        openCommentDialog(index)
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
        laundry = mutableListOf()
        order = mutableListOf()

        // Initiate repository
        repository = AppRepository()

        // Initiate presenter
        presenter = LaundryPresenter(scope, this, repository)
        adapter = ItemAdapter(this, laundry, roomId, Constants.LAUNDRY, this)
        orderAdapter = OrderAdapter(this, order, this)

        // Initiate recyclerView for laundry
        rv_laundry.layoutManager = LinearLayoutManager(this)
        rv_laundry.setHasFixedSize(true)
        rv_laundry.adapter = adapter

        // Initiate recyclerView for order
        rv_laundry_order.layoutManager = LinearLayoutManager(this)
        rv_laundry_order.setHasFixedSize(true)
        rv_laundry_order.adapter = orderAdapter

        // Set click listener
        btn_laundry_order.setOnClickListener {
            if (order.isNotEmpty()) {
                openTransactionDialog()
            } else {
                Toast.makeText(this, "Order cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        // Start downloading data
        loadLaundry = presenter.loadLaundry()
    }

    private fun isOrderEmpty() {
        // Check whether order list is empty or not then show/hide label
        if (order.isNotEmpty()) {
            tv_laundry_empty.visibility = View.GONE
        } else {
            tv_laundry_empty.visibility = View.VISIBLE
        }
    }

    private fun openCommentDialog(index: Int?) {
        //initiate dialog builder
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_comment, null)
        builder.setView(view)

        //create dialog
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        index?.let {orderIndex ->
            view.et_dialog_notes.setText(order[orderIndex]?.notes)
            view.btn_dialog_cancel.setOnClickListener { dialog.cancel() }
            view.btn_dialog_done.setOnClickListener {
                order[orderIndex]?.let { item ->
                    item.notes = view.et_dialog_notes.text.toString()
                }
                orderAdapter.notifyDataSetChanged()
                dialog.cancel()
            }
        }

        dialog.show()
        setDimensionSmall(dialog)

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
                    val price = it.orderPrice
                    val notes = it.notes

                    totalPrice += itemQty?.let { it1 -> price?.times(it1) } ?: 0

                    val detail = TransactionDetails(
                        itemId,
                        itemName,
                        itemCategoryId,
                        itemQty,
                        null,
                        null,
                        null,
                        price,
                        notes
                    )
                    details.add(detail)
                }
            }

            val transactionData = TransactionData(
                roomId,
                view.et_dialog_notes.text.toString(),
                Constants.LAUNDRY,
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
