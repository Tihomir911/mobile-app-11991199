package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context
import com.example.project_my_college.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    private fun getCurrentDate(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }

    fun insertNote(userId: Int, content: String): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_USER_ID, userId)
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, content)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, getCurrentDate())
        }

        return db.insert(
            DatabaseHelper.TABLE_NOTES,
            null,
            values
        )
    }

    fun getNotesByUserId(userId: Int): List<Note> {
        val db = dbHelper.readableDatabase
        val notes = mutableListOf<Note>()

        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            "${DatabaseHelper.COLUMN_NOTE_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_NOTE_ID} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(
                    it.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_NOTE_ID
                    )
                )

                val content = it.getString(
                    it.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_NOTE_CONTENT
                    )
                )

                val createdAt = it.getString(
                    it.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_NOTE_CREATED_AT
                    )
                )

                notes.add(
                    Note(
                        id,
                        userId,
                        content,
                        createdAt
                    )
                )
            }
        }

        return notes
    }

    fun updateNote(noteId: Int, newContent: String): Boolean {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, newContent)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, getCurrentDate())
        }

        val updatedRows = db.update(
            DatabaseHelper.TABLE_NOTES,
            values,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(noteId.toString())
        )

        return updatedRows > 0
    }

    fun deleteNote(noteId: Int): Boolean {
        val db = dbHelper.writableDatabase

        val deletedRows = db.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(noteId.toString())
        )

        return deletedRows > 0
    }
}
