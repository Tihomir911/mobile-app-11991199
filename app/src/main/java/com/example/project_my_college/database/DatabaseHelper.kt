package com.example.project_my_college.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "notes_app.db"
        const val DATABASE_VERSION = 3

        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_LOGIN = "login"
        const val COLUMN_USER_PASSWORD = "password"

        const val TABLE_NOTES = "Notes"
        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_USER_ID = "user_id"
        const val COLUMN_NOTE_CONTENT = "content"
        const val COLUMN_NOTE_CREATED_AT = "created_at"

        private const val INDEX_NOTES_USER_ID = "index_notes_user_id"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createUsersTable(db)
        createNotesTable(db)
        createNotesIndex(db)
    }

    private fun createUsersTable(db: SQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                    $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_USER_LOGIN TEXT NOT NULL UNIQUE,
                    $COLUMN_USER_PASSWORD TEXT NOT NULL
                )
            """.trimIndent()
        )
    }

    private fun createNotesTable(db: SQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS $TABLE_NOTES (
                    $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_NOTE_USER_ID INTEGER NOT NULL,
                    $COLUMN_NOTE_CONTENT TEXT NOT NULL,
                    $COLUMN_NOTE_CREATED_AT TEXT NOT NULL,
                    FOREIGN KEY ($COLUMN_NOTE_USER_ID)
                        REFERENCES $TABLE_USERS($COLUMN_USER_ID)
                        ON DELETE CASCADE
                )
            """.trimIndent()
        )
    }

    private fun createNotesIndex(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS $INDEX_NOTES_USER_ID " +
                    "ON $TABLE_NOTES($COLUMN_NOTE_USER_ID)"
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        createUsersTable(db)
        createNotesTable(db)

        // В старых версиях таблица Notes могла существовать без даты.
        if (!columnExists(db, TABLE_NOTES, COLUMN_NOTE_CREATED_AT)) {
            db.execSQL(
                "ALTER TABLE $TABLE_NOTES " +
                        "ADD COLUMN $COLUMN_NOTE_CREATED_AT TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                "UPDATE $TABLE_NOTES SET $COLUMN_NOTE_CREATED_AT = " +
                        "datetime('now', 'localtime') " +
                        "WHERE $COLUMN_NOTE_CREATED_AT = ''"
            )
        }

        if (oldVersion < 3) {
            createNotesIndex(db)
        }
    }

    private fun columnExists(
        db: SQLiteDatabase,
        tableName: String,
        columnName: String
    ): Boolean {
        db.rawQuery("PRAGMA table_info($tableName)", null).use { cursor ->
            val nameColumn = cursor.getColumnIndex("name")
            if (nameColumn == -1) return false

            while (cursor.moveToNext()) {
                if (cursor.getString(nameColumn) == columnName) {
                    return true
                }
            }
        }
        return false
    }
}
