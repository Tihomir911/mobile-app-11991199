package com.example.project_my_college

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.project_my_college.activities.EditorActivity
import com.example.project_my_college.activities.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // получаем сохраненные данные сессии
        val preferences = getSharedPreferences("notes_preferences", MODE_PRIVATE)
        val isLoggedIn = preferences.getBoolean("is_logged_in", false)
        val userId = preferences.getInt("user_id", -1)
        val userLogin = preferences.getString("user_login", "") ?: ""

        // если юзер уже залогинен, сразу перекидываем его в редактор заметок
        if (isLoggedIn && userId != -1) {
            val intent = Intent(this, EditorActivity::class.java).apply {
                putExtra(EditorActivity.EXTRA_USER_ID, userId)
                putExtra(EditorActivity.EXTRA_USER_LOGIN, userLogin)
            }
            startActivity(intent)
        } else {
            // иначе открываем экран входа
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // закрываем MainActivity, чтобы при нажатии кнопки назад не попадать на пустой экран
        finish()
    }
}
