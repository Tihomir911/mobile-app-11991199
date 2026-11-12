package com.example.project_my_college.activities

import android.os.Bundle
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

    // текст заметки, который передали из редактора
    private var noteContent: String = ""

    // адаптер и список для отображения файлов на экране
    private lateinit var adapter: ArrayAdapter<String>
    private val fileList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)

        listViewFiles = findViewById(R.id.listViewFiles)
        btnAddFile = findViewById(R.id.btnAddFile)
        btnBack = findViewById(R.id.btnBack)

        // забираем текст из EditorActivity
        noteContent = intent.getStringExtra("note_content") ?: ""

        // подключаем адаптер к списку
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList)
        listViewFiles.adapter = adapter

        loadFiles()

        // кнопка "+" открывает диалог создания нового файла
        btnAddFile.setOnClickListener {
            showCreateFileDialog()
        }

        // при нажатии на файл в списке записываем в него наш текст
        listViewFiles.setOnItemClickListener { _, _, position, _ ->
            val fileName = fileList[position]
            saveToFile(fileName)
        }

        // выход обратно в редактор
        btnBack.setOnClickListener {
            finish()
        }
    }

    // загружаем список файлов из памяти приложения
    private fun loadFiles() {
        fileList.clear()
        val allFiles = FileHelper.getFiles(this)

        // берем только текстовые файлы .txt, чтобы в список не попадал системный мусор андроида
        for (file in allFiles) {
            if (file.endsWith(".txt")) {
                fileList.add(file)
            }
        }

        adapter.notifyDataSetChanged()
    }

    // диалоговое окно для создания нового файла
    private fun showCreateFileDialog() {
        val input = EditText(this)
        input.hint = "Название файла"

        AlertDialog.Builder(this)
            .setTitle("Новый файл")
            .setView(input)
            .setPositiveButton("Создать") { _, _ ->
                val fileName = input.text.toString().trim()

                if (fileName.isEmpty()) {
                    Toast.makeText(this, "Введите название файла", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // проверяем, чтобы в имени не было слэшей, иначе создание файла сломается
                if (fileName.contains("/") || fileName.contains("\\")) {
                    Toast.makeText(this, "Недопустимые символы в названии", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // дописываем .txt в конец, если юзер сам не написал
                val fullName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"

                // проверяем нет ли уже файла с таким именем
                if (fileList.contains(fullName)) {
                    Toast.makeText(this, "Такой файл уже существует", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                FileHelper.createFile(this, fullName)
                loadFiles()

                // проверяем реально ли файл появился в списке после создания
                if (fileList.contains(fullName)) {
                    Toast.makeText(this, "Файл создан", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Не удалось создать файл", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // сохранение текста заметки в выбранный файл
    private fun saveToFile(fileName: String) {
        if (noteContent.trim().isEmpty()) {
            Toast.makeText(this, "Нет текста для записи", Toast.LENGTH_SHORT).show()
            return
        }

        // оборачиваем запись в try-catch, FileHelper.writeFile нет защиты от сбоев
        try {
            FileHelper.writeFile(this, fileName, noteContent)
            Toast.makeText(this, "Текст записан в $fileName", Toast.LENGTH_SHORT).show()
            // закрываем менеджер файлов и возвращаемся в редактор после успешного сохранения
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка при записи в файл", Toast.LENGTH_SHORT).show()
        }
    }
}