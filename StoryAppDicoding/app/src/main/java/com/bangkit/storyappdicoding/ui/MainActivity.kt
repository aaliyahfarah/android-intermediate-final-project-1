package com.bangkit.storyappdicoding.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.databinding.ActivityMainBinding
import com.bangkit.storyappdicoding.util.StoryAdapters
import com.bangkit.storyappdicoding.util.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupView()
        setupViewModel()
        setupRecyclerView()
        setupAction()
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
        mainActivityViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainActivityViewModel::class.java]

        binding.viewModel = mainActivityViewModel

        mainActivityViewModel.user.observe(this) { user ->
            if (user.isLogin) {
                mainActivityViewModel.getDicodingStories(user.token)
            } else {
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }

        mainActivityViewModel.navigateToSelectedStory.observe(this) {
            if (it != null) {
                val intent = Intent(this, DetailStory::class.java)
                intent.putExtra("story", it)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = StoryAdapters(StoryAdapters.OnClickListener {
            mainActivityViewModel.displayStoryDetails(it)
        })
    }

    private fun setupAction() {
        binding.signOutButton.setOnClickListener {
            mainActivityViewModel.signOut()
        }

        binding.newStoryFloatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddStory::class.java))
        }
    }
}