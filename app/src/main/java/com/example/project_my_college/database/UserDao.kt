package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context
import com.example.project_my_college.models.User
import java.security.MessageDigest

class UserDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // хэшируем пароль через sha-256
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        val hashString = StringBuilder()
        for (b in bytes) {
            hashString.append(String.format("%02x", b))
        }
        return hashString.toString()
    }

    // поиск пользователя по логину
    fun getUserByLogin(login: String): User? {
        val db = dbHelper.readableDatabase
        var user: User? = null

        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(login),
            null, null, null
        )

        // используем use, чтобы курсор точно закрылся сам даже при ошибке
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
                val userLogin = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_LOGIN))
                val passwordHash = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD))
                user = User(id, userLogin, passwordHash)
            }
        }

        db.close()
        return user
    }

    // добавление нового пользователя с защищенным паролем
    fun insertUser(login: String, password: String): Long {
        val db = dbHelper.writableDatabase
        val passwordHash = hashPassword(password)

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_LOGIN, login)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, passwordHash)
        }

        val newId = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        db.close()
        return newId
    }

    // проверка, занят ли логин
    fun userExists(login: String): Boolean {
        val db = dbHelper.readableDatabase
        var exists = false

        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_USER_ID),
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(login),
            null, null, null
        )

        cursor.use {
            exists = it.count > 0
        }

        db.close()
        return exists
    }
}