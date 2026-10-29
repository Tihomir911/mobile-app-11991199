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
        val file = File(context.filesDir, filename)
        return try {
            file.readText()
        }
        catch (e: Exception){
            ""
        }  // сделать так же для файла который не найден в идеале


    }
    //запись в файл
    fun writeFile (context : Context , filename: String , text : String) {
        val file = File(context.filesDir , filename )
        file.writeText(text)

    }

    //создать файл
    fun createFile (context : Context , filename : String) {
        val file = File(context.filesDir , filename)
        try{
            file.createNewFile()
        }
        catch(e : Exception){
        }
    }

    //получение всех файлов
    fun getFiles (context : Context) : List<String> {

        return try {
            context.fileList().toList()
        }

        catch (e: Exception) {
            emptyList()
        }
    }
}

