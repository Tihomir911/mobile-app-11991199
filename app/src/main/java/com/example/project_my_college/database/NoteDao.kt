package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context
import com.example.project_my_college.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // получаем текущую дату и время в удобном формате
    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    // сохранение новой заметки
    fun insertNote(userId: Int, content: String): Long {
        val db = dbHelper.writableDatabase
        val currentDate = getCurrentDate()

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_USER_ID, userId)
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, content)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, currentDate)
        }

        val newId = db.insert(DatabaseHelper.TABLE_NOTES, null, values)
        db.close()
        return newId
    }

    // получение всех заметок конкретного пользователя
    fun getNotesByUserId(userId: Int): List<Note> {
        val db = dbHelper.readableDatabase
        val notesList = mutableListOf<Note>()

        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            "${DatabaseHelper.COLUMN_NOTE_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        // безопасно читаем и закрываем курсор через use
        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_ID)
                val contentIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CONTENT)
                val dateIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CREATED_AT)

                do {
                    val id = it.getInt(idIndex)
                    val content = it.getString(contentIndex)
                    val date = it.getString(dateIndex)
                    notesList.add(Note(id, userId, content, date))
                } while (it.moveToNext())
            }
        }

        db.close()
        return notesList
    }

    // удаление заметки по id
    fun deleteNote(noteId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(noteId.toString())
        )
        db.close()
        return rowsDeleted > 0
    }

    // обновление текста заметки и времени изменения
    fun updateNote(noteId: Int, newContent: String): Boolean {
        val db = dbHelper.writableDatabase
        val currentDate = getCurrentDate()

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, newContent)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, currentDate)
        }

        val rowsUpdated = db.update(
            DatabaseHelper.TABLE_NOTES,
            values,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(noteId.toString())
        )
        db.close()
        return rowsUpdated > 0
    }
}