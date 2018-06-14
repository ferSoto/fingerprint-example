package com.zomaotoko.example.fingerprint

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.os.Handler
import android.widget.Toast

class FingerprintHandler(private var context: Context) : FingerprintManager.AuthenticationCallback() {
    private var cancellationSignal: CancellationSignal? = null


    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        cancellationSignal = CancellationSignal()
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        Toast.makeText(context, "holi 1", Toast.LENGTH_SHORT).show()
        cancellationSignal?.cancel()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        Toast.makeText(context, "holi 2", Toast.LENGTH_SHORT).show()
        cancellationSignal?.cancel()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        Toast.makeText(context, "holi 3", Toast.LENGTH_SHORT).show()
        cancellationSignal?.cancel()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(context, "holi 4", Toast.LENGTH_SHORT).show()
        cancellationSignal?.cancel()
    }
}