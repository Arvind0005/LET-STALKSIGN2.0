package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsRequestActivity : Activity() {

    companion object {
        const val READ_SMS_PERMISSION_REQUEST_CODE = 123
        const val READ_SMS_PERMISSION_RESULT_ACTION = "read_sms_permission_result_action"
        const val EXTRA_READ_SMS_PERMISSION_GRANTED = "extra_read_sms_permission_granted"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the READ_SMS permission is already granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendResultBack(true)
        } else {
            // Request the READ_SMS permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_SMS),
                READ_SMS_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun sendResultBack(permissionGranted: Boolean) {
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_READ_SMS_PERMISSION_GRANTED, permissionGranted)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_SMS_PERMISSION_REQUEST_CODE) {
            val readSmsPermissionGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if(readSmsPermissionGranted)
            {
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("SMSservice", true)
                editor.apply()
            }
            sendResultBack(readSmsPermissionGranted)
        }
    }
}