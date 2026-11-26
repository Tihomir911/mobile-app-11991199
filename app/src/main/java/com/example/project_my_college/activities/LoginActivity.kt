package com.example.project_my_college.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_my_college.R
import com.example.project_my_college.database.UserDao

class LoginActivity : AppCompatActivity() {

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDao = UserDao(this)

        // Проверяем сохранённую сессию и убеждаемся, что такой пользователь ещё есть в БД.
        val preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        if (preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            val savedId = preferences.getInt(KEY_USER_ID, -1)
            val savedLogin = preferences.getString(KEY_USER_LOGIN, "") ?: ""
            val savedUser = if (savedLogin.isNotBlank()) {
                userDao.getUserByLogin(savedLogin)
            } else {
                null
            }

            if (savedId > 0 && savedUser?.id == savedId) {
                openEditor(savedId, savedLogin)
                return
            }

            preferences.edit().clear().apply()
        }

        setContentView(R.layout.activity_login)

        loginEditText = findViewById(R.id.etLogin)
        passwordEditText = findViewById(R.id.etPassword)

        findViewById<android.widget.Button>(R.id.btnLogin).setOnClickListener {
            loginUser()
        }

        findViewById<android.widget.Button>(R.id.btnRegister).setOnClickListener {
            registerUser()
        }
    }

    private fun loginUser() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (login.isEmpty() || password.isEmpty()) {
            showMessage("Заполните логин и пароль")
            return
        }

        val user = userDao.getUserByLogin(login)
        if (user == null) {
            showMessage("Пользователь не найден. Зарегистрируйтесь")
            return
        }

        if (!userDao.verifyUserPassword(login, password)) {
            showMessage("Неверный пароль")
            return
        }

        saveUserSession(user.id, user.login)
        openEditor(user.id, user.login)
    }

    private fun registerUser() {
        val login = loginEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (login.isEmpty() || password.isEmpty()) {
            showMessage("Введите логин и пароль для регистрации")
            return
        }

        if (login.length < MIN_LOGIN_LENGTH) {
            showMessage("Логин должен содержать минимум $MIN_LOGIN_LENGTH символа")
            return
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            showMessage("Пароль должен содержать минимум $MIN_PASSWORD_LENGTH символа")
            return
        }

        if (userDao.userExists(login)) {
            showMessage("Такой пользователь уже существует")
            return
        }

        showPasswordConfirmation(login, password)
    }

    private fun showPasswordConfirmation(login: String, password: String) {
        val confirmationInput = EditText(this).apply {
            hint = "Повторите пароль"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSingleLine(true)
        }

        AlertDialog.Builder(this)
            .setTitle("Подтверждение пароля")
            .setMessage("Введите пароль ещё раз")
            .setView(confirmationInput)
            .setPositiveButton("Продолжить") { _, _ ->
                val confirmation = confirmationInput.text.toString()
                if (confirmation != password) {
                    showMessage("Пароли не совпадают")
                    return@setPositiveButton
                }

                createAccount(login, password)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun createAccount(login: String, password: String) {
        // Повторная проверка защищает от ситуации, когда такой логин создали за время диалога.
        if (userDao.userExists(login)) {
            showMessage("Такой пользователь уже существует")
            return
        }

        val newUserId = userDao.insertUser(login, password)
        if (newUserId <= 0L) {
            showMessage("Не удалось создать пользователя")
            return
        }

        showMessage("Регистрация успешно завершена")
        saveUserSession(newUserId.toInt(), login)
        openEditor(newUserId.toInt(), login)
    }

    private fun saveUserSession(userId: Int, login: String) {
        getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
            .edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_LOGIN, login)
            .putBoolean(KEY_IS_LOGGED_IN, true)
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

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val PREFERENCES_NAME = "notes_preferences"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_LOGIN = "user_login"
        const val KEY_IS_LOGGED_IN = "is_logged_in"

        private const val MIN_LOGIN_LENGTH = 3
        private const val MIN_PASSWORD_LENGTH = 4
    }
}
