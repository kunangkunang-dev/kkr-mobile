package com.kunangkunang.app.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.R
import com.kunangkunang.app.adapter.OrderAdapter
import com.kunangkunang.app.adapter.SpaAdapter
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.*
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.model.spa.Spa
import com.kunangkunang.app.model.spa.SpaData
import com.kunangkunang.app.model.spa.SpaDataSchedules
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionData
import com.kunangkunang.app.model.transaction.TransactionDetails
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.presenter.SpaPresenter
import com.kunangkunang.app.view.OrderView
import com.kunangkunang.app.view.TransactionView
import kotlinx.android.synthetic.main.activity_spa.*
import kotlinx.android.synthetic.main.dialog_comment.view.*
import kotlinx.android.synthetic.main.dialog_transaction.view.*
import kotlinx.android.synthetic.main.dialog_transaction.view.btn_dialog_cancel
import kotlinx.android.synthetic.main.dialog_transaction.view.et_dialog_notes
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class SpaActivity : AppCompatActivity(), TransactionView<Spa?>, OrderView<Order?> {
    private lateinit var presenter: SpaPresenter
    private lateinit var repository: AppRepository
    private lateinit var spaCategories: MutableList<String>
    private lateinit var adapter: SpaAdapter
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var spaData: MutableList<SpaData?>
    private lateinit var spaSchedules: MutableList<SpaDataSchedules?>
    private lateinit var order: MutableList<Order?>
    private lateinit var loadSpa: Job

    private var roomId by Delegates.notNull<Int>()
    private var customerId by Delegates.notNull<Int>()
    private var checkInNumber by Delegates.notNull<String>()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_spa)
        setSupportActionBar(tb_spa)

        // Starting task
        initiateTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive) {
            scope.cancel()
        }
    }

    override fun loadData(data: Spa?) {
        data?.data?.let { it ->
            for (item in it) {
                item?.name?.let {
                    spaCategories.add(it)
                }

                item?.let {
                    spaData.add(it)
                }
            }

            setSpinner()
        }
    }

    override fun loadDataFailed() {
        Log.e("SPA", "Error")
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
                            subtractSpaAvailability(data)
                            orderAdapter.notifyDataSetChanged()
                            isOrderEmpty()
                            return
                        }
                    }
                }
            }

            order.add(data)
            subtractSpaAvailability(data)
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
                addSpaAvailability(item)
            }

            orderAdapter.notifyDataSetChanged()
            isOrderEmpty()
        }
    }

    override fun addNotes(index: Int?) {
        openCommentDialog(index)
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
        spaCategories = mutableListOf()
        spaData = mutableListOf()
        spaSchedules = mutableListOf()
        order = mutableListOf()

        // Initiate repository
        repository = AppRepository()

        // Initiate presenter & adapter
        presenter = SpaPresenter(scope, this, repository)
        adapter =
            SpaAdapter(this, spaSchedules, roomId, Constants.SPA, null, null, null, null, this)
        orderAdapter = OrderAdapter(this, order, this)

        // Initiate recyclerView for food & beverage
        rv_spa.layoutManager = LinearLayoutManager(this)
        rv_spa.setHasFixedSize(true)
        rv_spa.adapter = adapter

        // Initiate recylerview for order
        rv_spa_order.layoutManager = LinearLayoutManager(this)
        rv_spa_order.setHasFixedSize(true)
        rv_spa_order.adapter = orderAdapter

        btn_spa_order.setOnClickListener {
            if (order.isNotEmpty()) {
                openTransactionDialog()
            } else {
                Toast.makeText(this, "Order cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        loadSpa = presenter.loadSpa()
    }

    private fun setSpinner() {
        // Initiate spinner
        val adapter = CustomSpinner(this, android.R.layout.simple_spinner_item, spaCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_spa.adapter = adapter

        // Add selected item listener
        spn_spa.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Empty method
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Load category
                loadCategory(position)
            }
        }
    }

    private fun loadCategory(index: Int) {
        // Select category and load related data
        spaSchedules.clear()
        spaData[index]?.let { it ->
            adapter.categoryId = it.id
            adapter.name = it.name
            adapter.image = it.image
            adapter.price = it.price

            it.schedules?.let {
                spaSchedules.addAll(it)
            }

            adapter.notifyDataSetChanged()
        }
    }

    private fun subtractSpaAvailability(data: Order) {
        val newSpa = spaSchedules.find { it?.id == data.itemId }
        val index = spaSchedules.indexOf(newSpa)
        newSpa?.spaAvailability = newSpa?.spaAvailability?.minus(1)

        spaSchedules[index] = newSpa
        adapter.notifyDataSetChanged()
    }

    private fun addSpaAvailability(data: Order) {
        val newSpa = spaSchedules.find { it?.id == data.itemId }
        val index = spaSchedules.indexOf(newSpa)
        newSpa?.spaAvailability = newSpa?.spaAvailability?.plus(1)

        spaSchedules[index] = newSpa
        adapter.notifyDataSetChanged()
    }

    private fun isOrderEmpty() {
        // Check whether order list is empty or not then show/hide label
        if (order.isNotEmpty()) {
            tv_spa_empty.visibility = View.GONE
        } else {
            tv_spa_empty.visibility = View.VISIBLE
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
                    val spaStart = it.spaStart
                    val spaEnd = it.spaEnd
                    val notes = it.notes

                    totalPrice += itemQty?.let { it1 -> price?.times(it1) } ?: 0

                    val detail = TransactionDetails(
                        itemCategoryId,
                        itemName,
                        null,
                        itemQty,
                        itemId,
                        spaStart?.substring(0, spaStart.length - 3),
                        spaEnd?.substring(0, spaEnd.length - 3),
                        price,
                        notes
                    )
                    details.add(detail)
                }
            }

            val transactionData = TransactionData(
                roomId,
                view.et_dialog_notes.text.toString(),
                Constants.SPA,
                Utilities.generateTransactiondate(),
                customerId,
                totalPrice,
                checkInNumber,
                details
            )
            val transaction = Transaction(transactionData)
            Log.e("transactionPayload", transaction.toString())
            // Post transaction to server

            presenter.postTransaction(transaction)

            // Close dialog
            dialog.cancel()
        }

        dialog.show()
        setDimensionLarge(dialog)
    }
}
