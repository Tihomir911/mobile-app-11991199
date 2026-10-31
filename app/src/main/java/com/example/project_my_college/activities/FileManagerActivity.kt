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

    // текст, который мы получили из редактора
    private var noteContent: String = ""
    
    // адаптер для отображения списка файлов
    private lateinit var adapter: ArrayAdapter<String>
    private val fileList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)

        listViewFiles = findViewById(R.id.listViewFiles)
        btnAddFile = findViewById(R.id.btnAddFile)
        btnBack = findViewById(R.id.btnBack)

        // получаем текст, переданный из EditorActivity
        noteContent = intent.getStringExtra("note_content") ?: ""

        // настраиваем список
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList)
        listViewFiles.adapter = adapter

        loadFiles()

        // кнопка "+" для создания нового файла
        btnAddFile.setOnClickListener {
            showCreateFileDialog()
        }

        // клик по файлу из списка (сохраняем в него текст)
        listViewFiles.setOnItemClickListener { _, _, position, _ ->
            val fileName = fileList[position]
            saveToFile(fileName)
        }

        // кнопка "назад" в редактор
        btnBack.setOnClickListener {
            finish()
        }
    }

    // загрузка списка файлов
    private fun loadFiles() {
        fileList.clear()
        // метод кента (с маленькой буквы)
        fileList.addAll(FileHelper.getFiles(this))
        adapter.notifyDataSetChanged()
    }

    // окно создания файла
    private fun showCreateFileDialog() {
        val input = EditText(this)
        input.hint = "Название файла"

        AlertDialog.Builder(this)
            .setTitle("Новый файл")
            .setView(input)
            .setPositiveButton("Создать") { _, _ ->
                val fileName = input.text.toString().trim()
                
                if (fileName.isEmpty()) {
                    Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // добавляем .txt, если пользователь забыл
                val fullName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"

                FileHelper.createFile(this, fullName)
                Toast.makeText(this, "Файл создан", Toast.LENGTH_SHORT).show()
                
                // обновляем список
                loadFiles()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // запись текста в выбранный файл
    private fun saveToFile(fileName: String) {
        if (noteContent.isEmpty()) {
            Toast.makeText(this, "Нет текста для записи", Toast.LENGTH_SHORT).show()
            return
        }

        FileHelper.writeFile(this, fileName, noteContent)
        Toast.makeText(this, "Текст записан в $fileName", Toast.LENGTH_SHORT).show()
    }
}
