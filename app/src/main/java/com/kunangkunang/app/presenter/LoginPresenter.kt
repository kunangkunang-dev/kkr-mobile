package com.kunangkunang.app.presenter

import com.kunangkunang.app.api.AppRepository
import com.kunangkunang.app.api.AppRepositoryCallback
import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.room.Room
import com.kunangkunang.app.view.LoginView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginPresenter(private val scope: CoroutineScope,
                     private val view: LoginView,
                     private val repository: AppRepository) {

    fun loadRoom(): Job {
        return scope.launch {
            repository
                .getRoom(object : AppRepositoryCallback<Room?> {
                    override fun onDataLoaded(data: Room?) {
                        view.loadRoom(data)
                    }

                    override fun onDataError() {
                        view.roomFailed()
                    }
                })
        }
    }

    fun login(id: Int, password: String): Job {
        return scope.launch {
            repository.getLoginData(id, password, object : AppRepositoryCallback<Login?> {
                override fun onDataLoaded(data: Login?) {
                    view.notifyLoginStatus(data)
                }

                override fun onDataError() {
                    view.loginFailed()
                }
            })
        }
    }

}