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

        const val DATABASE_VERSION = 2

        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_LOGIN = "login"
        const val COLUMN_USER_PASSWORD = "password"

        const val TABLE_NOTES = "Notes"
        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_USER_ID = "user_id"
        const val COLUMN_NOTE_CONTENT = "content"
        const val COLUMN_NOTE_CREATED_AT = "created_at"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createUsersTable(db)
        createNotesTable(db)
    }

    private fun createUsersTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_LOGIN TEXT NOT NULL UNIQUE,
                $COLUMN_USER_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    private fun createNotesTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTE_USER_ID INTEGER NOT NULL,
                $COLUMN_NOTE_CONTENT TEXT NOT NULL,
                $COLUMN_NOTE_CREATED_AT TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_NOTE_USER_ID)
                    REFERENCES $TABLE_USERS($COLUMN_USER_ID)
                    ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
        }
    }
}
