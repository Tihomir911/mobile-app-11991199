package com.example.project_my_college.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_my_college.R
import com.example.project_my_college.database.UserDao

class LoginActivity : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginEditText = findViewById(R.id.etLogin)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        registerButton = findViewById(R.id.btnRegister)

        userDao = UserDao(this)

        loginButton.setOnClickListener {
            loginUser()
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun loginUser() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (login.isEmpty()  password.isEmpty()) {
            Toast.makeText(this, "Заполните логин и пароль", Toast.LENGTH_SHORT).show()
            return
        }

        val user = userDao.GetUserByLogin(login)

        if (user == null) {
            Toast.makeText(this, "Пользователь не найден. Зарегистрируйтесь", Toast.LENGTH_SHORT).show()
            return
        }

        // проверяем пароль из модели User
        if (user.password != password) {
            Toast.makeText(this, "Неверный пароль", Toast.LENGTH_SHORT).show()
            return
        }

        saveUserSession(user.id, login)
        openEditor(user.id, login)
    }

    private fun registerUser() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (login.isEmpty()  password.isEmpty()) {
            Toast.makeText(this, "Введите логин и пароль для регистрации", Toast.LENGTH_SHORT).show()
            return
        }

        if (login.length < 3) {
            Toast.makeText(this, "Логин должен содержать минимум 3 символа", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 4) {
            Toast.makeText(this, "Пароль должен содержать минимум 4 символа", Toast.LENGTH_SHORT).show()
            return
        }

        if (userDao.UserExists(login)) {
            Toast.makeText(this, "Такой пользователь уже существует", Toast.LENGTH_SHORT).show()
            return
        }

        val newUserId = userDao.InsertUser(login, password)

        if (newUserId == -1L) {
            Toast.makeText(this, "Не удалось создать пользователя", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Регистрация успешно завершена", Toast.LENGTH_SHORT).show()
        saveUserSession(newUserId.toInt(), login)
        openEditor(newUserId.toInt(), login)
    }

    private fun saveUserSession(userId: Int, login: String) {
        val preferences = getSharedPreferences("notes_preferences", MODE_PRIVATE)
        preferences.edit()
            .putInt("user_id", userId)
            .putString("user_login", login)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    private fun openEditor(userId: Int, login: String) {
        val intent = Intent(this, EditorActivity::class.java).apply {
            putExtra(EditorActivity.EXTRA_USER_ID, userId)
            putExtra(EditorActivity.EXTRA_USER_LOGIN, login)
        }
        startActivity(intent)
        finish()
    }
}

