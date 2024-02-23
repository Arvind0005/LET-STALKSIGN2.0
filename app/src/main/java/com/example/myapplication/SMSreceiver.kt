package com.example.myapplication

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.telephony.SmsMessage
import androidx.appcompat.app.AppCompatActivity
import com.bugfender.sdk.Bugfender


class SmsListener :BroadcastReceiver() {
    private val preferences: SharedPreferences? = null
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

    override fun onReceive(context: Context, intent: Intent) {
        println("recievvvgvgvgvcgvgvgvgvcgvgvdcddddd");
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras //---get the SMS message passed in---
            var msgs: Array<SmsMessage?>? = null
            var msg_from: String
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    val pdus = bundle["pdus"] as Array<Any>?
                    msgs = arrayOfNulls<SmsMessage>(pdus!!.size)
                    var msgBody="";
                    for (i in msgs.indices) {
                        msgs!![i] = SmsMessage.createFromPdu(pdus!![i] as ByteArray)
                        msg_from = msgs[i]!!.getOriginatingAddress().toString()
                        msgBody= msgs[i]!!.getMessageBody()
                        println("njfjhcjhdsjcdbsjbcjhbsnjvfncbhvchdg");
                        println(msgBody);
                    }
                    val isFloatingWindowServiceRunning =
                        isServiceRunning(context, FloatingWindowGFG::class.java)
                    if (!isFloatingWindowServiceRunning) {
                        Bugfender.d("listView.setOnItemClickListener","floating window not running");
                        println("floating window not running")
                        val intent = Intent(context, FloatingWindowGFG::class.java)
                        intent.putExtra(
                            "message",
                            msgBody.toString()
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startService(intent)
                    }
                    else {
                        Bugfender.d("listView.setOnItemClickListener","floating window running");
                        // Process the event or text as needed
                        println("Floating window service is running");
                        val intent = Intent("SendMessage")
                        intent.putExtra("SendMessage",msgBody.toString());
                        context.sendBroadcast(intent)
                        Bugfender.d("listView.setOnItemClickListener","sendBroadcast");
//                                val intent0 = Intent("stop_action_overlay")
//                                intent0.putExtra("stop_message_overlay", "start")
//                                sendBroadcast(intent0)
                    }
                } catch (e: Exception) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}