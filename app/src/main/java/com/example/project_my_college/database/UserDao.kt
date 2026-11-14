package com.example.project_my_college.database


import android.content.ContentValues
import android.content.Context
import com.example.project_my_college.models.User
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class UserDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    companion object {
        private const val SALT_SIZE = 16
        private const val ITERATIONS = 120_000
        private const val KEY_LENGTH = 256

        private const val HASH_PREFIX = "pbkdf2"
    }

    private fun generateSalt(): ByteArray {
        return ByteArray(SALT_SIZE).also {
            SecureRandom().nextBytes(it)
        }
    }

    private fun hashPassword(
        password: String,
        salt: ByteArray
    ): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        return try {
            val factory = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            )

            val hash = factory.generateSecret(spec).encoded

            "$HASH_PREFIX:$ITERATIONS:" +
                    "${salt.toHex()}:${hash.toHex()}"
        } finally {
            spec.clearPassword()
        }
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }

    private fun String.hexToBytes(): ByteArray {
        require(length % 2 == 0)

        return chunked(2).map {
            it.toInt(16).toByte()
        }.toByteArray()
    }

    private fun verifyPassword(
        password: String,
        storedHash: String
    ): Boolean {
        return try {
            val parts = storedHash.split(":")

            if (parts.size != 4 || parts[0] != HASH_PREFIX) {
                val oldHash = MessageDigest
                    .getInstance("SHA-256")
                    .digest(password.toByteArray())
                    .toHex()

                return MessageDigest.isEqual(
                    oldHash.toByteArray(),
                    storedHash.toByteArray()
                )
            }

            val iterations = parts[1].toInt()
            val salt = parts[2].hexToBytes()
            val expectedHash = parts[3].hexToBytes()

            val spec = PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                expectedHash.size * 8
            )

            try {
                val actualHash = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .encoded

                MessageDigest.isEqual(
                    actualHash,
                    expectedHash
                )
            } finally {
                spec.clearPassword()
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getUserByLogin(login: String): User? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(login),
            null,
            null,
            null
        )

        return cursor.use {
            if (!it.moveToFirst()) {
                null
            } else {
                User(
                    it.getInt(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_USER_ID
                        )
                    ),
                    it.getString(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_USER_LOGIN
                        )
                    ),
                    it.getString(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_USER_PASSWORD
                        )
                    )
                )
            }
        }
    }

    fun insertUser(login: String, password: String): Long {
        val db = dbHelper.writableDatabase
        val salt = generateSalt()
        val passwordHash = hashPassword(password, salt)

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_LOGIN, login)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, passwordHash)
        }

        return db.insert(
            DatabaseHelper.TABLE_USERS,
            null,
            values
        )
    }

    fun verifyUserPassword(
        login: String,
        password: String
    ): Boolean {
        val user = getUserByLogin(login) ?: return false

        if (verifyPassword(password, user.password)) {
            if (!user.password.startsWith("$HASH_PREFIX:")) {
                val newHash = hashPassword(password, generateSalt())
                updatePasswordHash(user.id, newHash)
            }

            return true
        }

        return false
    }

    private fun updatePasswordHash(
        userId: Int,
        newHash: String
    ) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_PASSWORD, newHash)
        }

        db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun userExists(login: String): Boolean {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_USER_ID),
            "${DatabaseHelper.COLUMN_USER_LOGIN} = ?",
            arrayOf(login),
            null,
            null,
            null
        )

        return cursor.use {
            it.moveToFirst()
        }
    }
}
