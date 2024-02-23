package com.example.myapplication//package com.example.myapplication
//

//import androidx.test.platform.app.InstrumentationRegistry
//import org.junit.Test

import WebAppInterface
import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.ProgressDialog
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.LocaleList
import android.os.Process
import android.provider.MediaStore
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.accessibility.AccessibilityManager
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.MediaController
import android.widget.ScrollView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bugfender.sdk.Bugfender
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects


//import com.github.asvid.remotelogger.RemoteLogger


class MainActivity:  AppCompatActivity(){

    private var sessionDepth = 0
    var app_names = arrayOf("whatsapp,youtube,browser")
    val NEW_SPINNER_ID = 1
    private var token="";
    private var deviceId="";
    private val SMS_PERMISSION_REQUEST_CODE = 123
    private var userEmail="";
    private lateinit var secureTokenManager: SecureTokenManager
    private lateinit var layout: View
    private lateinit var interpretButton:Button
    private val USAGE_ACCESS_REQUEST_CODE = 1
    private lateinit var accessibilityService: MyAccessibilityService<Any?>
    private var smsReceiver: SmsListener? = null
    private val maxWordCount = 100
    private var remainingWordCount = 10
    private var Transcribetext="";
    private lateinit var fileName:String;
    private var currentWordCount = 0
    val smsFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")


    private var progressDialog: ProgressDialog? = null
    var language:String ="en-US"
    var sourcelanguage="TAMIL"
    var selectedLanguage="Choose language";
    //    private fun getAndroidId(context: Context): String {
//        retrun
//    }
//    var id= "";
    var ReadyFlag=false;

    val str:String=""
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private lateinit var webView: WebView

