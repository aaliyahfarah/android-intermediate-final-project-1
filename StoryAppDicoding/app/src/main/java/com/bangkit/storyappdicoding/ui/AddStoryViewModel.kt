package com.bangkit.storyappdicoding.ui

import androidx.lifecycle.*
import com.bangkit.storyappdicoding.data.DicodingApiNetwork
import com.bangkit.storyappdicoding.data.Status
import com.bangkit.storyappdicoding.data.User
import com.bangkit.storyappdicoding.util.ApiStatus
import com.bangkit.storyappdicoding.util.UserPreference
import com.bangkit.storyappdicoding.util.exceptionHandling
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.UnknownHostException

class AddStoryViewModel(private val pref: UserPreference) : ViewModel() {

    val user: LiveData<User> = pref.getUser().asLiveData()

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus>
        get() = _apiStatus

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    fun uploadStory(token: String, description: RequestBody, file: MultipartBody.Part) {
        _apiStatus.value = ApiStatus.LOADING
        viewModelScope.launch {
            try {
                val upload = DicodingApiNetwork.dicodingApiService.addNewStory(
                    "Bearer $token",
                    description,
                    file
                )
                _status.value = Status(upload.error, upload.message)
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