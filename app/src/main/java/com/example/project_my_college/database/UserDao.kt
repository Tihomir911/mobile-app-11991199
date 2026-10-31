package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context
import com.example.project_my_college.models.User

class UserDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // поиск пользователя по логину
    fun GetUserByLogin(login: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(login),
            null, null, null
        )

        var user: User? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
            val userLogin = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_LOGIN))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD))
            user = User(id, userLogin, password)
        }
        cursor.close()
        db.close()
        return user
    }

    // добавление нового пользователя
    fun InsertUser(login: String, password: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_LOGIN, login)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, password)
        }
        val newId = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        db.close()
        return newId
    }

    // проверка на существование логина
    fun UserExists(login: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_USER_ID),
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(login),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
}