    var options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.TELUGU)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()
    var englishGermanTranslator = Translation.getClient(options)
    private var alertDialog: AlertDialog? = null
    private var accessibilityAlert: AlertDialog? = null
    private var userselectedApp="";
    private lateinit var fileHandler: FileHandler


    fun getServerUrl(context: Context): String {
        // You can retrieve the URL from strings.xml or any other configuration file.
        return context.getString(R.string.server_url)
    }
    fun writeToFile(
        filename:String
    ) {
        var params: Map<String, String> = mapOf();


        params = mapOf(
            "content" to fileHandler.readFromFile(filename),
            "token" to token.toString(),
            "customer_id" to "10016",
            "device_id" to deviceId,
            "gmail_id" to userEmail
        )

        fileHandler.createAndWriteToFile("INFO " + "mainActivity " + "writeToFile", fileName);
        deviceId = secureTokenManager.loadId().toString();
        userEmail = secureTokenManager.loadEmail().toString()
        token = secureTokenManager.loadToken().toString();
        val serverUrl = getServerUrl(this)
        println(params.toString());
        fileHandler.createAndWriteToFile(
            "INFO " +
                    "write to file " + params.toString(),
            fileName
        );
        val requestUrl = "$serverUrl/write-to-file/$filename"

        mRequestQueue = Volley.newRequestQueue(this)

        mStringRequest = object : StringRequest(
            Method.POST, requestUrl.toString(),
            { response ->
                println("writeToFile response $response")
                fileHandler.createAndWriteToFile(
                    "INFO " +
                            "writeToFile response" + response.toString(),
                    fileName
                );

                val gson = Gson()
                val jsonObject: JsonObject = gson.fromJson(response, JsonObject::class.java)
                val status = jsonObject.getAsJsonPrimitive("status")?.asString;
                val message = jsonObject.getAsJsonPrimitive("message")?.asString;
                println(jsonObject)
                if (status == "<ERROR>") {
                    println("write to file error " + message)
                    fileHandler.createAndWriteToFile(
                        "CRITICAL " +
                                "write to file response Error " + response,
                        fileName
                    );
                }

            },
            { error ->
                fileHandler.createAndWriteToFile(
                    "CRITICAL " +
                            "write to file response Error " + error.printStackTrace(),
                    fileName
                );
                Log.i(ContentValues.TAG, "Error :" + error.networkResponse)
            }) {
            override fun getParams(): Map<String, String?> {
                return params
            }
        }

        mRequestQueue!!.add(mStringRequest)
    }


    fun uploadFile(filePath: String, serverUrl: String,filename:String) {
        println("kjdvncjdnfcjndsnsdkkvjdcnjnksjcks")
        println(filePath);
        val file = File(filePath)
        //val contents=fileHandler.readFromFile(filename);
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name,
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .build()

        val request = okhttp3.Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
                Log.e("File Upload",serverUrl);
                Log.e("File Upload", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                // Handle response
                if (response.isSuccessful) {
                    Log.d("File Upload", "Success: ${response.body?.string()}")
                } else {
                    Log.e("File Upload",serverUrl);
                    Log.e("File Upload", "Error: ${response.code}")
                }
            }
        })
    }
    //Image to text
    private lateinit var galleryActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    private lateinit var  recognisedText:TextView;

    private var mRequestQueue: RequestQueue? = null
    private var mStringRequest: StringRequest? = null

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    var textRecognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val profanityList = Globals.profanityList

    lateinit var mGoogleSignInClient: GoogleSignInClient

    private fun isCameraPermissionGranted(): Boolean {
        Bugfender.d("MainActivity","isCameraPermissionGranted");
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        Bugfender.d("isCameraPermissionGranted",(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED).toString());
    }

    private fun getData(url: String) {

        mRequestQueue = Volley.newRequestQueue(this)

        // String Request initialized
        mStringRequest = object : StringRequest(Request.Method.GET, url,
            { response ->
                // This code will be executed upon a successful response

                Bugfender.d("getData","respoonse from Server: $response");
                val gson = Gson()
                val jsonObject: JsonObject = gson.fromJson(response, JsonObject::class.java)
                val status = jsonObject.getAsJsonPrimitive("status")?.asString;
                val message = jsonObject.getAsJsonPrimitive("message")?.asString;

                if(status=="<ERROR>")
                {
                    sessionAlert(this@MainActivity);
                    Toast.makeText(this, "Unfortunate Error occurred : $message", Toast.LENGTH_LONG).show()
                }
            },
            { error ->

                if(error?.networkResponse?.statusCode!=null) {
                    if (error.networkResponse!!.statusCode.toString() == "401") {
                        sessionAlert(this@MainActivity);
                    }
                }
//                Log.i(ContentValues.TAG, "Error: ${error.networkResponse.statusCode}")
                Bugfender.d("getData","error from Server: $error");
            }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val statusCode = response.statusCode
                println("resssssssssssssssssssponssssssssssssseeeeeee code");
                println("Response Code: $statusCode")
                if(statusCode.toString()=="401")
                {
                    sessionAlert(this@MainActivity);
                }
                return super.parseNetworkResponse(response)
            }
        }

        mRequestQueue!!.add(mStringRequest)
    }

    fun sanitizeText(text: String): String {
        Bugfender.d("MainActivity","sanitizeText");
        Bugfender.d("sanitizeText",text);
        var text0= text.lowercase(Locale.ROOT)
        var sanitizedText = text0
        for (word in profanityList) {
            val replacement = "*".repeat(word.length)
            sanitizedText = sanitizedText.replace(Regex("(?i)\\b$word\\b"), replacement)
            sanitizedText = sanitizedText.replace(Regex("(?i)\\n$word\\n"), replacement)
        }
        Bugfender.d("sanitizeText",sanitizedText);
        return sanitizedText
    }
    val context=this;

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Bugfender.d("MainActivity","onRequestPermissionsResult");
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            Bugfender.d("onRequestPermissionsResult","requestCode==CAMERA_PERMISSION_REQUEST_CODE");
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Bugfender.d("onRequestPermissionsResult","true");
                // Camera permission granted, call the openCamera() function
                PickImageCamera()
            } else {
                Bugfender.d("onRequestPermissionsResult","false");
            }
        }
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, set up the CheckBox listener
                setUpCheckBoxListener()
            } else {
                // Permission denied, handle accordingly (e.g., inform the user)
            }
        }
    }
    fun isAccessibilityServiceEnabled(
        service: Class<out AccessibilityService?>
    ): Boolean {
        val am = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName.equals(context.packageName) && enabledServiceInfo.name.equals(
                    service.name
                )
            ) return true
        }
        return false
    }
    fun getAppNameFromPackageName(context: Context, packageName: String): String {
        Bugfender.d("MainActivity","getAppNameFromPackageName");
        val packageManager: PackageManager = context.packageManager
        try {
            Bugfender.d("getAppNameFromPackageName","try");
            val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
            Bugfender.d("getAppNameFromPackageName",packageManager.getApplicationLabel(applicationInfo).toString());
            return packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            Bugfender.e("getAppNameFromPackageName",e.printStackTrace().toString())
            e.printStackTrace()
        }
        return ""
    }
    private fun showCustomAlertDialog(packageName: String) {
        Bugfender.d("MainActivity","showCustomAlertDialog");
        val dialogView = layoutInflater.inflate(R.layout.openappalert, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.openApp_dialogTitle)

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
        var appName=getAppNameFromPackageName(this,packageName);

        dialogTitle.text = "Do you wish to open $appName with Let'sTalkSign interpretation?"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val customAlertDialog = builder.create()

        btnYes.setOnClickListener {
            Bugfender.d("showCustomAlertDialog","btnYes");
            openApp(packageName);

            // Handle "Yes" button click
            // You can implement the desired action here
            customAlertDialog.dismiss()
        }

        btnNo.setOnClickListener {
            Bugfender.d("showCustomAlertDialog","btnNo");
            // Handle "No" button click
            // You can implement the desired action here
            customAlertDialog.dismiss()
        }

        customAlertDialog.show()
    }
    private fun PickImageCamera() {
        Bugfender.d("MainActivity","Pick Image  from camera");
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Description")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        Bugfender.d("PickImageCamera",imageUri.toString());
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Bugfender.d("PickImageCamera","intent"+intent.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//        val Uri it=""
        imageUri?.let { recogniserImage(0, it) };
        cameraActivityResultLauncher.launch(intent)
    }
    private fun recogniserImage(myId: Int, imageUri: Uri?) {
        Bugfender.d("MainActivity","recogniserImage");
        Bugfender.d("recogniserImage",imageUri.toString());
        try {
            Bugfender.d("recogniserImage","Try");
            val inputImage = imageUri?.let { InputImage.fromFilePath(this, it) }
            //  progress.setMessage("Text...")

            val textTaskResult = inputImage?.let {
                textRecognizer.process(it)
                    .addOnSuccessListener { text ->
                        val resultText = text.text
                        println("tttttttttttttttexxxxxxxxxxxxxxxxxxxxxt");
                        println(resultText);
                        showPopupWithEditText(resultText,"Scanned Text");
                    }
            }
        }
        catch (e: IOException) {
            Bugfender.d("recogniserImage","catch");
            Bugfender.e("recogniserImage",e.toString());
            Bugfender.d("recogniserImage","Exception");
            println("erskjwbdschjsgvdcghvgvdgchdvfhhdfvb "+e.printStackTrace())
            Log.e("erskjwbdschjsgvdcghvgvdgchdvfhhdfvb", Log.getStackTraceString(e));
           // Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun pickImageGallery() {
        Bugfender.d("MainActivity","pickImageGallery");
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        Bugfender.d("pickImageGallery",intent.toString());
        galleryActivityLauncher.launch(intent)
    }
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        Bugfender.d("MainActivity","isServiceRunning");
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)

        for (service in runningServices) {
            if (serviceClass.name == service.service.className) {
                Bugfender.d("isServiceRunning","true");
                return true
            }
        }
        Bugfender.d("isServiceRunning","false");
        return false
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private fun showLogoutAlert() {
        Bugfender.d("MainActivity","showLogoutAlert");
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            Bugfender.d("showLogoutAlert","Yes");
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, Signin_page::class.java)
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
                getData("https://trrain4-web.letstalksign.org/app_log?mode=logout&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token");
                startActivity(intent)
                finish()
            }
        }

        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            Bugfender.d("showLogoutAlert","Cancel");
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
    private fun showAccessibilityPermissionDialog(packageName:String) {
        Bugfender.d("MainActivity","showAccessibilityPermissionDialog");
        val accessibility_dialogView = layoutInflater.inflate(R.layout.accessibility_permission, null)
        val dialogTitle = accessibility_dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogMessage = accessibility_dialogView.findViewById<TextView>(R.id.dialogMessage)
        val btnGrantPermission = accessibility_dialogView.findViewById<Button>(R.id.btnGrantPermission)
        val btnCancel = accessibility_dialogView.findViewById<Button>(R.id.btnCancel)

        dialogTitle.text = "Accessibility Permission Required"
        dialogMessage.text = "Please grant accessibility permission to open the app!"

        val builder = AlertDialog.Builder(this)
        builder.setView(accessibility_dialogView)
        accessibilityAlert = builder.create()
        userselectedApp=packageName;
        val enabled = isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
        if (enabled) {
            Bugfender.d("showAccessibilityPermissionDialog","enabled");
            accessibilityAlert?.dismiss()
            accessibilityAlert=null;
        }
        accessibilityAlert?.setOnDismissListener {
            accessibilityAlert=null;
        }

        btnGrantPermission.setOnClickListener {
            Bugfender.d("showAccessibilityPermissionDialog","btnGrantPermission");
            openAccessibilitySettings()
            val enabled = isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
            if (enabled) {
                accessibilityAlert?.dismiss()
                accessibilityAlert=null;
                Bugfender.d("showAccessibilityPermissionDialog","enabled accessibilityAlert?.dismiss()");
            }
        }

        btnCancel.setOnClickListener {
            Bugfender.d("showAccessibilityPermissionDialog","accessibilityAlert?.dismiss()");
            accessibilityAlert?.dismiss()
            accessibilityAlert=null;
        }
        accessibilityAlert?.show()
    }
    private fun requestCameraPermission() {
        Bugfender.d("MainActivity","requestCameraPermission");
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    private fun getProfilePicUri(): String? {
        Bugfender.d("MainActivity","getProfilePicUri");
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        Bugfender.d("MainActivity",sharedPreferences.getString("PROFILE_PIC_URI", null));
        return sharedPreferences.getString("PROFILE_PIC_URI", null)
    }
    private fun setUpCheckBoxListener() {
        // Get reference to CheckBox
        val checkBox: CheckBox = findViewById(R.id.SMSCheckBox)

        // Set a listener for checkbox changes
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            // Handle checkbox state change
            if (isChecked) {
                // Checkbox is checked
                // Do something when the checkbox is checked
            } else {
                // Checkbox is unchecked
                // Do something when the checkbox is unchecked
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Bugfender.d("MainActivity","onSaveInstanceState")
        outState.putParcelable("imageUri", imageUri)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Bugfender.d("ImageUriff",imageUri.toString())
        imageUri = savedInstanceState.getParcelable("imageUri")
    }
    override fun onStop() {
        super.onStop()
        if (sessionDepth > 0) sessionDepth--
        if (sessionDepth == 0) {
            Bugfender.d("MainActivity","background");
            val isFloatingWindowServiceRunning = isServiceRunning(this, FloatingWindowGFG::class.java)
            if (isFloatingWindowServiceRunning) {
                // Stop the service
                val intent = Intent("stop_action_overlay")
                intent.putExtra("stop_message_overlay", "start")
                sendBroadcast(intent)
//                val myService = Intent(this@MainActivity, FloatingWindowGFG::class.java)
//                stopService(myService);
            }
            // app went to background
        }
    }
    private fun hasUsageAccessPermission(): Boolean {
        Bugfender.d("MainActivity","hasUsageAccessPermission");
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), packageName
            )
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }
    private fun hasOverlayPermission(): Boolean {
        Bugfender.d("MainActivity","hasOverlayPermission");
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            // For versions prior to Android M, overlay permission is not required
            true
        }
    }
    private fun requestUsageAccessPermission() {
        Bugfender.d("MainActivity","requestUsageAccessPermission");
        val dialogView = layoutInflater.inflate(R.layout.permission_required, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val btnGrantUsageAccess = dialogView.findViewById<Button>(R.id.btnGrantUsageAccess)
        val btnGrantOverlayPermission = dialogView.findViewById<Button>(R.id.btnGrantOverlayPermission)
        if(hasUsageAccessPermission())
        {
            btnGrantUsageAccess.isEnabled=false
        }
        if(hasOverlayPermission())
        {
            btnGrantOverlayPermission.isEnabled=false
        }
        if(alertDialog!=null){
            alertDialog?.dismiss();
            alertDialog=null;
        }


        dialogTitle.text = "Permissions Required"
        dialogMessage.text = "Please grant the following permissions to use this app:\n\n" +
                "1. Usage access permission\n" +
                "2. Overlay permission\n"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView).setCancelable(false)
        alertDialog = builder.show()

        if(hasUsageAccessPermission() && hasOverlayPermission()) {
            println("111111111111111dissssssssssmissssssssssssssssssssssss");
            alertDialog?.dismiss()
            alertDialog=null;
        }
        btnGrantUsageAccess.setOnClickListener {
            openUsageAccessSettings()
//            alertDialog?.dismiss()
//            alertDialog = null
        }

        btnGrantOverlayPermission.setOnClickListener {
            openOverlaySettings()
//            alertDialog = null
        }
        builder.setOnDismissListener {
            alertDialog = null
        }

    }
    private fun openAccessibilitySettings() {
        Bugfender.d("MainActivity","openAccessibilitySettings");
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivityForResult(intent,1);
    }
    private fun openOverlaySettings() {
        Bugfender.d("MainActivity","openOverlaySettings");
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivityForResult(intent, 1);
    }
    private fun openUsageAccessSettings() {
        Bugfender.d("MainActivity","openUsageAccessSettings");
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivityForResult(intent, USAGE_ACCESS_REQUEST_CODE)
    }
    @SuppressLint("MissingInflatedId")
    fun removeQuotes(text: String): String {
        Bugfender.d("MainActivity","removeQuotes");
        val singleLineText = text.replace("\n", " ").replace("\r", " ")
        return singleLineText.replace("'", "").replace("\"", "")
    }
    fun sessionAlert(context: Context) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.session_timeout_alert, null)

        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()

        // Set click listener for the "OK" button
        view.findViewById<Button>(R.id.okButton).setOnClickListener {
            Bugfender.d("showLogoutAlert","Yes");
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, Signin_page::class.java)
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
                startActivity(intent)
                finish()
            }
            alertDialog.dismiss()
            // Add any logic you need to perform when the user clicks "OK"
        }

        alertDialog.show()
    }

    fun isAscii(char: Char): Boolean {
        val codePoint = char.toInt()
        return (codePoint in 97..122) || (codePoint in 65..90)
    }
    fun isSpecialCharacter(char: Char): Boolean {
        return char in setOf(',', '.', '?', '!', ';', ':', '-', '_', '/', '\\', '(', ')', '[', ']', '{', '}', '<', '>', '|', '*', '&', '^', '%', '$', '#', '@', '~', '`', '"', '\'', '+', '=', ' ', '\t', '\n', '\r') || char in '0'..'9'
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    private fun showPopupWithEditText(initialText: CharSequence, Title:String) {
        Bugfender.d("MainActivity","showPopupWithEditText");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val builder = AlertDialog.Builder(this)
        val layout = layoutInflater.inflate(R.layout.popup_layout, null)
        interpretButton =layout.findViewById<Button>(R.id.interpretButton);
        val languagetext:TextView=layout.findViewById(R.id.type_title);
        var editText = layout.findViewById<EditText>(R.id.popupEditText)
        var title =layout.findViewById<TextView>(R.id.titlePopup);
        var remainingWords =layout.findViewById<TextView>(R.id.remainingWordCount);

        val cancelButton=layout.findViewById<Button>(R.id.cancelButton);
        var titletext=languagetext.text;
        var ogtext=titletext;
        println("jkdsvhjbfhjbvfhbjbfvdjhbdjhdbhbf "+initialText)
        if(initialText=="")
        {
            interpretButton.isEnabled=false;
            println("sdbvhjbschsbhdbdhbdcnvjfvj");
        }
        editText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                val englishNote = layout.findViewById<TextView>(R.id.EnglishNote);
                if (charSequence?.length == 0 || charSequence.isNullOrBlank() || remainingWordCount == maxWordCount) {
                    println("yesssssssssssssssssdnfvjbfbjdvbdjb 2");
                    interpretButton.isEnabled = false
                    englishNote.visibility = View.GONE;
                }
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {


                val englishNote = layout.findViewById<TextView>(R.id.EnglishNote);
                // Calculate the current word count
                val words = charSequence?.trim()?.split(Regex("\\s+"))
                currentWordCount = words?.size ?: 0

                // Update the remaining word count
                remainingWordCount = maxWordCount - currentWordCount
                // Assuming you have a TextView to display the remaining count
                // You can replace R.id.remainingWordCount with the actual ID of your TextView
                remainingWords.text = "RemainingWords: $remainingWordCount"

                // Enable/disable the interpret button based on the word count
                if(remainingWordCount<0)
                {
                    interpretButton.isEnabled = false
                    ReadyFlag=false
                }
                if (interpretButton.text == "Interpret" && remainingWordCount >= 0) {
                    interpretButton.isEnabled = true
                    englishNote.visibility = View.GONE;
                }


                if (selectedLanguage != "English" && Title == "Text to Interpret") {
                    if (words?.isNotEmpty() == true) {
                        for (word in words) {
                            for (char in word) {
                                if (!isSpecialCharacter(char) && isAscii(char)) {
                                    englishNote.setText("Note: $selectedLanguage language is chosen. Please type  $selectedLanguage sentences.")
                                    englishNote.visibility = View.VISIBLE;
                                    interpretButton.isEnabled = false
                                    break;
                                }
                            }
                        }
                    }
                } else if (selectedLanguage == "English" && Title == "Text to Interpret") {
                    if (words?.isNotEmpty() == true) {
                        for (word in words) {
                            for (char in word) {
                                if (!isSpecialCharacter(char) && !isAscii(char)) {
                                    englishNote.setText("Note:English language is chosen. Please type English sentences.")
                                    englishNote.visibility = View.VISIBLE;
                                    interpretButton.isEnabled = false
                                    break;
                                }
                            }
                        }
                    }
                }
                if (charSequence?.length == 0 || charSequence.isNullOrBlank() || remainingWordCount == maxWordCount) {
                    println("yesssssssssssssssssdnfvjbfbjdvbdjb 2");
                    interpretButton.isEnabled = false
                    englishNote.visibility = View.GONE;
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        if(selectedLanguage=="English") {
            languagetext.visibility=View.GONE;
            editText.setImeHintLocales(LocaleList(Locale("en", "us")))
        }
        else if(selectedLanguage=="Tamil") {
            titletext=ogtext;
            languagetext.visibility=View.VISIBLE;
            titletext=titletext.toString()+"i.e "+ "vanakam for "+ "வணக்கம். This feature only works on selected keybords like Gboard."
            languagetext.setText(titletext);
            editText.setImeHintLocales(LocaleList(Locale("ta", "IN")))
        }
        else if(selectedLanguage=="Telugu") {
            titletext=ogtext;
            languagetext.visibility=View.VISIBLE;
            titletext=titletext.toString()+"i.e "+ "Halō for "+ "హలో. This feature only works on selected keybords like Gboard."
            languagetext.setText(titletext);
            editText.setImeHintLocales(LocaleList(Locale("te", "IN")))
        }
        else if(selectedLanguage=="Kannada") {
            titletext=ogtext;
            languagetext.visibility=View.VISIBLE;
            titletext=titletext.toString()+"i.e "+ "Namaskara for "+ "ನಮಸ್ಕಾರ. This feature only works on selected keybords like Gboard."
            languagetext.setText(titletext);
            editText.setImeHintLocales(LocaleList(Locale("kn", "IN")))
        }
        else if(selectedLanguage=="Malayalam") {
            editText.setImeHintLocales(LocaleList(Locale("ml", "IN")))
        }
        else if(selectedLanguage=="Hindi") {
            titletext=ogtext;
            languagetext.visibility=View.VISIBLE;
            titletext=titletext.toString()+"i.e "+ "namaste for "+ "नमस्ते. This feature only works on selected keybords like Gboard."
            languagetext.setText(titletext);
            editText.setImeHintLocales(LocaleList(Locale("hi", "IN")))
        }
        else if(selectedLanguage=="Gujarati") {
            titletext=ogtext;
            languagetext.visibility=View.VISIBLE;
            titletext=titletext.toString()+"i.e "+ "namaste for "+ "નમસ્તે. This feature only works on selected keybords like Gboard."
            languagetext.setText(titletext);
            editText.setImeHintLocales(LocaleList(Locale("gu", "IN")))
        }
        else if(selectedLanguage=="Marathi") {
            titletext=ogtext;
            languagetext.visibility=View.VISIBLE;
            titletext=titletext.toString()+"i.e "+ "Namaskāra for "+ "नमस्कार. This feature only works on selected keybords like Gboard."
            languagetext.setText(titletext);
            editText.setImeHintLocales(LocaleList(Locale("mr", "IN")))
        }
        else if(selectedLanguage=="Bengali") {
            titletext=ogtext;
            languagetext.visibility=View.VISIBLE;
            titletext=titletext.toString()+"i.e "+ "Hyālō for "+ "হ্যালো. This feature only works on selected keybords like Gboard."
            languagetext.setText(titletext);
            editText.setImeHintLocales(LocaleList(Locale("bn", "IN")))
        }


        title.setText(Title)
        //editText.setText("");
        if(initialText!="") {
            editText.setText(sanitizeText(initialText.toString()))
        }

//        if(!ReadyFlag){
//            interpretButton.setText("Loading..")
//            interpretButton.isEnabled=false;
//        }
//        else{
        interpretButton.setText("Interpret")
//        if(remainingWordCount>0)
//        {
//            interpretButton.isEnabled=true;
//        }
      //  }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            //    interpretButton.isEnabled = (!charSequence.isNullOrBlank())
//                if(!ReadyFlag)
//                {
//                    interpretButton.isEnabled=false;
//                }

            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        // Make the EditText scrollable
//        editText.setOnTouchListener { _, event ->
//            editText.parent.requestDisallowInterceptTouchEvent(true)
//            false
//        }

        builder.setView(layout)
        val dialog =builder.create();
        if(Title=="Scanned Text" && initialText=="")
        {
            showEmptyTextAlert(this)
        }
        else
            dialog.show();
        interpretButton.setOnClickListener {
            val dataEnabled = isDataEnabled(this)

            if (!dataEnabled) {
                // Mobile data is enabled
                Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show()
            }

            if(Title=="Scanned Text") {
                getData("https://trrain4-web.letstalksign.org/app_log?mode=scan_interpreted&language=english&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            }
            else if(Title=="Text to Interpret")
            {
                getData("https://trrain4-web.letstalksign.org/app_log?mode=text_interpreted&language=$selectedLanguage&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            }
            else if(Title=="Recognised Text")
            {
                getData("https://trrain4-web.letstalksign.org/app_log?mode=audio_interpreted&language=$selectedLanguage&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            }
            if (selectedLanguage != "English" && Title == "Text to Interpret") {
                fileHandler.createAndWriteToFile(
                    "MainActivity " + "selectedLanguage != \"English\" && Title==\"Text to Interpret\"",
                    fileName
                );
                val progressDialog = ProgressDialog(this@MainActivity)
                progressDialog.setMessage("Translating...")
                progressDialog.setCancelable(false)
                progressDialog.show()
//                if(token==null || deviceId==null || userEmail=="")
//                {
                deviceId = secureTokenManager.loadId().toString();
                userEmail = secureTokenManager.loadEmail().toString()
                token = secureTokenManager.loadToken().toString();
                //    }
//                if(token.isNullOrEmpty())
//                    token=Globals.token
//                if(userEmail.isNullOrEmpty())
//                    userEmail=Globals.email
//                if(deviceId.isNullOrEmpty())
//                    deviceId=Globals.deviceId;
                var params: Map<String, String> = mapOf();
                if (selectedLanguage == "Kannada") {
                    params = mapOf(
                        "token" to token.toString(),
                        "maxWordCount" to "50",
                        "fromLang" to "Kannada",
                        "toLang" to "english",
                        "textToTranslate" to editText.text.toString(),
                        "customer_id" to "10009",
                        "device_id" to deviceId,
                        "gmail_id" to userEmail
                    )
                } else if (selectedLanguage == "Hindi") {
                    params = mapOf(
                        "token" to token.toString(),
                        "maxWordCount" to "50",
                        "fromLang" to "Hindi",
                        "toLang" to "english",
                        "textToTranslate" to editText.text.toString(),
                        "customer_id" to "10009",
                        "device_id" to deviceId,
                        "gmail_id" to userEmail
                    )

                }
                else if (selectedLanguage == "Tamil") {
                    params = mapOf(
                        "token" to token.toString(),
                        "maxWordCount" to "50",
                        "fromLang" to "Tamil",
                        "toLang" to "english",
                        "textToTranslate" to editText.text.toString(),
                        "customer_id" to "10009",
                        "device_id" to deviceId,
                        "gmail_id" to userEmail
                    )

                }
                else if (selectedLanguage == "Telugu") {
                    params = mapOf(
                        "token" to token.toString(),
                        "maxWordCount" to "50",
                        "fromLang" to "Telugu",
                        "toLang" to "english",
                        "textToTranslate" to editText.text.toString(),
                        "customer_id" to "10009",
                        "device_id" to deviceId,
                        "gmail_id" to userEmail
                    )

                }
                else if (selectedLanguage == "Gujarati") {
                    params = mapOf(
                        "token" to token.toString(),
                        "maxWordCount" to "50",
                        "fromLang" to "Gujarati",
                        "toLang" to "english",
                        "textToTranslate" to editText.text.toString(),
                        "customer_id" to "10009",
                        "device_id" to deviceId,
                        "gmail_id" to userEmail
                    )

                }
                else if (selectedLanguage == "Marathi") {
                    params = mapOf(
                        "token" to token.toString(),
                        "maxWordCount" to "50",
                        "fromLang" to "Marathi",
                        "toLang" to "english",
                        "textToTranslate" to editText.text.toString(),
                        "customer_id" to "10009",
                        "device_id" to deviceId,
                        "gmail_id" to userEmail
                    )

                }
                else if (selectedLanguage == "Bengali") {
                    params = mapOf(
                        "token" to token.toString(),
                        "maxWordCount" to "50",
                        "fromLang" to "Bengali",
                        "toLang" to "english",
                        "textToTranslate" to editText.text.toString(),
                        "customer_id" to "10009",
                        "device_id" to deviceId,
                        "gmail_id" to userEmail
                    )

                }
                fileHandler.createAndWriteToFile("INFO "+"selectedLanguage " + params.toString(), fileName);
                sendGPTTranslationRequest(this, editText.text.toString(), params) { text ->
                    progressDialog.dismiss();
                    Transcribetext = text
                    fileHandler.createAndWriteToFile(
                        "sendGPTTranslationRequestTrans " + Transcribetext.toString(),
                        fileName
                    );
                    println("translated text" + text);
                    Bugfender.d("MainAcEdittextPopup", text.toString());
                    var ftext = text
                    ftext = ftext.replace("\n", "");
                    ftext = ftext.replace("\b", "");
                    var tv_Speech_to_text = findViewById<TextView>(R.id.webview_text);
                    if (!ReadyFlag) {
                        tv_Speech_to_text?.setText("Please wait the model is loading...!")
//                        val redColor = 0xFF0000 // Red color in hexadecimal
//                        tv_Speech_to_text?.setTextColor(Color.parseColor("#FF000"))
                    } else {
                        tv_Speech_to_text?.setText(ftext);
                        tv_Speech_to_text?.setTextColor(Color.parseColor("#808080"))
                        tv_Speech_to_text?.visibility = View.VISIBLE;

                        //val jsCode = "sendMessage('${ftext}');"
                        val jsCode = "sendMessage(\"${ftext}\")";
                        println(jsCode);
                        webView.evaluateJavascript(jsCode, null)
                    }
                }
            }
            else {
                Bugfender.d("EditText", editText.text.toString());

                fileHandler.createAndWriteToFile("INFO "+"EditText " + editText.text.toString(), fileName);
                println(editText.text);
                Transcribetext = editText.text.toString();
                var ftext = editText.text.toString()
                ftext = ftext.replace("\n", "");
                ftext = ftext.replace("\b", "");
                var tv_Speech_to_text = findViewById<TextView>(R.id.webview_text);
                var sanitized_text = "";
                println("wefhibgufhdvbhfdbhjbhjsd");
                sanitized_text = sanitizeText(ftext)
                if (!ReadyFlag) {
                    Transcribetext = sanitized_text;
                    fileHandler.createAndWriteToFile(
                        "EditText " + Transcribetext.toString(),
                        fileName
                    );
                    println("djncjsdjcsbdhjbsjhbs")
                    tv_Speech_to_text?.setText("Please wait the model is loading...!")
                    tv_Speech_to_text?.setTextColor(Color.parseColor("#FF0000"))
                    tv_Speech_to_text?.visibility = View.VISIBLE;
                } else {

                    sanitized_text = sanitizeText(ftext)
                    fileHandler.createAndWriteToFile(
                        "EditText " + sanitized_text.toString(),
                        fileName
                    )
                    tv_Speech_to_text?.setTextColor(Color.parseColor("#808080"))
                    tv_Speech_to_text?.setText(sanitized_text);
                    tv_Speech_to_text?.visibility = View.VISIBLE;
                }
                if (ReadyFlag) {
                    if (sanitized_text != ftext.toLowerCase()) {
                        println("fdjvnjbfhvbudvdhdhfvbhdfhfudvdfbvufbvhvudb 2")
                        println(sanitized_text)
                        fileHandler.createAndWriteToFile("INFO "+
                                "sanitized_text!=ftext.toLowerCase() " + sanitized_text.toString(),
                            fileName
                        )
                        println(ftext);
                        var errortext = "Exclude inappropriate words for interpretation."
                        val jsCode = "sendMessage(\"${errortext}\")";
                        println(jsCode);
                        webView.evaluateJavascript(jsCode, null)
                        dialog.dismiss()
                    } else {
                        fileHandler.createAndWriteToFile("INFO "+"showpopup " + sanitized_text, fileName)
                        println(sanitized_text)
                        println(ftext);
                        val jsCode = "sendMessage(\"${sanitized_text}\")";
                        println(jsCode);
                        webView.evaluateJavascript(jsCode, null)
                        dialog.dismiss()
                    }
                } else {
                    if (sanitized_text != ftext.toLowerCase()) {
                        println("fdjvnjbfhvbudvdhdhfvbhdfhfudvdfbvufbvhvudb 2")
                        println(sanitized_text)
                        fileHandler.createAndWriteToFile("INFO "+
                                "sanitized_text " + sanitized_text,
                            fileName
                        )
                        fileHandler.createAndWriteToFile("INFO "+"ftext " + ftext, fileName)
                        println(ftext);
                        var errortext = "Exclude inappropriate words for interpretation."
                        Transcribetext = "Exclude inappropriate words for interpretation."
//                        val jsCode = "sendMessage(\"${errortext}\")";
//                        println(jsCode);
//                        webView.evaluateJavascript(jsCode, null)
                        dialog.dismiss()
                    } else {

                        println(sanitized_text)
                        println(ftext);
                        Transcribetext = sanitized_text
                        fileHandler.createAndWriteToFile("INFO "+
                                "showpopup else Transcribetext" + Transcribetext,
                            fileName
                        )
//                        val jsCode = "sendMessage(\"${ftext}\")";
//                        println(jsCode);
//                        webView.evaluateJavascript(jsCode, null)
                        dialog.dismiss()
                    }
                }

                // Do something with the new text
                dialog.dismiss()
            }
            if(selectedLanguage!="English")
            {
                if(selectedLanguage=="Tamil") {
                    language = "ta-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.TAMIL)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Telugu") {
                    language = "te-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.TELUGU)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Kannada") {
                    language = "kn-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.KANNADA)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Hindi") {
                    language = "hi-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.HINDI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Gujarati") {
                    language = "gu-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.GUJARATI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Marathi") {
                    language = "mr-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.MARATHI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Bengali") {
                    language = "bn-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.BENGALI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                this@MainActivity.englishGermanTranslator = Translation.getClient(options)
            }
            else {
                Bugfender.d("EditText",editText.text.toString());
                println(editText.text);
                Transcribetext= editText.text.toString();
                var ftext=this@MainActivity.removeQuotes(editText.text.toString())
                ftext=ftext.replace("\n","");
                ftext=ftext.replace("\b","");
                var tv_Speech_to_text = findViewById<TextView>(R.id.webview_text);
                println("wefhibgufhdvbhfdbhjbhjsd");
                if(!ReadyFlag)
                {
                    println("djncjsdjcsbdhjbsjhbs")
                    tv_Speech_to_text?.setText("Please wait the model is loading...!")
                    tv_Speech_to_text?.visibility=View.VISIBLE;
                }
                else
                {
                    tv_Speech_to_text?.setText(sanitizeText(ftext));
                    tv_Speech_to_text?.visibility=View.VISIBLE;
                }
                val jsCode = "sendMessage(\"${ftext}\")";
                println(jsCode);
                webView.evaluateJavascript(jsCode,null)
            }
            // Handle the Ok button click

//            val msg=removeQuotes(msg)+"thankyou";

            // Do something with the new text
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun showEmptyTextAlert(context: Context) {
        // Inflate the custom layout
        val layoutInflater = layoutInflater
        val view = layoutInflater.inflate(R.layout.rescanalert, null)

        // Initialize buttons from the custom layout
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val rescanButton = view.findViewById<Button>(R.id.rescanButton)

        // Create the AlertDialog
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        val alertDialog = builder.create()

        // Set up click listeners for the buttons
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
            // Add action for cancel button
        }

        rescanButton.setOnClickListener {
            if(selectedLanguage!="English")
            {
                Bugfender.d("Scan","Not English")
                fileHandler.createAndWriteToFile("Scan "+"Not English",fileName);

                showLanguageNotSupportedDialog(this);
            }
            else
            {
                if (isCameraPermissionGranted()) {
                    Bugfender.d("REScan","camera permission granted");
                    fileHandler.createAndWriteToFile("REScan "+"camera permission granted",fileName);

                    PickImageCamera();
                }
                else {
                    Bugfender.d("Scan","camera permission Not granted");
                    fileHandler.createAndWriteToFile("Scan "+"camera permission Not granted",fileName);

                    requestCameraPermission()
                }
            }
            alertDialog.dismiss()
            // Add action for rescan button
        }

        // Show the AlertDialog
        alertDialog.show()
    }
    fun showLanguageNotSupportedDialog(context: Context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val builder = AlertDialog.Builder(this)
        val layout = layoutInflater.inflate(R.layout.language_support_alert, null)
        val okButoon:Button = layout.findViewById(R.id.okButton);

        builder.setView(layout)
        val dialog =builder.create();
        okButoon.setOnClickListener {
            dialog.dismiss();
        }
        dialog.show();

    }
    private fun showProgressDialog() {
        Bugfender.d("MainActivity","showProgressDialog");
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Downloading translation model please wait this may take few seconds...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }
    private fun dismissProgressDialog() {
        Bugfender.d("MainActivity","dismissProgressDialog");
        progressDialog?.dismiss()
        progressDialog = null
    }
    fun translatetoEnglish(msg: CharSequence, callback: (String) -> Unit) {
        showProgressDialog()

        englishGermanTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                dismissProgressDialog()

                englishGermanTranslator.translate(msg.toString())
                    .addOnSuccessListener { translatedText ->
                        println("Translation success: $translatedText")
                        callback(translatedText)
                    }
                    .addOnFailureListener { exception: Exception ->
                        println("Translation failure: $exception")
                        callback("Translation failed")
                    }
            }
            .addOnFailureListener { exception: Exception ->
                dismissProgressDialog()
                println("Model download failure: $exception")
                callback("Model download failed")
            }
    }
    private fun openApp(packageName: String) {
        Bugfender.d("MainActivity","openApp");
        val packageManager = this.packageManager
        // Check if app is installed
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            println("openAppAction");
            getData("https://trrain4-web.letstalksign.org/app_log?mode=app_interpreted&app_name=$packageName&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            getData("https://trrain4-web.letstalksign.org/app_log?mode=app_opened&app_name=$packageName&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            startActivity(launchIntent)
        } else {
            // App not found or not installed
            Toast.makeText(this, "App not found: $packageName", Toast.LENGTH_SHORT).show()
        }
    }

    val sessionMsg = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sms = intent.extras?.getString("session_message")
            println("session Messagekdcnnjndcjndjnjcdnjcdnjc");
            if(sms=="ready")
            {
                interpretButton.setText("Interpret");
                if(remainingWordCount>0)
                {
                    ReadyFlag=true;
                    var tv_Speech_to_text = findViewById<TextView>(R.id.webview_text);
                    tv_Speech_to_text?.setText(sanitizeText(Transcribetext));
                    tv_Speech_to_text?.visibility=View.VISIBLE;
                    interpretButton.isEnabled=true;
                }
            }
        }
    }

    val usageLog = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val Vocab_viewed = intent.extras?.getString("vocab_viewed")
            val customer_id = intent.extras?.getString("category_name")
            val word = intent.extras?.getString("word")
            println("usageLog Messagekdcnnjndcjndjnjcdnjcdnjc");
            fileHandler.createAndWriteToFile("INFO usageLog"+
                    "Vocab_viewed: " + Vocab_viewed.toString()+"\ncustomer_id: "+customer_id.toString()+"\nword: "+word.toString()
                ,fileName
            );
            getData("https://trrain4-web.letstalksign.org/app_log?mode=vocab_viewed&vocab_category=$customer_id&vocab_word=$word&customer_id=10016&device_id=$deviceId&gmail_id=$userEmail&token=$token");
            println("Vocab_viewed: " + Vocab_viewed.toString()+"\ncustomer_id: "+customer_id.toString()+"\nword: "+word.toString());
        }
    }
    val missingAnimation = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val word = intent.extras?.getString("missingAnimations")
            println("session Messagekdcnnjndcjndjnjcdnjcdnjc");
            if(word!="" || word.isNotEmpty())
            {
//                getData("https://trrain4-web.letstalksign.org/app_log?mode=logout&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token");
                getData("https://trrain4-web.letstalksign.org/app_log?mode=missing_animation&word=$word&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            }
        }
    }
    fun isDataEnabled(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return networkInfo != null && networkInfo.isConnected
    }
    fun sendGPTTranslationRequest(
        context: Context,
        content: String,
        params: Map<String, String?>,
        callback: (String) -> Unit
    ) {
        val url = "https://trrain4-web.letstalksign.org/get_translation"
        println("yessssssssssssssssssssssssssssjbhvbdhbvhjbjdbhfg")
        fileHandler.createAndWriteToFile("INFO "+
                "sendGPTTranslationRequest " + params.toString(),
            fileName
        );

        mRequestQueue = Volley.newRequestQueue(this)

        mStringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                fileHandler.createAndWriteToFile("INFO "+"Scan " + "Not English", fileName);
                println("response got from server1" + response.toString())
                fileHandler.createAndWriteToFile("INFO "+
                        "sendGPTTranslationRequest " + response.toString(),
                    fileName
                );

                    val gson = Gson()
                val jsonObject: JsonObject = gson.fromJson(response, JsonObject::class.java)
                val translatedText = jsonObject.getAsJsonPrimitive("translated")?.asString;
                val status = jsonObject.getAsJsonPrimitive("status")?.asString;
                val message = jsonObject.getAsJsonPrimitive("message")?.asString;
                println(jsonObject)
                if(status=="<ERROR>")
                {
                    fileHandler.createAndWriteToFile("CRITICAL "+
                            "sendGPTTranslationRequest Error " + response,
                        fileName
                    );
                    Toast.makeText(this, "Unfortunate Error occurred : $message", Toast.LENGTH_LONG).show()
                }
                if (translatedText != null) {
                    fileHandler.createAndWriteToFile("INFO "+
                            "sendGPTTranslationRequest " + translatedText.toString(),
                        fileName
                    );
                    callback(translatedText)
                }
            },
            { error ->
                fileHandler.createAndWriteToFile("CRITICAL "+
                        "sendGPTTranslationRequest Error " + error.printStackTrace(),
                    fileName
                );
                callback("Unexpected error please check your internet connection and try again!")
                println("errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr mainActivity")
                println(error)
                Log.i(ContentValues.TAG, "Error :" + error?.networkResponse)
            }) {
            override fun getParams(): Map<String, String?> {
                return params
            }
        }

        mRequestQueue!!.add(mStringRequest)

    }
    override fun onStart() {
        super.onStart()
        layout = layoutInflater.inflate(R.layout.popup_layout, null)

        // Initialize other properties
        interpretButton = layout.findViewById(R.id.interpretButton)
        sessionDepth++
        if (sessionDepth == 1) {
            val accessibilityStatusTextView: TextView = findViewById(R.id.access_permission)
            val home_stopbutton:com.google.android.material.floatingactionbutton.FloatingActionButton=findViewById(R.id.home_myButton)
            val help_stopbutton:com.google.android.material.floatingactionbutton.FloatingActionButton=findViewById(R.id.help_myButton)
            val stopbutton:com.google.android.material.floatingactionbutton.FloatingActionButton=findViewById(R.id.myButton);

            val enabled = isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
            if(enabled)
            {
                accessibilityStatusTextView.text = "Granted"
                accessibilityStatusTextView.setTextColor(Color.parseColor("#00FF00"))
                if(stopbutton.visibility==View.GONE) {
                    stopbutton.visibility = View.VISIBLE;
                    help_stopbutton.visibility=View.VISIBLE;
                    home_stopbutton.visibility=View.VISIBLE
                }
            }
            else if(!enabled)
            {
                accessibilityStatusTextView.text = "Not Granted"
                accessibilityStatusTextView.setTextColor(Color.parseColor("#FF0000"))
                if(stopbutton.visibility==View.VISIBLE) {
                    stopbutton.visibility = View.GONE;
                    help_stopbutton.visibility = View.GONE;
                    home_stopbutton.visibility = View.GONE;
                }
            }
            Bugfender.d("MainActivity","foreground session");
            println("app is in foreground");
            val isFloatingWindowServiceRunning = isServiceRunning(this, FloatingWindowGFG::class.java)
            if (isFloatingWindowServiceRunning) {
                println("Floating service is running");
                Bugfender.d("forward","Floating service is running");
                // Stop the service
                val intent = Intent("stop_action_overlay")
                intent.putExtra("stop_message_overlay", "stop")
                sendBroadcast(intent);
                Bugfender.d("forward","broadcast is sent");
//                val myService = Intent(this@MainActivity, FloatingWindowGFG::class.java)
//                stopService(myService);
            }
            else{
                Bugfender.d("MainActivity","not in foreground");
                println("not in foreground");
            }
            if(accessibilityAlert!=null)
            {
                Bugfender.d("forward","accessibilityAlert!=null");
                accessibilityAlert!!.dismiss()
                accessibilityAlert=null;
                val enabled = isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
                if (enabled) {
                    Bugfender.d("forward","enabled accessibilityAlert!=null ");
                    // Call your function here
                    showCustomAlertDialog(userselectedApp);
                }
            }
            if(alertDialog==null &&  (!hasUsageAccessPermission() || !hasOverlayPermission())) {
                Bugfender.d("forward","requestUsageAccessPermission");
                requestUsageAccessPermission();
            }
            if(alertDialog!=null && hasUsageAccessPermission() && hasOverlayPermission())
            {
                Bugfender.d("forward"," alertDialog?.dismiss();");
                alertDialog?.dismiss();
                alertDialog=null;
            }
            if(alertDialog!=null && (!hasOverlayPermission() || !hasUsageAccessPermission()))
            {
                Bugfender.d("forward","if alertDialog?.dismiss();");
//                alertDialog?.dismiss();
//                alertDialog=null;
                requestUsageAccessPermission();
            }
        }
    }
    private val handlerfile = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            writeToFile(fileName)
            fileHandler.deleteFile(fileName);
            handlerfile.postDelayed(this, 900000) // 10 seconds delay
        }
    }
    override fun onResume() {
        super.onResume()
        token = secureTokenManager.loadToken().toString()
        deviceId = secureTokenManager.loadId().toString();
        userEmail = secureTokenManager.loadEmail().toString()
        handlerfile.postDelayed(runnable, 1000) // Start the runnable immediately
    }

    override fun onPause() {
        super.onPause()
        token = secureTokenManager.loadToken().toString()
        deviceId = secureTokenManager.loadId().toString();
        userEmail = secureTokenManager.loadEmail().toString()
        handlerfile.removeCallbacks(runnable) // Stop the runnable when the activity is paused
    }


    @SuppressLint("MissingInflatedId", "WrongViewCast", "ResourceType", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_page)
        fileHandler = FileHandler(this)
        secureTokenManager = SecureTokenManager(this)
        deviceId= secureTokenManager.loadId().toString();
        // Example usage:
        val data = "app is in background"
        fileName = "$deviceId.txt"
//        fileHandler.deleteFile(fileName);
//        fileHandler.deleteFile(fileName);

        fileHandler.createAndWriteToFile(data, fileName)
        writeToFile(fileName);
        println("kjfvnhjndjfbvjdnhjbfhjfbfbjbfjhbjdhb");
//        val isDeleted0 = fileHandler.deleteFile("example.txt")
//        println(isDeleted0);
        val fileContent = fileHandler.readFromFile(fileName)
        println("kjfvnhjndjfbvjdnhjbfhjfbfbjbfjhbjdhb");
        println(fileContent);

        val file=File(context.filesDir, fileName);
        fileHandler.createAndWriteToFile("content1", fileName);
//        Handler().postDelayed({
        fileHandler.createAndWriteToFile("content2",fileName);

      //  writeToFile(fileName)
//        try {
//            val process: java.lang.Process? = Runtime.getRuntime().exec("logcat")
//            val bufferedReader = BufferedReader(
//                InputStreamReader(process?.getInputStream())
//            )
//            val log = StringBuilder()
//            var line: String? = ""
//            while (bufferedReader.readLine().also { line = it } != null) {
////                println("heeeeeeeeeeeeeeeeeeeeeeeeeee;ppppppppppppppppppppppp");
////                println(line);
//                log.append(line)
//         //       fileHandler.createAndWriteToFile(line.toString(),fileName)
//            }
//        } catch (e: IOException) {
//        }

        //uploadFile(file.path,"https://b129-2402-3a80-458a-1718-183f-cd17-f6f-6f94.ngrok-free.app/upload",fileName);
        // Example: Uploading a file


//
//        if (isDeleted) {
//            val fileName9 = "example.txt"
//            val fileContent9 = fileHandler.readFromFile(fileName9)
//            println("hvfbhbsvgdbhgbghsggshvghvdgsvfgas");
//            println(fileContent);
//        } else {
//            println("answer is falseeeee");
//            // File not found or unable to delete
//        }
//        val fileToSend = File(filesDir, fileName)
//        fileHandler.sendFileToServer(fileToSend)
//        RemoteLogger().initialize(
//            Config(
//                "192.168.36.81", // your server IP, will be displayed in CMD after server is started
//                3000, // optional port
//                applicationContext.packageName // package name, required for proper LogCat filtering
//            )
//        )
//        val intent = Intent(this, RazorpayPayment::class.java)
//        startActivity(intent);
        // Permission is not granted, request it
        layout = layoutInflater.inflate(R.layout.popup_layout, null)

        // Initialize other properties
        interpretButton = layout.findViewById(R.id.interpretButton)
        registerReceiver(sessionMsg, IntentFilter("session_message"))
        registerReceiver(usageLog, IntentFilter("usageLog"))
        registerReceiver(missingAnimation, IntentFilter("missingAnimations"));
        val sharedPreferences0 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor0 = sharedPreferences0.edit()
        editor0.putBoolean("SMSservice", false)
        editor0.apply()

        deviceId= secureTokenManager.loadId().toString();
        userEmail=secureTokenManager.loadEmail().toString()
        token=secureTokenManager.loadToken().toString();
        val home_stopbutton:com.google.android.material.floatingactionbutton.FloatingActionButton=findViewById(R.id.home_myButton)
        val help_stopbutton:com.google.android.material.floatingactionbutton.FloatingActionButton=findViewById(R.id.help_myButton)
        val stopbutton:com.google.android.material.floatingactionbutton.FloatingActionButton=findViewById(R.id.myButton);
        val accessibilityStatusTextView: TextView = findViewById(R.id.access_permission)
        println("jsvdcghsvdvscghvshdvgcsh");
        var tv_Speech_to_text = findViewById<TextView>(R.id.webview_text);
        val webviewTitle:TextView=findViewById(R.id.title_webview_text);
        val checkBox = findViewById<CheckBox>(R.id.SMSCheckBox);
        val SMSPermission=(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED);
        if(SMSPermission) {
            checkBox.isChecked = true;
            editor0.putBoolean("SMSservice", true)
            editor0.apply()
        }
        else {
            checkBox.isChecked = false;
            editor0.putBoolean("SMSservice", false)
            editor0.apply()
        }
        checkBox.setOnClickListener{
            if(checkBox.isChecked)
            {
                println("checkeddddddddddddddddd");
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_SMS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this, PermissionsRequestActivity::class.java)
                    startActivityForResult(intent, PermissionsRequestActivity.READ_SMS_PERMISSION_REQUEST_CODE)
                    // Permission is not granted, request it
                } else {
                    // Permission is already granted, set up the CheckBox listener
                    setUpCheckBoxListener()
                }
            }
            else
            {
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("SMSservice", false)
                editor.apply()
            }
            println("clickedddddddddddddddddddd");
        }

        val handler0=Handler();

        stopbutton.setOnClickListener {
            if(stopbutton.visibility==View.VISIBLE) {
                stopbutton.visibility = View.GONE;
                help_stopbutton.visibility = View.GONE;
                home_stopbutton.visibility = View.GONE;
            }
            Bugfender.d("MainActivity","stopbutton");
            fileHandler.createAndWriteToFile("MainActiviy "+"stopbutton",fileName);
            println("Stop Button Clicked")
            val intent = Intent("stop_action")
            intent.putExtra("stop_message", "stop")
            sendBroadcast(intent)
            Bugfender.d("stopbutton","sendBroadcast");
            fileHandler.createAndWriteToFile("stopbutton "+"sendBroadcast",fileName);
        }
        home_stopbutton.setOnClickListener{
            if(home_stopbutton.visibility==View.VISIBLE) {
                stopbutton.visibility = View.GONE;
                help_stopbutton.visibility = View.GONE;
                home_stopbutton.visibility = View.GONE;
            }
            Bugfender.d("MainActivity","home_stopbutton");
            fileHandler.createAndWriteToFile("MainActivity "+"home_stopbutton",fileName);
            println("home_stopbutton");
            val intent = Intent("stop_action")
            intent.putExtra("stop_message", "stop")
            sendBroadcast(intent);
            Bugfender.d("home_stopbutton","sendBroadcast");
            fileHandler.createAndWriteToFile("home_stopbutton "+"sendBroadcast",fileName);
        }
        help_stopbutton.setOnClickListener {
            if(help_stopbutton.visibility==View.VISIBLE) {
                stopbutton.visibility = View.GONE;
                help_stopbutton.visibility = View.GONE;
                home_stopbutton.visibility = View.GONE;
            }
            Bugfender.d("MainActivity","help_stopbutton");
            fileHandler.createAndWriteToFile("MainActivity "+"help_stopbutton",fileName);
            println("help_stopbutton");
            val intent = Intent("stop_action")
            intent.putExtra("stop_message", "stop")
            sendBroadcast(intent)
            Bugfender.d("help_stopbutton","sendBroadcast");
            fileHandler.createAndWriteToFile("help_stopbutton "+"sendBroadcast",fileName);
        }
        fun disableAccessibilityService(context: Context, serviceClass: Class<out AccessibilityService>) {
            Bugfender.d("MainActivity","disableAccessibilityService");
            try {
                Bugfender.d("disableAccessibilityService","try");
                fileHandler.createAndWriteToFile("disableAccessibilityService "+"try",fileName);
                val componentName = ComponentName(context, serviceClass)
                Settings.Secure.putString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                        ?.replace(componentName.flattenToString(), "")
                )
                Settings.Secure.putString(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED,
                    "0"
                )
            } catch (e: Exception) {
                Bugfender.e("disableAccessibilityService",e.toString());
                fileHandler.createAndWriteToFile("disableAccessibilityService "+"e.toString()",fileName);
                e.printStackTrace()
            }
        }

        fun openDrawer() {
            Bugfender.d("MainActivity","openDrawer");
            fileHandler.createAndWriteToFile("MainActivity "+"openDrawer",fileName);

            val drawerLayout = findViewById<DrawerLayout>(R.id.my_drawer_layout)
            drawerLayout.openDrawer(GravityCompat.END)
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("238702597146-4f34ai7mlr0n81r85rn3ih0mpi4f79a2.apps.googleusercontent.com")
            .requestEmail()
            .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager



        val profilePicUriString = intent.getStringExtra("profilepic");
        var profilePicUri="";
        if(getProfilePicUri()!=null)
            profilePicUri = Uri.parse(getProfilePicUri()).toString();
        val imageViewProfilePicture = findViewById<ImageView>(R.id.profile_ic)
        if (profilePicUri != null) {
            Bugfender.d("MainActivity","profilePicUri");
            fileHandler.createAndWriteToFile("MainActivity "+"profilePicUri",fileName);

            Bugfender.d("profilePicUri",profilePicUri);
            println("profile pic uri-"+profilePicUri);
            Glide.with(this)
                .load(profilePicUri)
                .placeholder(R.drawable.ic_profile) // Placeholder image while loading
                .error(R.drawable.ic_profile) // Error image if loading fails
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageViewProfilePicture)
        }
        imageViewProfilePicture.setOnClickListener {
            Bugfender.d("MainActivity","imageViewProfilePicture");
            fileHandler.createAndWriteToFile("MainActivity "+"imageViewProfilePicture",fileName);

            openDrawer();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (accessibilityManager.isEnabled) {
                Bugfender.d("MainActivity","accessibilityManager.isEnabled");
                fileHandler.createAndWriteToFile("MainActivity "+"accessibilityManager.isEnabled",fileName);

                accessibilityStatusTextView.text = "Granted"
                accessibilityStatusTextView.setTextColor(Color.parseColor("#00FF00"))
            } else {
                Bugfender.d("MainActivity","NOTaccessibilityManager.isEnabled");
                fileHandler.createAndWriteToFile("MainActivity "+"NOTaccessibilityManager.isEnabled",fileName);
                accessibilityStatusTextView.text = "Not Granted"
                accessibilityStatusTextView.setTextColor(Color.parseColor("#FF0000"))
            }
        }

        val scanButton =findViewById<ImageView>(R.id.webview_scan_ic);

        galleryActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
            }
        }
        cameraActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Bugfender.d("Scan","Camera activity launcher");
            Bugfender.d("ScanResult",result.toString());
            Bugfender.d("ScanResult",result.resultCode.toString()+imageUri.toString());
            fileHandler.createAndWriteToFile("ScanResult "+result.toString(),fileName);

            if (result.resultCode == Activity.RESULT_OK) {
                Bugfender.d("ImageOK",imageUri.toString());
                fileHandler.createAndWriteToFile("ImageOK "+imageUri.toString(),fileName);

                imageUri?.let { recogniserImage(0, it) };
                // Handle the result here
                // The captured image is usually available via the 'imageUri' property
            }
            else
            {
                Bugfender.e("ScanError",result.toString());
                fileHandler.createAndWriteToFile("Error "+result.toString(),fileName);

            }
        }


        fun exampleCheckCameraPermission() {
            Bugfender.d("MainActivity","exampleCheckCameraPermission");
            fileHandler.createAndWriteToFile("MainActivity "+"exampleCheckCameraPermission",fileName);

            if (isCameraPermissionGranted()) {
                Bugfender.d("exampleCheckCameraPermission","isCameraPermissionGranted()");
                fileHandler.createAndWriteToFile("exampleCheckCameraPermission "+"isCameraPermissionGranted()",fileName);

                // Camera permission is already granted, proceed with camera-related operations
            } else {
                // Camera permission is not granted, request it
                Bugfender.d("exampleCheckCameraPermission","NOTisCameraPermissionGranted()");
                requestCameraPermission()
                fileHandler.createAndWriteToFile("exampleCheckCameraPermission "+"NOTisCameraPermissionGranted()",fileName);

            }
        }

        scanButton.setOnClickListener{

            Bugfender.d("MainActivity","scanButton");
            fileHandler.createAndWriteToFile("MainActivity "+"scanButton()",fileName);

            getData("https://trrain4-web.letstalksign.org/app_log?mode=scan_opened&language=english&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            webviewTitle.setText("Scanned Text:");
            val text_button: ImageView = findViewById(R.id.webview_text_ic)
            val text_title: TextView = findViewById(R.id.texttitle)
            text_title.setTextSize(15F)
            fun dpToPx(dp: Int): Int {
                val density = resources.displayMetrics.density
                return (dp * density).toInt()
            }
            text_button.layoutParams.height=dpToPx(35);
            text_button.layoutParams.width=dpToPx(35);

            val mic_button: ImageView = findViewById(R.id.webview_mic_ic)
            val mic_title: TextView = findViewById(R.id.speakTitle)
            mic_title.setTextSize(15F)
            mic_button.layoutParams.height=dpToPx(35) // Increase height by 10dp
            mic_button.layoutParams.width=dpToPx(35)

            val scan_button: ImageView = findViewById(R.id.webview_scan_ic)
            val scan_title: TextView = findViewById(R.id.scanTitle)
            scan_title.setTextSize(18F)
            scan_button.layoutParams.height=dpToPx(42)
            scan_button.layoutParams.width=dpToPx(42)
            tv_Speech_to_text.setText("Click the scan button above to scan text and initiate interpretation.")
            if(selectedLanguage!="English")
            {
                Bugfender.d("Scan","Not English")
                fileHandler.createAndWriteToFile("Scan "+"Not English",fileName);

                showLanguageNotSupportedDialog(this);
            }
            else
            {

                if (isCameraPermissionGranted()) {
                    Bugfender.d("Scan","camera permission granted");
                    fileHandler.createAndWriteToFile("Scan "+"camera permission granted",fileName);

                    PickImageCamera();
                }
                else {
                    Bugfender.d("Scan","camera permission Not granted");
                    fileHandler.createAndWriteToFile("Scan "+"camera permission Not granted",fileName);

                    requestCameraPermission()
                }
            }

        }




//        val interpretButton =findViewById<Button>(R.id.interpretButton);

        val permissionText=findViewById<TextView>(R.id.recentapps_text)
        val recentapps=findViewById<LinearLayout>(R.id.recentapps);


        val text1: TextView =findViewById(R.id.apptext0);
        val text2: TextView =findViewById(R.id.apptext1);
        val text3: TextView =findViewById(R.id.apptext2);
        val text4: TextView =findViewById(R.id.apptext3);
        val text5: TextView =findViewById(R.id.apptext4);
        val text6: TextView =findViewById(R.id.apptext5);


        val ic_1: ImageView =findViewById(R.id.app_ic0);
        val ic_2: ImageView =findViewById(R.id.app_ic1);
        val ic_3: ImageView =findViewById(R.id.app_ic2);
        val ic_4: ImageView =findViewById(R.id.app_ic3);
        val ic_5: ImageView =findViewById(R.id.app_ic4);
        val ic_6: ImageView =findViewById(R.id.app_ic5);

        //home
        val pm = packageManager
        val scrollview = findViewById<ScrollView>(R.id.scrollView)
        val listView = findViewById<ListView>(R.id.list)
        val searchview:SearchView =findViewById(R.id.searchView);
        val scale = resources.displayMetrics.density



        listView.setOnTouchListener(OnTouchListener { v, event ->
            Bugfender.d("MainActivity","listView.setOnTouchListener");
            Bugfender.d("listView.setOnTouchListener",event.toString());
            fileHandler.createAndWriteToFile("listView.setOnTouchListener "+event.toString(),fileName);

            scrollview.requestDisallowInterceptTouchEvent(true)
            val action = event.actionMasked
            when (action) {
                MotionEvent.ACTION_UP -> scrollview.requestDisallowInterceptTouchEvent(false)
            }
            false
        })

        var packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val currentPackageName = context.packageName // Replace 'context' with the appropriate context
        packages = packages.filter { it.packageName != currentPackageName }

        val nonSystemApppackagenames = mutableListOf<String>()
        val appnames = mutableListOf<String>()
        val images = mutableListOf<Icon>();
        val appinfos = mutableListOf<AppInfo>()
        var sortedAppInfos = mutableListOf<AppInfo>()

        for (packageInfo in packages) {

            val isChromeOrYouTube = packageInfo.packageName == "com.android.chrome" ||
                    packageInfo.packageName == "com.google.android.youtube"
            val currentpackage = packageInfo.packageName == "com.example.aiish";
            println("kjfvnjfnvjhfnhvhjbdvnjddfnn"+packageInfo.packageName)
            if(!currentpackage) {
                if ((packageInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || isChromeOrYouTube) {
                    // The app is not a system app
                    val appinfo = AppInfo(
                        packageInfo.loadLabel(packageManager).toString(),
                        packageInfo.packageName,
                        packageInfo.loadIcon(packageManager)
                    )
                    appinfos.add(appinfo);
                    appnames.add(packageInfo.loadLabel(packageManager).toString())
                    val appIcon: Drawable = packageInfo.loadIcon(packageManager)
                    nonSystemApppackagenames.add(packageInfo.packageName)
                }
            }
            else
            {
                println("kjfvnjfnvjhfnhvhjbdvnjddfnn");
                println(packageInfo);
                println(packageInfo.packageName);
            }
        }
        sortedAppInfos = appinfos.sortedBy { it.appName }.toMutableList()
        val adapter =  MyArrayAdapter(this,R.id.iteamlistview,sortedAppInfos);

        fun otherApps() {
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                Bugfender.d("listView.setOnItemClickListener",adapter.getItem(position).toString());
                val selectedAppInfo = adapter.getItem(position)
                if (selectedAppInfo != null) {
                    println("bjvhjbvjfhbbfhdvbhnjjn");
                    println(selectedAppInfo.packageName)
                    val selectedPackageName = selectedAppInfo.packageName
                    if (selectedPackageName != null) {
                        Bugfender.d("listView.setOnItemClickListener",selectedPackageName.toString());
                        fileHandler.createAndWriteToFile("listView.setOnTouchListener "+selectedPackageName.toString(),fileName);

                        val enabled = isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
                        if (!enabled) {
                            // Accessibility not granted
                            showAccessibilityPermissionDialog(selectedPackageName)
                        } else {
                            openApp(selectedPackageName)
                            val isFloatingWindowServiceRunning =
                                isServiceRunning(this, FloatingWindowGFG::class.java)
                            if (!isFloatingWindowServiceRunning) {
                                Bugfender.d("listView.setOnItemClickListener","floating window not running");
                                fileHandler.createAndWriteToFile("listView.setOnTouchListener "+"floating window not running",fileName);
                                println("floating window not running")
                                val intent = Intent(this, FloatingWindowGFG::class.java)
                                intent.putExtra(
                                    "message",
                                    "Hello Im Arya, Im here to interpret for you!"
                                )
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startService(intent)
                            }
                            else {
                                Bugfender.d("listView.setOnItemClickListener","floating window running");
                                fileHandler.createAndWriteToFile("listView.setOnTouchListener "+"floating window running",fileName);
                                println("Floating window service is running");
                                val intent = Intent("SendMessage")
                                intent.putExtra("SendMessage","Hello Im Arya, Im here to interpret for you!");
                                sendBroadcast(intent)
                                Bugfender.d("listView.setOnItemClickListener","sendBroadcast");
                                fileHandler.createAndWriteToFile("listView.setOnTouchListener "+"sendBroadcast",fileName);

//                                val intent0 = Intent("stop_action_overlay")
//                                intent0.putExtra("stop_message_overlay", "start")
//                                sendBroadcast(intent0)
                            }
                        }
                    }
                }
            }
        }
        otherApps()

        fun getAppName(packageName: String): String {
            val packageManager = packageManager
            return try {
                val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationLabel(applicationInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {


                // Handle exception or return the package name as a fallback
                packageName
            }
        }



        fun getAppIcon(packageName: String): Drawable? {
            Bugfender.e("MainActivity","getAppIcon");
            fileHandler.createAndWriteToFile("MainActivity "+"getAppIcon",fileName);
            return try {
                val packageManager = packageManager
                val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationIcon(applicationInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                Bugfender.e("getAppIcon",e.toString());
                fileHandler.createAndWriteToFile("getAppIcon "+e.toString(),fileName);
                println("error - getAppIcon");
                // Handle exception or return null as a fallback
                null
            }
        }

        fun recentappsPermission()
        {
            Bugfender.d("MainActivity","recentappsPermission");
            if (hasUsageAccessPermission()) {
                Bugfender.d("recentappsPermission","hasUsageAccessPermission");
                fileHandler.createAndWriteToFile("recentappsPermission "+"hasUsageAccessPermission",fileName);
                val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
                val currentTime = System.currentTimeMillis()
                val startTime = currentTime - 24 * 60 * 60 * 1000 // 24 hours ago
                val packman:PackageManager = getPackageManager();
                val usageStatsList = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
                )
                recentapps.visibility=View.VISIBLE;
                permissionText.visibility=View.GONE;
                println("packkkkkkkkkkkkkkagenameeeeeeeeeeeee");
                println(context.packageName)
                val currentPackageName = context.packageName // Replace 'context' with the appropriate context
                var nonSystemApps = usageStatsList.filter { usageStats ->
                    try {
                        // Get the PackageInfo for the app
                        val packageInfo = packageManager.getPackageInfo(usageStats.packageName, 0)

                        // Check if the app is Chrome or YouTube
                        val isChromeOrYouTube = usageStats.packageName == "com.android.chrome" ||
                                usageStats.packageName == "com.google.android.youtube"

                        // Check if the app is not a system app, and if it's not Chrome/YouTube or if it's Chrome/YouTube and not the current app
                        !((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        // Handle the case where package information is not found (app is uninstalled)
                        // You can log the exception or handle it based on your requirements
                        false
                    }
                }
                nonSystemApps = nonSystemApps.filter { it.packageName.toString() != currentPackageName.toString() }
                nonSystemApps = nonSystemApps.filter { it.packageName.toString() != "com.google.android.youtube" }

                text1.text = getAppName(nonSystemApps[0].packageName.toString());
                text2.text = getAppName(nonSystemApps[1].packageName.toString());
                text3.text = getAppName(nonSystemApps[2].packageName.toString());
                text4.text = getAppName(nonSystemApps[3].packageName.toString());
                text5.text = getAppName(nonSystemApps[4].packageName.toString());
                text6.text = getAppName(nonSystemApps[5].packageName.toString());

                text1.setOnClickListener {
                    Bugfender.d("MainActivity","text1");
                    fileHandler.createAndWriteToFile("MainActivity "+"text1",fileName);
                    val enabled = isAccessibilityServiceEnabled(
                        MyAccessibilityService::class.java
                    )
                    if(nonSystemApps[0].packageName.toString()=="com.google.android.youtube")
                    {
                        openApp(nonSystemApps[0].packageName.toString());
                    }
                    else if (!enabled) {
                        // Accessibility granted
                        showAccessibilityPermissionDialog(nonSystemApps[0].packageName.toString())
                    }
                    else
                        openApp(nonSystemApps[0].packageName.toString())
                }

                text2.setOnClickListener {
                    Bugfender.d("MainActivity","text2");
                    val enabled = isAccessibilityServiceEnabled(
                        MyAccessibilityService::class.java
                    )
                    if(nonSystemApps[1].packageName.toString()=="com.google.android.youtube")
                    {
                        openApp(nonSystemApps[1].packageName.toString())
                    }
                    else if (!enabled) {
                        // Accessibility granted
                        showAccessibilityPermissionDialog(nonSystemApps[1].packageName.toString())
                    }
                    else
                        openApp(nonSystemApps[1].packageName.toString())
                }
                text3.setOnClickListener {
                    Bugfender.d("MainActivity","text3");
                    val enabled = isAccessibilityServiceEnabled(
                        MyAccessibilityService::class.java
                    )
                    if(nonSystemApps[2].packageName.toString()=="com.google.android.youtube")
                    {
                        openApp(nonSystemApps[2].packageName.toString())
                    }
                    else if (!enabled) {
                        // Accessibility granted
                        showAccessibilityPermissionDialog(nonSystemApps[2].packageName.toString())
                    }
                    else
                        openApp(nonSystemApps[2].packageName.toString())
                }

                text4.setOnClickListener {
                    Bugfender.d("MainActivity","text4");
                    val enabled = isAccessibilityServiceEnabled(
                        MyAccessibilityService::class.java
                    )
                    if(nonSystemApps[3].packageName.toString()=="com.google.android.youtube")
                    {
                        openApp(nonSystemApps[3].packageName.toString())
                    }
                    else if (!enabled) {
                        // Accessibility granted
                        showAccessibilityPermissionDialog(nonSystemApps[3].packageName.toString())
                    }
                    else
                        openApp(nonSystemApps[3].packageName.toString())
                }
                text5.setOnClickListener {
                    Bugfender.d("MainActivity","text5");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!accessibilityManager.isEnabled) {
                            // Accessibility granted
                            showAccessibilityPermissionDialog(nonSystemApps[4].packageName.toString())
                        }
                    }
                    else
                        openApp(nonSystemApps[4].packageName.toString())
                }
                text6.setOnClickListener {
                    Bugfender.d("MainActivity","text6");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!accessibilityManager.isEnabled) {
                            // Accessibility granted
                            showAccessibilityPermissionDialog(nonSystemApps[5].packageName.toString())
                        }
                    }
                    else
                        openApp(nonSystemApps[5].packageName.toString())
                }

                ic_1.setImageDrawable(getAppIcon(nonSystemApps[0].packageName.toString()));
                ic_2.setImageDrawable(getAppIcon(nonSystemApps[1].packageName.toString()));
                ic_3.setImageDrawable(getAppIcon(nonSystemApps[2].packageName.toString()));
                ic_4.setImageDrawable(getAppIcon(nonSystemApps[3].packageName.toString()));
                ic_5.setImageDrawable(getAppIcon(nonSystemApps[4].packageName.toString()));
                ic_6.setImageDrawable(getAppIcon(nonSystemApps[5].packageName.toString()));


                for (usageStats in nonSystemApps) {
                    val packageName = usageStats.packageName
                    println(packageName);
                    // Display or store the package name as needed
                }
            }
            else
            {
                recentapps.visibility=View.GONE;
                permissionText.visibility=View.VISIBLE;
            }
        }
        val overlayStatusTextView: TextView = findViewById(R.id.overlay_permissionText)
        fun checkpermission() {
            Bugfender.d("MainActivity","checkpermission");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                // Overlay permission not granted
                overlayStatusTextView.text = "Not Granted"
                overlayStatusTextView.setTextColor(Color.parseColor("#FF0000"))

                // Open settings to grant overlay permission

            } else {
                // Overlay permission granted
                overlayStatusTextView.text = "Granted"
                overlayStatusTextView.setTextColor(Color.parseColor("#00FF00"))

            }
        }
        checkpermission();
        recentappsPermission();


        val handler = Handler()
        val receivedIntent = intent
        var sharedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)


        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val accessibilityServiceString = "$packageName/${MyAccessibilityService::class.java.canonicalName}"
        val isServiceEnabled = enabledServices?.contains(accessibilityServiceString) == true

        val MainserviceIntent = Intent(this, MyAccessibilityService::class.java)
        startService(MainserviceIntent);

        accessibilityService = MyAccessibilityService();


        if (sharedText != null) {
            getData("https://trrain4-web.letstalksign.org/app_log?mode=video_interpreted&video_url=$sharedText&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
            val intent4 = Intent(this@MainActivity, FloatingWindowGFG::class.java);
            intent4.putExtra("url", sharedText.toString());
            startService(intent4);
            finish();
        };


        fun checkUsageAccessPermission() {
            Bugfender.d("MainActivity","checkUsageAccessPermission");
            fileHandler.createAndWriteToFile("MainActivity "+"checkUsageAccessPermission",fileName);
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (alertDialog == null && (!hasUsageAccessPermission() || !hasOverlayPermission())) {
                        requestUsageAccessPermission();
//                        if(hasUsageAccessPermission() && hasOverlayPermission())
//                            showAllPermissionsGrantedDialog();
                    } else {
                        // If the dialog is open or permission is granted, delay the next check

                    }
//                    if(alertDialog!=null && hasUsageAccessPermission() && hasOverlayPermission())
//                        showAllPermissionsGrantedDialog();
                    handler.postDelayed(this, 100)
                }
            }, 0)

        }
        checkUsageAccessPermission()



        val dropdownbutton0: LinearLayout =findViewById(R.id.dropdown_button);
        val permisionText0: LinearLayout=findViewById(R.id.permission_description);


        val yt_usage_button:LinearLayout =findViewById(R.id.dropdownusage);
        val yt_description: LinearLayout=findViewById(R.id.Youtube_description);

        val wts_usage_button:LinearLayout=findViewById(R.id.dropdownusagewhatsapp);
        val wts_description:LinearLayout =findViewById(R.id.whatsapp_description);


        val helppagebutton:ImageView=findViewById(R.id.help_ic)
        val helpage: FrameLayout =findViewById(R.id.help_page);


        val homeButton:ImageView =findViewById(R.id.helpthome);
        val homepage:ScrollView=findViewById(R.id.home_page);

        val homeselection:View =findViewById(R.id.selectionhome);
        val helpselction:View=findViewById(R.id.selectionhelp);
        val micselection:View=findViewById(R.id.mic_page);
        val micbar:View=findViewById(R.id.selectionmic);

        val micButton:ImageView=findViewById(R.id.mic0_ic);
        val searchSelection:LinearLayout =findViewById(R.id.search_bar)
        val appbar:LinearLayout =findViewById(R.id.Appbar)

