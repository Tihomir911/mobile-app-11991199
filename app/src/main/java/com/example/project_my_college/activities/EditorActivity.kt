package com.example.project_my_college.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_my_college.R
import com.example.project_my_college.database.NoteDao
import com.example.project_my_college.database.UserDao

class EditorActivity : AppCompatActivity() {

    private lateinit var noteEditText: EditText
    private lateinit var noteDao: NoteDao

    private var userId: Int = -1
    private var currentNoteId: Int = -1

    companion object {
        const val EXTRA_USER_ID = "user_id"
        const val EXTRA_USER_LOGIN = "user_login"

        private const val REQUEST_FILE_MANAGER = 1001
        private const val STATE_NOTE_ID = "state_note_id"
        private const val STATE_USER_ID = "state_user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        noteEditText = findViewById(R.id.etNoteContent)
        val saveButton = findViewById<Button>(R.id.btnSave)
        val chooseFileButton = findViewById<Button>(R.id.btnChooseFile)
        val logoutButton = findViewById<Button>(R.id.btnLogout)

        noteDao = NoteDao(this)

        val preferences = getSharedPreferences(LoginActivity.PREFERENCES_NAME, MODE_PRIVATE)
        userId = intent.getIntExtra(
            EXTRA_USER_ID,
            preferences.getInt(LoginActivity.KEY_USER_ID, -1)
        )
        val userLogin = intent.getStringExtra(EXTRA_USER_LOGIN)
            ?: preferences.getString(LoginActivity.KEY_USER_LOGIN, "")
            ?: ""

        if (!isSessionValid(userLogin)) {
            showMessage("Пользователь не найден. Войдите снова")
            logout()
            return
        }

        currentNoteId = savedInstanceState?.getInt(STATE_NOTE_ID, -1) ?: -1
        if (savedInstanceState == null || currentNoteId <= 0) {
            loadLastNote()
        } else {
            val savedUserId = savedInstanceState.getInt(STATE_USER_ID, -1)
            if (savedUserId != userId) {
                currentNoteId = -1
                loadLastNote()
            }
        }

        saveButton.setOnClickListener {
            saveNote()
        }

        chooseFileButton.setOnClickListener {
            openFileManager()
        }

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun isSessionValid(login: String): Boolean {
        if (userId <= 0 || login.isBlank()) return false

        val user = UserDao(this).getUserByLogin(login)
        return user?.id == userId
    }

    private fun loadLastNote() {
        val lastNote = noteDao.getLastNoteByUserId(userId)
        if (lastNote == null) {
            currentNoteId = -1
            noteEditText.setText("")
        } else {
            currentNoteId = lastNote.id
            noteEditText.setText(lastNote.content)
        }
    }

    private fun saveNote() {
        val content = noteEditText.text.toString()
        if (content.isBlank()) {
            showMessage("Нельзя сохранить пустую заметку")
            return
        }

        if (currentNoteId <= 0) {
            insertNewNote(content)
            return
        }

        val updated = noteDao.updateNote(userId, currentNoteId, content)
        if (updated) {
            showMessage("Заметка обновлена")
        } else {
            // Если запись была удалена, сохраняем текст как новую заметку.
            insertNewNote(content)
        }
    }

    private fun insertNewNote(content: String) {
        val newNoteId = noteDao.insertNote(userId, content)
        if (newNoteId > 0L) {
            currentNoteId = newNoteId.toInt()
            showMessage("Заметка сохранена")
        } else {
            showMessage("Не удалось сохранить заметку")
        }
    }

    private fun openFileManager() {
        val intent = Intent(this, FileManagerActivity::class.java).apply {
            putExtra(FileManagerActivity.EXTRA_NOTE_CONTENT, noteEditText.text.toString())
        }
        startActivityForResult(intent, REQUEST_FILE_MANAGER)
    }

    @Deprecated("Используется для совместимости с текущей структурой проекта")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_FILE_MANAGER || resultCode != RESULT_OK || data == null) {
            return
        }

        if (data.hasExtra(FileManagerActivity.EXTRA_OPENED_FILE_CONTENT)) {
            val fileContent = data.getStringExtra(FileManagerActivity.EXTRA_OPENED_FILE_CONTENT)
                ?: ""
            val fileName = data.getStringExtra(FileManagerActivity.EXTRA_OPENED_FILE_NAME) ?: ""

            noteEditText.setText(fileContent)
            noteEditText.setSelection(noteEditText.text.length)
            showMessage(if (fileName.isBlank()) "Файл открыт" else "Открыт файл $fileName")
        }
    }

    private fun logout() {
        getSharedPreferences(LoginActivity.PREFERENCES_NAME, MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_NOTE_ID, currentNoteId)
        outState.putInt(STATE_USER_ID, userId)
        super.onSaveInstanceState(outState)
    }
}
