package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Choreographer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KtxLoader
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.system.exitProcess


class Alertbox : Service() {
    private var floatView: ViewGroup? = null
    private var LAYOUT_TYPE = 0
    private var floatWindowLayoutParam: WindowManager.LayoutParams? = null
    private var windowManager: WindowManager? = null
    private var yeszeBtn: Button? = null
    private var NoBtn: Button?=null
    private var closeApplicationBtn: ImageButton? = null
    private var readingTextView: TextView? = null
    lateinit var sharedPreferences:SharedPreferences;

    companion object {
        init { Utils.init() }
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        System.out.println("im iNnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatView = inflater.inflate(R.layout.dialougebox, null) as ViewGroup

//        myTextView = floatView!!.findViewById(R.id.text30)
        yeszeBtn = floatView!!.findViewById(R.id.button_yes)


//
        yeszeBtn?.setOnClickListener(View.OnClickListener {
//            val intent0 = Intent(this, FloatingWindowGFG::class.java);
//            startService(intent0);
            val editor = sharedPreferences.edit()
            editor.putBoolean("youtube",false)
            editor.apply()
            stopService(Intent(this@Alertbox, Alertbox::class.java)) // Stop the service
            // Remove the view
            windowManager?.removeView(floatView)
        })


        LAYOUT_TYPE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_TOAST
        }
        floatWindowLayoutParam = WindowManager.LayoutParams(
            (width * 0.55f).toInt(), (height * 0.58f).toInt(),
            LAYOUT_TYPE,
            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            PixelFormat.OPAQUE
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParam!!.gravity = Gravity.CENTER
        floatWindowLayoutParam!!.x = 0
        floatWindowLayoutParam!!.y = 0
        windowManager!!.addView(floatView, floatWindowLayoutParam)

        floatView!!.setOnTouchListener(object : OnTouchListener {
            val floatWindowLayoutUpdateParam: WindowManager.LayoutParams =
                floatWindowLayoutParam as WindowManager.LayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                System.out.println("xcvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = floatWindowLayoutUpdateParam.x.toDouble()
                        y = floatWindowLayoutUpdateParam.y.toDouble()
                        px = event.rawX.toDouble()
                        py = event.rawY.toDouble()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        floatWindowLayoutUpdateParam.x = (x + event.rawX - px).toInt()
                        floatWindowLayoutUpdateParam.y = (y + event.rawY - py).toInt()
                        windowManager!!.updateViewLayout(floatView, floatWindowLayoutUpdateParam)
                    }
                }
                return false
            }
        })
    }

    override fun onBind(intent: Intent): IBinder? {
    //    choreographer.postFrameCallback(frameCallback)
        return null
    }

    private fun finish() {
        System.exit(0)
    }


    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when(level)
        {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL->
            {
                //      bitmap.recycle()
                System.gc()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()


        stopSelf()
        //modelViewer.release()
    }

}

