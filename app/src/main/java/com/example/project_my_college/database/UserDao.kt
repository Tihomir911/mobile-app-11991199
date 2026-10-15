package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context

class UserDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    //ищем пользователя по логину
    //вернет user или null
    fun GetUserByLogin(login: String): Any? {
        //TODO (будет позже)
        return null
    }

    //новый юзер
    //вернуть айди юзера или -1 при ошибке
    fun InsertUser(login: String, password: String): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_LOGIN, login)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, password)
        }

        val newId = db.insert(
            DatabaseHelper.TABLE_USERS,
            null,
            values
        )

        db.close()
        return newId
    }

    //проверяем есть ли такой логин
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
