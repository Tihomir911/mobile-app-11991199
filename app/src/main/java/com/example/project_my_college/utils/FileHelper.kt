package com.example.project_my_college.utils

import android.content.Context
import java.io.File
import java.io.IOException

object FileHelper {

    private fun isValidFileName(filename: String): Boolean {
        return filename.isNotBlank() &&
                filename == filename.trim() &&
                filename != "." &&
                filename != ".." &&
                '/' !in filename &&
                '\\' !in filename &&
                ':' !in filename &&
                filename.none { it.isISOControl() }
    }

    private fun getSafeFile(context: Context, filename: String): File? {
        if (!isValidFileName(filename)) return null

        val directory = context.filesDir.canonicalFile
        val file = File(directory, filename).canonicalFile

        return if (file.parentFile == directory) file else null
    }

    fun readFile(context: Context, filename: String): String? {
        val file = getSafeFile(context, filename) ?: return null

        return try {
            if (!file.isFile) return null
            file.readText(Charsets.UTF_8)
        } catch (e: IOException) {
            null
        }
    }

    fun writeFile(
        context: Context,
        filename: String,
        text: String
    ): Boolean {
        val file = getSafeFile(context, filename) ?: return false

        return try {
            file.writeText(text, Charsets.UTF_8)
            true
        } catch (e: IOException) {
            false
        }
    }

    fun createFile(context: Context, filename: String): Boolean {
        val file = getSafeFile(context, filename) ?: return false

        return try {
            file.createNewFile()
        } catch (e: IOException) {
            false
        }
    }

    fun getFiles(context: Context): List<String> {
        return try {
            context.filesDir.listFiles()
                ?.filter { it.isFile && it.name.endsWith(".txt", ignoreCase = true) }
                ?.map { it.name }
                ?.sortedBy { it.lowercase() }
                ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    fun deleteFile(context: Context, filename: String): Boolean {
        val file = getSafeFile(context, filename) ?: return false

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