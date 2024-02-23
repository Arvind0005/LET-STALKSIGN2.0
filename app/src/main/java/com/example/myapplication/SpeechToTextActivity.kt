//package com.example.myapplication
//
////noinspection SuspiciousImport
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.speech.RecognizerIntent
//import android.view.View
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.Nullable
//import androidx.appcompat.app.AppCompatActivity
//import java.util.*
//
//
//class SpeechToTextActivity : AppCompatActivity() {
//
//    private var tv_Speech_to_text: TextView? = null
//    private val REQUEST_CODE_SPEECH_INPUT = 1
//    private var iv_mic: ImageView? = null
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.stt)
//
////        iv_mic = findViewById<ImageView>(R.id.iv_mic)
////        tv_Speech_to_text = findViewById<TextView>(R.id.tv_speech_to_text0)
//
//        iv_mic?.let { micButton ->
//            micButton.setOnClickListener(View.OnClickListener {
//                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//                intent.putExtra(
//                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//                )
//                intent.putExtra(
//                    RecognizerIntent.EXTRA_LANGUAGE,
//                    Locale.getDefault()
//                )
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
//                try {
//                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
//                } catch (e: Exception) {
//                    Toast
//                        .makeText(
//                            this, " " + e.message,
//                            Toast.LENGTH_SHORT
//                        )
//                        .show()
//                }
//            })
//        }
//
//    }
//
//
//}
