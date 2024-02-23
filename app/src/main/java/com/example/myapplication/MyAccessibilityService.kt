package com.example.myapplication

//import android.content.BroadcastReceiver
import java.util.regex.Pattern
import HighlightOverlay
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData


class MyAccessibilityService<AccessibilityNodeInfo> : AccessibilityService() {
//    private var broadcastReceiver: BroadcastReceiver? = null
    private val capturedTextLiveData = MutableLiveData<String>()
    private var highlightOverlay: HighlightOverlay? = null
    private var youtube=false;
    lateinit var sharedPreferences:SharedPreferences;
    lateinit var webIntent: Intent
    companion object {
        const val ACTION_CUSTOM_BROADCAST = "com.example.myapplication.ACTION_CUSTOM_BROADCAST"
    }

//    var mysms: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(arg0: Context, arg1: Intent) {
//            val sms = arg1.extras!!.getString("stop_message")
//
//        }
//    }
    val mysms = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sms = intent.extras?.getString("stop_message")
            println("sssssssssssssssstttttttttttttttttttttttooooooooooooooooooooopppppppppppp");
            if(sms=="stop")
            {
                disableSelf();
            }
            if(sms=="stop_and_exit")
            {
                disableSelf();
                System.exit(0);
            }
        }
    }
    var caption="";
//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            println("xxxxxxxxxxxxxxxxsbbbjjjjjjjjjjjjjjjjjjjjjjjjjjjjcdddd")
//            if (intent?.action == ACTION_CUSTOM_BROADCAST) {
//                // Handle the broadcast message received from the main activity
//            }
//        }
//    }
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)

        for (service in runningServices) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
      //  registerReceiver(mysms, new );
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        var serviceStarted = sharedPreferences.getBoolean("serviceStarted", false)
        val editor = sharedPreferences.edit()
        super.onCreate()
        val filter = IntentFilter(ACTION_CUSTOM_BROADCAST)
        registerReceiver(mysms, IntentFilter("stop_action"))
        highlightOverlay = HighlightOverlay(this)
    }
    override fun onInterrupt() {}
    override fun onServiceConnected() {
        super.onServiceConnected()
        println("Accessibility was connected!")
//        val scheduleTaskExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
//        scheduleTaskExecutor.scheduleAtFixedRate({ serviceChecker() }, 0, 5, TimeUnit.SECONDS)
    }
    override fun onDestroy() {
        unregisterReceiver(mysms);
        super.onDestroy()
        // Unregister the BroadcastReceiver in the onDestroy method

    }

    private fun serviceChecker() {
//        if (!isActivityRunning(MainActivity::class.java)!!) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                disableSelf()
      //      }
    //    }
    }


    protected fun isActivityRunning(activityClass: Class<*>): Boolean? {
        val activityManager = baseContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)
        for (task in tasks) {
            if (activityClass.canonicalName.equals(
                    task.baseActivity!!.className,
                    ignoreCase = true
                )
            ) return true
        }
        return false
    }

        @SuppressLint("SuspiciousIndentation")
        override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventType = event?.eventType

        val text = event?.text;

        println(text.toString());
        println("des${event?.contentDescription}")

        youtube = sharedPreferences.getBoolean("youtube",false)
        println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
        println(text.toString());
        println(event?.packageName);
        println(text.toString().length);
        println(youtube)

        fun countWords(text:String): Int {
            return text.trim().split("\\s+").size;
        }

        val isSystemApp = event?.packageName?.startsWith("com.android") == true || event?.packageName?.startsWith("com.google.android") == true
        val isSystemUi = (event?.packageName?.equals("com.android.systemui") == true || event?.packageName?.contains("launcher") == true || event?.packageName?.equals("com.example.myapplication") == true)
        val packageName = event?.packageName.toString()
        val textLength = text?.toString()?.length ?: 0
        println("heeeeeeeeeeellllkmdmckm");
        println(text.toString());
        println(packageName.toString());

