package com.mobile.vedroid.compose.ui.compose

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Информация о файле для отображения в SettingsFragment.
 */
data class FileInfo(
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val createdAt: Long
) {
    fun sizeText(): String = when {
        sizeBytes < 1024 -> "$sizeBytes Б"
        sizeBytes < 1024 * 1024 -> "%.1f КБ".format(sizeBytes / 1024.0)
        else -> "%.1f МБ".format(sizeBytes / (1024.0 * 1024.0))
    }

    fun dateText(): String =
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(createdAt))
}

object BackupManager {

    /** Имя внешнего файла — номер по списку. Замените 12 на свой. */
    private const val EXTERNAL_FILE_NAME = "rentcam_equipment_12.txt"

    /** Имя файла резервной копии во внутреннем хранилище. */
    private const val INTERNAL_FILE_NAME = "rentcam_equipment_backup.txt"

    // ==================== INTERNAL ====================

    fun internalFile(context: Context): File =
        File(context.filesDir, INTERNAL_FILE_NAME)

    fun hasInternal(context: Context): Boolean = internalFile(context).exists()

    fun internalInfo(context: Context): FileInfo? {
        val f = internalFile(context)
        if (!f.exists()) return null
        return FileInfo(
            name = f.name,
            path = "внутреннее хранилище (files/)",
            sizeBytes = f.length(),
            createdAt = f.lastModified()
        )
    }

    fun saveInternal(context: Context, content: String): Boolean = try {
        internalFile(context).writeText(content)
        true
    } catch (e: Exception) {
        false
    }

    fun deleteInternal(context: Context): Boolean = try {
        val f = internalFile(context)
        if (f.exists()) f.delete()
        true
    } catch (e: Exception) {
        false
    }

    // ==================== EXTERNAL ====================

    fun hasExternal(context: Context): Boolean = findExternalUri(context) != null

    fun externalInfo(context: Context): FileInfo? {
        val uri = findExternalUri(context) ?: return null
        val projection = arrayOf(
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads.SIZE,
            MediaStore.Downloads.DATE_ADDED,
            MediaStore.Downloads.RELATIVE_PATH
        )
        context.contentResolver.query(uri, projection, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val name = c.getString(
                    c.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
                )
                val size = c.getLong(
                    c.getColumnIndexOrThrow(MediaStore.Downloads.SIZE)
                )
                val dateSec = c.getLong(
                    c.getColumnIndexOrThrow(MediaStore.Downloads.DATE_ADDED)
                )
                val rel = c.getString(
                    c.getColumnIndexOrThrow(MediaStore.Downloads.RELATIVE_PATH)
                )
                return FileInfo(
                    name = name,
                    path = (rel ?: "${Environment.DIRECTORY_DOWNLOADS}/") + name,
                    sizeBytes = size,
                    createdAt = dateSec * 1000L
                )
            }
        }
        return null
    }

    /**
     * Создать (или перезаписать) .txt в /Downloads.
     */
    fun saveToExternal(context: Context, content: String): Boolean = try {
        // если файл уже есть — удаляем, чтобы не было дублей
        findExternalUri(context)?.let { context.contentResolver.delete(it, null, null) }

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, EXTERNAL_FILE_NAME)
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
        ) ?: return false

        context.contentResolver.openOutputStream(uri)?.use {
            it.write(content.toByteArray(Charsets.UTF_8))
        } ?: return false

        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        context.contentResolver.update(uri, values, null, null)
        true
    } catch (e: Exception) {
        false
    }

    /**
     * Удалить файл из /Downloads, предварительно сохранив его содержимое
     * во внутреннем хранилище.
     */
    fun deleteExternalSavingInternal(context: Context): Boolean = try {
        val uri = findExternalUri(context) ?: return false

        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return false
        internalFile(context).writeBytes(bytes)

        context.contentResolver.delete(uri, null, null) > 0
    } catch (e: Exception) {
        false
    }

    /**
     * Восстановить: скопировать internal → /Downloads.
     */
    fun restoreInternalToExternal(context: Context): Boolean = try {
        if (!hasInternal(context)) return false
        saveToExternal(context, internalFile(context).readText())
    } catch (e: Exception) {
        false
    }

    // ==================== HELPERS ====================

    /** URI внешнего файла в MediaStore, либо null, если файла нет. */
    private fun findExternalUri(context: Context): Uri? {
        val projection = arrayOf(MediaStore.Downloads._ID)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val args = arrayOf(EXTERNAL_FILE_NAME)

        context.contentResolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection, selection, args, null
        )?.use { c ->
            if (c.moveToFirst()) {
                val id = c.getLong(0)
                return Uri.withAppendedPath(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, id.toString()
                )
            }
        }
        return null
    }
}