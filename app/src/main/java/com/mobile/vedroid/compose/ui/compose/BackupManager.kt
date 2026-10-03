package com.mobile.vedroid.compose.ui.compose

import android.content.Context
import android.util.Log
import java.io.File

object BackupManager {
    private const val BACKUP_FILE = "rentcam_backup.json"

    fun backupFile(context: Context): File = File(context.filesDir, BACKUP_FILE)

    fun hasBackup(context: Context): Boolean = backupFile(context).exists()

    fun createBackup(context: Context): Boolean {
        return try {
            val demoData = """
                [
                  {"id":1,"name":"Sony A7 III","price":2500,"status":"Доступно"},
                  {"id":2,"name":"Canon EF 50mm f/1.8","price":500,"status":"Занято"},
                  {"id":3,"name":"Manfrotto MT055","price":700,"status":"Доступно"}
                ]
            """.trimIndent()
            backupFile(context).writeText(demoData)
            Log.d("BackupManager", "backup created: ${backupFile(context).absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "backup error", e)
            false
        }
    }

    fun deleteBackup(context: Context): Boolean {
        return try {
            val file = backupFile(context)
            if (file.exists()) file.delete()
            Log.d("BackupManager", "backup deleted")
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "delete backup error", e)
            false
        }
    }

    fun restoreBackup(context: Context): String? {
        return try {
            val file = backupFile(context)
            if (!file.exists()) null else file.readText()
        } catch (e: Exception) {
            Log.e("BackupManager", "restore backup error", e)
            null
        }
    }
}