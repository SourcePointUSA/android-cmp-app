import kotlin.String

object Libs {
    const val kotlinxCoroutinesCore: String = "org.jetbrains.kotlinx:kotlinx-coroutines-core:" + Versions.coroutinesVersion
    const val kotlinReflect: String = "org.jetbrains.kotlin:kotlin-reflect:" + Versions.kotlinVersion
    const val mockk: String = "io.mockk:mockk:" + Versions.mockkVersion
    const val jacksonJr: String = "com.fasterxml.jackson.jr:jackson-jr-objects:" + Versions.jacksonJrVersion
    const val jacksonJrsTree: String = "com.fasterxml.jackson.jr:jackson-jr-stree:" + Versions.jacksonJrVersion
    const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.mockWSVersion}"
}


