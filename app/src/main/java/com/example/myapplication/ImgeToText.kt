//package com.example.myapplication
//
//import android.Manifest
//import android.app.Activity
//import android.content.ContentValues
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.TextRecognizer
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//import java.io.IOException
//
//
//class ImageToText : AppCompatActivity()
//{
//    private lateinit var galleryActivityLauncher: ActivityResultLauncher<Intent>
//    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Intent>
//    private var imageUri: Uri? = null
//    private lateinit var  recognisedText:TextView;
//
//    companion object {
//        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
//    }
//
//    var textRecognizer: TextRecognizer =
//        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//    private fun isCameraPermissionGranted(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//    private fun requestCameraPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.CAMERA),
//            CAMERA_PERMISSION_REQUEST_CODE
//        )
//    }
//
//    private fun PickImageCamera() {
//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, "Image")
//        values.put(MediaStore.Images.Media.DESCRIPTION, "Description")
//        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//        cameraActivityResultLauncher.launch(intent)
//
//    }
//    private fun recogniserImage(myId: Int,imageUri:Uri) {
////        progress.setMessage("Search Image....")
////        progress.show()
//
//        try {
//            val inputImage = InputImage.fromFilePath(this, imageUri)
//          //  progress.setMessage("Text...")
//
//            val textTaskResult = textRecognizer.process(inputImage)
//                .addOnSuccessListener { text ->
//            //        progress.dismiss()
//                    val resultText = text.text
//                    println("tttttttttttttttexxxxxxxxxxxxxxxxxxxxxt");
//                    println(resultText);
//                    recognisedText.text = resultText;
//                    // Do something with the recognized text (resultText)
//                }
//        } catch (e: IOException) {
//            Toast.makeText(this@ImageToText, "Failed", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }
//
//
//
//    private fun pickImageGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        galleryActivityLauncher.launch(intent)
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.text_recogniser);
//        recognisedText = findViewById<TextView>(R.id.translated_text);
//
////        val captureImage =findViewById<Button>(R.id.captureButton);
////        val copybutton =findViewById<Button>(R.id.copyTextButton);
//
//        galleryActivityLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // Handle the result here
//                val data: Intent? = result.data
//                // Extract data from the intent as needed
//            }
//        }
//
//        cameraActivityResultLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                imageUri?.let { recogniserImage(0, it) };
//                // Handle the result here
//                // The captured image is usually available via the 'imageUri' property
//            }
//        }
////        captureImage.setOnClickListener{
////            pickImageGallery();
////        }
////        copybutton.setOnClickListener{
////            PickImageCamera();
////        }
//
//        fun exampleCheckCameraPermission() {
//            if (isCameraPermissionGranted()) {
//                // Camera permission is already granted, proceed with camera-related operations
//            } else {
//                // Camera permission is not granted, request it
//                requestCameraPermission()
//            }
//        }
//        exampleCheckCameraPermission()
//    }
//}