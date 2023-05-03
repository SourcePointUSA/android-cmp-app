package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.util.check
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader

internal interface RemoteDataSource {
    suspend fun fetchLatestVersion(): Either<String>

    companion object
}

internal fun RemoteDataSource.Companion.create(urlFile: String): RemoteDataSource = RemoteDataSourceImpl(urlFile)

private class RemoteDataSourceImpl(
    val urlFile: String
) : RemoteDataSource {

    lateinit var latestVersion: String

    override suspend fun fetchLatestVersion(): Either<String> = coroutineScope {
        check {
            if (this@RemoteDataSourceImpl::latestVersion.isInitialized) {
                latestVersion
            } else {
                val input = OkHttpClient().newCall(
                    Request.Builder()
                        .url(urlFile)
                        .build()
                )
                    .execute()
                    .body?.byteStream()
                val stream = BufferedReader(InputStreamReader(input))
                stream.readLine()
                    .split("=")
                    .getOrNull(1)
                    ?.trim()
                    ?.apply { latestVersion = this }
                    ?: throw RuntimeException("Latest version not valid!!!")
            }
        }
    }
}
