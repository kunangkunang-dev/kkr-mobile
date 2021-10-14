package com.kunangkunang.app.view

import com.kunangkunang.app.model.login.Login
import com.kunangkunang.app.model.room.Room

interface LoginView {
    fun loadRoom(data: Room?)
    fun notifyLoginStatus(data: Login?)
    fun roomFailed()
    fun loginFailed()
}