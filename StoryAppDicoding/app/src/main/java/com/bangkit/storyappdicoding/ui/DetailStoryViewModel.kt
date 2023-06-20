package com.bangkit.storyappdicoding.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.storyappdicoding.data.Status
import com.bangkit.storyappdicoding.data.Story
import com.bangkit.storyappdicoding.util.ApiStatus
import com.bangkit.storyappdicoding.util.UserPreference

class DetailStoryViewModel(private val pref: UserPreference) : ViewModel() {
    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus>
        get() = _apiStatus

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story>
        get() = _story

    fun getStoryData(story: Story) {
        _apiStatus.value = ApiStatus.LOADING
        _story.value = story
        _apiStatus.value = ApiStatus.DONE
    }
}