//        if (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
//            println("he")
//            val text = event.text
//            val isFloatingWindowServiceRunning = isServiceRunning(this, FloatingWindowGFG::class.java)
//            // Check if the text is null or empty
//            if (text.isNullOrEmpty() && !isFloatingWindowServiceRunning) {
//                val intent = Intent(this, FloatingWindowGFG::class.java)
//                intent.putExtra("message", "No text is detected,May be the app is not compatible with the accessibility! try again.");
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startService(intent)
//            } else {
//                // Process the event or text as needed
//                println("mmmmmmmmmmmmmmmmdcvdghvhcdvhgv");
//                println(text);
//                val intent = Intent("SendMessage")
//                intent.putExtra("SendMessage","No text is detected,May be the app is not compatible with the accessibility! try again.");
//                sendBroadcast(intent)
//            }54
//        }

//            (packageName == "com.android.chrome" || packageName == "com.whatsapp" || packageName == "com.linkedin.android" || packageName == "com.twitter.android" || packageName == "com.facebook.katana" || packageName == "com.instagram.android")) ||
//            (textLength > 50 && packageName == "com.whatsapp")
        if(isSystemUi && textLength > 2)
        {
            println("SYyyyyyyyyyyyyyyyystsgsfgsgsgcsfxfccf");
            println(text);
            println(event);
            println(event?.contentDescription);
        }
        if ((textLength > 10 &&  !isSystemUi) && packageName!="com.example.myapplication" && text.toString()!="[page of]" && text.toString()!="[Recent apps]" && text.toString()!="[Navigate up]" && !((text.toString().startsWith("[page", ignoreCase = true)) && countWords(text.toString())<=4) && (text.toString()!="[LetsTalkSign]" || text.toString()!="[Let'sTalkSign]"))
        {
            println(text);
            println(packageName)
            val isFloatingWindowServiceRunning = isServiceRunning(this, FloatingWindowGFG::class.java)
            if (!isFloatingWindowServiceRunning) {
                println("heeeeeeeeeeeeeeeelooooooooooodnsj");
                println(text);
                val intent = Intent(this, FloatingWindowGFG::class.java)
                intent.putExtra("message", text.toString());
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startService(intent)
                println(text);
            }
            else{
                println("mmmmmmmmmmmmmmmmdcvdghvhcdvhgv "+text);
                println(text);
                println(event);
                println(event?.contentDescription);
                val intent = Intent("SendMessage")
                intent.putExtra("SendMessage",text.toString());
                sendBroadcast(intent)
            }
        }
        //println(gettext(event?.source));
//        if (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
//            // Call the HighlightOverlay to show the overlay on the clicked view
//            val clickedView = event.source as View?
//            applyGreenBorder(clickedView);
//            println("clickkkkkkkkkkkkkkkkkkkkkkkkkkkkked");
//
//            // Apply green border to the clicked view
//
//            // Apply green border to the clicked view
//
//
//            // Other handling logic
//        }

//        broadcastReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                val readingText = intent.getStringExtra("READING_TEXT")
//                System.out.println("cccccccccccccccccccccccccc");
//                System.out.println(readingText);
//                // Process the reading text here
//            }
//        }
//        val intentFilter = IntentFilter("READING_TEXT")
//        registerReceiver(broadcastReceiver, intentFilter);
//        when (eventType) {
//            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
//                //System.out.println("testing");
//                val text = event.text?.joinToString("\n") { it.toString() }
//                //System.out.println(text);
//               // capturedTextLiveData.postValue(text)
//                // Update UI or perform actions based on the captured text
//            }
//            // Handle other evvent types...
//        }
    }


    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action = event.action
        val keyCode = event.keyCode
        // the service listens for both pressing and releasing the key
        // so the below code executes twice, i.e. you would encounter two Toasts
        // in order to avoid this, we wrap the code inside an if statement
        // which executes only when the key is released
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d("Check", "KeyUp")
                Toast.makeText(this, "KeyUp", Toast.LENGTH_SHORT).show()
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d("Check", "KeyDown")
                Toast.makeText(this, "KeyDown", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onKeyEvent(event)
    }
}