package com.example.theaterparking.ui

import android.content.Intent
import com.example.theaterparking.utils.KeyboardUtils
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.theaterparking.R
import com.example.theaterparking.api.api
import com.example.theaterparking.dto.LoginDto
import com.example.theaterparking.utils.StorageUtils
import com.example.theaterparking.utils.ToastUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

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

        // check if the user is already logged in
        userNameField.setText("vamsi@gmail.com")
        passwordField.setText("jaibalayya")
    }

    private fun validateFields(): Boolean {
        val userName = userNameField.text.toString()
        val password = passwordField.text.toString()
        if (userName.isEmpty()) {
            userNameField.error = "Username is required"
            return false
        } else if (userName.length < 6) {
            userNameField.error = "Username must be at least 6 characters"
            return false
        } else if (password.isEmpty()) {
            passwordField.error = "Password is required"
            return false
        } else if (password.length < 6) {
            passwordField.error = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    private fun handleLogin() {
        GlobalScope.launch {
            val userName = userNameField.text.toString()
            val password = passwordField.text.toString()
            val loginDto = LoginDto(userName, password)
            val response = api.login(loginDto)
            if (response.isSuccessful) {
                // navigate to the parking entry screen
                val showsList = response.body()?.shows
                // pass the shows list to the next screen
                StorageUtils.putData("email", userName, this@HomeScreen)
                StorageUtils.putData("userId", response.body()?.id.toString(), this@HomeScreen)
                val intent = Intent(this@HomeScreen, ShowSelectScreen::class.java)
                intent.putExtra("shows", showsList?.toTypedArray())
                startActivity(intent)
            } else {
                // show error message
                runOnUiThread {
                    hideLoader()
                }
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                val msg = jsonObj.getString("msg")
                when (response.code()) {
                    // when i got 400 as response code
                    400 -> {
                        runOnUiThread {
                            ToastUtils.showToast(
                                this@HomeScreen,
                                msg,
                                Toast.LENGTH_LONG
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleClick() {
        try {
            loginBtn.setOnClickListener {
                showLoader()
                if (!validateFields()) {
                    hideLoader()
                    return@setOnClickListener
                } else {
                    handleLogin()
                }
            }
        } catch (e: Exception) {
            ToastUtils.showToast(this, e.message ?: "Something went wrong")
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