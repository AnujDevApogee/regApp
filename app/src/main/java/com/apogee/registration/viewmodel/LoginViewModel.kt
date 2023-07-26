package com.apogee.registration.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apogee.registration.model.LoginRequest
import com.apogee.registration.repository.LoginRepository
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.Event
import com.apogee.registration.utils.checkVaildString
import com.apogee.registration.utils.isNetworkAvailable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application
    private var repository = LoginRepository(application)


    private val _loginResponse = MutableLiveData<DataResponse<out Any?>>()
    val loginResponse: LiveData<DataResponse<out Any?>>
        get() = _loginResponse


    private val _saveCredentials = MutableLiveData<Pair<Pair<String, String>, Boolean>>()
    val saveCredentials: LiveData<Pair<Pair<String, String>, Boolean>>
        get() = _saveCredentials


    private val _event = MutableLiveData<Event<String>>()
    val event: LiveData<Event<String>>
        get() = _event


    fun loginUser(username: String?, password: String?, isRemember: Boolean) {
        if (!app.isNetworkAvailable()) {
            _event.postValue(Event("No Internet Connection"))
            return
        }
        if (checkVaildString(username)) {
            _event.postValue(Event("InValid Email id"))
            return
        }
        if (checkVaildString(password)) {
            _event.postValue(Event("InValid Password"))
            return
        }

        viewModelScope.launch {
            val request = LoginRequest(
                password = password!!,
                username = username!!,
                projectName = ApiUrl.loginProjectName
            )
            repository.validateUser(
                request
            )
            repository.saveCredentials(request, isRemember)
        }
    }

    init {
        viewModelScope.launch {
            listenForResponse()
        }
    }

    private suspend fun listenForResponse() {
        repository.loginResponse.collect {
            _loginResponse.postValue(it)
        }
    }


    fun getLoginResponse() {
        viewModelScope.launch {
            repository.getSaveCredentials().let { res ->
                if (res.second && !checkVaildString(res.first.first) && !checkVaildString(res.first.second)) {
                    _saveCredentials.postValue(res)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        repository.cancel()
    }


}