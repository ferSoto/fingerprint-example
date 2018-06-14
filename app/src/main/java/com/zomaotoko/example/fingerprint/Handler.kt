package com.zomaotoko.example.fingerprint

import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal

class FingerprintHandler(private var listener: Handler) : FingerprintManager.AuthenticationCallback() {
    interface Handler {
        fun onSuccess()
        fun onFailed(errorCode: Int)
    }

    private var cancellationSignal: CancellationSignal? = null

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        cancellationSignal = CancellationSignal()
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        cancellationSignal?.cancel()
        listener.onSuccess()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        cancellationSignal?.cancel()
        listener.onFailed(helpCode)
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        cancellationSignal?.cancel()
        listener.onFailed(errorCode)
    }

    override fun onAuthenticationFailed() {
        cancellationSignal?.cancel()
        listener.onFailed(1000)
    }
}