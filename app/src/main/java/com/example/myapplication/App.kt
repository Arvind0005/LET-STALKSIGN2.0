package com.example.myapplication

import android.app.Application
import com.bugfender.android.BuildConfig
import com.bugfender.sdk.Bugfender
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Bugfender.init(this, "B69vFgtbsIQhskcfCmPz4n17GHNN3MVO", BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
        Bugfender.enableUIEventLogging(this)
        Bugfender.enableLogcatLogging()
    }
}