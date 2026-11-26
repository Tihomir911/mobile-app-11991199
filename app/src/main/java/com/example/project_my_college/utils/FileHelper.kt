package com.example.project_my_college.utils

import android.content.Context
import java.io.File
import java.io.IOException

object FileHelper {

    private fun isValidFileName(fileName: String): Boolean {
        return fileName.isNotBlank() &&
                fileName == fileName.trim() &&
                fileName != "." &&
                fileName != ".." &&
                '/' !in fileName &&
                '\\' !in fileName &&
                ':' !in fileName &&
                fileName.none { it.isISOControl() }
    }

    /** Возвращает файл только в корневой внутренней папке приложения. */
    private fun getSafeFile(context: Context, fileName: String): File? {
        if (!isValidFileName(fileName)) return null

        return try {
            val directory = context.applicationContext.filesDir.canonicalFile
            val file = File(directory, fileName).canonicalFile
            if (file.parentFile == directory) file else null
        } catch (e: IOException) {
            null
        } catch (e: SecurityException) {
            null
        }
    }

    /** Читает UTF-8 файл. null означает, что файл не найден или его не удалось прочитать. */
    fun readFile(context: Context, fileName: String): String? {
        val file = getSafeFile(context, fileName) ?: return null

        return try {
            if (!file.isFile || !file.canRead()) return null
            file.readText(Charsets.UTF_8)
        } catch (e: IOException) {
            null
        } catch (e: SecurityException) {
            null
        }
    }

    /** Записывает текст в файл, заменяя его прежнее содержимое. */
    fun writeFile(
        context: Context,
        fileName: String,
        text: String
    ): Boolean {
        val file = getSafeFile(context, fileName) ?: return false

        return try {
            if (file.exists() && !file.isFile) return false
            file.writeText(text, Charsets.UTF_8)
            true
        } catch (e: IOException) {
            false
        } catch (e: SecurityException) {
            false
        }
    }

    /** Создаёт пустой файл и не перезаписывает уже существующий. */
    fun createFile(context: Context, fileName: String): Boolean {
        val file = getSafeFile(context, fileName) ?: return false

        return try {
            if (file.exists()) return false
            file.createNewFile()
        } catch (e: IOException) {
            false
        } catch (e: SecurityException) {
            false
        }
    }

    /** Возвращает список обычных текстовых файлов во внутренней папке приложения. */
    fun getFiles(context: Context): List<String> {
        return try {
            context.applicationContext.filesDir.listFiles()
                ?.filter { file ->
                    file.isFile &&
                            file.name.endsWith(".txt", ignoreCase = true) &&
                            getSafeFile(context, file.name) != null
                }
                ?.map { it.name }
                ?.sortedBy { it.lowercase() }
                ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    fun deleteFile(context: Context, fileName: String): Boolean {
        val file = getSafeFile(context, fileName) ?: return false

        return try {
            file.isFile && file.delete()
        } catch (e: SecurityException) {
            false
        }
    }

    fun renameFile(
        context: Context,
        oldName: String,
        newName: String
    ): Boolean {
        val oldFile = getSafeFile(context, oldName) ?: return false
        val newFile = getSafeFile(context, newName) ?: return false

        return try {
            oldFile.isFile &&
                    !newFile.exists() &&
                    oldFile.renameTo(newFile)
        } catch (e: SecurityException) {
            false
        }
    }
}
