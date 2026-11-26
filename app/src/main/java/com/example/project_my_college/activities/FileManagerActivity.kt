package com.example.project_my_college.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_my_college.R
import com.example.project_my_college.utils.FileHelper

class FileManagerActivity : AppCompatActivity() {

    private lateinit var listViewFiles: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private var noteContent: String = ""
    private val fileList = mutableListOf<String>()

    companion object {
        const val EXTRA_NOTE_CONTENT = "note_content"
        const val EXTRA_OPENED_FILE_CONTENT = "opened_file_content"
        const val EXTRA_OPENED_FILE_NAME = "opened_file_name"
        private const val MAX_FILE_NAME_LENGTH = 80
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)

        listViewFiles = findViewById(R.id.listViewFiles)
        val addFileButton = findViewById<Button>(R.id.btnAddFile)
        val backButton = findViewById<Button>(R.id.btnBack)

        noteContent = intent.getStringExtra(EXTRA_NOTE_CONTENT) ?: ""

        adapter = ArrayAdapter(
            this,
            R.layout.item_file,
            R.id.tvFileName,
            fileList
        )
        listViewFiles.adapter = adapter

        loadFiles()

        addFileButton.setOnClickListener {
            showCreateFileDialog()
        }

        listViewFiles.setOnItemClickListener { _, _, position, _ ->
            val fileName = fileList.getOrNull(position) ?: return@setOnItemClickListener
            showFileActions(fileName)
        }

        listViewFiles.setOnItemLongClickListener { _, _, position, _ ->
            val fileName = fileList.getOrNull(position) ?: return@setOnItemLongClickListener true
            showFileOptions(fileName)
            true
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) loadFiles()
    }

    private fun loadFiles() {
        fileList.clear()
        fileList.addAll(FileHelper.getFiles(this))
        if (::adapter.isInitialized) adapter.notifyDataSetChanged()
    }

    private fun showCreateFileDialog() {
        val input = EditText(this).apply {
            hint = "Название файла"
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine(true)
        }

        AlertDialog.Builder(this)
            .setTitle("Новый файл")
            .setView(input)
            .setPositiveButton("Создать") { _, _ ->
                val fileName = normalizeFileName(input.text.toString())
                if (fileName == null) {
                    showMessage("Введите корректное название файла")
                    return@setPositiveButton
                }

                if (fileList.any { it.equals(fileName, ignoreCase = true) }) {
                    showMessage("Такой файл уже существует")
                    return@setPositiveButton
                }

                if (FileHelper.createFile(this, fileName)) {
                    loadFiles()
                    showMessage("Файл создан: $fileName")
                } else {
                    showMessage("Не удалось создать файл")
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun normalizeFileName(name: String): String? {
        val cleanName = name.trim()
        if (cleanName.isEmpty() || cleanName.length > MAX_FILE_NAME_LENGTH) return null
        if (
            cleanName == "." ||
            cleanName == ".." ||
            '/' in cleanName ||
            '\\' in cleanName ||
            ':' in cleanName ||
            cleanName.any { it.isISOControl() }
        ) {
            return null
        }

        return if (cleanName.endsWith(".txt", ignoreCase = true)) {
            cleanName
        } else {
            "$cleanName.txt"
        }
    }

    private fun showFileActions(fileName: String) {
        val actions = if (noteContent.isBlank()) {
            arrayOf("Открыть файл")
        } else {
            arrayOf("Открыть файл", "Сохранить текущую заметку")
        }

        AlertDialog.Builder(this)
            .setTitle(fileName)
            .setItems(actions) { _, which ->
                when (which) {
                    0 -> openFile(fileName)
                    1 -> showSaveDialog(fileName)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun openFile(fileName: String) {
        val content = FileHelper.readFile(this, fileName)
        if (content == null) {
            showMessage("Не удалось прочитать файл")
            return
        }

        val result = Intent().apply {
            putExtra(EXTRA_OPENED_FILE_CONTENT, content)
            putExtra(EXTRA_OPENED_FILE_NAME, fileName)
        }
        setResult(RESULT_OK, result)
        finish()
    }

    private fun showSaveDialog(fileName: String) {
        AlertDialog.Builder(this)
            .setTitle("Сохранение заметки")
            .setMessage("Записать текущую заметку в файл $fileName? Содержимое файла будет заменено.")
            .setPositiveButton("Сохранить") { _, _ ->
                saveToFile(fileName)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveToFile(fileName: String) {
        if (noteContent.isBlank()) {
            showMessage("Нет текста для записи")
            return
        }

        if (FileHelper.writeFile(this, fileName, noteContent)) {
            showMessage("Заметка сохранена в $fileName")
            setResult(RESULT_OK)
            finish()
        } else {
            showMessage("Ошибка при сохранении файла")
        }
    }

    private fun showFileOptions(fileName: String) {
        val options = arrayOf("Открыть файл", "Переименовать", "Удалить")

        AlertDialog.Builder(this)
            .setTitle(fileName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openFile(fileName)
                    1 -> showRenameDialog(fileName)
                    2 -> showDeleteDialog(fileName)
                }
            }
            .show()
    }

    private fun showRenameDialog(oldName: String) {
        val input = EditText(this).apply {
            setText(oldName.removeTxtExtension())
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine(true)
            setSelection(text.length)
        }

        AlertDialog.Builder(this)
            .setTitle("Переименование файла")
            .setView(input)
            .setPositiveButton("Переименовать") { _, _ ->
                val newName = normalizeFileName(input.text.toString())
                if (newName == null) {
                    showMessage("Введите корректное название")
                    return@setPositiveButton
                }

                if (newName.equals(oldName, ignoreCase = true)) {
                    showMessage("Название не изменилось")
                    return@setPositiveButton
                }

                if (fileList.any { it.equals(newName, ignoreCase = true) }) {
                    showMessage("Файл с таким названием уже существует")
                    return@setPositiveButton
                }

                if (FileHelper.renameFile(this, oldName, newName)) {
                    loadFiles()
                    showMessage("Файл переименован")
                } else {
                    showMessage("Не удалось переименовать файл")
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteDialog(fileName: String) {
        AlertDialog.Builder(this)
            .setTitle("Удаление файла")
            .setMessage("Вы действительно хотите удалить $fileName?")
            .setPositiveButton("Удалить") { _, _ ->
                if (FileHelper.deleteFile(this, fileName)) {
                    loadFiles()
                    showMessage("Файл удалён")
                } else {
                    showMessage("Не удалось удалить файл")
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun String.removeTxtExtension(): String {
        return if (endsWith(".txt", ignoreCase = true)) {
            dropLast(4)
        } else {
            this
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
