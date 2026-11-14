package com.example.project_my_college.activities

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
    private lateinit var btnAddFile: Button
    private lateinit var btnBack: Button

    private var noteContent: String = ""

    private lateinit var adapter: ArrayAdapter<String>
    private val fileList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)

        listViewFiles = findViewById(R.id.listViewFiles)
        btnAddFile = findViewById(R.id.btnAddFile)
        btnBack = findViewById(R.id.btnBack)

        noteContent = intent.getStringExtra("note_content") ?: ""

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            fileList
        )

        listViewFiles.adapter = adapter

        loadFiles()

        btnAddFile.setOnClickListener {
            showCreateFileDialog()
        }

        listViewFiles.setOnItemClickListener { _, _, position, _ ->
            val fileName = fileList[position]
            showSaveDialog(fileName)
        }

        listViewFiles.setOnItemLongClickListener { _, _, position, _ ->
            val fileName = fileList[position]
            showFileOptions(fileName)
            true
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadFiles() {
        fileList.clear()
        fileList.addAll(FileHelper.getFiles(this))
        adapter.notifyDataSetChanged()
    }

    private fun showCreateFileDialog() {
        val input = EditText(this).apply {
            hint = "Название файла"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Новый файл")
            .setView(input)
            .setPositiveButton("Создать") { _, _ ->

                val enteredName = input.text.toString().trim()

                if (enteredName.isBlank()) {
                    showMessage("Введите название файла")
                    return@setPositiveButton
                }

                val fileName = normalizeFileName(enteredName)

                if (fileName == null) {
                    showMessage("Недопустимое название файла")
                    return@setPositiveButton
                }

                if (fileList.contains(fileName)) {
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
        if (
            name == "." ||
            name == ".." ||
            '/' in name ||
            '\\' in name ||
            ':' in name ||
            name.any { it.isISOControl() }
        ) {
            return null
        }

        val result = if (name.endsWith(".txt", ignoreCase = true)) {
            name
        } else {
            "$name.txt"
        }

        return result.takeIf { it.isNotBlank() }
    }

    private fun showSaveDialog(fileName: String) {
        AlertDialog.Builder(this)
            .setTitle("Сохранение заметки")
            .setMessage("Записать текущую заметку в файл $fileName?")
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

        val isSaved = FileHelper.writeFile(
            this,
            fileName,
            noteContent
        )

        if (isSaved) {
            showMessage("Заметка сохранена в $fileName")
            finish()
        } else {
            showMessage("Ошибка при сохранении файла")
        }
    }

    private fun showFileOptions(fileName: String) {
        val options = arrayOf(
            "Переименовать",
            "Удалить"
        )

        AlertDialog.Builder(this)
            .setTitle(fileName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showRenameDialog(fileName)
                    1 -> showDeleteDialog(fileName)
                }
            }
            .show()
    }

    private fun showRenameDialog(oldName: String) {
        val input = EditText(this).apply {
            setText(oldName.removeSuffix(".txt"))
            inputType = InputType.TYPE_CLASS_TEXT
        }

        AlertDialog.Builder(this)
            .setTitle("Переименование файла")
            .setView(input)
            .setPositiveButton("Переименовать") { _, _ ->

                val enteredName = input.text.toString().trim()

                if (enteredName.isBlank()) {
                    showMessage("Введите новое название")
                    return@setPositiveButton
                }

                val newName = normalizeFileName(enteredName)

                if (newName == null) {
                    showMessage("Недопустимое название")
                    return@setPositiveButton
                }

                if (newName == oldName) {
                    showMessage("Название не изменилось")
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

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}