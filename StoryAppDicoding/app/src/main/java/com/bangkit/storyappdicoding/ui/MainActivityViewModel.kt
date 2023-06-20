package com.bangkit.storyappdicoding.ui

import androidx.lifecycle.*
import com.bangkit.storyappdicoding.data.*
import com.bangkit.storyappdicoding.util.ApiStatus
import com.bangkit.storyappdicoding.util.UserPreference
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class MainActivityViewModel (private val pref: UserPreference) : ViewModel(){
    val user: LiveData<User> = pref.getUser().asLiveData()

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus>
        get() = _apiStatus

    private val _story = MutableLiveData<List<Story>>()
    val story: LiveData<List<Story>>
        get() = _story

    private val _navigateToSelectedStory = MutableLiveData<Story>()
    val navigateToSelectedStory: LiveData<Story>
        get() = _navigateToSelectedStory

    fun signOut() {
        viewModelScope.launch {
            pref.deleteUser()
        }
    }

    fun getDicodingStories(token: String) {
        _apiStatus.value = ApiStatus.LOADING
        viewModelScope.launch {
            try {
                _story.value =
                    DicodingApiNetwork.dicodingApiService.getAllStory("Bearer $token")
                        .asDomainModel()
                _apiStatus.value = ApiStatus.DONE
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> _apiStatus.value = ApiStatus.NO_CONNECTION
                    else -> _apiStatus.value = ApiStatus.ERROR
                }
                _story.value = ArrayList()
            }
        }
    }


    fun displayStoryDetails(story: Story) {
        _navigateToSelectedStory.value = story
    }
}