//        val settingsIc: ImageView = findViewById(R.id.settings_ic)
//
//        settingsIc.setOnClickListener {
//            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//            startActivity(intent)
//        }



        val videoView: VideoView = findViewById(R.id.yt_video)
        val whatsapp_video: VideoView = findViewById(R.id.whatsapp_video)

// Set the path of the video file (res/raw/sample.mp4)
        val path = "android.resource://" + packageName + "/" + R.raw.yt_video
        val pathw = "android.resource://" + packageName + "/" + R.raw.whatspp_video

// Set up a MediaController to control playback
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)

        val mediaController0 = MediaController(this)
        mediaController0.setAnchorView(whatsapp_video)



// Set the Uri of the video file
        val uri = Uri.parse(path)
        videoView.setVideoURI(uri)

        val uri0 = Uri.parse(pathw)
        whatsapp_video.setVideoURI(uri0)


// Set the MediaController for the VideoView
        videoView.setMediaController(mediaController)
        whatsapp_video.setMediaController(mediaController0)

        videoView.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.seekTo(0) // Seek to the beginning
            mediaPlayer.start() // Start the video again
        }

        whatsapp_video.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
        }



// Start playing the video
        videoView.start()
        whatsapp_video.start()






//        val overlayPermissionStatusTextView: TextView = findViewById(R.id.overlay_permissionText)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            // Overlay permission not granted
//            overlayPermissionStatusTextView.text = "Not Granted"
//            overlayPermissionStatusTextView.setTextColor(Color.parseColor("#00FF00"))
//
//            // Open settings to grant overlay permission
//
//        } else {
//            // Overlay permission granted
//            overlayPermissionStatusTextView.text = "Granted"
//            overlayPermissionStatusTextView.setTextColor(Color.parseColor("#FF0000"))
//        }



