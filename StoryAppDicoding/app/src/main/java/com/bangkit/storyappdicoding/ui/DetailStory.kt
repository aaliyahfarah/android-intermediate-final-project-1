package com.bangkit.storyappdicoding.ui

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.data.Story
import com.bangkit.storyappdicoding.databinding.ActivityDetailStoryBinding
import com.bangkit.storyappdicoding.util.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailStory : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailStoryViewModel: DetailStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_story)

        setupViewModel()
        setupData()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        supportActionBar?.hide()
        binding.lifecycleOwner = this
    }

    private fun setupViewModel() {
        detailStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DetailStoryViewModel::class.java]

        binding.viewModel = detailStoryViewModel
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<Story>("story") as Story
        detailStoryViewModel.getStoryData(story)
    }
}