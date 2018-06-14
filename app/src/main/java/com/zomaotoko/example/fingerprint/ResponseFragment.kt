package com.zomaotoko.example.fingerprint

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_response.*

class ResponseFragment : Fragment() {
    companion object {
        private const val HEADER_KEY = "header"
        private const val IMAGE_KEY = "image"
        private const val MESSAGE_KEY = "message"

        fun getInstance(header: Int, image: Int, message: Int) = ResponseFragment().apply {
            arguments = buildBundle(header, image, message)
        }

        private fun buildBundle(header: Int, image: Int, message: Int) = Bundle().apply {
            putInt(HEADER_KEY, header)
            putInt(IMAGE_KEY, image)
            putInt(MESSAGE_KEY, message)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_response, container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerTextView.text = getString(arguments.getInt(HEADER_KEY, 0))
        imageView.setImageResource(arguments.getInt(IMAGE_KEY, 0))
        messageTextView.text = getString(arguments.getInt(MESSAGE_KEY, 0))
    }
}