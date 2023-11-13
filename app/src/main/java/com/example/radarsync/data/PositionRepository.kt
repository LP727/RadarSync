package com.example.radarsync.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.radarsync.LOG_TAG
import com.example.radarsync.POSITION_URL_ENDPOINT
import com.example.radarsync.R
import com.example.radarsync.utilities.CustomTrustManager
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext

class BasicAuthInterceptor(user: String, password: String) : Interceptor {
    private val credentials: String = Credentials.basic(user, password)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}

// Class that will be used to access the database to fetch positions (Make Object (singleton) instead?)
class PositionRepository(val app: Application) {

    private val positionDatabase = PositionDatabase.getInstance(app)
    val positionList = MutableLiveData<MutableList<PositionEntity>>()
    private var settings = UserSettings()

    fun updateSettings(newSettings: UserSettings) {
        settings.url = newSettings.url
        settings.port = newSettings.port
        settings.username = newSettings.username
        settings.password = newSettings.password
        refreshData()
    }

    @WorkerThread
    suspend fun callWebService() {
        if (networkAvailable()) {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val url = "${settings.url}:${settings.port.toString()}" + POSITION_URL_ENDPOINT

            val baseUrl = HttpUrl.parse(url)
            if (baseUrl != null) {
                // Following part is to allow connection to my local server with a self-signed certificate
                val customCertificate = readCustomCertificate() // Read the custom certificate
                val customTrustManager = CustomTrustManager(customCertificate)

                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, arrayOf(customTrustManager), SecureRandom())

                val okHttpClient = OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.socketFactory, customTrustManager)
                    .hostnameVerifier { _, _ -> true } // Bypass hostname verification
                    .addInterceptor(
                        BasicAuthInterceptor(settings.username, settings.password)
                    )
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .client(okHttpClient)
                    .build()

                val service = retrofit.create(PositionService::class.java)
                val serviceData = service.getPositionData(url).body() ?: emptyList()

                positionDatabase.positionDao().insertAll(serviceData)
            } else {
                Log.d(LOG_TAG, "Invalid URL")
            }
        }
    }

    private fun readCustomCertificate(): X509Certificate {
        // NOTE: This is a self-signed certificate that I created for my local server,
        // it needs to be added manually  in res/raw for the app to work
        val certificateStream = app.resources.openRawResource(R.raw.server_certificate)
        val certificateFactory = CertificateFactory.getInstance("X.509")
        return certificateFactory.generateCertificate(certificateStream) as X509Certificate
    }

    private fun networkAvailable() =
        (app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }

    fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            // Get latest data from web service into the local database
            callWebService()

            // Post the data from the local database to the positionList
            positionList.postValue(positionDatabase.positionDao().getAll().toMutableList())
        }
    }
}
