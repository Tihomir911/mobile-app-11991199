package com.example.project_my_college.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_my_college.R
import com.example.project_my_college.database.NoteDao

class EditorActivity : AppCompatActivity() {

    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var chooseFileButton: Button
    private lateinit var logoutButton: Button

    private lateinit var noteDao: NoteDao

    private var userId: Int = -1
    private var currentNoteId: Int = -1

    companion object {
        const val EXTRA_USER_ID = "user_id"
        const val EXTRA_USER_LOGIN = "user_login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // внимание: ID должны совпадать с activity_editor.xml
        noteEditText = findViewById(R.id.etNoteContent)
        saveButton = findViewById(R.id.btnSave)
        chooseFileButton = findViewById(R.id.btnChooseFile)
        logoutButton = findViewById(R.id.btnLogout)

        noteDao = NoteDao(this)

        userId = intent.getIntExtra(EXTRA_USER_ID, -1)

        if (userId == -1) {
            val preferences = getSharedPreferences("notes_preferences", MODE_PRIVATE)
            userId = preferences.getInt("user_id", -1)
        }

        if (userId == -1) {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
            goToLogin()
            return
        }

        loadLastNote()

        saveButton.setOnClickListener {
            saveNote()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        // открываем наш экран файлов и передаем туда текст
        chooseFileButton.setOnClickListener {
            val textToSave = noteEditText.text.toString()

            if (textToSave.trim().isEmpty()) {
                Toast.makeText(this, "Сначала напишите текст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, FileManagerActivity::class.java)
            intent.putExtra("note_content", textToSave)
            startActivity(intent)
        }
    }

    private fun loadLastNote() {
        val notes = noteDao.GetNotesByUserId(userId)

        if (notes.isEmpty()) {
            return
        }

        val lastNote = notes.last()
        currentNoteId = lastNote.id
        noteEditText.setText(lastNote.content)
    }

    private fun saveNote() {
        val content = noteEditText.text.toString()

        if (content.trim().isEmpty()) {
            Toast.makeText(this, "Нельзя сохранить пустую заметку", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentNoteId == -1) {
            val newNoteId = noteDao.InsertNote(userId, content)

            if (newNoteId == -1L) {
                Toast.makeText(this, "Не удалось сохранить заметку", Toast.LENGTH_SHORT).show()
                return
            }

            currentNoteId = newNoteId.toInt()
            Toast.makeText(this, "Заметка сохранена", Toast.LENGTH_SHORT).show()
        } else {
            val updated = noteDao.UpdateNote(currentNoteId, content)

            if (updated) {
                Toast.makeText(this, "Заметка обновлена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Не удалось обновить заметку", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        val preferences = getSharedPreferences("notes_preferences", MODE_PRIVATE)
        preferences.edit().clear().apply()
        goToLogin()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}