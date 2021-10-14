package com.kunangkunang.app.view

import com.kunangkunang.app.model.banner.Banner
import com.kunangkunang.app.model.config.Config
import com.kunangkunang.app.model.customer.Customer
import com.kunangkunang.app.model.history.History
import com.kunangkunang.app.model.logout.Logout
import com.kunangkunang.app.model.menu.Menu
import com.kunangkunang.app.model.news.News
import com.kunangkunang.app.model.status.Status
import com.kunangkunang.app.model.weather.Weather

interface MainView {
    fun loadBanner(data: Banner?)
    fun loadMenu(data: MutableList<Menu?>)
    fun loadNews(data: News?)
    fun loadWeather(data: Weather?)
    fun loadCustomerInfo(data: Customer?)
    fun loadConfig(data: Config?)
    fun loadHistory(data: History?)
    fun setTotalPrice(price: Int)

    fun notifyPasswordStatus(data: Status?)
    fun notifySubmitItemStatus(data: Status?)
    fun notifyCheckoutStatus(data: Status?)
    fun notifyLogoutStatus(data: Logout?)
    fun notifyHelpStatus(data: Status?)
    fun notifyReviewStatus(data: Status?)

    fun bannerFailed()
    fun newsFailed()
    fun weatherFailed()
    fun customerFailed()
    fun checkOutFailed()
    fun logOutFailed()
    fun submitItemFailed()
    fun configFailed()
    fun passwordFailed()
    fun historyFailed()
    fun requestHelpFailed()
    fun submitReviewFailed()
}