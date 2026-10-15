package com.example.project_my_college.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "notes_app.db"
        const val DATABASE_VERSION = 1

        //табл users
        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_LOGIN = "login"
        const val COLUMN_USER_PASSWORD = "password"

        //табл notes
        const val TABLE_NOTES = "Notes"
        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_USER_ID = "user_id"
        const val COLUMN_NOTE_CONTENT = "content"
        const val COLUMN_NOTE_CREATED_AT = "created_at"
    }

    //когда бд создается впервые
    override fun onCreate(db: SQLiteDatabase) {
        //создание users
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_LOGIN TEXT UNIQUE NOT NULL,
                $COLUMN_USER_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        //создание notes
        val createNotesTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTE_USER_ID INTEGER NOT NULL,
                $COLUMN_NOTE_CONTENT TEXT NOT NULL,
                $COLUMN_NOTE_CREATED_AT TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_NOTE_USER_ID) 
                    REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()
        db.execSQL(createNotesTable)
    }

    //когда меняется версия базы
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //удалять старые таблицы и создать заново
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}
