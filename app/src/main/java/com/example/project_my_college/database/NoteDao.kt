package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context

class NoteDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    //сохранить новую заметку
    //вернет айди новой заметки либо -1 при ошибке
    fun InsertNote(userId: Int, content: String): Long {
        val db = dbHelper.writableDatabase

        //получаем текущую дату
        val currentDate = java.text.SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_USER_ID, userId)
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, content)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, currentDate)
        }

        val newId = db.insert(
            DatabaseHelper.TABLE_NOTES,
            null,
            values
        )

        db.close()
        return newId
    }

    //получаем все заметки
    //возвращает список note (надо позже заменить Any на Note)
    fun GetNotesByUserId(userId: Int): List<Any> {
        // TODO ждем монеси
        return emptyList()
    }

    //удаление заметки по айди
    fun DeleteNote(noteId: Int): Boolean {
        val db = dbHelper.writableDatabase

        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(noteId.toString())
        )

        db.close()
        return rowsDeleted > 0
    }

    //обновляет текст заметки
    fun UpdateNote(noteId: Int, newContent: String): Boolean {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, newContent)
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
