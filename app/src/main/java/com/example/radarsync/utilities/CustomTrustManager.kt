package com.example.radarsync.utilities

import android.annotation.SuppressLint
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

@SuppressLint("CustomX509TrustManager")
class CustomTrustManager constructor(private val customCertificate: X509Certificate) : X509TrustManager {
    @SuppressLint("TrustAllX509TrustManager")
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // TODO: Implement user authentication
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        for (certificate in chain ?: emptyArray()) {
            if (certificate == customCertificate) {
                return  // Trust the custom certificate
            }
        }
        // You can implement additional checks if needed.
        throw CertificateException("Certificate not trusted")
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf(customCertificate)
    }
}
