package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.util.check
import kotlinx.coroutines.coroutineScope
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

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
                val url = URL(urlFile)
                val stream = BufferedReader(InputStreamReader(url.openStream()))
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
