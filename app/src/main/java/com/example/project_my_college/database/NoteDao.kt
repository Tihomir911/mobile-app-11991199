package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context
import com.example.project_my_college.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDao(context: Context) {

    private val dbHelper = DatabaseHelper(context.applicationContext)

    private fun getCurrentDate(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }

    fun insertNote(userId: Int, content: String): Long {
        if (userId <= 0) return -1L

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_USER_ID, userId)
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, content)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, getCurrentDate())
        }

        return dbHelper.writableDatabase.insert(
            DatabaseHelper.TABLE_NOTES,
            null,
            values
        )
    }

    fun getNotesByUserId(userId: Int): List<Note> {
        if (userId <= 0) return emptyList()

        val notes = mutableListOf<Note>()
        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_NOTES,
            arrayOf(
                DatabaseHelper.COLUMN_NOTE_ID,
                DatabaseHelper.COLUMN_NOTE_USER_ID,
                DatabaseHelper.COLUMN_NOTE_CONTENT,
                DatabaseHelper.COLUMN_NOTE_CREATED_AT
            ),
            "${DatabaseHelper.COLUMN_NOTE_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_NOTE_ID} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                notes.add(
                    Note(
                        id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_ID)),
                        userId = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_USER_ID)),
                        content = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CONTENT)),
                        date = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CREATED_AT))
                    )
                )
            }
        }

        return notes
    }

    fun getLastNoteByUserId(userId: Int): Note? {
        if (userId <= 0) return null

        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_NOTES,
            arrayOf(
                DatabaseHelper.COLUMN_NOTE_ID,
                DatabaseHelper.COLUMN_NOTE_USER_ID,
                DatabaseHelper.COLUMN_NOTE_CONTENT,
                DatabaseHelper.COLUMN_NOTE_CREATED_AT
            ),
            "${DatabaseHelper.COLUMN_NOTE_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_NOTE_ID} DESC",
            "1"
        )

        return cursor.use {
            if (!it.moveToFirst()) {
                null
            } else {
                Note(
                    id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_ID)),
                    userId = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_USER_ID)),
                    content = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CONTENT)),
                    date = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CREATED_AT))
                )
            }
        }
    }

    // Старый вариант метода оставлен, чтобы не ломать остальной код проекта.
    fun updateNote(noteId: Int, newContent: String): Boolean {
        return updateNoteInternal(noteId, null, newContent)
    }

    // Этот вариант дополнительно проверяет владельца заметки.
    fun updateNote(userId: Int, noteId: Int, newContent: String): Boolean {
        if (userId <= 0) return false
        return updateNoteInternal(noteId, userId, newContent)
    }

    private fun updateNoteInternal(
        noteId: Int,
        userId: Int?,
        newContent: String
    ): Boolean {
        if (noteId <= 0) return false

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, newContent)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, getCurrentDate())
        }

        val whereClause = if (userId == null) {
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?"
        } else {
            "${DatabaseHelper.COLUMN_NOTE_ID} = ? AND " +
                    "${DatabaseHelper.COLUMN_NOTE_USER_ID} = ?"
        }
        val whereArgs = if (userId == null) {
            arrayOf(noteId.toString())
        } else {
            arrayOf(noteId.toString(), userId.toString())
        }

        return dbHelper.writableDatabase.update(
            DatabaseHelper.TABLE_NOTES,
            values,
            whereClause,
            whereArgs
        ) > 0
    }

    fun deleteNote(noteId: Int): Boolean {
        if (noteId <= 0) return false

        return dbHelper.writableDatabase.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(noteId.toString())
        ) > 0
    }

    fun deleteNote(userId: Int, noteId: Int): Boolean {
        if (userId <= 0 || noteId <= 0) return false

        return dbHelper.writableDatabase.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ? AND ${DatabaseHelper.COLUMN_NOTE_USER_ID} = ?",
            arrayOf(noteId.toString(), userId.toString())
        ) > 0
    }
}
