package eu.kanade.tachiyomi.enhancement

import android.annotation.SuppressLint
import eu.kanade.tachiyomi.enhancement.dto.EnhancedImage
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.network.awaitSuccess
import eu.kanade.tachiyomi.network.jsonMime
import eu.kanade.tachiyomi.network.parseAs
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import uy.kohesive.injekt.injectLazy
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class EnhancementApi(client: OkHttpClient? = null, interceptor: Interceptor? = null) {
    private val json: Json by injectLazy()
    private val networkService: NetworkHelper by injectLazy()
    private val client = client ?: networkService.client
    private val enhancerClient = interceptor?.let { buildUnsafeOkHttpClient(it) }
        ?: buildUnsafeOkHttpClient()

    suspend fun enhanceImage(
        imageName: String?, imageData: String, imageURL: String?, mangaSource: String?,
        mangaTitle: String, mangaChapter: String, config: EnhancementConfig
    ): EnhancedImage? {
        // By multipart:
        /*val imageBytes = imageSource.readByteString()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "imageSource", "image.png",
                imageBytes.toByteArray().toRequestBody("image/png".toMediaTypeOrNull(), 0, imageBytes.size)
            )
            .addFormDataPart("imageName", imageName)
            .build()
        */

        val data = buildJsonObject {
            put("imgName", imageName)
            put("imgData", imageData)
            put("imgURL", imageURL)
            put("mangaSource", mangaSource)
            put("mangaTitle", mangaTitle)
            put("mangaChapter", mangaChapter)

            put("colorize", config.useColorizer)
            put("denoise", config.useDenoiser)
            put("upscale", config.useUpscaler)
            put("denoiseSigma", config.denoiserSigma)
            put("cache", config.useServerCache)
        }

        return try {
            with(json) {
                enhancerClient.newCall(POST(
                    url = "${config.baseURL}/colorize-image-data",
                    body = data.toString().toRequestBody(jsonMime),)
                )
                .awaitSuccess()
                .parseAs<EnhancedImage>()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun buildUnsafeOkHttpClient(interceptor: Interceptor? = null): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = client.newBuilder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }

            interceptor?.let { builder.addInterceptor(it) }

            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
