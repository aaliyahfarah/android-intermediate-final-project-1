package com.bangkit.storyappdicoding.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyappdicoding.data.DicodingApiNetwork
import com.bangkit.storyappdicoding.data.Status
import com.bangkit.storyappdicoding.data.User
import com.bangkit.storyappdicoding.util.ApiStatus
import com.bangkit.storyappdicoding.util.UserPreference
import com.bangkit.storyappdicoding.util.exceptionHandling
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class LoginViewModel (private val pref: UserPreference) : ViewModel() {
    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus>
        get() = _apiStatus

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    fun login(email: String, password: String) {
        _apiStatus.value = ApiStatus.LOADING
        viewModelScope.launch {
            try {
                val networkLogin =
                    DicodingApiNetwork.dicodingApiService.login(email, password)
                if (!networkLogin.error) {
                    pref.saveUser(
                        User(
                            networkLogin.loginResult.userId,
                            email,
                            password,
                            networkLogin.loginResult.name,
                            networkLogin.loginResult.token,
                            true
                        )
                    )
                }
                _status.value = Status(networkLogin.error, networkLogin.message)
                _apiStatus.value = ApiStatus.DONE
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> _apiStatus.value = ApiStatus.NO_CONNECTION
                    else -> _apiStatus.value = ApiStatus.ERROR
                }
                _status.value = Status(true, exceptionHandling(e))
            }
        }
    }
}