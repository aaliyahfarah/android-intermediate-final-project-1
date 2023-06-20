package com.bangkit.storyappdicoding.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.bangkit.storyappdicoding.data.Status
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.*
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

private val timeStamp: String
    get() = SimpleDateFormat(
        "dd-MMM-yyyy-HH-mm-ss",
        Locale.getDefault()
    ).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImage: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val mFile = createTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImage) as InputStream
    val outputStream: OutputStream = FileOutputStream(mFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) {
        outputStream.write(buf, 0, len)
    }
    outputStream.close()
    inputStream.close()

    return mFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

fun String.withDateFormat(): String {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(this) as Date
    return SimpleDateFormat("EEEE, dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(date)
}

fun exceptionHandling(e: Exception): String? {
    return when (e) {
        is HttpException -> {
            when (e.code()) {
                in 400..451 -> parseHttpError(e)
                in 500..599 -> "Server error"
                else -> "Undefined error"
            }
        }
        is UnknownHostException -> "No internet connection"
        else -> e.message.toString()
    }
}

private fun parseHttpError(httpException: HttpException): String? {
    return try {
        val errorBody = httpException.response()?.errorBody()?.string() ?: "Unknown HTTP error body"
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Status::class.java)
        adapter.fromJson(errorBody)?.message
    } catch (e: Exception) {
        e.localizedMessage
    }
}