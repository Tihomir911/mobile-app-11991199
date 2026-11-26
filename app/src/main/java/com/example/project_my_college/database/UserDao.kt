package com.example.project_my_college.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.project_my_college.models.User
import java.security.MessageDigest
import java.security.SecureRandom
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.util.Arrays
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class UserDao(context: Context) {

    private val dbHelper = DatabaseHelper(context.applicationContext)

    companion object {
        private const val SALT_SIZE = 16
        private const val ITERATIONS = 120_000
        private const val KEY_LENGTH = 256
        private const val HASH_PREFIX = "pbkdf2"
    }

    private fun generateSalt(): ByteArray {
        return ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }
    }

    private fun hashPassword(password: String, salt: ByteArray): String {
        val hash = deriveKey(password, salt, ITERATIONS, KEY_LENGTH / 8)
        return "$HASH_PREFIX:$ITERATIONS:${salt.toHex()}:${hash.toHex()}"
    }

    /** Реализация PBKDF2-HMAC-SHA256 на Mac доступна и на Android 7.0 (API 24). */
    private fun deriveKey(
        password: String,
        salt: ByteArray,
        iterations: Int,
        keyLengthBytes: Int
    ): ByteArray {
        require(iterations > 0)
        require(keyLengthBytes > 0)

        val passwordChars = password.toCharArray()
        val encodedPassword = StandardCharsets.UTF_8.encode(CharBuffer.wrap(passwordChars))
        val passwordBytes = ByteArray(encodedPassword.remaining())
        encodedPassword.get(passwordBytes)
        Arrays.fill(passwordChars, '\u0000')

        try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(passwordBytes, "HmacSHA256"))

            val hashLength = mac.macLength
            val blockCount = (keyLengthBytes + hashLength - 1) / hashLength
            val derivedBytes = ByteArray(blockCount * hashLength)

            for (blockIndex in 1..blockCount) {
                val blockInput = ByteBuffer.allocate(salt.size + 4)
                    .put(salt)
                    .putInt(blockIndex)
                    .array()

                var current = mac.doFinal(blockInput)
                val blockHash = current.copyOf()

                repeat(iterations - 1) {
                    current = mac.doFinal(current)
                    for (index in blockHash.indices) {
                        blockHash[index] = (
                                blockHash[index].toInt() xor current[index].toInt()
                                ).toByte()
                    }
                }

                System.arraycopy(
                    blockHash,
                    0,
                    derivedBytes,
                    (blockIndex - 1) * hashLength,
                    hashLength
                )
            }

            return derivedBytes.copyOf(keyLengthBytes)
        } finally {
            Arrays.fill(passwordBytes, 0)
        }
    }

    private fun ByteArray.toHex(): String = joinToString("") {
        "%02x".format(it.toInt() and 0xff)
    }

    private fun String.hexToBytes(): ByteArray? {
        if (isEmpty() || length % 2 != 0 || any { it.digitToIntOrNull(16) == null }) {
            return null
        }

        return try {
            chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun verifyPassword(password: String, storedHash: String): Boolean {
        if (!storedHash.startsWith("$HASH_PREFIX:")) {
            // Поддержка старых записей, где пароль хранился как SHA-256.
            val oldHash = MessageDigest
                .getInstance("SHA-256")
                .digest(password.toByteArray(Charsets.UTF_8))
                .toHex()

            return MessageDigest.isEqual(
                oldHash.toByteArray(Charsets.UTF_8),
                storedHash.toByteArray(Charsets.UTF_8)
            )
        }

        return try {
            val parts = storedHash.split(":")
            if (parts.size != 4 || parts[0] != HASH_PREFIX) return false

            val iterations = parts[1].toIntOrNull() ?: return false
            if (iterations !in 1..1_000_000) return false

            val salt = parts[2].hexToBytes() ?: return false
            val expectedHash = parts[3].hexToBytes() ?: return false
            if (salt.size != SALT_SIZE || expectedHash.isEmpty()) return false

            val actualHash = deriveKey(
                password,
                salt,
                iterations,
                expectedHash.size
            )
            MessageDigest.isEqual(actualHash, expectedHash)
        } catch (e: Exception) {
            false
        }
    }

    fun getUserByLogin(login: String): User? {
        val cleanLogin = login.trim()
        if (cleanLogin.isEmpty()) return null

        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_USER_LOGIN,
                DatabaseHelper.COLUMN_USER_PASSWORD
            ),
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(cleanLogin),
            null,
            null,
            null,
            "1"
        )

        return cursor.use {
            if (!it.moveToFirst()) {
                null
            } else {
                User(
                    id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    login = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_LOGIN)),
                    password = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD))
                )
            }
        }
    }

    fun insertUser(login: String, password: String): Long {
        val cleanLogin = login.trim()
        if (cleanLogin.isEmpty() || password.isEmpty()) return -1L

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_LOGIN, cleanLogin)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, hashPassword(password, generateSalt()))
        }

        return try {
            dbHelper.writableDatabase.insertOrThrow(
                DatabaseHelper.TABLE_USERS,
                null,
                values
            )
        } catch (e: SQLiteConstraintException) {
            -1L
        } catch (e: Exception) {
            -1L
        }
    }

    fun verifyUserPassword(login: String, password: String): Boolean {
        if (password.isEmpty()) return false

        val user = getUserByLogin(login) ?: return false
        if (!verifyPassword(password, user.password)) return false

        // Если пароль остался в старом формате SHA-256, обновляем его хеш.
        if (!user.password.startsWith("$HASH_PREFIX:")) {
            updatePasswordHash(user.id, hashPassword(password, generateSalt()))
        }

        return true
    }

    private fun updatePasswordHash(userId: Int, newHash: String) {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_PASSWORD, newHash)
        }

        dbHelper.writableDatabase.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun userExists(login: String): Boolean {
        val cleanLogin = login.trim()
        if (cleanLogin.isEmpty()) return false

        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_USER_ID),
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(cleanLogin),
            null,
            null,
            null,
            "1"
        )

        return cursor.use { it.moveToFirst() }
    }
}