//
//        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//        startActivity(intent)


        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - 24 * 60 * 60 * 1000 // 24 hours ago
        val packman:PackageManager = getPackageManager();

        val pks: List<ApplicationInfo> =
            packman.getInstalledApplications(PackageManager.GET_META_DATA)

//        for (packageInfo in pks) {
//            println( "Installed package :" + packageInfo.packageName)
//            println("Source dir : " + packageInfo.sourceDir)
//            println("Launch Activity :" + packman.getLaunchIntentForPackage(packageInfo.packageName))
//        }

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
        )
//        val dictionary = PlainDictionary(this, "bad_words_en_us.txt")
//        val profanityFilter = AndroidProfanityFilter(dictionary = dictionary)
//        val censoredText = profanityFilter.censor("originalText")
        val drawerLayout = findViewById<DrawerLayout>(R.id.my_drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            Bugfender.d("MainActivity","navigationView.setNavigationItemSelectedListener");
            fileHandler.createAndWriteToFile("MainActivity "+"navigationView.setNavigationItemSelectedListener",fileName);
            when (menuItem.itemId) {
                R.id.nav_settings -> {
                    openAccessibilitySettings();
                    println("sssssssssseeeeeeeeeeeeeeeeetttttttttttttttttt");
                    // Handle settings click
                    // Add your logic here
                    true
                }
                R.id.nav_logout -> {
                    showLogoutAlert();

                    // Handle logout click
                    // Add your logic here
                    true
                }
                else -> false
            }
        }

        webView = findViewById(R.id.micView)

        // Enable JavaScript in the WebView
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Load the URL
        val userurl = "https://letstalksign.org/extension/page1.html"
        webView.webViewClient = MyWebViewClient()

        webView.settings.javaScriptEnabled = true
        webView.settings.userAgentString = "useragentstring"
        webView.getSettings().setSupportMultipleWindows(true);

        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        webView.settings.domStorageEnabled = true
