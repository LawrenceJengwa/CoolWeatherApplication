package com.lawrence.coolweatherapplication.dagger

import android.app.Application
import com.lawrence.coolweatherapplication.MainActivity
import dagger.Component

// Definition of the Application graph
@Component
interface ApplicationComponent {
    fun inject(activity: MainActivity)
}

// appComponent lives in the Application class to share its lifecycle
class MyApplication : Application() {
    // Reference to the application graph that is used across the whole app
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
}