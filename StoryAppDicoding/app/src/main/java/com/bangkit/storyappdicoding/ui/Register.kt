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
import androidx.appcompat.app.AlertDialog
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.data.User
import com.bangkit.storyappdicoding.util.UserPreference
import com.bangkit.storyappdicoding.databinding.ActivityRegisterBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

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
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]

        binding.viewModel = registerViewModel

        registerViewModel.status.observe(this) {
            if (!it.error) {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.success))
                    setMessage(getString(R.string.register_success))
                    setPositiveButton(getString(R.string.okay)) { _, _ ->
                        startActivity(Intent(this@Register, Login::class.java))
                        finish()
                    }
                    create()
                    show()
                }
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = getString(R.string.empty_name)
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.empty_password)
                }
                else -> {
                    registerViewModel.register(
                        User(
                            "",
                            email,
                            password,
                            name,
                            "",
                            false
                        )
                    )
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

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(250)
        val name =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(250)
        val loginMessage =
            ObjectAnimator.ofFloat(binding.loginMessageTextView, View.ALPHA, 1f).setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.loginTextView, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title, name, email, password, register, loginMessage, login
            )
            startDelay = 500
            start()
        }
    }
}