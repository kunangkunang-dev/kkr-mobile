package com.kunangkunang.app.activity

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.kunangkunang.app.BuildConfig
import com.kunangkunang.app.R
import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.constant.Constants
import com.kunangkunang.app.helper.CustomSpinner
import com.kunangkunang.app.helper.DeviceAdminReceiver
import com.kunangkunang.app.helper.hideSystemBar
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.model.room.RoomData
import com.kunangkunang.app.presenter.LoginPresenter
import com.kunangkunang.app.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity(), LoginView {
    private lateinit var presenter: LoginPresenter
    private lateinit var repository: AppRepository
    private lateinit var roomNames: MutableList<String>
    private lateinit var room: MutableList<RoomData>
    private lateinit var selectedRoom: RoomData
    private lateinit var loadingRoom: Job
    private lateinit var loggingIn: Job
    private lateinit var login: Login
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    private var state by Delegates.notNull<Boolean>()

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()

        // Verify lock policies
        verifyLockPolicies()

        // Starting task
        checkLoginState()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (state) {
            if (scope.isActive) {
                scope.cancel()
            }
        }
    }

    override fun loadRoom(data: Room?) {
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

            // Set data to spinner
            setSpinner()
        }
    }

    override fun notifyLoginStatus(data: Login?) {
        data?.let {
            if (it.message == "Success.") {
                login = it

                // Store data in shared preferences
                val sharedPreferences =
                    getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("room", Gson().toJson(login))
                editor.apply()

                // Start main activity
                lockApp()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                et_login_password.text = null
                et_login_password.hint = "Wrong password"
            }
        }
    }

    override fun roomFailed() {
        Log.e("ROOM", "Error")
    }

    override fun loginFailed() {
        Log.e("LOGIN", "Error")
    }

    private fun checkLoginState() {
        // Check stored data
        val sharedPreferences =
            getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val room = sharedPreferences.getString("room", null)

        // If stored data != null proceed to main activity
        if (room != null) {
            state = false
            lockApp()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            state = true
            initiateTask()
        }
    }

    private fun initiateTask() {
        // Set content view
        setContentView(R.layout.activity_login)

        // Initiate collections
        roomNames = mutableListOf()
        room = mutableListOf()

        // Initiate repository
        repository = AppRepository()

        // Initiate presenter
        presenter = LoginPresenter(scope, this, repository)

        // Initiate listener for login button
        btn_login.setOnClickListener {
            login()
        }

        loadingRoom = presenter.loadRoom()
    }

    private fun login() {
        // Check if password empty, if not continue to login
        if (et_login_password.text.isEmpty()) {
            et_login_password.hint = "Password can not be empty"
        } else {
            selectedRoom.id?.let {
                loggingIn = presenter.login(it, et_login_password.text.toString())
            }
        }
    }

    private fun setSpinner() {
        // Initiate spinner
        val adapter = CustomSpinner(this, android.R.layout.simple_spinner_item, roomNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_login_room.adapter = adapter

        // Add selected item listener
        spn_login_room.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    private fun lockApp() {
        // Hide home button, recent apps button, and status bar in upcoming activities
        if (devicePolicyManager.isDeviceOwnerApp(applicationContext.packageName)) {
            packageManager
                .setComponentEnabledSetting(
                    ComponentName(applicationContext, MainActivity::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
        } else {
            Toast.makeText(applicationContext, R.string.not_lock_whitelisted, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun verifyLockPolicies() {
        // Initiate device policy manager
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        // Initiate admin component
        adminComponentName = DeviceAdminReceiver().getComponentName(this)

        // Check to see if started by LockActivity and disable LockActivity if so
        intent?.let {
            if (it.getIntExtra(Constants.MAIN_ACTIVITY_KEY, 0) == Constants.FROM_MAIN_ACTIVITY) {
                if (devicePolicyManager.isAdminActive(adminComponentName)) {
                    devicePolicyManager
                        .clearPackagePersistentPreferredActivities(
                            adminComponentName,
                            packageName
                        )

                    packageManager
                        .setComponentEnabledSetting(
                            ComponentName(applicationContext, MainActivity::class.java),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                }

            }
        }
    }
}
