package com.suplz.aichat.data.remote.cloud

import android.content.Context
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.suplz.aichat.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class YandexS3Uploader @Inject constructor(
    private val s3Client: AmazonS3Client,
    @param:ApplicationContext private val context: Context
) {
    suspend fun uploadImage(localUriString: String): String = withContext(Dispatchers.IO) {
        val uri = localUriString.toUri()
        val fileName = "avatars/${UUID.randomUUID()}.jpg"
        val bucketName = BuildConfig.S3_BUCKET_NAME

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream for URI")

        val metadata = ObjectMetadata().apply {
            contentType = "image/jpeg"
        }

        val request = PutObjectRequest(bucketName, fileName, inputStream, metadata)
        s3Client.putObject(request)

        "https://$bucketName.storage.yandexcloud.net/$fileName"
    }
}