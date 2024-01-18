package com.example.theaterparking.ui

import android.content.Intent
import com.example.theaterparking.utils.KeyboardUtils
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.example.theaterparking.R
import com.example.theaterparking.api.api

class HomeScreen : AppCompatActivity() {
    // elements
    private lateinit var loginBtn: Button
    private lateinit var userNameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loader: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        loginBtn = findViewById(R.id.loginBtn)
        userNameField = findViewById(R.id.userName)
        passwordField = findViewById(R.id.password)
        loader = findViewById(R.id.loader)
        // focus on username field and open the keyboard
        userNameField.requestFocus()
        userNameField.viewTreeObserver.addOnGlobalLayoutListener {
            // Show the keyboard when the layout is ready
            if (!userNameField.hasFocus() && userNameField.isVisible) {
                KeyboardUtils.showKeyboard(this, userNameField)
            }
        }
        handleClick()
    }

    private fun handleClick() {
        loginBtn.setOnClickListener {
            // todo: api request here and check for login
            showLoader()
            // navigate to the parking entry screen
            startActivity(Intent(this, ShowSelectScreen::class.java))
        }
    }

    private val showLoader = {
        loader.visibility = ProgressBar.VISIBLE
        passwordField.visibility = EditText.GONE
        userNameField.visibility = EditText.GONE
        loginBtn.visibility = Button.GONE
    }

    private val hideLoader = {
        loader.visibility = ProgressBar.GONE
        userNameField.visibility = EditText.VISIBLE
        passwordField.visibility = EditText.VISIBLE
        loginBtn.visibility = Button.VISIBLE
    }
}