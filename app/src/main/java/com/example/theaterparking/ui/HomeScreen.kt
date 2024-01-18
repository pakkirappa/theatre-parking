package com.example.theaterparking.ui

import com.example.theaterparking.utils.KeyboardUtils
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.theaterparking.R


class HomeScreen : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var userNameField: EditText
    private lateinit var passwordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        loginBtn = findViewById(R.id.loginBtn)
        userNameField = findViewById(R.id.userName)
        passwordField = findViewById(R.id.password)
        // focus on username field and open the keyboard
        userNameField.requestFocus()

        userNameField.viewTreeObserver.addOnGlobalLayoutListener {
            // Show the keyboard when the layout is ready
            if (!userNameField.hasFocus())
                KeyboardUtils.showKeyboard(this, userNameField)
        }
        handleClick()
    }


    private fun handleClick() {
        loginBtn.setOnClickListener {
            // todo: api request here and check for login
        }
    }
}