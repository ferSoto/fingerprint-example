package com.zomaotoko.example.fingerprint

import android.Manifest
import android.app.Fragment
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val ALLOW_FINGERPRINT_SCAN_TAG = "allow_fingerprint"
        private const val HARDWARE_ERROR_TAG = "hardware_error"
        private const val PERMISSION_ERROR_TAG = "permission_error"
        private const val FINGERPRINT_ERROR_TAG = "fingerprint_error"
        private const val INSECURE_ERROR_TAG = "insecure_error"
    }

    private lateinit var keyguardManager: KeyguardManager
    private lateinit var fingerprintManager: FingerprintManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

        checkFingerprintRequirement()
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
