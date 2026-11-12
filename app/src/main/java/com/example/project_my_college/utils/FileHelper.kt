package com.example.project_my_college.utils

import android.content.Context
import java.io.File


//удалить
//переименовать
//выбрать файл
// размер файла
// формат файла
object FileHelper {
    //прочиать содержимое файла
    fun readFile ( context: Context , filename : String): String {
        if ("/" in filename || "\\" in filename || filename.isBlank() || filename == "..") {
            println("недопустимое имя файла : $filename")
            return "ошибка чтения файла: неправильное имя файла"
        }
        val file = File(context.filesDir, filename)
        return try {
            file.readText()
        }
        catch (e: Exception){
            println("ошибка чтения файла: ${e.message}")
            ""
        }  // сделать так же для файла который не найден в идеале


    }
    //запись в файл
    fun writeFile (context : Context , filename: String , text : String): Boolean {

        if ("/" in filename|| "\\" in filename || filename.isBlank() || filename == "..") {
            println("недопустимое имя файла : $filename")
            return false
        }

        val file = File(context.filesDir , filename )

        return try {
            file.writeText(text)
            true
            
        }
        catch (e: Exception){
            println("ошибка записи в файл: ${e.message}")
            false
        }

    }

    //создать файл
    fun createFile (context : Context , filename : String) : Boolean {

        if ("/" in filename || "\\" in filename || filename.isBlank() || filename == "..") {
            println("недопустимое имя файла : $filename")
            return false
        }

        val file = File(context.filesDir , filename)
        return try{
            file.createNewFile()
        }
        catch(e : Exception){
            println("ошибка создания файла: ${e.message}")
            false
        }
    }

    //получение всех файлов
    fun getFiles (context : Context) : List<String> {

        return try {
            context.fileList().toList().filter {it.endsWith(".txt")}
            context.fileList().toList().filter {it.endWith(".obt")}
            context.fileList().toList().filter {it.endWith(".xml")}
            context.fileList().toList().filter {it.endWith(".docx")}
            context.fileList().toList().filter {it.endWith(".md")
            
        }
        catch (e: Exception) {
            println("ошибка получения списка файлов: ${e.message}")
            emptyList()
        }
    }
}

