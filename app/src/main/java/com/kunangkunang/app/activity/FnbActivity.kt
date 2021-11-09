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
import com.kunangkunang.app.adapter.ItemAdapter
import com.kunangkunang.app.adapter.OrderAdapter
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.CustomSpinner
import com.kunangkunang.app.helper.Utilities
import com.kunangkunang.app.helper.hideSystemBar
import com.kunangkunang.app.helper.setDimensionLarge
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.fnb.FnbCategory
import com.kunangkunang.app.model.fnb.FnbCategoryData
import com.kunangkunang.app.model.fnb.FnbCategoryDataFood
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.order.Order
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.room.RoomData
import com.kunangkunang.app.model.transaction.Transaction
import com.kunangkunang.app.model.transaction.TransactionData
import com.kunangkunang.app.model.transaction.TransactionDetails
import com.kunangkunang.app.model.transaction.TransactionResponse
import com.kunangkunang.app.presenter.FnbPresenter
import com.kunangkunang.app.view.InternalTransactionView
import com.kunangkunang.app.view.OrderView
import com.kunangkunang.app.view.TransactionView
import kotlinx.android.synthetic.main.activity_fnb.*
import kotlinx.android.synthetic.main.dialog_name_check.view.*
import kotlinx.android.synthetic.main.dialog_transaction.view.*
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class FnbActivity : AppCompatActivity(), TransactionView<FnbCategory?>, InternalTransactionView,
    OrderView<Order?> {
    private lateinit var presenter: FnbPresenter
    private lateinit var repository: AppRepository
    private lateinit var fnbCategories: MutableList<String>
    private lateinit var roomNames: MutableList<String>
    private lateinit var room: MutableList<RoomData>
    private lateinit var selectedRoom: RoomData
    private lateinit var adapter: ItemAdapter<FnbCategoryDataFood?>
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var fnbData: MutableList<FnbCategoryData?>
    private lateinit var fnbs: MutableList<FnbCategoryDataFood?>
    private lateinit var order: MutableList<Order?>
    private lateinit var loadFnb: Job
    private lateinit var loadRoom: Job
    private lateinit var loadCustomerInfo: Job

    private var isRestaurant: Boolean = false
    private var customer: Customer? = null

    private var categoryId by Delegates.notNull<Int>()
    private var roomId by Delegates.notNull<Int>()
    private var customerId by Delegates.notNull<Int>()
    private var checkInNumber by Delegates.notNull<String>()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        setContentView(R.layout.activity_fnb)
        setSupportActionBar(tb_fnb)

        // Starting task
        initiateTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scope.isActive) {
            scope.cancel()
        }
    }

    override fun loadData(data: FnbCategory?) {
        data?.data?.let { it ->
            for (item in it) {
                item?.name?.let {
                    fnbCategories.add(it)
                }

                item?.let {
                    fnbData.add(it)
                }
            }

            setSpinner()
        }
    }

    override fun loadDataFailed() {
        Log.e("FnB", "Error")
    }

    override fun loadRoom(data: Room?) {
        Log.e("roomData", data.toString())
        data?.data?.let { it ->
            // Add data to list
            for (item in it) {
                item?.roomNumber?.let {
                    roomNames.add(it)
                }

                item?.let {
                    room.add(it)
                }
            }

            // Set data to room spinner
            setRoomSpinner()
        }
    }

    override fun roomFailed() {
        Log.e("roomData", "failed to load roomdata")
    }

    override fun loadCustomerInfo(data: Customer?) {
        data?.let { customer ->
            customer.data?.let {
                this.customer = customer
                openNameCheckDialog()
                Log.e("customerInfo", it.toString())
            } ?: kotlin.run {
                Toast.makeText(this, "Room Kosong Kamu berbohong ya hihihi", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun customerFailed() {
        Log.e("CUSTOMER", "Error")
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

    override fun loadTransaction(data: TransactionResponse?) {
        Log.e("transactionResponse - SUCCESS", data.toString())
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
                Log.e("roomId", it.toString())
                roomId = it
            }

            login.data?.type.let {
                Log.e("roomType", it.toString())
                if (it == Constants.ROOM_RESTAURANT) {
                    isRestaurant = true
                }
            }

        }

        // Get customer id
        sharedPreferences.getString("customer", null)?.let { it ->
            val customer = Gson().fromJson(it, Customer::class.java)

            customer.data?.customerId?.let {
                Log.e("customerId", it.toString())
                customerId = it
            }

            customer.data?.orderNumber?.let {
                Log.e("checkInNumber", it)
                checkInNumber = it
            }
        }

        // Initiate collection
        fnbCategories = mutableListOf()
        fnbData = mutableListOf()
        fnbs = mutableListOf()
        order = mutableListOf()
        roomNames = mutableListOf()
        room = mutableListOf()

        // Initiate repository
        repository = AppRepository()

        // Initiate presenter & adapter
        presenter = FnbPresenter(scope, this, this, repository)
        adapter = ItemAdapter(this, fnbs, roomId, Constants.FNB, this)
        orderAdapter = OrderAdapter(this, order, this)

        // Initiate recyclerView for food & beverage
        rv_fnb.layoutManager = LinearLayoutManager(this)
        rv_fnb.setHasFixedSize(true)
        rv_fnb.adapter = adapter

        // Initiate recylerview for order
        rv_fnb_order.layoutManager = LinearLayoutManager(this)
        rv_fnb_order.setHasFixedSize(true)
        rv_fnb_order.adapter = orderAdapter

        // Set click listener
        btn_fnb_order.setOnClickListener {
            if (order.isNotEmpty()) {
                if (isRestaurant) {
                    selectedRoom.id?.let {
                        loadCustomerInfo = presenter.loadCustomerInfo(it)
                    } ?: kotlin.run {
                        Toast.makeText(this, "Pilih Room", Toast.LENGTH_LONG).show()
                    }
                } else {
                    openTransactionDialog()
                }
            } else {
                Toast.makeText(this, "Order cannot be empty", Toast.LENGTH_LONG).show()
            }

        }

        // Start downloading data
        loadFnb = presenter.loadFnbCategory()
        if (isRestaurant) {
            cvSpnRoom.visibility = View.VISIBLE
            loadRoom = presenter.loadRoom()
        } else {
            cvSpnRoom.visibility = View.GONE
        }
    }

    private fun setSpinner() {
        // Initiate spinner
        val adapter = CustomSpinner(this, android.R.layout.simple_spinner_item, fnbCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_fnb.adapter = adapter

        // Add selected item listener
        spn_fnb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    private fun setRoomSpinner() {
        // Initiate spinner
        val adapter = CustomSpinner(this, android.R.layout.simple_spinner_item, roomNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRoom.adapter = adapter

        // Add selected item listener
        spnRoom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Empty method
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Change text size and text color
                selectedRoom = room[position]
            }
        }
    }

    private fun loadCategory(index: Int) {
        // Select category then load related data
        fnbs.clear()
        fnbData[index]?.let { it ->
            it.id?.let { categoryId = it }
            it.food?.let {
                fnbs.addAll(it)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun isOrderEmpty() {
        // Check whether order list is empty or not then show/hide label
        if (order.isNotEmpty()) {
            tv_fnb_empty.visibility = View.GONE
        } else {
            tv_fnb_empty.visibility = View.VISIBLE
        }
    }

    private fun openNameCheckDialog() {
        // Initiate dialog builder
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_name_check, null)
        builder.setView(view)

        // Create dialog
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        view.tvNameConfirmation.text = "Apakah benar ini ${customer?.data?.customer?.name} !!?!?!?"
        view.btnCancel.setOnClickListener { dialog.cancel() }
        view.btnConfirm.setOnClickListener {
            openTransactionDialog()
            dialog.cancel()
        }


        dialog.show()
        setDimensionLarge(dialog)

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

                    totalPrice += itemQty?.let { it1 -> price?.times(it1) } ?: 0

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

            val transactionData: TransactionData = if (isRestaurant) {
                TransactionData(
                    selectedRoom.id!!,
                    view.et_dialog_notes.text.toString(),
                    Constants.FNB,
                    Utilities.generateTransactiondate(),
                    customer?.data?.customerId!!,
                    totalPrice,
                    customer?.data?.orderNumber!!,
                    details
                )
            } else {
                TransactionData(
                    roomId,
                    view.et_dialog_notes.text.toString(),
                    Constants.FNB,
                    Utilities.generateTransactiondate(),
                    customerId,
                    totalPrice,
                    checkInNumber,
                    details
                )
            }
            //normal transaction
            //transactionData = TransactionData(roomId, view.et_dialog_notes.text.toString(), Constants.FNB, Utilities.generateTransactiondate(), customerId, totalPrice, checkInNumber, details)

            //restaurant transaction
            //transactionData = TransactionData(roomId , view.et_dialog_notes.text.toString(), Constants.FNB, Utilities.generateTransactiondate(), customerId, totalPrice, checkInNumber, details)

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
