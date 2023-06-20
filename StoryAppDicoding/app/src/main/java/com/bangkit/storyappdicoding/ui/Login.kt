package com.bangkit.storyappdicoding.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.databinding.ActivityLoginBinding
import com.bangkit.storyappdicoding.util.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setupView()
        setupViewModel()
        setupAction()
        setupAnimation()
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
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        binding.viewModel = loginViewModel

        loginViewModel.status.observe(this) {
            if (!it.error) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.empty_password)
                }
                else -> {
                    loginViewModel.login(email, password)
                }
            }
        }
    }

    private fun setupAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(250)
        val registerMessage =
            ObjectAnimator.ofFloat(binding.registerMessageTextView, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                email, password, login, registerMessage, register
            )
            startDelay = 500
            start()
        }
    }
}