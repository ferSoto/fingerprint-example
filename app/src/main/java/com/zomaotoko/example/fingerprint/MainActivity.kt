package com.zomaotoko.example.fingerprint

import android.Manifest
import android.app.Fragment
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.android.synthetic.main.activity_main.*
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class MainActivity : AppCompatActivity() {
    companion object {
        private const val ALLOW_FINGERPRINT_SCAN_TAG = "allow_fingerprint"
        private const val HARDWARE_ERROR_TAG = "hardware_error"
        private const val PERMISSION_ERROR_TAG = "permission_error"
        private const val FINGERPRINT_ERROR_TAG = "fingerprint_error"
        private const val INSECURE_ERROR_TAG = "insecure_error"

        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_NAME = "holi"
    }

    private lateinit var cipher: Cipher
    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var cryptoObject: FingerprintManager.CryptoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

        if (checkFingerprintRequirement()) {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

            keyStore.load(null)
            val builder = KeyGenParameterSpec
                    .Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            keyGenerator.init(builder.build())
            keyGenerator.generateKey()

            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )

            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            cryptoObject = FingerprintManager.CryptoObject(cipher)

            val fingerprintHandler = FingerprintHandler(applicationContext)
            fingerprintHandler.startAuth(fingerprintManager, cryptoObject)
        }
    }

    private fun checkFingerprintRequirement() = when {
        !fingerprintManager.isHardwareDetected -> showHardwareErrorFragment()
        !hasFingerprintPermission() -> showPermissionErrorFragment()
        !fingerprintManager.hasEnrolledFingerprints() -> showFingerprintErrorFragment()
        !keyguardManager.isKeyguardSecure -> showInsecureErrorFragment()
        else -> showFingerprintFragment()
    }

    private fun hasFingerprintPermission() =
            checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED

    private fun showFingerprintFragment(): Boolean {
        addFragment(ResponseFragment.getInstance(
                R.string.auth_text,
                R.drawable.ic_fingerprint,
                R.string.fingerprint_text
        ), ALLOW_FINGERPRINT_SCAN_TAG)
        return true
    }

    private fun showHardwareErrorFragment(): Boolean {
        addFragment(ResponseFragment.getInstance(
                R.string.error,
                R.drawable.ic_error,
                R.string.error_hardware
        ), HARDWARE_ERROR_TAG)
        return false
    }

    private fun showPermissionErrorFragment(): Boolean {
        addFragment(ResponseFragment.getInstance(
                R.string.error,
                R.drawable.ic_error,
                R.string.error_permission
        ), PERMISSION_ERROR_TAG)
        return false
    }

    private fun showFingerprintErrorFragment(): Boolean {
        addFragment(ResponseFragment.getInstance(
                R.string.error,
                R.drawable.ic_error,
                R.string.error_fingerprint
        ), FINGERPRINT_ERROR_TAG)
        return false
    }

    private fun showInsecureErrorFragment(): Boolean {
        addFragment(ResponseFragment.getInstance(
                R.string.error,
                R.drawable.ic_error,
                R.string.error_insecure
        ), INSECURE_ERROR_TAG)
        return false
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        fragmentManager.beginTransaction()
                .add(fragment_container.id, fragment, tag)
                .commit()
    }
}