//
        webView.loadUrl(userurl)
//        webView.loadUrl("file:///android_asset/webview.html");


        webView.clearCache(true) // Clears the cache, including disk and memory caches.
        webView.clearFormData()  // Clears any stored form data in the WebView.
        webView.clearHistory()
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("WebView Console.log",consoleMessage.sourceId()+" "+consoleMessage.lineNumber()+" "+consoleMessage.message());
                return true
            }
        }
        var msg=tv_Speech_to_text!!.text.toString();
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidInterface")


        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        val languages = arrayOf("English", "Tamil", "Telugu", "Kannada", "Hindi", "Gujarati", "Marathi", "Bengali")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val sadapter = ArrayAdapter(this, R.layout.spinner_iteam, languages);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        languageSpinner.adapter = sadapter

        // Set up the OnItemSelectedListener
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Get the selected item from the spinner
                selectedLanguage = languages[position]

                val englishGermanTranslator = Translation.getClient(options)
                if(selectedLanguage!="Choose language")
                    Toast.makeText(this@MainActivity, "Selected Language: $selectedLanguage", Toast.LENGTH_SHORT).show();

                if(selectedLanguage=="English") {
                    language = "en-us";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Tamil") {
                    language = "ta-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.TAMIL)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Telugu") {
                    language = "te-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.TELUGU)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Kannada") {
                    language = "kn-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.KANNADA)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Malayalam") {
                    language = "ml-IN";
//                    options = TranslatorOptions.Builder()
//                        .setSourceLanguage(TranslateLanguage.M)
//                        .setTargetLanguage(TranslateLanguage.ENGLISH)
//                        .build()
                }
                else if(selectedLanguage=="Hindi") {
                    language = "hi-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.HINDI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Gujarati") {
                    language = "gu-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.GUJARATI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Marathi") {
                    language = "mr-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.MARATHI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }
                else if(selectedLanguage=="Bengali") {
                    language = "bn-IN";
                    options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.BENGALI)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                }

                this@MainActivity.englishGermanTranslator = Translation.getClient(options)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle the case where nothing is selected (optional)
            }
        }


        val iv_mic: ImageView = findViewById<ImageView>(R.id.webview_mic_ic)
        val text_button :ImageView=findViewById(R.id.webview_text_ic);
        val text_title:TextView=findViewById(R.id.texttitle);
        tv_Speech_to_text = findViewById<TextView>(R.id.webview_text);
        iv_mic.layoutParams.width=(35 * scale + 0.5f).toInt();
        text_button.layoutParams.width=(35 * scale + 0.5f).toInt();
        scanButton.layoutParams.width=(35 * scale + 0.5f).toInt();


        text_button.setOnClickListener {
            Bugfender.d("MainActivity", "text_button")
            fileHandler.createAndWriteToFile("MainActivity "+"text_button",fileName);
            getData("https://trrain4-web.letstalksign.org/app_log?mode=text_opened&language=$selectedLanguage&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token");
            webviewTitle.text = "Entered Text:";
            val text_button: ImageView = findViewById(R.id.webview_text_ic)
            val text_title: TextView = findViewById(R.id.texttitle)
            text_title.setTextSize(18F)
            fun dpToPx(dp: Int): Int {
                val density = resources.displayMetrics.density
                return (dp * density).toInt()
            }
            text_button.layoutParams.height=dpToPx(42);
            text_button.layoutParams.width=dpToPx(42);

            val mic_button: ImageView = findViewById(R.id.webview_mic_ic)
            val mic_title: TextView = findViewById(R.id.speakTitle)
            mic_title.setTextSize(15F)
            mic_button.layoutParams.height=dpToPx(35) // Increase height by 10dp
            mic_button.layoutParams.width=dpToPx(35)

            val scan_button: ImageView = findViewById(R.id.webview_scan_ic)
            val scan_title: TextView = findViewById(R.id.scanTitle)
            scan_title.setTextSize(15F)
            scan_button.layoutParams.height=dpToPx(35)
            scan_button.layoutParams.width=dpToPx(35);

            tv_Speech_to_text.setText("Click the text button above to type text and initiate interpretation.")
            showPopupWithEditText("","Text to Interpret")
        }
        iv_mic?.let { micButton ->
            micButton.setOnClickListener(View.OnClickListener {
                getData("https://trrain4-web.letstalksign.org/app_log?mode=audio_opened&language=$selectedLanguage&customer_id=10009&device_id=$deviceId&gmail_id=$userEmail&token=$token")
                Bugfender.d("MainActivity","micButton");
                fileHandler.createAndWriteToFile("MainActivity "+"micButton",fileName);
                val text_button: ImageView = findViewById(R.id.webview_text_ic)
                val text_title: TextView = findViewById(R.id.texttitle)
                text_title.setTextSize(15F)
                fun dpToPx(dp: Int): Int {
                    val density = resources.displayMetrics.density
                    return (dp * density).toInt()
                }
                text_button.layoutParams.height=dpToPx(35);
                text_button.layoutParams.width=dpToPx(35);

                val mic_button: ImageView = findViewById(R.id.webview_mic_ic)
                val mic_title: TextView = findViewById(R.id.speakTitle)
                mic_title.setTextSize(18F)
                mic_button.layoutParams.height=dpToPx(42) // Increase height by 10dp
                mic_button.layoutParams.width=dpToPx(42)

                val scan_button: ImageView = findViewById(R.id.webview_scan_ic)
                val scan_title: TextView = findViewById(R.id.scanTitle)
                scan_title.setTextSize(15F)
                scan_button.layoutParams.height=dpToPx(35)
                scan_button.layoutParams.width=dpToPx(35);
                webviewTitle.setText("Spoken Text:")
                tv_Speech_to_text.setText("Click the mic button above perform speach to sign interpretation.")
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)

                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
                } catch (e: Exception) {
                    println(e.toString())
                }

            })
        }
















        helppagebutton.setOnClickListener{
            Bugfender.d("MainActivity","helppagebutton");
            fileHandler.createAndWriteToFile("MainActivity "+"helppagebutton",fileName);
            if(helpage.visibility==View.GONE)
            {
                homeButton.layoutParams.width=(25 * scale + 0.5f).toInt();
                homeButton.setImageResource(R.drawable.home_page_bw);
                micButton.layoutParams.width=(25 * scale + 0.5f).toInt();
                micButton.setColorFilter(Color.parseColor("#808080"))
                helppagebutton.layoutParams.width=(35 * scale + 0.5f).toInt();
                helppagebutton.setImageResource(R.drawable.help);
                homepage.visibility=View.GONE;
                helpage.visibility=View.VISIBLE;
                helpselction.visibility=View.GONE;
                homeselection.visibility=View.GONE;
                micselection.visibility=View.GONE;
                micbar.visibility=View.GONE;
                checkpermission();
                val appbar:TextView=findViewById(R.id.appbar_title)
                appbar.setText("Help Page");
            }
        }

        micButton.setOnClickListener{
            Bugfender.d("MainActivity","micButton");
            fileHandler.createAndWriteToFile("MainActivity "+"micButton",fileName);
            if(micselection.visibility==View.GONE)
            {
                homeButton.layoutParams.width=(25 * scale + 0.5f).toInt();
                homeButton.setImageResource(R.drawable.home_page_bw)
                micButton.layoutParams.width=(35 * scale + 0.5f).toInt();
                micButton.setColorFilter(Color.parseColor("#FF4D00"));
                helppagebutton.layoutParams.width=(25 * scale + 0.5f).toInt();
                helppagebutton.setImageResource(R.drawable.help_bw);

                homepage.visibility=View.GONE;
                helpage.visibility=View.GONE;
                helpselction.visibility=View.GONE;
                homeselection.visibility=View.GONE;
                micselection.visibility=View.VISIBLE;
                micbar.visibility=View.GONE;
                val appbar:TextView=findViewById(R.id.appbar_title)
                appbar.setText("Interpret Apps");
            }
        }


        homeButton.setOnClickListener{
            Bugfender.d("MainActivity","homeButton");
            fileHandler.createAndWriteToFile("MainActivity "+"homeButton",fileName);
            if(homepage.visibility==View.GONE)
            {
                homeButton.layoutParams.width=(35 * scale + 0.5f).toInt();
                homeButton.setImageResource(R.drawable.small_logo);
                micButton.layoutParams.width=(25 * scale + 0.5f).toInt();
                micButton.setColorFilter(Color.parseColor("#808080"));
                helppagebutton.layoutParams.width=(25 * scale + 0.5f).toInt();
                helppagebutton.setImageResource(R.drawable.help_bw);
                helpage.visibility=View.GONE;
                homepage.visibility=View.VISIBLE;
                helpselction.visibility=View.GONE;
                homeselection.visibility=View.GONE;
                micselection.visibility=View.GONE;
                micbar.visibility=View.GONE;
                recentappsPermission();
                val appbar:TextView=findViewById(R.id.appbar_title)
                appbar.setText("Interpret Text/Voice/Scan");
            }
        }


        wts_usage_button.setOnClickListener{
            Bugfender.d("MainActivity","wts_usage_button");
            fileHandler.createAndWriteToFile("MainActivity "+"wts_usage_button",fileName);
            if(wts_description.visibility==View.VISIBLE) {
                wts_description.visibility = View.GONE;
                whatsapp_video.pause()
            }
            else {
                wts_description.visibility = View.VISIBLE
                permisionText0.visibility = View.GONE;
                yt_description.visibility=View.GONE
            }
        }
        yt_usage_button.setOnClickListener{
            Bugfender.d("MainActivity","yt_usage_button");
            fileHandler.createAndWriteToFile("MainActivity "+"yt_usage_button",fileName);
            if(yt_description.visibility==View.VISIBLE) {

                yt_description.visibility = View.GONE;
                videoView.pause()
            }
            else {
                yt_description.visibility = View.VISIBLE;
                permisionText0.visibility = View.GONE;
                wts_description.visibility=View.GONE
            }
        }

        dropdownbutton0.setOnClickListener{
            Bugfender.d("MainActivity","dropdownbutton0");
            fileHandler.createAndWriteToFile("MainActivity "+"dropdownbutton0",fileName);
            if(permisionText0.visibility==View.VISIBLE)
                permisionText0.visibility=View.GONE;
            else {
                permisionText0.visibility = View.VISIBLE;
                yt_description.visibility=View.GONE
                wts_description.visibility=View.GONE
            }
        }


        // Set OnClickListener for the ImageView



        //home page
        fun isTouchInsideView(event: MotionEvent, view: View): Boolean {
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val x = event.rawX
            val y = event.rawY
            return x > location[0] && x < location[0] + view.width && y > location[1] && y < location[1] + view.height
        }



        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                Bugfender.d("MainActivity","searchview");
                fileHandler.createAndWriteToFile("MainActivity "+"searchview",fileName);
                if (appnames.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    Toast.makeText(this@MainActivity, "No Apps found..", Toast.LENGTH_LONG)
                        .show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                var tv_Speech_to_text = findViewById<TextView>(R.id.webview_text);
                tv_Speech_to_text!!.visibility=View.VISIBLE;
                if (selectedLanguage != "English") {
                    fileHandler.createAndWriteToFile(
                        "onActivityResult() " + "selectedLanguage!=\"English\";",
                        fileName
                    )
                    val progressDialog = ProgressDialog(this@MainActivity)
                    progressDialog.setMessage("Translating...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    token = secureTokenManager.loadToken().toString();
                    deviceId = secureTokenManager.loadId().toString();
                    userEmail = secureTokenManager.loadEmail().toString()


                    var params: Map<String, String> = mapOf();
                    if(selectedLanguage=="Hindi")
                    {
                        params = mapOf(
                            "token" to token.toString(),
                            "maxWordCount" to "100",
                            "fromLang" to "Hindi",
                            "toLang" to "english",
                            "textToTranslate" to " " + Objects.requireNonNull(result)?.get(0),
                            "customer_id" to "10009",
                            "device_id" to deviceId,
                            "gmail_id" to userEmail
                        )
                    }
                    else if(selectedLanguage=="Kannada")
                    {
                        params = mapOf(
                            "token" to token.toString(),
                            "maxWordCount" to "100",
                            "fromLang" to "Kannada",
                            "toLang" to "english",
                            "textToTranslate" to " " + Objects.requireNonNull(result)?.get(0),
                            "customer_id" to "10009",
                            "device_id" to deviceId,
                            "gmail_id" to userEmail
                        )
                    }
                    else if(selectedLanguage=="Tamil")
                    {
                        params = mapOf(
                            "token" to token.toString(),
                            "maxWordCount" to "100",
                            "fromLang" to "Tamil",
                            "toLang" to "english",
                            "textToTranslate" to " " + Objects.requireNonNull(result)?.get(0),
                            "customer_id" to "10009",
                            "device_id" to deviceId,
                            "gmail_id" to userEmail
                        )
                    }
                    else if(selectedLanguage=="Telugu")
                    {
                        params = mapOf(
                            "token" to token.toString(),
                            "maxWordCount" to "100",
                            "fromLang" to "Telugu",
                            "toLang" to "english",
                            "textToTranslate" to " " + Objects.requireNonNull(result)?.get(0),
                            "customer_id" to "10009",
                            "device_id" to deviceId,
                            "gmail_id" to userEmail
                        )
                    }
                    else if(selectedLanguage=="Gujarati")
                    {
                        params = mapOf(
                            "token" to token.toString(),
                            "maxWordCount" to "100",
                            "fromLang" to "Gujarati",
                            "toLang" to "english",
                            "textToTranslate" to " " + Objects.requireNonNull(result)?.get(0),
                            "customer_id" to "10009",
                            "device_id" to deviceId,
                            "gmail_id" to userEmail
                        )
                    }
                    else if(selectedLanguage=="Marathi")
                    {
                        params = mapOf(
                            "token" to token.toString(),
                            "maxWordCount" to "100",
                            "fromLang" to "Marathi",
                            "toLang" to "english",
                            "textToTranslate" to " " + Objects.requireNonNull(result)?.get(0),
                            "customer_id" to "10009",
                            "device_id" to deviceId,
                            "gmail_id" to userEmail
                        )
                    }
                    else if(selectedLanguage=="Gujarati")
                    {
                        params = mapOf(
                            "token" to token.toString(),
                            "maxWordCount" to "100",
                            "fromLang" to "Bengali",
                            "toLang" to "english",
                            "textToTranslate" to " " + Objects.requireNonNull(result)?.get(0),
                            "customer_id" to "10009",
                            "device_id" to deviceId,
                            "gmail_id" to userEmail
                        )
                    }

                    // Make the translation request
                    fileHandler.createAndWriteToFile(
                        "onActivityResult() selectedLanguage!=\"English\\ " + params.toString(),
                        fileName
                    )
                    //     sendTranslationRequest(this," " + Objects.requireNonNull(result)?.get(0),params);
                    sendGPTTranslationRequest(
                        this,
                        " " + Objects.requireNonNull(result)?.get(0),
                        params
                    ) { translatedText ->
                        // Dismiss the progress dialog
                        progressDialog.dismiss()

                        if (translatedText == "Translation failed" || translatedText == "Unexpected error please check your internet connection and try again!") {
                            Toast.makeText(this@MainActivity, translatedText, Toast.LENGTH_LONG)
                                .show()
                        } else {
                            if (translatedText != null) {
                                fileHandler.createAndWriteToFile(
                                    "onActivityResult() selectedLanguage!=\"English\\ " + translatedText,
                                    fileName
                                )
                                showPopupWithEditText(translatedText, "Recognised Text")
                            }
                        }
                    }
                    progressDialog.dismiss();

                    println("hjbdsvhfdsbvyfbvgyfbvdfbbfyggdfnjnfj uuhujnjn")
                } else {
                    fileHandler.createAndWriteToFile(
                        "onActivityResult() else " + Objects.requireNonNull(
                            result
                        )?.get(0), fileName
                    )
                    showPopupWithEditText(
                        " " + Objects.requireNonNull(result)?.get(0),
                        "Recognised Text"
                    )
                }
            }
        }
    }
}