package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context
import com.example.project_my_college.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // сохранение заметки
    fun InsertNote(userId: Int, content: String): Long {
        val db = dbHelper.writableDatabase
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTE_USER_ID, userId)
            put(DatabaseHelper.COLUMN_NOTE_CONTENT, content)
            put(DatabaseHelper.COLUMN_NOTE_CREATED_AT, currentDate)
        }
        val newId = db.insert(DatabaseHelper.TABLE_NOTES, null, values)
        db.close()
        return newId
    }

    // получение всех заметок пользователя
    fun GetNotesByUserId(userId: Int): List<Note> {
        val db = dbHelper.readableDatabase
        val notesList = mutableListOf<Note>()
        
        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            "${DatabaseHelper.COLUMN_NOTE_USER_ID} = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_ID)
            val contentIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CONTENT)
            val dateIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CREATED_AT)

            do {
                val id = cursor.getInt(idIndex)
                val content = cursor.getString(contentIndex)
                val date = cursor.getString(dateIndex)
                notesList.add(Note(id, userId, content, date))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notesList
    }

    // удаление заметки
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

    // обновление текста заметки
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