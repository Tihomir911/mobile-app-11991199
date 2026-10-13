package com.example.project_my_college.utils

import android.content.Context
import java.io.File


object FileHelper {
    //прочиать содержимое файла
    fun ReadFile ( context: Context , filename : String): String {
        val file = File(context.filesDir, filename)
        return "" //временная заглушка
    }
    //запись в файл
    fun WriteFile (context : Context , filename: String , textfile:String ) {}

    //создать файл
    fun CreateFile (context : Context , filename : String) {}

    //получение всех файлов
    fun GetFiles (context : Context) : List<String> {
        return emptyList()
    }
}

